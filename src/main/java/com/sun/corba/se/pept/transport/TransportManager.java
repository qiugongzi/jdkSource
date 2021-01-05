
public interface TransportManager
{
    public ByteBufferPool getByteBufferPool(int id);

    public OutboundConnectionCache getOutboundConnectionCache(
        ContactInfo contactInfo);

    public Collection getOutboundConnectionCaches();

    public InboundConnectionCache getInboundConnectionCache(Acceptor acceptor);

    public Collection getInboundConnectionCaches();

    public Selector getSelector(int id);

    public void registerAcceptor(Acceptor acceptor);

    public Collection getAcceptors();

    public void unregisterAcceptor(Acceptor acceptor);

    public void close();
}


