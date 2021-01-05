

public interface RequestMessage extends Message {

    byte RESPONSE_EXPECTED_BIT = 0x01;

    ServiceContexts getServiceContexts();
    int getRequestId();
    boolean isResponseExpected();
    byte[] getReserved();
    ObjectKey getObjectKey();
    String getOperation();
    Principal getPrincipal();


    void setThreadPoolToUse(int poolToUse);


}
