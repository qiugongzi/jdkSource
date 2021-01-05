package org.omg.CORBA;


public final class ParameterModeHolder implements org.omg.CORBA.portable.Streamable
{
  public org.omg.CORBA.ParameterMode value = null;

  public ParameterModeHolder ()
  {
  }

  public ParameterModeHolder (org.omg.CORBA.ParameterMode initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.omg.CORBA.ParameterModeHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.omg.CORBA.ParameterModeHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.omg.CORBA.ParameterModeHelper.type ();
  }

}
