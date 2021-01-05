
public interface ORBSocketFactory
{
    public void setORB(ORB orb);

    public ServerSocket createServerSocket(String type,
                                           InetSocketAddress inetSocketAddress)
        throws IOException;

    public Socket createSocket(String type,
                               InetSocketAddress inetSocketAddress)
        throws IOException;

    public void setAcceptedSocketOptions(Acceptor acceptor,
                                         ServerSocket serverSocket,
                                         Socket socket)
        throws SocketException;

}


