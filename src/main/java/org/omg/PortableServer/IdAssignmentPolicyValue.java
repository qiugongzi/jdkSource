package org.omg.PortableServer;



public class IdAssignmentPolicyValue implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 2;
  private static org.omg.PortableServer.IdAssignmentPolicyValue[] __array = new org.omg.PortableServer.IdAssignmentPolicyValue [__size];

  public static final int _USER_ID = 0;
  public static final org.omg.PortableServer.IdAssignmentPolicyValue USER_ID = new org.omg.PortableServer.IdAssignmentPolicyValue(_USER_ID);
  public static final int _SYSTEM_ID = 1;
  public static final org.omg.PortableServer.IdAssignmentPolicyValue SYSTEM_ID = new org.omg.PortableServer.IdAssignmentPolicyValue(_SYSTEM_ID);

  public int value ()
  {
    return __value;
  }

  public static org.omg.PortableServer.IdAssignmentPolicyValue from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected IdAssignmentPolicyValue (int value)
  {
    __value = value;
    __array[__value] = this;
  }
}
