package org.omg.CosNaming.NamingContextPackage;




public final class NotFound extends org.omg.CORBA.UserException
{
  public org.omg.CosNaming.NamingContextPackage.NotFoundReason why = null;
  public org.omg.CosNaming.NameComponent rest_of_name[] = null;

  public NotFound ()
  {
    super(NotFoundHelper.id());
  }

  public NotFound (org.omg.CosNaming.NamingContextPackage.NotFoundReason _why, org.omg.CosNaming.NameComponent[] _rest_of_name)
  {
    super(NotFoundHelper.id());
    why = _why;
    rest_of_name = _rest_of_name;
  }


  public NotFound (String $reason, org.omg.CosNaming.NamingContextPackage.NotFoundReason _why, org.omg.CosNaming.NameComponent[] _rest_of_name)
  {
    super(NotFoundHelper.id() + "  " + $reason);
    why = _why;
    rest_of_name = _rest_of_name;
  }

}
