package com.sun.corba.se.PortableActivationIDL;




public final class ORBAlreadyRegistered extends org.omg.CORBA.UserException
{
  public String orbId = null;

  public ORBAlreadyRegistered ()
  {
    super(ORBAlreadyRegisteredHelper.id());
  }

  public ORBAlreadyRegistered (String _orbId)
  {
    super(ORBAlreadyRegisteredHelper.id());
    orbId = _orbId;
  }


  public ORBAlreadyRegistered (String $reason, String _orbId)
  {
    super(ORBAlreadyRegisteredHelper.id() + "  " + $reason);
    orbId = _orbId;
  }

}
