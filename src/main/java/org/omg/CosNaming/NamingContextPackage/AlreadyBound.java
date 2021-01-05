package org.omg.CosNaming.NamingContextPackage;




public final class AlreadyBound extends org.omg.CORBA.UserException
{

  public AlreadyBound ()
  {
    super(AlreadyBoundHelper.id());
  }


  public AlreadyBound (String $reason)
  {
    super(AlreadyBoundHelper.id() + "  " + $reason);
  }

}
