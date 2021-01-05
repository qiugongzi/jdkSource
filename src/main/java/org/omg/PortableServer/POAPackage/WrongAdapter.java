package org.omg.PortableServer.POAPackage;




public final class WrongAdapter extends org.omg.CORBA.UserException
{

  public WrongAdapter ()
  {
    super(WrongAdapterHelper.id());
  }


  public WrongAdapter (String $reason)
  {
    super(WrongAdapterHelper.id() + "  " + $reason);
  }

}
