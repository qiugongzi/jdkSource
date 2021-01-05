package org.omg.CosNaming;


public final class BindingIteratorHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosNaming.BindingIterator value = null;

  public BindingIteratorHolder ()
  {
  }

  public BindingIteratorHolder (org.omg.CosNaming.BindingIterator initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosNaming.BindingIteratorHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosNaming.BindingIteratorHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosNaming.BindingIteratorHelper.type ();
  }

}
