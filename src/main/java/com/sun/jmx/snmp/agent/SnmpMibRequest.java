
package com.sun.jmx.snmp.agent;

import java.util.Enumeration;
import java.util.Vector;

import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpEngine;


public interface SnmpMibRequest {

    public Enumeration<SnmpVarBind> getElements();


    public Vector<SnmpVarBind> getSubList();


    public int getVersion();


    public int getRequestPduVersion();


    public SnmpEngine getEngine();

    public String getPrincipal();

    public int getSecurityLevel();

    public int getSecurityModel();

    public byte[] getContextName();

    public byte[] getAccessContextName();


    public Object getUserData();


    public int getVarIndex(SnmpVarBind varbind);


    public void addVarBind(SnmpVarBind varbind);



    public int getSize();

    public SnmpPdu getPdu();
}
