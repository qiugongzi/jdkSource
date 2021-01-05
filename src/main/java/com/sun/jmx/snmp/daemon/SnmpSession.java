
package com.sun.jmx.snmp.daemon;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.Stack;
import java.net.InetAddress;
import java.net.SocketException;

import static com.sun.jmx.defaults.JmxProperties.SNMP_ADAPTOR_LOGGER;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBindList;



class SnmpSession implements SnmpDefinitions, Runnable {

    protected transient SnmpAdaptorServer adaptor;

    protected transient SnmpSocket informSocket = null;

    private transient Hashtable<SnmpInformRequest, SnmpInformRequest> informRequestList =
            new Hashtable<>();

    private transient Stack<SnmpInformRequest> informRespq =
            new Stack<>();

    private transient Thread myThread = null;

    private transient SnmpInformRequest syncInformReq ;

    SnmpQManager snmpQman = null;

    private boolean isBeingCancelled = false;

    public SnmpSession(SnmpAdaptorServer adp) throws SocketException {
        adaptor = adp;
        snmpQman = new SnmpQManager();
        SnmpResponseHandler snmpRespHdlr = new SnmpResponseHandler(adp, snmpQman);
        initialize(adp, snmpRespHdlr);
    }

    public SnmpSession() throws SocketException {
    }
    protected synchronized void initialize(SnmpAdaptorServer adp,
                                           SnmpResponseHandler snmpRespHdlr)
        throws SocketException {
        informSocket = new SnmpSocket(snmpRespHdlr, adp.getAddress(), adp.getBufferSize().intValue());

        myThread = new Thread(this, "SnmpSession");
        myThread.start();
    }


    synchronized boolean isSessionActive() {
        return ((adaptor.isActive()) && (myThread != null) && (myThread.isAlive()));
    }


    SnmpSocket getSocket() {
        return informSocket;
    }


    SnmpQManager getSnmpQManager() {
        return snmpQman;
    }


    private synchronized boolean syncInProgress() {
        return syncInformReq != null ;
    }

    private synchronized void setSyncMode(SnmpInformRequest req) {
        syncInformReq = req ;
    }

    private synchronized void resetSyncMode() {
        if (syncInformReq == null)
            return ;
        syncInformReq = null ;
        if (thisSessionContext())
            return ;
        this.notifyAll() ;
    }


    boolean thisSessionContext() {
        return (Thread.currentThread() == myThread) ;
    }


    SnmpInformRequest makeAsyncRequest(InetAddress addr, String cs,
                                       SnmpInformHandler cb,
                                       SnmpVarBindList vblst, int port)
        throws SnmpStatusException {

        if (!isSessionActive()) {
            throw new SnmpStatusException("SNMP adaptor server not ONLINE");
        }
        SnmpInformRequest snmpreq = new SnmpInformRequest(this, adaptor, addr, cs, port, cb);
        snmpreq.start(vblst);
        return snmpreq;
    }


