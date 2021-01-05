

package com.sun.jmx.snmp.agent;

import java.util.Enumeration;
import java.util.Vector;


import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpEngine;


final class SnmpMibRequestImpl implements SnmpMibRequest {


    public SnmpMibRequestImpl(SnmpEngine engine,
                              SnmpPdu reqPdu,
                              Vector<SnmpVarBind> vblist,
                              int protocolVersion,
                              Object userData,
                              String principal,
                              int securityLevel,
                              int securityModel,
                              byte[] contextName,
                              byte[] accessContextName) {
        varbinds   = vblist;
        version    = protocolVersion;
        data       = userData;
        this.reqPdu = reqPdu;
        this.engine = engine;
        this.principal = principal;
        this.securityLevel = securityLevel;
        this.securityModel = securityModel;
        this.contextName = contextName;
        this.accessContextName = accessContextName;
    }
    @Override
    public SnmpEngine getEngine() {
        return engine;
    }


    @Override
    public String getPrincipal() {
        return principal;
    }


    @Override
    public int getSecurityLevel() {
        return securityLevel;
    }

    @Override
    public int getSecurityModel() {
        return securityModel;
    }

    @Override
    public byte[] getContextName() {
        return contextName;
    }


    @Override
    public byte[] getAccessContextName() {
        return accessContextName;
    }

    @Override
    public final SnmpPdu getPdu() {
        return reqPdu;
    }

    @Override
    public final Enumeration<SnmpVarBind> getElements()  {return varbinds.elements();}

    @Override
    public final Vector<SnmpVarBind> getSubList()  {return varbinds;}

    @Override
    public final int getSize()  {
        if (varbinds == null) return 0;
        return varbinds.size();
    }

    @Override
    public final int         getVersion()  {return version;}

    @Override
    public final int         getRequestPduVersion()  {return reqPdu.version;}

    @Override
    public final Object      getUserData() {return data;}

    @Override
    public final int getVarIndex(SnmpVarBind varbind) {
        return varbinds.indexOf(varbind);
    }

    @Override
    public void addVarBind(SnmpVarBind varbind) {
        varbinds.addElement(varbind);
    }

    final void setRequestTree(SnmpRequestTree tree) {this.tree = tree;}

    final SnmpRequestTree getRequestTree() {return tree;}

    final Vector<SnmpVarBind> getVarbinds() {return varbinds;}

    private Vector<SnmpVarBind> varbinds;
    private int    version;
    private Object data;
    private SnmpPdu reqPdu = null;
    private SnmpRequestTree tree = null;
    private SnmpEngine engine = null;
    private String principal = null;
    private int securityLevel = -1;
    private int securityModel = -1;
    private byte[] contextName = null;
    private byte[] accessContextName = null;
}
