

package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpStatusException;


public interface SnmpGenericMetaServer {


    Object buildAttributeValue(long id, SnmpValue value)
        throws SnmpStatusException;


    SnmpValue buildSnmpValue(long id, Object value)
        throws SnmpStatusException;


    String getAttributeName(long id)
        throws SnmpStatusException;


    void checkSetAccess(SnmpValue x, long id, Object data)
        throws SnmpStatusException;


    void checkGetAccess(long id, Object data)
        throws SnmpStatusException;

}
