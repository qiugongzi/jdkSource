package com.sun.corba.se.PortableActivationIDL;




public final class InvalidORBid extends org.omg.CORBA.UserException
{

  public InvalidORBid ()
  {
    super(InvalidORBidHelper.id());
  }


  public InvalidORBid (String $reason)
  {
    super(InvalidORBidHelper.id() + "  " + $reason);
  }

}
