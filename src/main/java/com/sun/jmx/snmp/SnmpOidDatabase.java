
package com.sun.jmx.snmp;

import java.util.Vector;

import com.sun.jmx.snmp.SnmpOidTable;
import com.sun.jmx.snmp.SnmpOidRecord;
import com.sun.jmx.snmp.SnmpStatusException;



public interface SnmpOidDatabase extends SnmpOidTable {


    public void add(SnmpOidTable table);


    public void remove(SnmpOidTable table) throws SnmpStatusException;


    public void removeAll();


    public SnmpOidRecord resolveVarName(String name) throws SnmpStatusException ;


    public SnmpOidRecord resolveVarOid(String oid) throws SnmpStatusException;


    public Vector<?> getAllEntries() ;
    }
