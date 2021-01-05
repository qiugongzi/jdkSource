package org.omg.PortableInterceptor;


public final class ObjectReferenceTemplateHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.PortableInterceptor.ObjectReferenceTemplate value = null;

  public ObjectReferenceTemplateHolder ()
  {
  }

  public ObjectReferenceTemplateHolder (org.omg.PortableInterceptor.ObjectReferenceTemplate initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.type ();
  }

}
