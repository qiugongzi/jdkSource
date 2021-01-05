

package com.sun.corba.se.impl.transport;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;

import com.sun.corba.se.impl.encoding.BufferManagerFactory;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.CorbaMessageMediatorImpl;
import com.sun.corba.se.impl.protocol.SharedCDRClientRequestDispatcherImpl;

public class SharedCDRContactInfoImpl
    extends
        CorbaContactInfoBase
{
    private static int requestId = 0;

    protected ORBUtilSystemException wrapper;

    public SharedCDRContactInfoImpl(
        ORB orb,
        CorbaContactInfoList contactInfoList,
        IOR effectiveTargetIOR,
        short addressingDisposition)
    {
        this.orb = orb;
        this.contactInfoList = contactInfoList;
        this.effectiveTargetIOR = effectiveTargetIOR;
        this.addressingDisposition = addressingDisposition;
    }

    public ClientRequestDispatcher getClientRequestDispatcher()
    {
        return new SharedCDRClientRequestDispatcherImpl();
    }

    public boolean isConnectionBased()
    {
        return false;
    }

    public boolean shouldCacheConnection()
    {
        return false;
    }

    public String getConnectionCacheType()
    {
        throw getWrapper().methodShouldNotBeCalled();
    }

    public Connection createConnection()
    {
        throw getWrapper().methodShouldNotBeCalled();
    }

    public MessageMediator createMessageMediator(Broker broker,
                                                 ContactInfo contactInfo,
                                                 Connection connection,
                                                 String methodName,
                                                 boolean isOneWay)
    {
        if (connection != null) {
            throw new RuntimeException("connection is not null");
        }

        CorbaMessageMediator messageMediator =
            new CorbaMessageMediatorImpl(
                (ORB) broker,
                contactInfo,
                null, GIOPVersion.chooseRequestVersion( (ORB)broker,
                     effectiveTargetIOR),
                effectiveTargetIOR,
                requestId++, getAddressingDisposition(),
                methodName,
                isOneWay);

        return messageMediator;
    }

    public OutputObject createOutputObject(MessageMediator messageMediator)
    {
        CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)
            messageMediator;
        OutputObject outputObject =
            sun.corba.OutputStreamFactory.newCDROutputObject(orb, messageMediator,
                                corbaMessageMediator.getRequestHeader(),
                                corbaMessageMediator.getStreamFormatVersion(),
                                BufferManagerFactory.GROW);
        messageMediator.setOutputObject(outputObject);
        return outputObject;
    }

    public String getMonitoringName()
    {
        throw getWrapper().methodShouldNotBeCalled();
    }

    public String toString()
    {
        return
            "SharedCDRContactInfoImpl["
            + "]";
    }

    protected ORBUtilSystemException getWrapper()
    {
        if (wrapper == null) {
            wrapper = ORBUtilSystemException.get( orb,
                          CORBALogDomains.RPC_TRANSPORT ) ;
        }
        return wrapper;
    }
}

