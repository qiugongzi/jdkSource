package com.sun.corba.se.PortableActivationIDL;



public final class ServerAlreadyRegisteredHolder implements org.omg.CORBA.portable.Streamable
{
  public com.sun.corba.se.PortableActivationIDL.ServerAlreadyRegistered value = null;

  public ServerAlreadyRegisteredHolder ()
  {
  }

  public ServerAlreadyRegisteredHolder (com.sun.corba.se.PortableActivationIDL.ServerAlreadyRegistered initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = com.sun.corba.se.PortableActivationIDL.ServerAlreadyRegisteredHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    com.sun.corba.se.PortableActivationIDL.ServerAlreadyRegisteredHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return com.sun.corba.se.PortableActivationIDL.ServerAlreadyRegisteredHelper.type ();
  }

}
