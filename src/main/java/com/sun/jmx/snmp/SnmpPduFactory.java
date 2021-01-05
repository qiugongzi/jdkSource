


package com.sun.jmx.snmp;






public interface SnmpPduFactory {


    public SnmpPdu decodeSnmpPdu(SnmpMsg msg) throws SnmpStatusException ;


    public SnmpMsg encodeSnmpPdu(SnmpPdu p, int maxDataLength)
        throws SnmpStatusException, SnmpTooBigException ;
}
