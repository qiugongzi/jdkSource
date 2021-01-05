
package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;

public interface SnmpAccessControlModel extends SnmpModel {

    public void checkAccess(int version,
                            String principal,
                            int securityLevel,
                            int pduType,
                            int securityModel,
                            byte[] contextName,
                            SnmpOid oid)
        throws SnmpStatusException;

    public void checkPduAccess(int version,
                               String principal,
                               int securityLevel,
                               int pduType,
                               int securityModel,
                               byte[] contextName,
                               SnmpPdu pdu)
        throws SnmpStatusException;


    public boolean enableSnmpV1V2SetRequest();

    public boolean disableSnmpV1V2SetRequest();


    public boolean isSnmpV1V2SetRequestAuthorized();
}
