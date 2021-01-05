
package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpUnknownSecModelException;
import com.sun.jmx.snmp.SnmpSecurityException;
import com.sun.jmx.snmp.SnmpSecurityParameters;


public interface SnmpSecuritySubSystem extends SnmpSubSystem {

    public SnmpSecurityCache createSecurityCache(int id) throws SnmpUnknownSecModelException;

    public void releaseSecurityCache(int id,
                                     SnmpSecurityCache cache) throws SnmpUnknownSecModelException;


    public int generateRequestMsg(SnmpSecurityCache cache,
                                  int version,
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
        throws SnmpTooBigException, SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;


    public int generateResponseMsg(SnmpSecurityCache cache,
                                   int version,
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
        throws SnmpTooBigException, SnmpStatusException,
               SnmpSecurityException, SnmpUnknownSecModelException;

    public SnmpSecurityParameters
        processIncomingRequest(SnmpSecurityCache cache,
                               int version,
                               int msgID,
                               int msgMaxSize,
                               byte msgFlags,
                               int msgSecurityModel,
                               byte[] params,
                               byte[] contextEngineID,
                               byte[] contextName,
                               byte[] data,
                               byte[] encryptedPdu,
                               SnmpDecryptedPdu decryptedPdu)
        throws SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;

    public SnmpSecurityParameters processIncomingResponse(SnmpSecurityCache cache,
                                                          int version,
                                                          int msgID,
                                                          int msgMaxSize,
                                                          byte msgFlags,
                                                          int msgSecurityModel,
                                                          byte[] params,
                                                          byte[] contextEngineID,
                                                          byte[] contextName,
                                                          byte[] data,
                                                          byte[] encryptedPdu,
                                                          SnmpDecryptedPdu decryptedPdu)
        throws SnmpStatusException, SnmpSecurityException, SnmpUnknownSecModelException;
}
