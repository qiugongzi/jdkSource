package org.omg.CosNaming.NamingContextExtPackage;




public final class InvalidAddress extends org.omg.CORBA.UserException
{

  public InvalidAddress ()
  {
    super(InvalidAddressHelper.id());
  }


  public InvalidAddress (String $reason)
  {
    super(InvalidAddressHelper.id() + "  " + $reason);
  }

}
