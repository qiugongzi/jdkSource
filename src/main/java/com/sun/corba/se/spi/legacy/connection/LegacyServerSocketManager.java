
public interface LegacyServerSocketManager
{
    public int legacyGetTransientServerPort(String type);
    public int legacyGetPersistentServerPort(String socketType);
    public int legacyGetTransientOrPersistentServerPort(String socketType);

    public LegacyServerSocketEndPointInfo legacyGetEndpoint(String name);

    public boolean legacyIsLocalServerPort(int port);
}


