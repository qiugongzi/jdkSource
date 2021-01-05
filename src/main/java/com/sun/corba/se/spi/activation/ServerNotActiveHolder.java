package com.sun.corba.se.spi.activation;



public final class ServerNotActiveHolder implements org.omg.CORBA.portable.Streamable
{
  public com.sun.corba.se.spi.activation.ServerNotActive value = null;

  public ServerNotActiveHolder ()
  {
  }

  public ServerNotActiveHolder (com.sun.corba.se.spi.activation.ServerNotActive initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = com.sun.corba.se.spi.activation.ServerNotActiveHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    com.sun.corba.se.spi.activation.ServerNotActiveHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return com.sun.corba.se.spi.activation.ServerNotActiveHelper.type ();
  }

}
