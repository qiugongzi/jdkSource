
package com.sun.jmx.snmp.daemon;

import java.net.DatagramPacket;
import java.util.logging.Level;

import static com.sun.jmx.defaults.JmxProperties.SNMP_ADAPTOR_LOGGER;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;



class SnmpResponseHandler {

    SnmpAdaptorServer adaptor = null;
    SnmpQManager snmpq = null;

    public SnmpResponseHandler(SnmpAdaptorServer adp, SnmpQManager s) {
        adaptor = adp;
        snmpq = s;
    }

    public synchronized void processDatagram(DatagramPacket dgrm) {

        byte []data = dgrm.getData();
        int datalen = dgrm.getLength();

        if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpResponseHandler.class.getName(),
                "action", "processDatagram", "Received from " + dgrm.getAddress().toString() +
                 " Length = " + datalen + "\nDump : \n" + SnmpMessage.dumpHexBuffer(data, 0, datalen));
        }

        try {
            SnmpMessage msg = new SnmpMessage();
            msg.decodeMessage(data, datalen);
            msg.address = dgrm.getAddress();
            msg.port = dgrm.getPort();

            SnmpPduFactory pduFactory = adaptor.getPduFactory();
            if (pduFactory == null) {
                if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                    SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(),
                        "processDatagram", "Dropping packet. Unable to find the pdu factory of the SNMP adaptor server");
                }
            }
            else {
                SnmpPduPacket snmpProt = (SnmpPduPacket)pduFactory.decodeSnmpPdu(msg);

                if (snmpProt == null) {
                    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(),
                            "processDatagram", "Dropping packet. Pdu factory returned a null value");
                    }
                }
                else if (snmpProt instanceof SnmpPduRequest) {

                    SnmpPduRequest pduReq = (SnmpPduRequest)snmpProt;
                    SnmpInformRequest req = snmpq.removeRequest(pduReq.requestId) ;
                    if (req != null) {
                        req.invokeOnResponse(pduReq);
                    } else {
                        if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                            SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(),
                                "processDatagram", "Dropping packet. Unable to find corresponding for InformRequestId = " + pduReq.requestId);
                        }
                    }
                }
                else {
                    if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                        SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(),
                            "processDatagram", "Dropping packet. The packet does not contain an inform response");
                    }
                }
                snmpProt = null ;
            }
        } catch (Exception e) {
            if (SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpResponseHandler.class.getName(),
                    "processDatagram", "Exception while processsing", e);
            }
        }
    }

}
