package com.sun.corba.se.PortableActivationIDL;



public final class ORBPortInfoListHolder implements org.omg.CORBA.portable.Streamable
{
  public com.sun.corba.se.PortableActivationIDL.ORBPortInfo value[] = null;

  public ORBPortInfoListHolder ()
  {
  }

  public ORBPortInfoListHolder (com.sun.corba.se.PortableActivationIDL.ORBPortInfo[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = com.sun.corba.se.PortableActivationIDL.ORBPortInfoListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    com.sun.corba.se.PortableActivationIDL.ORBPortInfoListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return com.sun.corba.se.PortableActivationIDL.ORBPortInfoListHelper.type ();
  }

}
