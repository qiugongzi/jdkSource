
          java.util.Enumeration<InetAddress> it = netif.getInetAddresses();
          InetAddress inetaddr = null;
          while (!(inetaddr instanceof Inet4Address) &&
                 it.hasMoreElements())
              inetaddr = it.nextElement();
          if (inetaddr instanceof Inet4Address)
              ifaddr = inetaddr.getAddress();
      }
      return isReachable0(addr.getAddress(), timeout, ifaddr, ttl);
  }
    private InetAddress      anyLocalAddress;
    private InetAddress      loopbackAddress;
}
