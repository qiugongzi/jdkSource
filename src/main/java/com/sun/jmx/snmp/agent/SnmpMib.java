

package com.sun.jmx.snmp.agent;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.Vector;

import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;

import static com.sun.jmx.defaults.JmxProperties.SNMP_ADAPTOR_LOGGER;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpStatusException;


public abstract class SnmpMib extends SnmpMibAgent implements Serializable {


    public SnmpMib() {
        root= new SnmpMibOid();
    }


    protected String getGroupOid(String groupName, String defaultOid) {
        return defaultOid;
    }


    protected ObjectName getGroupObjectName(String name, String oid,
                                            String defaultName)
        throws MalformedObjectNameException {
        return new ObjectName(defaultName);
    }


    protected void registerGroupNode(String groupName,   String groupOid,
                                     ObjectName groupObjName, SnmpMibNode node,
                                     Object group, MBeanServer server)
        throws NotCompliantMBeanException, MBeanRegistrationException,
        InstanceAlreadyExistsException, IllegalAccessException {
        root.registerNode(groupOid,node);
        if (server != null && groupObjName != null && group != null)
            server.registerMBean(group,groupObjName);
    }


    public abstract void registerTableMeta(String name, SnmpMibTable table);


    public abstract SnmpMibTable getRegisteredTableMeta(String name);

