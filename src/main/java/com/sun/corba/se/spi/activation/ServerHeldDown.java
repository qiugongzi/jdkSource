package com.sun.corba.se.spi.activation;




public final class ServerHeldDown extends org.omg.CORBA.UserException
{
  public int serverId = (int)0;

  public ServerHeldDown ()
  {
    super(ServerHeldDownHelper.id());
  }

  public ServerHeldDown (int _serverId)
  {
    super(ServerHeldDownHelper.id());
    serverId = _serverId;
  }


  public ServerHeldDown (String $reason, int _serverId)
  {
    super(ServerHeldDownHelper.id() + "  " + $reason);
    serverId = _serverId;
  }

}
