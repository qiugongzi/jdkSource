
package com.sun.jmx.snmp;

import java.util.Objects;
import java.util.Vector;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.Hashtable;

import static com.sun.jmx.defaults.JmxProperties.SNMP_LOGGER;



public class SnmpOidTableSupport implements SnmpOidTable {


    public SnmpOidTableSupport(String name) {
        myName=name;
    }


    @Override
    public SnmpOidRecord resolveVarName(String name) throws SnmpStatusException {

        SnmpOidRecord var  = oidStore.get(name) ;
        if (var != null) {
            return var;
        } else {
            throw new SnmpStatusException("Variable name <" + name + "> not found in Oid repository") ;
        }
    }


    @Override
    public SnmpOidRecord resolveVarOid(String oid) throws SnmpStatusException {

        int index = oid.indexOf('.') ;
        if (index < 0) {
            throw new SnmpStatusException("Variable oid <" + oid + "> not found in Oid repository") ;
        }
        if (index == 0) {
            oid= oid.substring(1, oid.length());
        }

        for(Enumeration<SnmpOidRecord> list= oidStore.elements(); list.hasMoreElements(); ) {
            SnmpOidRecord element= list.nextElement();
            if (element.getOid().equals(oid))
                return element;
        }

        throw new SnmpStatusException("Variable oid <" + oid + "> not found in Oid repository") ;
    }


    @Override
    public Vector<SnmpOidRecord> getAllEntries() {

        Vector<SnmpOidRecord> elementsVector = new Vector<>();
        for (Enumeration<SnmpOidRecord> e = oidStore.elements();
             e.hasMoreElements(); ) {
            elementsVector.addElement(e.nextElement());
        }
        return elementsVector ;
    }


    public synchronized void loadMib(SnmpOidRecord[] mibs) {
        try {
            for (int i = 0; ; i++) {
                SnmpOidRecord s = mibs[i] ;
                if (SNMP_LOGGER.isLoggable(Level.FINER)) {
                    SNMP_LOGGER.logp(Level.FINER,
                            SnmpOidTableSupport.class.getName(),
                            "loadMib", "Load " + s.getName());
                }
                oidStore.put(s.getName(), s) ;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }


    @Override
    public boolean equals(Object object) {

        if (!(object instanceof SnmpOidTableSupport)) {
            return false;
        }
        SnmpOidTableSupport val = (SnmpOidTableSupport) object;
        return myName.equals(val.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(myName);
    }


    public String getName() {
        return myName;
    }




    private Hashtable<String, SnmpOidRecord> oidStore = new Hashtable<>();
    private String myName;
}
