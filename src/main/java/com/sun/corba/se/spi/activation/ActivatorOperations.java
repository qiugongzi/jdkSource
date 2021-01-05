package com.sun.corba.se.spi.activation;




public interface ActivatorOperations 
{


  void active (int serverId, com.sun.corba.se.spi.activation.Server serverObj) throws com.sun.corba.se.spi.activation.ServerNotRegistered;


  void registerEndpoints (int serverId, String orbId, com.sun.corba.se.spi.activation.EndPointInfo[] endPointInfo) throws com.sun.corba.se.spi.activation.ServerNotRegistered, com.sun.corba.se.spi.activation.NoSuchEndPoint, com.sun.corba.se.spi.activation.ORBAlreadyRegistered;


  int[] getActiveServers ();


  void activate (int serverId) throws com.sun.corba.se.spi.activation.ServerAlreadyActive, com.sun.corba.se.spi.activation.ServerNotRegistered, com.sun.corba.se.spi.activation.ServerHeldDown;


  void shutdown (int serverId) throws com.sun.corba.se.spi.activation.ServerNotActive, com.sun.corba.se.spi.activation.ServerNotRegistered;


  void install (int serverId) throws com.sun.corba.se.spi.activation.ServerNotRegistered, com.sun.corba.se.spi.activation.ServerHeldDown, com.sun.corba.se.spi.activation.ServerAlreadyInstalled;


  String[] getORBNames (int serverId) throws com.sun.corba.se.spi.activation.ServerNotRegistered;


  void uninstall (int serverId) throws com.sun.corba.se.spi.activation.ServerNotRegistered, com.sun.corba.se.spi.activation.ServerHeldDown, com.sun.corba.se.spi.activation.ServerAlreadyUninstalled;
}
