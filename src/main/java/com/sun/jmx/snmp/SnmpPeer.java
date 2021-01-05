
package com.sun.jmx.snmp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.Serializable;



public class SnmpPeer implements Serializable {
    private static final long serialVersionUID = -5554565062847175999L;

    public static final int defaultSnmpRequestPktSize = 2 * 1024 ;


    public static final int defaultSnmpResponsePktSize = 8 * 1024 ;


    private int maxVarBindLimit = 25 ;


    private int portNum = 161 ;


    private int maxTries = 3 ;


    private int timeout = 3000;


    private SnmpPduFactory pduFactory = new SnmpPduFactoryBER() ;


    private long _maxrtt ;

    private long _minrtt ;

    private long _avgrtt ;


    private SnmpParams _snmpParameter = new SnmpParameters() ;


    private InetAddress _devAddr = null ;


    private int maxSnmpPacketSize = defaultSnmpRequestPktSize ;


    InetAddress _devAddrList[] = null ;


    int _addrIndex = 0 ;


    private boolean customPduFactory = false;

    public SnmpPeer(String host) throws UnknownHostException {
        this(host, 161) ;
    }


    public SnmpPeer(InetAddress netaddr, int port) {
        _devAddr = netaddr ;
        portNum = port;
   }


    public SnmpPeer(InetAddress netaddr) {
        _devAddr = netaddr ;
     }


    public SnmpPeer(String host, int port) throws UnknownHostException {
        useIPAddress(host) ;
        portNum = port;
    }


    final public synchronized void useIPAddress(String ipaddr) throws UnknownHostException {
        _devAddr = InetAddress.getByName(ipaddr) ;
    }


    final public synchronized String ipAddressInUse() {
        byte [] adr = _devAddr.getAddress() ;
        return
            (adr[0]&0xFF) + "." + (adr[1]&0xFF) + "." +
            (adr[2]&0xFF) + "." + (adr[3]&0xFF);
    }


    final public synchronized void useAddressList(InetAddress adrList[]) {
        _devAddrList = (adrList != null) ? adrList.clone() : null;
        _addrIndex = 0 ;
        useNextAddress() ;
    }


    final public synchronized void useNextAddress() {
        if (_devAddrList == null)
            return ;


        if (_addrIndex > _devAddrList.length-1)

            _addrIndex = 0 ;
        _devAddr = _devAddrList[_addrIndex++] ;
    }


    public boolean allowSnmpSets() {
        return _snmpParameter.allowSnmpSets() ;
    }


    final public InetAddress[] getDestAddrList() {
        return _devAddrList == null ? null : _devAddrList.clone();
    }


    final public InetAddress getDestAddr() {
        return _devAddr ;
    }


    final public int getDestPort() {
        return portNum ;
    }


    final public synchronized void setDestPort(int newPort) {
        portNum = newPort ;
    }


    final public int getTimeout() {
        return timeout;
    }


    final public synchronized void setTimeout(int newTimeout) {
        if (newTimeout < 0)
            throw new IllegalArgumentException();
        timeout= newTimeout;
    }


    final public int getMaxTries() {
        return maxTries;
    }


    final public synchronized void setMaxTries(int newMaxTries) {
        if (newMaxTries < 0)
            throw new IllegalArgumentException();
        maxTries= newMaxTries;
    }


    final public String getDevName() {
        return getDestAddr().getHostName() ;
    }


    @Override
    public String toString() {
        return "Peer/Port : " + getDestAddr().getHostAddress() + "/" + getDestPort() ;
    }


    final public synchronized int getVarBindLimit() {
        return maxVarBindLimit ;
    }


    final public synchronized void setVarBindLimit(int limit) {
        maxVarBindLimit = limit ;
    }


    public void setParams(SnmpParams params) {
        _snmpParameter = params;
    }

    public SnmpParams getParams() {
        return _snmpParameter;
    }


    final public int getMaxSnmpPktSize() {
        return maxSnmpPacketSize ;
    }


    final public synchronized void setMaxSnmpPktSize(int newsize) {
        maxSnmpPacketSize = newsize ;
    }

    boolean isCustomPduFactory() {
        return customPduFactory;
    }


    @Override
    protected void finalize() {
        _devAddr = null ;
        _devAddrList = null ;
        _snmpParameter = null ;
    }


    public long getMinRtt() {
        return _minrtt ;
    }


    public long getMaxRtt() {
        return _maxrtt ;
    }


    public long getAvgRtt() {
        return _avgrtt ;
    }


    private void updateRttStats(long tm) {
        if (_minrtt > tm)
            _minrtt = tm ;
        else if (_maxrtt < tm)
            _maxrtt = tm ;
        else
            _avgrtt = tm ;  }
}
