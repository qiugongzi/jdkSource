package com.sun.corba.se.spi.activation;




public interface RepositoryOperations 
{


  int registerServer (com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef serverDef) throws com.sun.corba.se.spi.activation.ServerAlreadyRegistered, com.sun.corba.se.spi.activation.BadServerDefinition;


  void unregisterServer (int serverId) throws com.sun.corba.se.spi.activation.ServerNotRegistered;


  com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef getServer (int serverId) throws com.sun.corba.se.spi.activation.ServerNotRegistered;


  boolean isInstalled (int serverId) throws com.sun.corba.se.spi.activation.ServerNotRegistered;


  void install (int serverId) throws com.sun.corba.se.spi.activation.ServerNotRegistered, com.sun.corba.se.spi.activation.ServerAlreadyInstalled;


  void uninstall (int serverId) throws com.sun.corba.se.spi.activation.ServerNotRegistered, com.sun.corba.se.spi.activation.ServerAlreadyUninstalled;


  int[] listRegisteredServers ();


  String[] getApplicationNames ();


  int getServerID (String applicationName) throws com.sun.corba.se.spi.activation.ServerNotRegistered;
}
