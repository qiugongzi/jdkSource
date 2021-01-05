package org.omg.CosNaming;



public final class NameHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosNaming.NameComponent value[] = null;

  public NameHolder ()
  {
  }

  public NameHolder (org.omg.CosNaming.NameComponent[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosNaming.NameHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosNaming.NameHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosNaming.NameHelper.type ();
  }

}
