package com.sun.corba.se.spi.activation;




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
