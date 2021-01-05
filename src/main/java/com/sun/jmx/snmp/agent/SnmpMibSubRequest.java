

package com.sun.jmx.snmp.agent;

import java.util.Enumeration;
import java.util.Vector;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpOid;
public interface SnmpMibSubRequest extends SnmpMibRequest {

    @Override
    public Enumeration<SnmpVarBind> getElements();


    @Override
    public Vector<SnmpVarBind> getSubList();


    public SnmpOid     getEntryOid();


    public boolean     isNewEntry();


    public SnmpVarBind getRowStatusVarBind();


    public void registerGetException(SnmpVarBind varbind,
                                     SnmpStatusException exception)
        throws SnmpStatusException;


    public void registerSetException(SnmpVarBind varbind,
                                     SnmpStatusException exception)
        throws SnmpStatusException;


    public void registerCheckException(SnmpVarBind varbind,
                                       SnmpStatusException exception)
        throws SnmpStatusException;
}
