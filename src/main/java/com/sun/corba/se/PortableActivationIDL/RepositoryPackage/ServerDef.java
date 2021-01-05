package com.sun.corba.se.PortableActivationIDL.RepositoryPackage;




public final class ServerDef implements org.omg.CORBA.portable.IDLEntity
{
  public String applicationName = null;


  public String serverName = null;


  public String serverClassPath = null;


  public String serverArgs = null;


  public String serverVmArgs = null;


  public boolean isInstalled = false;

  public ServerDef ()
  {
  }

  public ServerDef (String _applicationName, String _serverName, String _serverClassPath, String _serverArgs, String _serverVmArgs, boolean _isInstalled)
  {
    applicationName = _applicationName;
    serverName = _serverName;
    serverClassPath = _serverClassPath;
    serverArgs = _serverArgs;
    serverVmArgs = _serverVmArgs;
    isInstalled = _isInstalled;
  }

}
