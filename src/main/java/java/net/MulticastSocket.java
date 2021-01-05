

package java.net;

import java.io.IOException;
import java.util.Enumeration;


public
class MulticastSocket extends DatagramSocket {


    private boolean interfaceSet;


    public MulticastSocket() throws IOException {
        this(new InetSocketAddress(0));
    }


    public MulticastSocket(int port) throws IOException {
        this(new InetSocketAddress(port));
    }


    public MulticastSocket(SocketAddress bindaddr) throws IOException {
        super((SocketAddress) null);

        setReuseAddress(true);

        if (bindaddr != null) {
            try {
                bind(bindaddr);
            } finally {
                if (!isBound())
                    close();
            }
        }
    }


    private Object ttlLock = new Object();


    private Object infLock = new Object();


    private InetAddress infAddress = null;



    @Deprecated
    public void setTTL(byte ttl) throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        getImpl().setTTL(ttl);
    }


    public void setTimeToLive(int ttl) throws IOException {
        if (ttl < 0 || ttl > 255) {
            throw new IllegalArgumentException("ttl out of range");
        }
        if (isClosed())
            throw new SocketException("Socket is closed");
        getImpl().setTimeToLive(ttl);
    }


    @Deprecated
    public byte getTTL() throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        return getImpl().getTTL();
    }


    public int getTimeToLive() throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        return getImpl().getTimeToLive();
    }


    public void joinGroup(InetAddress mcastaddr) throws IOException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }

        checkAddress(mcastaddr, "joinGroup");
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkMulticast(mcastaddr);
        }

        if (!mcastaddr.isMulticastAddress()) {
            throw new SocketException("Not a multicast address");
        }


        NetworkInterface defaultInterface = NetworkInterface.getDefault();

        if (!interfaceSet && defaultInterface != null) {
            setNetworkInterface(defaultInterface);
        }

        getImpl().join(mcastaddr);
    }


    public void leaveGroup(InetAddress mcastaddr) throws IOException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }

        checkAddress(mcastaddr, "leaveGroup");
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkMulticast(mcastaddr);
        }

        if (!mcastaddr.isMulticastAddress()) {
            throw new SocketException("Not a multicast address");
        }

        getImpl().leave(mcastaddr);
    }


    public void joinGroup(SocketAddress mcastaddr, NetworkInterface netIf)
        throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");

        if (mcastaddr == null || !(mcastaddr instanceof InetSocketAddress))
            throw new IllegalArgumentException("Unsupported address type");

        if (oldImpl)
            throw new UnsupportedOperationException();

        checkAddress(((InetSocketAddress)mcastaddr).getAddress(), "joinGroup");
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkMulticast(((InetSocketAddress)mcastaddr).getAddress());
        }

        if (!((InetSocketAddress)mcastaddr).getAddress().isMulticastAddress()) {
            throw new SocketException("Not a multicast address");
        }

        getImpl().joinGroup(mcastaddr, netIf);
    }


    public void leaveGroup(SocketAddress mcastaddr, NetworkInterface netIf)
        throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");

        if (mcastaddr == null || !(mcastaddr instanceof InetSocketAddress))
            throw new IllegalArgumentException("Unsupported address type");

        if (oldImpl)
            throw new UnsupportedOperationException();

        checkAddress(((InetSocketAddress)mcastaddr).getAddress(), "leaveGroup");
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkMulticast(((InetSocketAddress)mcastaddr).getAddress());
        }

        if (!((InetSocketAddress)mcastaddr).getAddress().isMulticastAddress()) {
            throw new SocketException("Not a multicast address");
        }

        getImpl().leaveGroup(mcastaddr, netIf);
     }


    public void setInterface(InetAddress inf) throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        checkAddress(inf, "setInterface");
        synchronized (infLock) {
            getImpl().setOption(SocketOptions.IP_MULTICAST_IF, inf);
            infAddress = inf;
            interfaceSet = true;
        }
    }


    public InetAddress getInterface() throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        synchronized (infLock) {
            InetAddress ia =
                (InetAddress)getImpl().getOption(SocketOptions.IP_MULTICAST_IF);


            if (infAddress == null) {
                return ia;
            }


            if (ia.equals(infAddress)) {
                return ia;
            }


            try {
                NetworkInterface ni = NetworkInterface.getByInetAddress(ia);
                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr.equals(infAddress)) {
                        return infAddress;
                    }
                }


                infAddress = null;
                return ia;
            } catch (Exception e) {
                return ia;
            }
        }
    }


    public void setNetworkInterface(NetworkInterface netIf)
        throws SocketException {

        synchronized (infLock) {
            getImpl().setOption(SocketOptions.IP_MULTICAST_IF2, netIf);
            infAddress = null;
            interfaceSet = true;
        }
    }


    public NetworkInterface getNetworkInterface() throws SocketException {
        NetworkInterface ni
            = (NetworkInterface)getImpl().getOption(SocketOptions.IP_MULTICAST_IF2);
        if ((ni.getIndex() == 0) || (ni.getIndex() == -1)) {
            InetAddress[] addrs = new InetAddress[1];
            addrs[0] = InetAddress.anyLocalAddress();
            return new NetworkInterface(addrs[0].getHostName(), 0, addrs);
        } else {
            return ni;
        }
    }


    public void setLoopbackMode(boolean disable) throws SocketException {
        getImpl().setOption(SocketOptions.IP_MULTICAST_LOOP, Boolean.valueOf(disable));
    }


    public boolean getLoopbackMode() throws SocketException {
        return ((Boolean)getImpl().getOption(SocketOptions.IP_MULTICAST_LOOP)).booleanValue();
    }


    @Deprecated
    public void send(DatagramPacket p, byte ttl)
        throws IOException {
            if (isClosed())
                throw new SocketException("Socket is closed");
            checkAddress(p.getAddress(), "send");
            synchronized(ttlLock) {
                synchronized(p) {
                    if (connectState == ST_NOT_CONNECTED) {
                        SecurityManager security = System.getSecurityManager();
                        if (security != null) {
                            if (p.getAddress().isMulticastAddress()) {
                                security.checkMulticast(p.getAddress(), ttl);
                            } else {
                                security.checkConnect(p.getAddress().getHostAddress(),
                                                      p.getPort());
                            }
                        }
                    } else {
                        InetAddress packetAddress = null;
                        packetAddress = p.getAddress();
                        if (packetAddress == null) {
                            p.setAddress(connectedAddress);
                            p.setPort(connectedPort);
                        } else if ((!packetAddress.equals(connectedAddress)) ||
                                   p.getPort() != connectedPort) {
                            throw new SecurityException("connected address and packet address" +
                                                        " differ");
                        }
                    }
                    byte dttl = getTTL();
                    try {
                        if (ttl != dttl) {
                            getImpl().setTTL(ttl);
                        }
                        getImpl().send(p);
                    } finally {
                        if (ttl != dttl) {
                            getImpl().setTTL(dttl);
                        }
                    }
                } }  } }