    @Override
    public void get(SnmpMibRequest req) throws SnmpStatusException {

        final int reqType = SnmpDefinitions.pduGetRequestPdu;
        SnmpRequestTree handlers = getHandlers(req,false,false,reqType);

        SnmpRequestTree.Handler h = null;
        SnmpMibNode meta = null;

        if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(),
                    "get", "Processing handlers for GET... ");
        }

        for (Enumeration<SnmpRequestTree.Handler> eh=handlers.getHandlers();eh.hasMoreElements();) {
            h = eh.nextElement();

            meta = handlers.getMetaNode(h);

            final int depth = handlers.getOidDepth(h);

            for (Enumeration<SnmpMibSubRequest> rqs=handlers.getSubRequests(h);
                 rqs.hasMoreElements();) {

                meta.get(rqs.nextElement(),depth);
            }
        }
    }


    @Override
    public void set(SnmpMibRequest req) throws SnmpStatusException {

        SnmpRequestTree handlers = null;

        if (req instanceof SnmpMibRequestImpl)
            handlers = ((SnmpMibRequestImpl)req).getRequestTree();

        final int reqType = SnmpDefinitions.pduSetRequestPdu;
        if (handlers == null) handlers = getHandlers(req,false,true,reqType);
        handlers.switchCreationFlag(false);
        handlers.setPduType(reqType);

        SnmpRequestTree.Handler h;
        SnmpMibNode meta;

        if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(),
                    "set", "Processing handlers for SET... ");
        }

        for (Enumeration<SnmpRequestTree.Handler> eh=handlers.getHandlers();eh.hasMoreElements();) {
            h = eh.nextElement();

            meta = handlers.getMetaNode(h);

            final int depth = handlers.getOidDepth(h);

            for (Enumeration<SnmpMibSubRequest> rqs=handlers.getSubRequests(h);
                 rqs.hasMoreElements();) {

                meta.set(rqs.nextElement(),depth);
            }
        }
    }


    @Override
    public void check(SnmpMibRequest req) throws SnmpStatusException {

        final int reqType = SnmpDefinitions.pduWalkRequest;
        SnmpRequestTree handlers = getHandlers(req,true,true,reqType);

        SnmpRequestTree.Handler h;
        SnmpMibNode meta;

        if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(),
                    "check", "Processing handlers for CHECK... ");
        }

        for (Enumeration<SnmpRequestTree.Handler> eh=handlers.getHandlers();eh.hasMoreElements();) {
            h = eh.nextElement();

            meta = handlers.getMetaNode(h);

            final int depth = handlers.getOidDepth(h);

            for (Enumeration<SnmpMibSubRequest> rqs=handlers.getSubRequests(h);
                 rqs.hasMoreElements();) {

                meta.check(rqs.nextElement(),depth);
            }
        }

        if (req instanceof SnmpMibRequestImpl) {
            ((SnmpMibRequestImpl)req).setRequestTree(handlers);
        }

    }


    @Override
    public void getNext(SnmpMibRequest req) throws SnmpStatusException {
        SnmpRequestTree handlers = getGetNextHandlers(req);

        SnmpRequestTree.Handler h;
        SnmpMibNode meta;

        if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(),
                    "getNext", "Processing handlers for GET-NEXT... ");
        }

        for (Enumeration<SnmpRequestTree.Handler> eh=handlers.getHandlers();eh.hasMoreElements();) {
            h = eh.nextElement();

            meta = handlers.getMetaNode(h);

            int depth = handlers.getOidDepth(h);

            for (Enumeration<SnmpMibSubRequest> rqs=handlers.getSubRequests(h);
                 rqs.hasMoreElements();) {

                meta.get(rqs.nextElement(),depth);
            }
        }
    }



    @Override
    public void getBulk(SnmpMibRequest req, int nonRepeat, int maxRepeat)
        throws SnmpStatusException {

        getBulkWithGetNext(req, nonRepeat, maxRepeat);
    }


    @Override
    public long[] getRootOid() {

        if( rootOid == null) {
            Vector<Integer> list= new Vector<>(10);

            root.getRootOid(list);

            rootOid= new long[list.size()];
            int i=0;
            for(Enumeration<Integer> e= list.elements(); e.hasMoreElements(); ) {
                Integer val= e.nextElement();
                rootOid[i++]= val.longValue();
            }
        }
        return rootOid.clone();
    }

    private SnmpRequestTree getHandlers(SnmpMibRequest req,
                                        boolean createflag, boolean atomic,
                                        int type)
        throws SnmpStatusException {

        SnmpRequestTree handlers =
            new SnmpRequestTree(req,createflag,type);

        int index=0;
        SnmpVarBind var;
        final int ver= req.getVersion();

        for (Enumeration<SnmpVarBind> e= req.getElements(); e.hasMoreElements(); index++) {

            var= e.nextElement();

            try {
                root.findHandlingNode(var,var.oid.longValue(false),
                                      0,handlers);
            } catch(SnmpStatusException x) {

                if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                    SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                            SnmpMib.class.getName(),
                            "getHandlers",
                            "Couldn't find a handling node for " +
                            var.oid.toString());
                }

                if (ver == SnmpDefinitions.snmpVersionOne) {

                    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                                SnmpMib.class.getName(),
                                "getHandlers", "\tV1: Throwing exception");
                    }

                    final SnmpStatusException sse =
                        new SnmpStatusException(x, index + 1);
                    sse.initCause(x);
                    throw sse;
                } else if ((type == SnmpDefinitions.pduWalkRequest)   ||
                           (type == SnmpDefinitions.pduSetRequestPdu)) {
                    final int status =
                        SnmpRequestTree.mapSetException(x.getStatus(),ver);

                    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                                SnmpMib.class.getName(),
                                "getHandlers", "\tSET: Throwing exception");
                    }

                    final SnmpStatusException sse =
                        new SnmpStatusException(status, index + 1);
                    sse.initCause(x);
                    throw sse;
                } else if (atomic) {

                    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                                SnmpMib.class.getName(),
                                "getHandlers", "\tATOMIC: Throwing exception");
                    }

                    final SnmpStatusException sse =
                        new SnmpStatusException(x, index + 1);
                    sse.initCause(x);
                    throw sse;
                }

                final int status =
                    SnmpRequestTree.mapGetException(x.getStatus(),ver);

                if (status == SnmpStatusException.noSuchInstance) {

                    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                                SnmpMib.class.getName(),
                                "getHandlers",
                                "\tGET: Registering noSuchInstance");
                    }

                    var.value= SnmpVarBind.noSuchInstance;

                } else if (status == SnmpStatusException.noSuchObject) {
                    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                                SnmpMib.class.getName(),
                                "getHandlers",
                                "\tGET: Registering noSuchObject");
                    }

                        var.value= SnmpVarBind.noSuchObject;

                } else {

                    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                                SnmpMib.class.getName(),
                                "getHandlers",
                                "\tGET: Registering global error: " + status);
                    }

                    final SnmpStatusException sse =
                        new SnmpStatusException(status, index + 1);
                    sse.initCause(x);
                    throw sse;
                }
            }
        }
        return handlers;
    }


    private SnmpRequestTree getGetNextHandlers(SnmpMibRequest req)
        throws SnmpStatusException {

        SnmpRequestTree handlers = new
            SnmpRequestTree(req,false,SnmpDefinitions.pduGetNextRequestPdu);

        handlers.setGetNextFlag();

        if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpMib.class.getName(),
                    "getGetNextHandlers", "Received MIB request : " + req);
        }
        AcmChecker checker = new AcmChecker(req);
        int index=0;
        SnmpVarBind var = null;
        final int ver= req.getVersion();
        SnmpOid original = null;
        for (Enumeration<SnmpVarBind> e= req.getElements(); e.hasMoreElements(); index++) {

            var = e.nextElement();
            SnmpOid result;
            try {
                if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                    SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                            SnmpMib.class.getName(),
                            "getGetNextHandlers", " Next OID of : " + var.oid);
                }
                result = new SnmpOid(root.findNextHandlingNode
                                     (var,var.oid.longValue(false),0,
                                      0,handlers, checker));

                if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                    SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                            SnmpMib.class.getName(),
                            "getGetNextHandlers", " is : " + result);
                }
                var.oid = result;
            } catch(SnmpStatusException x) {

                if (ver == SnmpDefinitions.snmpVersionOne) {
                    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                                SnmpMib.class.getName(),
                                "getGetNextHandlers",
                                "\tThrowing exception " + x.toString());
                    }
                    throw new SnmpStatusException(x, index + 1);
                }
                if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                    SNMP_ADAPTOR_LOGGER.logp(Level.FINEST,
                            SnmpMib.class.getName(),
                            "getGetNextHandlers",
                            "Exception : " + x.getStatus());
                }

                var.setSnmpValue(SnmpVarBind.endOfMibView);
            }
        }
        return handlers;
    }

    protected SnmpMibOid root;


    private transient long[] rootOid= null;
}
