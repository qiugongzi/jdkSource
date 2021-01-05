


package com.sun.jmx.snmp.daemon;

import java.util.Vector;
import java.io.IOException;
import java.net.InetAddress;

import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpTimeticks;
import com.sun.jmx.snmp.SnmpIpAddress;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.InetAddressAcl;
import com.sun.jmx.snmp.SnmpPeer;

import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpMibHandler;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;



public interface SnmpAdaptorServerMBean extends CommunicatorServerMBean {

    public InetAddressAcl getInetAddressAcl();

    public Integer getTrapPort();


    public void setTrapPort(Integer port);


    public int getInformPort();


    public void setInformPort(int port);


    public int getServedClientCount();


    public int getActiveClientCount();


    public int getMaxActiveClientCount();


    public void setMaxActiveClientCount(int c) throws java.lang.IllegalStateException;


    @Override
    public String getProtocol();


    public Integer getBufferSize();


    public void setBufferSize(Integer s) throws java.lang.IllegalStateException;


    public int getMaxTries();


    public void setMaxTries(int newMaxTries);


    public int getTimeout();


    public void setTimeout(int newTimeout);


    public SnmpPduFactory getPduFactory();


    public void setPduFactory(SnmpPduFactory factory);



    public void setUserDataFactory(SnmpUserDataFactory factory);


    public SnmpUserDataFactory getUserDataFactory();


    public boolean getAuthTrapEnabled();


    public void setAuthTrapEnabled(boolean enabled);


    public boolean getAuthRespEnabled();


    public void setAuthRespEnabled(boolean enabled);


    public String getEnterpriseOid();


    public void setEnterpriseOid(String oid) throws IllegalArgumentException;


    public String[] getMibs();

    public Long getSnmpOutTraps();


    public Long getSnmpOutGetResponses();


    public Long getSnmpOutGenErrs();


    public Long getSnmpOutBadValues();


    public Long getSnmpOutNoSuchNames();


    public Long getSnmpOutTooBigs();


    public Long getSnmpInASNParseErrs();


    public Long getSnmpInBadCommunityUses();


    public Long getSnmpInBadCommunityNames();


    public Long getSnmpInBadVersions();


    public Long getSnmpOutPkts();


    public Long getSnmpInPkts();


    public Long getSnmpInGetRequests();


    public Long getSnmpInGetNexts();


    public Long getSnmpInSetRequests();


    public Long getSnmpInTotalSetVars();


    public Long getSnmpInTotalReqVars();


    public Long getSnmpSilentDrops();


    public Long getSnmpProxyDrops();

    public SnmpMibHandler addMib(SnmpMibAgent mib) throws IllegalArgumentException;


    public SnmpMibHandler addMib(SnmpMibAgent mib, SnmpOid[] oids) throws IllegalArgumentException;


    public boolean removeMib(SnmpMibAgent mib);


    public void snmpV1Trap(int generic, int specific, SnmpVarBindList varBindList) throws IOException, SnmpStatusException;



    public void snmpV1Trap(InetAddress address, String cs, int generic, int specific, SnmpVarBindList varBindList)
        throws IOException, SnmpStatusException;



    public void snmpV1Trap(SnmpPeer peer,
                           SnmpIpAddress agentAddr,
                           SnmpOid enterpOid,
                           int generic,
                           int specific,
                           SnmpVarBindList varBindList,
                           SnmpTimeticks time) throws IOException, SnmpStatusException;


    public void snmpV2Trap(SnmpPeer peer,
                           SnmpOid trapOid,
                           SnmpVarBindList varBindList,
                           SnmpTimeticks time) throws IOException, SnmpStatusException;


    public void snmpV2Trap(SnmpOid trapOid, SnmpVarBindList varBindList) throws IOException, SnmpStatusException;



    public void snmpV2Trap(InetAddress address, String cs, SnmpOid trapOid, SnmpVarBindList varBindList)
        throws IOException, SnmpStatusException;


    public void snmpPduTrap(InetAddress address, SnmpPduPacket pdu)
        throws IOException, SnmpStatusException;

    public void snmpPduTrap(SnmpPeer peer,
                            SnmpPduPacket pdu)
        throws IOException, SnmpStatusException;


    public Vector<?> snmpInformRequest(SnmpInformHandler cb, SnmpOid trapOid,
            SnmpVarBindList varBindList)
        throws IllegalStateException, IOException, SnmpStatusException;


    public SnmpInformRequest snmpInformRequest(InetAddress address, String cs, SnmpInformHandler cb,
                                               SnmpOid trapOid, SnmpVarBindList varBindList)
        throws IllegalStateException, IOException, SnmpStatusException;



    public SnmpInformRequest snmpInformRequest(SnmpPeer peer,
                                               SnmpInformHandler cb,
                                               SnmpOid trapOid,
                                               SnmpVarBindList varBindList) throws IllegalStateException, IOException, SnmpStatusException;
}
