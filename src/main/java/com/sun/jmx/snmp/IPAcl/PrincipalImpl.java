


package com.sun.jmx.snmp.IPAcl;



import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.Serializable;




class PrincipalImpl implements java.security.Principal, Serializable {
    private static final long serialVersionUID = -7910027842878976761L;

    private InetAddress[] add = null;


    public PrincipalImpl () throws UnknownHostException {
        add = new InetAddress[1];
        add[0] = java.net.InetAddress.getLocalHost();
    }


    public PrincipalImpl(String hostName) throws UnknownHostException {
        if ((hostName.equals("localhost")) || (hostName.equals("127.0.0.1"))) {
            add = new InetAddress[1];
            add[0] = java.net.InetAddress.getByName(hostName);
        }
        else
            add = java.net.InetAddress.getAllByName( hostName );
    }


    public PrincipalImpl(InetAddress address) {
        add = new InetAddress[1];
        add[0] = address;
    }


    public String getName() {
        return add[0].toString();
    }


    public boolean equals(Object a) {
        if (a instanceof PrincipalImpl){
            for(int i = 0; i < add.length; i++) {
                if(add[i].equals (((PrincipalImpl) a).getAddress()))
                    return true;
            }
            return false;
        } else {
            return false;
        }
    }


    public int hashCode(){
        return add[0].hashCode();
    }


    public String toString() {
        return ("PrincipalImpl :"+add[0].toString());
    }


    public InetAddress getAddress(){
        return add[0];
    }


    public InetAddress[] getAddresses(){
        return add;
    }
}
