package com.sun.corba.se.PortableActivationIDL;


public final class ServerProxyHolder implements org.omg.CORBA.portable.Streamable
{
  public com.sun.corba.se.PortableActivationIDL.ServerProxy value = null;

  public ServerProxyHolder ()
  {
  }

  public ServerProxyHolder (com.sun.corba.se.PortableActivationIDL.ServerProxy initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = com.sun.corba.se.PortableActivationIDL.ServerProxyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    com.sun.corba.se.PortableActivationIDL.ServerProxyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return com.sun.corba.se.PortableActivationIDL.ServerProxyHelper.type ();
  }

}
