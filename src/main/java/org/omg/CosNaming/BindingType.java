package org.omg.CosNaming;



public class BindingType implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 2;
  private static org.omg.CosNaming.BindingType[] __array = new org.omg.CosNaming.BindingType [__size];

  public static final int _nobject = 0;
  public static final org.omg.CosNaming.BindingType nobject = new org.omg.CosNaming.BindingType(_nobject);
  public static final int _ncontext = 1;
  public static final org.omg.CosNaming.BindingType ncontext = new org.omg.CosNaming.BindingType(_ncontext);

  public int value ()
  {
    return __value;
  }

  public static org.omg.CosNaming.BindingType from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected BindingType (int value)
  {
    __value = value;
    __array[__value] = this;
  }
}
