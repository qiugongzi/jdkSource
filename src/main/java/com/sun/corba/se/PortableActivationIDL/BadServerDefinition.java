package com.sun.corba.se.PortableActivationIDL;




public final class BadServerDefinition extends org.omg.CORBA.UserException
{
  public String reason = null;

  public BadServerDefinition ()
  {
    super(BadServerDefinitionHelper.id());
  }

  public BadServerDefinition (String _reason)
  {
    super(BadServerDefinitionHelper.id());
    reason = _reason;
  }


  public BadServerDefinition (String $reason, String _reason)
  {
    super(BadServerDefinitionHelper.id() + "  " + $reason);
    reason = _reason;
  }

}
