package org.omg.PortableServer;




public final class ForwardRequest extends org.omg.CORBA.UserException
{
  public org.omg.CORBA.Object forward_reference = null;

  public ForwardRequest ()
  {
    super(ForwardRequestHelper.id());
  }

  public ForwardRequest (org.omg.CORBA.Object _forward_reference)
  {
    super(ForwardRequestHelper.id());
    forward_reference = _forward_reference;
  }


  public ForwardRequest (String $reason, org.omg.CORBA.Object _forward_reference)
  {
    super(ForwardRequestHelper.id() + "  " + $reason);
    forward_reference = _forward_reference;
  }

}
