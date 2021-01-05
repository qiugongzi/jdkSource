package com.sun.corba.se.spi.activation;



public final class BadServerDefinitionHolder implements org.omg.CORBA.portable.Streamable
{
  public com.sun.corba.se.spi.activation.BadServerDefinition value = null;

  public BadServerDefinitionHolder ()
  {
  }

  public BadServerDefinitionHolder (com.sun.corba.se.spi.activation.BadServerDefinition initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = com.sun.corba.se.spi.activation.BadServerDefinitionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    com.sun.corba.se.spi.activation.BadServerDefinitionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return com.sun.corba.se.spi.activation.BadServerDefinitionHelper.type ();
  }

}
