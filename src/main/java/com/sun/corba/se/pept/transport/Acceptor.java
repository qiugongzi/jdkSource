

package com.sun.corba.se.pept.transport;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.EventHandler;


public interface Acceptor
{

    public boolean initialize();


    public boolean initialized();


    public String getConnectionCacheType();


    public void setConnectionCache(InboundConnectionCache connectionCache);


    public InboundConnectionCache getConnectionCache();


    public boolean shouldRegisterAcceptEvent();


    public void accept();


    public void close();


    public EventHandler getEventHandler();

    public MessageMediator createMessageMediator(Broker xbroker,
                                                 Connection xconnection);

    public MessageMediator finishCreatingMessageMediator(Broker broker,
                                                         Connection xconnection,
                                                         MessageMediator messageMediator);


    public InputObject createInputObject(Broker broker,
                                         MessageMediator messageMediator);


    public OutputObject createOutputObject(Broker broker,
                                           MessageMediator messageMediator);

    }

