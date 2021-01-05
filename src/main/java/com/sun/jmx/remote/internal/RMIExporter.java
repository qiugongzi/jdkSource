
public interface RMIExporter {
    public static final String EXPORTER_ATTRIBUTE =
        "com.sun.jmx.remote.rmi.exporter";

    public Remote exportObject(Remote obj,
                               int port,
                               RMIClientSocketFactory csf,
                               RMIServerSocketFactory ssf)
            throws RemoteException;

    public boolean unexportObject(Remote obj, boolean force)
            throws NoSuchObjectException;
}
