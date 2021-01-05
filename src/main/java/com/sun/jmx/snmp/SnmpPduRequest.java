
    public SnmpPdu getResponsePdu() {
        SnmpPduRequest result = new SnmpPduRequest();
        result.address = address;
        result.port = port;
        result.version = version;
        result.community = community;
        result.type = SnmpDefinitions.pduGetResponsePdu;
        result.requestId = requestId;
        result.errorStatus = SnmpDefinitions.snmpRspNoError;
        result.errorIndex = 0;

        return result;
    }
}
