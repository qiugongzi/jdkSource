package org.omg.CosNaming;



public final class BindingListHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CosNaming.Binding value[] = null;

  public BindingListHolder ()
  {
  }

  public BindingListHolder (org.omg.CosNaming.Binding[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CosNaming.BindingListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CosNaming.BindingListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CosNaming.BindingListHelper.type ();
  }

}
