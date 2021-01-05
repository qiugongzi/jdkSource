package com.sun.corba.se.spi.activation;




public final class ServerAlreadyInstalled extends org.omg.CORBA.UserException
{
  public int serverId = (int)0;

  public ServerAlreadyInstalled ()
  {
    super(ServerAlreadyInstalledHelper.id());
  }

  public ServerAlreadyInstalled (int _serverId)
  {
    super(ServerAlreadyInstalledHelper.id());
    serverId = _serverId;
  }


  public ServerAlreadyInstalled (String $reason, int _serverId)
  {
    super(ServerAlreadyInstalledHelper.id() + "  " + $reason);
    serverId = _serverId;
  }

}
