package com.sun.corba.se.PortableActivationIDL;




public final class ServerAlreadyActive extends org.omg.CORBA.UserException
{
  public String serverId = null;

  public ServerAlreadyActive ()
  {
    super(ServerAlreadyActiveHelper.id());
  }

  public ServerAlreadyActive (String _serverId)
  {
    super(ServerAlreadyActiveHelper.id());
    serverId = _serverId;
  }


  public ServerAlreadyActive (String $reason, String _serverId)
  {
    super(ServerAlreadyActiveHelper.id() + "  " + $reason);
    serverId = _serverId;
  }

}
