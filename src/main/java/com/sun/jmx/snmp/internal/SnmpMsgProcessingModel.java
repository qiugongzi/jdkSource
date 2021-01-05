
package com.sun.jmx.snmp.internal;


import com.sun.jmx.snmp.mpm.SnmpMsgTranslator;

import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpSecurityParameters;

import com.sun.jmx.snmp.SnmpParams;

public interface SnmpMsgProcessingModel extends SnmpModel {

    public SnmpOutgoingRequest getOutgoingRequest(SnmpPduFactory factory);

    public SnmpIncomingRequest getIncomingRequest(SnmpPduFactory factory);


    public SnmpIncomingResponse getIncomingResponse(SnmpPduFactory factory);

    public SnmpPdu getRequestPdu(SnmpParams p, int type) throws SnmpStatusException;


    public int encode(int version,
                      int msgID,
                      int msgMaxSize,
                      byte msgFlags,
                      int msgSecurityModel,
                      SnmpSecurityParameters params,
                      byte[] contextEngineID,
                      byte[] contextName,
                      byte[] data,
                      int dataLength,
                      byte[] outputBytes) throws SnmpTooBigException;

    public int encodePriv(int version,
                          int msgID,
                          int msgMaxSize,
                          byte msgFlags,
                          int msgSecurityModel,
                          SnmpSecurityParameters params,
                          byte[] encryptedPdu,
                          byte[] outputBytes) throws SnmpTooBigException;

    public SnmpDecryptedPdu decode(byte[] pdu) throws SnmpStatusException;


    public int encode(SnmpDecryptedPdu pdu,
                      byte[] outputBytes) throws SnmpTooBigException;


    public void setMsgTranslator(SnmpMsgTranslator translator);


    public SnmpMsgTranslator getMsgTranslator();
}
