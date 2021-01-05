package org.omg.PortableServer.CurrentPackage;




public final class NoContext extends org.omg.CORBA.UserException
{

  public NoContext ()
  {
    super(NoContextHelper.id());
  }


  public NoContext (String $reason)
  {
    super(NoContextHelper.id() + "  " + $reason);
  }

}
