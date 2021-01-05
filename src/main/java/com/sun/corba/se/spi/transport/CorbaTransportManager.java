
public interface CorbaTransportManager
    extends
        TransportManager
{
    public static final String SOCKET_OR_CHANNEL_CONNECTION_CACHE =
        "SocketOrChannelConnectionCache";

    public Collection getAcceptors(String objectAdapterManagerId,
                                   ObjectAdapterId objectAdapterId);


    public void addToIORTemplate(IORTemplate iorTemplate,
                                 Policies policies,
                                 String codebase,
                                 String objectAdapterManagerId,
                                 ObjectAdapterId objectAdapterId);
}


