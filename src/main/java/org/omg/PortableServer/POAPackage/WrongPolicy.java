package org.omg.PortableServer.POAPackage;




public final class WrongPolicy extends org.omg.CORBA.UserException
{

  public WrongPolicy ()
  {
    super(WrongPolicyHelper.id());
  }


  public WrongPolicy (String $reason)
  {
    super(WrongPolicyHelper.id() + "  " + $reason);
  }

}
