package org.omg.CosNaming;


public final class BindingTypeHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosNaming.BindingType value = null;

  public BindingTypeHolder ()
  {
  }

  public BindingTypeHolder (org.omg.CosNaming.BindingType initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosNaming.BindingTypeHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosNaming.BindingTypeHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosNaming.BindingTypeHelper.type ();
  }

}
