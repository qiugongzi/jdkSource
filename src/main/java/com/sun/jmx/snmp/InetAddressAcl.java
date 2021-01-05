

package com.sun.jmx.snmp;

import java.net.InetAddress;
import java.util.Enumeration;



public interface InetAddressAcl {


    public String getName();


    public boolean checkReadPermission(InetAddress address);


    public boolean checkReadPermission(InetAddress address, String community);


    public boolean checkCommunity(String community);


    public boolean checkWritePermission(InetAddress address);


    public boolean checkWritePermission(InetAddress address, String community);


    public Enumeration<InetAddress> getTrapDestinations();


    public Enumeration<String> getTrapCommunities(InetAddress address);


    public Enumeration<InetAddress> getInformDestinations();


    public Enumeration<String> getInformCommunities(InetAddress address);
}
