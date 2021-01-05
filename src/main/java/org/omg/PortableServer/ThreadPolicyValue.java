package org.omg.PortableServer;



public class ThreadPolicyValue implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 2;
  private static org.omg.PortableServer.ThreadPolicyValue[] __array = new org.omg.PortableServer.ThreadPolicyValue [__size];

  public static final int _ORB_CTRL_MODEL = 0;
  public static final org.omg.PortableServer.ThreadPolicyValue ORB_CTRL_MODEL = new org.omg.PortableServer.ThreadPolicyValue(_ORB_CTRL_MODEL);
  public static final int _SINGLE_THREAD_MODEL = 1;
  public static final org.omg.PortableServer.ThreadPolicyValue SINGLE_THREAD_MODEL = new org.omg.PortableServer.ThreadPolicyValue(_SINGLE_THREAD_MODEL);

  public int value ()
  {
    return __value;
  }

  public static org.omg.PortableServer.ThreadPolicyValue from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected ThreadPolicyValue (int value)
  {
    __value = value;
    __array[__value] = this;
  }
}
