package com.sun.corba.se.PortableActivationIDL;


public final class ServerManagerHolder implements org.omg.CORBA.portable.Streamable
{
  public com.sun.corba.se.PortableActivationIDL.ServerManager value = null;

  public ServerManagerHolder ()
  {
  }

  public ServerManagerHolder (com.sun.corba.se.PortableActivationIDL.ServerManager initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = com.sun.corba.se.PortableActivationIDL.ServerManagerHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    com.sun.corba.se.PortableActivationIDL.ServerManagerHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return com.sun.corba.se.PortableActivationIDL.ServerManagerHelper.type ();
  }

}
