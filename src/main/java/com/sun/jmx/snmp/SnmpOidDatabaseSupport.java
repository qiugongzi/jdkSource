
package com.sun.jmx.snmp;

import java.util.Vector;

import com.sun.jmx.snmp.SnmpOidTable;
import com.sun.jmx.snmp.SnmpOidRecord;
import com.sun.jmx.snmp.SnmpStatusException;

import static com.sun.jmx.mbeanserver.Util.cast;



public class SnmpOidDatabaseSupport implements SnmpOidDatabase {


    public SnmpOidDatabaseSupport(){
        tables=new Vector<SnmpOidTable>();
    }


    public SnmpOidDatabaseSupport(SnmpOidTable table){
        tables=new Vector<SnmpOidTable>();
        tables.addElement(table);
    }


    public void add(SnmpOidTable table) {
        if (!tables.contains(table)) {
            tables.addElement(table);
        }
    }


    public void remove(SnmpOidTable table) throws SnmpStatusException {
        if (!tables.contains(table)) {
            throw new SnmpStatusException("The specified SnmpOidTable does not exist in this SnmpOidDatabase");
        }
        tables.removeElement(table);
    }


    public SnmpOidRecord resolveVarName(String name) throws SnmpStatusException {
        for (int i=0;i<tables.size();i++) {
            try {
                return (tables.elementAt(i).resolveVarName(name));
            }
            catch (SnmpStatusException e) {
                if (i==tables.size()-1) {
                    throw new SnmpStatusException(e.getMessage());
                }
            }
        }
        return null;
    }


    public SnmpOidRecord resolveVarOid(String oid) throws SnmpStatusException {
        for (int i=0;i<tables.size();i++) {
            try {
                return tables.elementAt(i).resolveVarOid(oid);
            }
            catch (SnmpStatusException e) {
                if (i==tables.size()-1) {
                    throw new SnmpStatusException(e.getMessage());
                }
            }
        }
        return null;
    }


    public Vector<?> getAllEntries() {
        Vector<SnmpOidTable> res = new Vector<SnmpOidTable>();
        for (int i=0;i<tables.size();i++) {
            Vector<SnmpOidTable> tmp = cast(tables.elementAt(i).getAllEntries());
            if (tmp != null) {
                for(int ii=0; ii<tmp.size(); ii++) {
                        res.addElement(tmp.elementAt(ii));
                }
            }
        }
return res;
    }


    public void removeAll(){
        tables.removeAllElements() ;
    }

    private Vector<SnmpOidTable> tables;
}
