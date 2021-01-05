

package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpDefinitions;
import java.io.Serializable;
import com.sun.jmx.snmp.SnmpStatusException;



public abstract class SnmpMibEntry extends SnmpMibNode
    implements Serializable {


    public abstract boolean isVariable(long arc);


    public abstract boolean isReadable(long arc);


    public long getNextVarId(long id, Object userData)
        throws SnmpStatusException {
        long nextvar = super.getNextVarId(id,userData);
        while (!isReadable(nextvar))
            nextvar = super.getNextVarId(nextvar,userData);
        return nextvar;
    }


    public void validateVarId(long arc, Object userData)
        throws SnmpStatusException {
        if (isVariable(arc) == false) {
            throw new SnmpStatusException(SnmpDefinitions.snmpRspNoSuchName);
        }
    }


    abstract public void get(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException;


    abstract public void set(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException;


    abstract public void check(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException;

}
