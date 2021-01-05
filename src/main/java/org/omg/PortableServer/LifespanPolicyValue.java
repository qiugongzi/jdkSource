package org.omg.PortableServer;



public class LifespanPolicyValue implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 2;
  private static org.omg.PortableServer.LifespanPolicyValue[] __array = new org.omg.PortableServer.LifespanPolicyValue [__size];

  public static final int _TRANSIENT = 0;
  public static final org.omg.PortableServer.LifespanPolicyValue TRANSIENT = new org.omg.PortableServer.LifespanPolicyValue(_TRANSIENT);
  public static final int _PERSISTENT = 1;
  public static final org.omg.PortableServer.LifespanPolicyValue PERSISTENT = new org.omg.PortableServer.LifespanPolicyValue(_PERSISTENT);

  public int value ()
  {
    return __value;
  }

  public static org.omg.PortableServer.LifespanPolicyValue from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected LifespanPolicyValue (int value)
  {
    __value = value;
    __array[__value] = this;
  }
}
