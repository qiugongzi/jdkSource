package com.sun.corba.se.spi.activation;



public final class InitialNameServiceHolder implements org.omg.CORBA.portable.Streamable
{
  public com.sun.corba.se.spi.activation.InitialNameService value = null;

  public InitialNameServiceHolder ()
  {
  }

  public InitialNameServiceHolder (com.sun.corba.se.spi.activation.InitialNameService initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = com.sun.corba.se.spi.activation.InitialNameServiceHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    com.sun.corba.se.spi.activation.InitialNameServiceHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return com.sun.corba.se.spi.activation.InitialNameServiceHelper.type ();
  }

}
