
package com.sun.jmx.snmp.internal;

import java.net.InetAddress;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpSecurityException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpMsg;

import com.sun.jmx.snmp.internal.SnmpSecurityCache;
import com.sun.jmx.snmp.SnmpBadSecurityLevelException;


public interface SnmpIncomingResponse {

    public InetAddress getAddress();


    public int getPort();


    public SnmpSecurityParameters getSecurityParameters();

    public void setSecurityCache(SnmpSecurityCache cache);

    public int getSecurityLevel();

    public int getSecurityModel();

    public byte[] getContextName();


    public SnmpMsg decodeMessage(byte[] inputBytes,
                                 int byteCount,
                                 InetAddress address,
                                 int port)
        throws SnmpStatusException, SnmpSecurityException;


    public SnmpPdu decodeSnmpPdu()
        throws SnmpStatusException;


    public int getRequestId(byte[] data) throws SnmpStatusException;


    public String printMessage();
}
