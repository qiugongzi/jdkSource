

package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;

import java.io.IOException;


public interface MessageMediator
{

    public Broker getBroker();


    public ContactInfo getContactInfo();


    public Connection getConnection();


    public void initializeMessage();


    public void finishSendingRequest();


    @Deprecated
    public InputObject waitForResponse();


    public void setOutputObject(OutputObject outputObject);


    public OutputObject getOutputObject();


    public void setInputObject(InputObject inputObject);


    public InputObject getInputObject();
}

