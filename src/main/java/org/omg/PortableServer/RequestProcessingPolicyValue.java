package org.omg.PortableServer;



public class RequestProcessingPolicyValue implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 3;
  private static org.omg.PortableServer.RequestProcessingPolicyValue[] __array = new org.omg.PortableServer.RequestProcessingPolicyValue [__size];

  public static final int _USE_ACTIVE_OBJECT_MAP_ONLY = 0;
  public static final org.omg.PortableServer.RequestProcessingPolicyValue USE_ACTIVE_OBJECT_MAP_ONLY = new org.omg.PortableServer.RequestProcessingPolicyValue(_USE_ACTIVE_OBJECT_MAP_ONLY);
  public static final int _USE_DEFAULT_SERVANT = 1;
  public static final org.omg.PortableServer.RequestProcessingPolicyValue USE_DEFAULT_SERVANT = new org.omg.PortableServer.RequestProcessingPolicyValue(_USE_DEFAULT_SERVANT);
  public static final int _USE_SERVANT_MANAGER = 2;
  public static final org.omg.PortableServer.RequestProcessingPolicyValue USE_SERVANT_MANAGER = new org.omg.PortableServer.RequestProcessingPolicyValue(_USE_SERVANT_MANAGER);

  public int value ()
  {
    return __value;
  }

  public static org.omg.PortableServer.RequestProcessingPolicyValue from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected RequestProcessingPolicyValue (int value)
  {
    __value = value;
    __array[__value] = this;
  }
}