    void waitForResponse(SnmpInformRequest req, long waitTime) {

        if (! req.inProgress())
            return ;
        setSyncMode(req) ;
        if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(),
                "waitForResponse", "Session switching to sync mode for inform request " + req.getRequestId());
        }
        long maxTime ;
        if (waitTime <= 0)
            maxTime = System.currentTimeMillis() + 6000 * 1000 ;
        else
            maxTime = System.currentTimeMillis() + waitTime ;

        while (req.inProgress() || syncInProgress()) {
            waitTime = maxTime - System.currentTimeMillis() ;
            if (waitTime <= 0)
                break ;
            synchronized (this) {
                if (! informRespq.removeElement(req)) {
                    try {
                        this.wait(waitTime) ;
                    } catch(InterruptedException e) {
                    }
                    continue ;
                }
            }
            try {
                processResponse(req) ;
            } catch (Exception e) {
                if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                    SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(),
                        "waitForResponse", "Got unexpected exception", e);
                }
            }
        }
        resetSyncMode() ;
    }


    @Override
    public void run() {
        myThread = Thread.currentThread();
        myThread.setPriority(Thread.NORM_PRIORITY);

        SnmpInformRequest reqc = null;
        while (myThread != null) {
            try {
                reqc = nextResponse();
                if (reqc != null) {
                    processResponse(reqc);
                }
            } catch (ThreadDeath d) {
                myThread = null;
                if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                    SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(),
                        "run", "ThreadDeath, session thread unexpectedly shutting down");
                }
                throw d ;
            }
        }
        if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(),
                "run", "Session thread shutting down");
        }
        myThread = null ;
    }

    private void processResponse(SnmpInformRequest reqc) {

        while (reqc != null && myThread != null) {
            try {
                if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                    SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(),
                            "processResponse", "Processing response to req = " + reqc.getRequestId());
                }
                reqc.processResponse() ;  reqc = null ;  } catch (Exception e) {
                if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                    SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(),
                        "processResponse", "Got unexpected exception", e);
                }
                reqc = null ;
            } catch (OutOfMemoryError ome) {
                if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                    SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(),
                        "processResponse", "Out of memory error in session thread", ome);
                }
                Thread.yield();
                continue ;   }
        }
    }

    synchronized void addInformRequest(SnmpInformRequest snmpreq) throws SnmpStatusException {

        if (!isSessionActive()) {
            throw new SnmpStatusException("SNMP adaptor is not ONLINE or session is dead...") ;
        }
        informRequestList.put(snmpreq, snmpreq);
    }


    synchronized void removeInformRequest(SnmpInformRequest snmpreq) {
        if(!isBeingCancelled)
            informRequestList.remove(snmpreq) ;

        if (syncInformReq != null && syncInformReq == snmpreq) {
            resetSyncMode() ;
        }
    }


    private void cancelAllRequests() {
        final SnmpInformRequest[] list;

        synchronized(this) {

            if (informRequestList.isEmpty()) {
                return ;
            }

            isBeingCancelled = true;

            list = new SnmpInformRequest[informRequestList.size()];
            java.util.Iterator<SnmpInformRequest> it = informRequestList.values().iterator();
            int i = 0;
            while(it.hasNext()) {
                SnmpInformRequest req = it.next();
                list[i++] = req;
                it.remove();
            }
            informRequestList.clear();
        }

        for(int i = 0; i < list.length; i++)
            list[i].cancelRequest();
    }


    void addResponse(SnmpInformRequest reqc) {

        SnmpInformRequest snmpreq = reqc;
        if (isSessionActive()) {
            synchronized(this) {
                informRespq.push(reqc) ;
                this.notifyAll() ;
            }
        } else {
            if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSession.class.getName(),
                    "addResponse", "Adaptor not ONLINE or session thread dead, so inform response is dropped..." + reqc.getRequestId());
            }
        }
    }

    private synchronized SnmpInformRequest nextResponse() {

        if (informRespq.isEmpty()) {
            try {
                if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                    SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(),
                       "nextResponse", "Blocking for response");
                }
                this.wait();
            } catch(InterruptedException e) {
            }
        }
        if (informRespq.isEmpty())
            return null;
        SnmpInformRequest reqc = informRespq.firstElement();
        informRespq.removeElementAt(0) ;
        return reqc ;
    }

    private synchronized void cancelAllResponses() {
        if (informRespq != null) {
            syncInformReq = null ;
            informRespq.removeAllElements() ;
            this.notifyAll() ;
        }
    }


    final void destroySession() {

        cancelAllRequests() ;
        cancelAllResponses() ;
        synchronized(this) {
            informSocket.close() ;
            informSocket = null ;
        }
        snmpQman.stopQThreads() ;
        snmpQman = null ;
        killSessionThread() ;
    }


    private synchronized void killSessionThread() {

        if ((myThread != null) && (myThread.isAlive())) {
            if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(),
                   "killSessionThread", "Destroying session");
            }
            if (!thisSessionContext()) {
                myThread = null ;
                this.notifyAll() ;
            } else
                myThread = null ;
        }
    }


    @Override
    protected void finalize() {

        if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSession.class.getName(),
               "finalize", "Shutting all servers");
        }

        if (informRespq != null)
            informRespq.removeAllElements() ;
        informRespq = null ;
        if (informSocket != null)
            informSocket.close() ;
        informSocket = null ;

        snmpQman = null ;
    }

}
