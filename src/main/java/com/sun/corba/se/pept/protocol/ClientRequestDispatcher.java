

package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.ContactInfo;


public interface ClientRequestDispatcher
{

    public OutputObject beginRequest(Object self,
                                     String methodName,
                                     boolean isOneWay,
                                     ContactInfo contactInfo);


    public InputObject marshalingComplete(java.lang.Object self,
                                          OutputObject outputObject)
    throws
            org.omg.CORBA.portable.ApplicationException,
            org.omg.CORBA.portable.RemarshalException;


    public void endRequest(Broker broker,
                           java.lang.Object self,
                           InputObject inputObject);
}

