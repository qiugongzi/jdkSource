package com.sun.corba.se.spi.activation;



public final class ServerNotRegisteredHolder implements org.omg.CORBA.portable.Streamable
{
  public com.sun.corba.se.spi.activation.ServerNotRegistered value = null;

  public ServerNotRegisteredHolder ()
  {
  }

  public ServerNotRegisteredHolder (com.sun.corba.se.spi.activation.ServerNotRegistered initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = com.sun.corba.se.spi.activation.ServerNotRegisteredHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    com.sun.corba.se.spi.activation.ServerNotRegisteredHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return com.sun.corba.se.spi.activation.ServerNotRegisteredHelper.type ();
  }

}
