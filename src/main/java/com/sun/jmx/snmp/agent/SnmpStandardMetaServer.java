

package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpStatusException;


public interface SnmpStandardMetaServer {

    public SnmpValue get(long arc, Object userData)
        throws SnmpStatusException ;


    public SnmpValue set(SnmpValue x, long arc, Object userData)
        throws SnmpStatusException ;


    public void check(SnmpValue x, long arc, Object userData)
        throws SnmpStatusException ;

}
