package com.sun.corba.se.PortableActivationIDL;



public final class InvalidORBidHolder implements org.omg.CORBA.portable.Streamable
{
  public com.sun.corba.se.PortableActivationIDL.InvalidORBid value = null;

  public InvalidORBidHolder ()
  {
  }

  public InvalidORBidHolder (com.sun.corba.se.PortableActivationIDL.InvalidORBid initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = com.sun.corba.se.PortableActivationIDL.InvalidORBidHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    com.sun.corba.se.PortableActivationIDL.InvalidORBidHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return com.sun.corba.se.PortableActivationIDL.InvalidORBidHelper.type ();
  }

}
