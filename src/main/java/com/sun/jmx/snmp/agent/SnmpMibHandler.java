


package com.sun.jmx.snmp.agent;



import java.util.Vector;
import java.io.IOException;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;



public interface SnmpMibHandler {


    public SnmpMibHandler addMib(SnmpMibAgent mib) throws IllegalArgumentException;


    public SnmpMibHandler addMib(SnmpMibAgent mib, SnmpOid[] oids) throws IllegalArgumentException;


    public SnmpMibHandler addMib(SnmpMibAgent mib, String contextName)
        throws IllegalArgumentException;


    public SnmpMibHandler addMib(SnmpMibAgent mib, String contextName, SnmpOid[] oids)
        throws IllegalArgumentException;


    public boolean removeMib(SnmpMibAgent mib);

    public boolean removeMib(SnmpMibAgent mib, SnmpOid[] oids);

    public boolean removeMib(SnmpMibAgent mib, String contextName);

    public boolean removeMib(SnmpMibAgent mib, String contextName, SnmpOid[] oids);
}
