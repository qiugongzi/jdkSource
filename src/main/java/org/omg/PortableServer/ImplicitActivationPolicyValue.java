package org.omg.PortableServer;



public class ImplicitActivationPolicyValue implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 2;
  private static org.omg.PortableServer.ImplicitActivationPolicyValue[] __array = new org.omg.PortableServer.ImplicitActivationPolicyValue [__size];

  public static final int _IMPLICIT_ACTIVATION = 0;
  public static final org.omg.PortableServer.ImplicitActivationPolicyValue IMPLICIT_ACTIVATION = new org.omg.PortableServer.ImplicitActivationPolicyValue(_IMPLICIT_ACTIVATION);
  public static final int _NO_IMPLICIT_ACTIVATION = 1;
  public static final org.omg.PortableServer.ImplicitActivationPolicyValue NO_IMPLICIT_ACTIVATION = new org.omg.PortableServer.ImplicitActivationPolicyValue(_NO_IMPLICIT_ACTIVATION);

  public int value ()
  {
    return __value;
  }

  public static org.omg.PortableServer.ImplicitActivationPolicyValue from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected ImplicitActivationPolicyValue (int value)
  {
    __value = value;
    __array[__value] = this;
  }
}
