package com.sun.corba.se.spi.activation.RepositoryPackage;




public final class ServerDef implements org.omg.CORBA.portable.IDLEntity
{
  public String applicationName = null;


  public String serverName = null;


  public String serverClassPath = null;


  public String serverArgs = null;
  public String serverVmArgs = null;

  public ServerDef ()
  {
  }

  public ServerDef (String _applicationName, String _serverName, String _serverClassPath, String _serverArgs, String _serverVmArgs)
  {
    applicationName = _applicationName;
    serverName = _serverName;
    serverClassPath = _serverClassPath;
    serverArgs = _serverArgs;
    serverVmArgs = _serverVmArgs;
  }

}
