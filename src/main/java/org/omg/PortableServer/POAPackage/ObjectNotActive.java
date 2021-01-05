package org.omg.PortableServer.POAPackage;




public final class ObjectNotActive extends org.omg.CORBA.UserException
{

  public ObjectNotActive ()
  {
    super(ObjectNotActiveHelper.id());
  }


  public ObjectNotActive (String $reason)
  {
    super(ObjectNotActiveHelper.id() + "  " + $reason);
  }

}
