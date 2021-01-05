package com.sun.corba.se.PortableActivationIDL;




public final class ServerNotActive extends org.omg.CORBA.UserException
{
  public String serverId = null;

  public ServerNotActive ()
  {
    super(ServerNotActiveHelper.id());
  }

  public ServerNotActive (String _serverId)
  {
    super(ServerNotActiveHelper.id());
    serverId = _serverId;
  }


  public ServerNotActive (String $reason, String _serverId)
  {
    super(ServerNotActiveHelper.id() + "  " + $reason);
    serverId = _serverId;
  }

}
