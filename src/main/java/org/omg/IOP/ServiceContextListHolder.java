package org.omg.IOP;



public final class ServiceContextListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.IOP.ServiceContext value[] = null;

  public ServiceContextListHolder ()
  {
  }

  public ServiceContextListHolder (org.omg.IOP.ServiceContext[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.IOP.ServiceContextListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.IOP.ServiceContextListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.IOP.ServiceContextListHelper.type ();
  }

}
