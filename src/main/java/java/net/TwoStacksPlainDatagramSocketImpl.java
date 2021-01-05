
package java.net;

import java.io.IOException;
import java.io.FileDescriptor;
import sun.net.ResourceManager;



class TwoStacksPlainDatagramSocketImpl extends AbstractPlainDatagramSocketImpl
{

    private FileDescriptor fd1;


    private InetAddress anyLocalBoundAddr=null;

    private int fduse=-1;


    private int lastfd=-1;

    static {
        init();
    }

    private final boolean exclusiveBind;


    private boolean reuseAddressEmulated;

    private boolean isReuseAddress;

    TwoStacksPlainDatagramSocketImpl(boolean exclBind) {
        exclusiveBind = exclBind;
    }

    protected synchronized void create() throws SocketException {
        fd1 = new FileDescriptor();
        try {
            super.create();
        } catch (SocketException e) {
            fd1 = null;
            throw e;
        }
    }

    protected synchronized void bind(int lport, InetAddress laddr)
        throws SocketException {
        super.bind(lport, laddr);
        if (laddr.isAnyLocalAddress()) {
            anyLocalBoundAddr = laddr;
        }
    }

    @Override
    protected synchronized void bind0(int lport, InetAddress laddr)
        throws SocketException
    {
        bind0(lport, laddr, exclusiveBind);

    }

    protected synchronized void receive(DatagramPacket p)
        throws IOException {
        try {
            receive0(p);
        } finally {
            fduse = -1;
        }
    }

    public Object getOption(int optID) throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket Closed");
        }

        if (optID == SO_BINDADDR) {
            if ((fd != null && fd1 != null) && !connected) {
                return anyLocalBoundAddr;
            }
            int family = connectedAddress == null ? -1 : connectedAddress.holder().getFamily();
            return socketLocalAddress(family);
        } else if (optID == SO_REUSEADDR && reuseAddressEmulated) {
            return isReuseAddress;
        } else {
            return super.getOption(optID);
        }
    }

    protected void socketSetOption(int opt, Object val)
        throws SocketException
    {
        if (opt == SO_REUSEADDR && exclusiveBind && localPort != 0)  {
            reuseAddressEmulated = true;
            isReuseAddress = (Boolean)val;
        } else {
            socketNativeSetOption(opt, val);
        }

    }

    protected boolean isClosed() {
        return (fd == null && fd1 == null) ? true : false;
    }

    protected void close() {
        if (fd != null || fd1 != null) {
            datagramSocketClose();
            ResourceManager.afterUdpClose();
            fd = null;
            fd1 = null;
        }
    }



    protected synchronized native void bind0(int lport, InetAddress laddr,
                                             boolean exclBind)
        throws SocketException;

    protected native void send(DatagramPacket p) throws IOException;

    protected synchronized native int peek(InetAddress i) throws IOException;

    protected synchronized native int peekData(DatagramPacket p) throws IOException;

    protected synchronized native void receive0(DatagramPacket p)
        throws IOException;

    protected native void setTimeToLive(int ttl) throws IOException;

    protected native int getTimeToLive() throws IOException;

    @Deprecated
    protected native void setTTL(byte ttl) throws IOException;

    @Deprecated
    protected native byte getTTL() throws IOException;

    protected native void join(InetAddress inetaddr, NetworkInterface netIf)
        throws IOException;

    protected native void leave(InetAddress inetaddr, NetworkInterface netIf)
        throws IOException;

    protected native void datagramSocketCreate() throws SocketException;

    protected native void datagramSocketClose();

    protected native void socketNativeSetOption(int opt, Object val)
        throws SocketException;

    protected native Object socketGetOption(int opt) throws SocketException;

    protected native void connect0(InetAddress address, int port) throws SocketException;

    protected native Object socketLocalAddress(int family) throws SocketException;

    protected native void disconnect0(int family);

    native int dataAvailable();


    private native static void init();
}
