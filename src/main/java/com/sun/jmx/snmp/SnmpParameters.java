
package com.sun.jmx.snmp;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpStatusException;





public class SnmpParameters extends SnmpParams implements Cloneable, Serializable {
    private static final long serialVersionUID = -1822462497931733790L;


    public SnmpParameters() {
        _readCommunity = defaultRdCommunity ;
        _informCommunity = defaultRdCommunity ;
    }


    public SnmpParameters(String rdc, String wrc) {
        _readCommunity = rdc ;
        _writeCommunity = wrc ;
        _informCommunity = defaultRdCommunity ;
    }


    public SnmpParameters(String rdc, String wrc, String inform) {
        _readCommunity = rdc ;
        _writeCommunity = wrc ;
        _informCommunity = inform ;
    }


    public String getRdCommunity() {
        return _readCommunity ;
    }


    public synchronized void setRdCommunity(String read) {
        if (read == null)
            _readCommunity = defaultRdCommunity ;
        else
            _readCommunity = read ;
    }


    public String getWrCommunity() {
        return _writeCommunity ;
    }


    public void setWrCommunity(String write) {
        _writeCommunity = write;
    }


    public String getInformCommunity() {
        return _informCommunity ;
    }


    public void setInformCommunity(String inform) {
        if (inform == null)
            _informCommunity = defaultRdCommunity ;
        else
            _informCommunity = inform ;
    }


    public boolean allowSnmpSets() {
        return _writeCommunity != null ;
    }


    @Override
    public synchronized boolean equals(Object obj) {
        if (!( obj instanceof SnmpParameters))
            return false;

        if (this == obj)
            return true ;
        SnmpParameters param = (SnmpParameters) obj ;
        if (_protocolVersion == param._protocolVersion)
            if (_readCommunity.equals(param._readCommunity))
                return true ;
        return false ;
    }

    @Override
    public synchronized int hashCode() {
        return (_protocolVersion * 31) ^ Objects.hashCode(_readCommunity);
    }


    public synchronized Object clone() {
        SnmpParameters par = null ;
        try {
            par = (SnmpParameters) super.clone() ;
            par._readCommunity = _readCommunity ;
            par._writeCommunity = _writeCommunity ;
            par._informCommunity = _informCommunity ;
        } catch (CloneNotSupportedException e) {
            throw new InternalError() ; }
        return par ;
    }


    public byte[] encodeAuthentication(int snmpCmd)
        throws SnmpStatusException {
        try {
            if (snmpCmd == pduSetRequestPdu)
                return _writeCommunity.getBytes("8859_1");
            else if (snmpCmd == pduInformRequestPdu)
                return _informCommunity.getBytes("8859_1") ;
            else
                return _readCommunity.getBytes("8859_1") ;
        }catch(UnsupportedEncodingException e) {
            throw new SnmpStatusException(e.getMessage());
        }
    }


    final static String defaultRdCommunity = "public" ;


    private int         _protocolVersion = snmpVersionOne ;

    private String      _readCommunity ;

    private String      _writeCommunity ;

    private String      _informCommunity ;

    }
