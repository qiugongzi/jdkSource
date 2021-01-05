package org.omg.IOP.CodecPackage;




public final class FormatMismatch extends org.omg.CORBA.UserException
{

  public FormatMismatch ()
  {
    super(FormatMismatchHelper.id());
  }


  public FormatMismatch (String $reason)
  {
    super(FormatMismatchHelper.id() + "  " + $reason);
  }

}
