

package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;



public interface SnmpTableEntryFactory extends SnmpTableCallbackHandler {


    public void createNewEntry(SnmpMibSubRequest request, SnmpOid rowOid,
                               int depth, SnmpMibTable meta)
        throws SnmpStatusException;
}
