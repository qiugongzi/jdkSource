

package com.sun.corba.se.impl.transport;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;

import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.pept.transport.Selector;

import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ConnectionCache;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.impl.orbutil.ORBUtility;


public class CorbaTransportManagerImpl
    implements
        CorbaTransportManager
{
    protected ORB orb;
    protected List acceptors;
    protected Map outboundConnectionCaches;
    protected Map inboundConnectionCaches;
    protected Selector selector;

    public CorbaTransportManagerImpl(ORB orb)
    {
        this.orb = orb;
        acceptors = new ArrayList();
        outboundConnectionCaches = new HashMap();
        inboundConnectionCaches = new HashMap();
        selector = new SelectorImpl(orb);
    }

    public ByteBufferPool getByteBufferPool(int id)
    {
        throw new RuntimeException();
    }

    public OutboundConnectionCache getOutboundConnectionCache(
        ContactInfo contactInfo)
    {
        synchronized (contactInfo) {
            if (contactInfo.getConnectionCache() == null) {
                OutboundConnectionCache connectionCache = null;
                synchronized (outboundConnectionCaches) {
                    connectionCache = (OutboundConnectionCache)
                        outboundConnectionCaches.get(
                            contactInfo.getConnectionCacheType());
                    if (connectionCache == null) {
                        connectionCache =
                            new CorbaOutboundConnectionCacheImpl(orb,
                                                                 contactInfo);
                        outboundConnectionCaches.put(
                            contactInfo.getConnectionCacheType(),
                            connectionCache);
                    }
                }
                contactInfo.setConnectionCache(connectionCache);
            }
            return contactInfo.getConnectionCache();
        }
    }

    public Collection getOutboundConnectionCaches()
    {
        return outboundConnectionCaches.values();
    }

    public InboundConnectionCache getInboundConnectionCache(
        Acceptor acceptor)
    {
        synchronized (acceptor) {
            if (acceptor.getConnectionCache() == null) {
                InboundConnectionCache connectionCache = null;
                synchronized (inboundConnectionCaches) {
                    connectionCache = (InboundConnectionCache)
                        inboundConnectionCaches.get(
                            acceptor.getConnectionCacheType());
                    if (connectionCache == null) {
                        connectionCache =
                            new CorbaInboundConnectionCacheImpl(orb,
                                                                acceptor);
                        inboundConnectionCaches.put(
                            acceptor.getConnectionCacheType(),
                            connectionCache);
                    }
                }
                acceptor.setConnectionCache(connectionCache);
            }
            return acceptor.getConnectionCache();
        }
    }

    public Collection getInboundConnectionCaches()
    {
        return inboundConnectionCaches.values();
    }

    public Selector getSelector(int id)
    {
        return selector;
    }

    public synchronized void registerAcceptor(Acceptor acceptor)
    {
        if (orb.transportDebugFlag) {
            dprint(".registerAcceptor->: " + acceptor);
        }
        acceptors.add(acceptor);
        if (orb.transportDebugFlag) {
            dprint(".registerAcceptor<-: " + acceptor);
        }
    }

    public Collection getAcceptors()
    {
        return getAcceptors(null, null);
    }

    public synchronized void unregisterAcceptor(Acceptor acceptor)
    {
        acceptors.remove(acceptor);
    }

    public void close()
    {
        try {
            if (orb.transportDebugFlag) {
                dprint(".close->");
            }
            for (Object cc : outboundConnectionCaches.values()) {
                ((ConnectionCache)cc).close() ;
            }
            for (Object icc : inboundConnectionCaches.values()) {
                ((ConnectionCache)icc).close() ;
                unregisterAcceptor(((InboundConnectionCache)icc).getAcceptor());
            }
            getSelector(0).close();
        } finally {
            if (orb.transportDebugFlag) {
                dprint(".close<-");
            }
        }
    }

    public Collection getAcceptors(String objectAdapterManagerId,
                                   ObjectAdapterId objectAdapterId)
    {
        Iterator iterator = acceptors.iterator();
        while (iterator.hasNext()) {
            Acceptor acceptor = (Acceptor) iterator.next();
            if (acceptor.initialize()) {
                if (acceptor.shouldRegisterAcceptEvent()) {
                    orb.getTransportManager().getSelector(0)
                        .registerForEvent(acceptor.getEventHandler());
                }
            }
        }
        return acceptors;
    }

    public void addToIORTemplate(IORTemplate iorTemplate,
                                 Policies policies,
                                 String codebase,
                                 String objectAdapterManagerId,
                                 ObjectAdapterId objectAdapterId)
    {
        Iterator iterator =
            getAcceptors(objectAdapterManagerId, objectAdapterId).iterator();
        while (iterator.hasNext()) {
            CorbaAcceptor acceptor = (CorbaAcceptor) iterator.next();
            acceptor.addToIORTemplate(iorTemplate, policies, codebase);
        }
    }


    protected void dprint(String msg)
    {
        ORBUtility.dprint("CorbaTransportManagerImpl", msg);
    }
}

