


package com.sun.jmx.snmp.daemon;





public interface CommunicatorServerMBean {


    public void start() ;


    public void stop() ;


    public boolean isActive() ;


    public boolean waitState(int state , long timeOut) ;


    public int getState() ;


    public String getStateString() ;


    public String getHost() ;


    public int getPort() ;


    public void setPort(int port) throws java.lang.IllegalStateException ;


    public abstract String getProtocol() ;
}
