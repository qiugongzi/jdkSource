
package com.sun.jmx.snmp.internal;

import java.util.Vector;
import com.sun.jmx.snmp.SnmpMsg;
import com.sun.jmx.snmp.SnmpParams;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpSecurityParameters;

import com.sun.jmx.snmp.SnmpUnknownMsgProcModelException;


public interface SnmpMsgProcessingSubSystem extends SnmpSubSystem {


    public void setSecuritySubSystem(SnmpSecuritySubSystem security);

    public SnmpSecuritySubSystem getSecuritySubSystem();


    public SnmpIncomingRequest getIncomingRequest(int model,
                                                  SnmpPduFactory factory)
        throws SnmpUnknownMsgProcModelException;

    public SnmpOutgoingRequest getOutgoingRequest(int model,
                                                  SnmpPduFactory factory) throws SnmpUnknownMsgProcModelException ;

    public SnmpPdu getRequestPdu(int model, SnmpParams p, int type) throws SnmpUnknownMsgProcModelException, SnmpStatusException ;

    public SnmpIncomingResponse getIncomingResponse(int model,
                                                    SnmpPduFactory factory) throws SnmpUnknownMsgProcModelException;

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
                      byte[] outputBytes)
        throws SnmpTooBigException,
               SnmpUnknownMsgProcModelException ;

    public int encodePriv(int version,
                          int msgID,
                          int msgMaxSize,
                          byte msgFlags,
                          int msgSecurityModel,
                          SnmpSecurityParameters params,
                          byte[] encryptedPdu,
                          byte[] outputBytes) throws SnmpTooBigException, SnmpUnknownMsgProcModelException;


    public SnmpDecryptedPdu decode(int version,
                                   byte[] pdu)
        throws SnmpStatusException, SnmpUnknownMsgProcModelException;


    public int encode(int version,
                      SnmpDecryptedPdu pdu,
                      byte[] outputBytes)
        throws SnmpTooBigException, SnmpUnknownMsgProcModelException;
}
