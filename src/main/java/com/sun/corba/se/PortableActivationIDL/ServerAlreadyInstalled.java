package com.sun.corba.se.PortableActivationIDL;




public final class ServerAlreadyInstalled extends org.omg.CORBA.UserException
{
  public String serverId = null;

  public ServerAlreadyInstalled ()
  {
    super(ServerAlreadyInstalledHelper.id());
  }

  public ServerAlreadyInstalled (String _serverId)
  {
    super(ServerAlreadyInstalledHelper.id());
    serverId = _serverId;
  }


  public ServerAlreadyInstalled (String $reason, String _serverId)
  {
    super(ServerAlreadyInstalledHelper.id() + "  " + $reason);
    serverId = _serverId;
  }

}
