
package com.sun.jmx.snmp.internal;

import java.net.InetAddress;

import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpMsg;

import com.sun.jmx.snmp.SnmpUnknownSecModelException;
import com.sun.jmx.snmp.SnmpBadSecurityLevelException;


public interface SnmpIncomingRequest {

    public SnmpSecurityParameters getSecurityParameters();

    public boolean isReport();

    public boolean isResponse();


    public void noResponse();

    public String getPrincipal();

    public int getSecurityLevel();

    public int getSecurityModel();

    public byte[] getContextName();

    public byte[] getContextEngineId();

    public byte[] getAccessContext();

    public int encodeMessage(byte[] outputBytes)
        throws SnmpTooBigException;


    public void decodeMessage(byte[] inputBytes,
                              int byteCount,
                              InetAddress address,
                              int port)
        throws SnmpStatusException, SnmpUnknownSecModelException,
               SnmpBadSecurityLevelException;


    public SnmpMsg encodeSnmpPdu(SnmpPdu p,
                                 int maxDataLength)
        throws SnmpStatusException, SnmpTooBigException;


    public SnmpPdu decodeSnmpPdu()
        throws SnmpStatusException;


    public String printRequestMessage();

    public String printResponseMessage();
}
