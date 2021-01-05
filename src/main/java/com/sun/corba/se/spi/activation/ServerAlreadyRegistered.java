package com.sun.corba.se.spi.activation;




public final class ServerAlreadyRegistered extends org.omg.CORBA.UserException
{
  public int serverId = (int)0;

  public ServerAlreadyRegistered ()
  {
    super(ServerAlreadyRegisteredHelper.id());
  }

  public ServerAlreadyRegistered (int _serverId)
  {
    super(ServerAlreadyRegisteredHelper.id());
    serverId = _serverId;
  }


  public ServerAlreadyRegistered (String $reason, int _serverId)
  {
    super(ServerAlreadyRegisteredHelper.id() + "  " + $reason);
    serverId = _serverId;
  }

}
