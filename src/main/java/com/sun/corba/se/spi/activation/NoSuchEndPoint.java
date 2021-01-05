package com.sun.corba.se.spi.activation;




public final class NoSuchEndPoint extends org.omg.CORBA.UserException
{

  public NoSuchEndPoint ()
  {
    super(NoSuchEndPointHelper.id());
  }


  public NoSuchEndPoint (String $reason)
  {
    super(NoSuchEndPointHelper.id() + "  " + $reason);
  }

}
