package org.omg.DynamicAny.DynAnyPackage;




public final class InvalidValue extends org.omg.CORBA.UserException
{

  public InvalidValue ()
  {
    super(InvalidValueHelper.id());
  }


  public InvalidValue (String $reason)
  {
    super(InvalidValueHelper.id() + "  " + $reason);
  }

}
