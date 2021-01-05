
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public final class StackMapType implements Cloneable {
  private byte         type;
  private int          index = -1; private ConstantPool constant_pool;


  StackMapType(DataInputStream file, ConstantPool constant_pool) throws IOException
  {
    this(file.readByte(), -1, constant_pool);

    if(hasIndex())
      setIndex(file.readShort());

    setConstantPool(constant_pool);
  }


  public StackMapType(byte type, int index, ConstantPool constant_pool) {
    setType(type);
    setIndex(index);
    setConstantPool(constant_pool);
  }

  public void setType(byte t) {
    if((t < Constants.ITEM_Bogus) || (t > Constants.ITEM_NewObject))
      throw new RuntimeException("Illegal type for StackMapType: " + t);
    type = t;
  }

  public byte getType()       { return type; }
  public void setIndex(int t) { index = t; }


  public int  getIndex()      { return index; }


  public final void dump(DataOutputStream file) throws IOException
  {
    file.writeByte(type);
    if(hasIndex())
      file.writeShort(getIndex());
  }


  public final boolean hasIndex() {
    return ((type == Constants.ITEM_Object) ||
            (type == Constants.ITEM_NewObject));
  }

  private String printIndex() {
    if(type == Constants.ITEM_Object)
      return ", class=" + constant_pool.constantToString(index, Constants.CONSTANT_Class);
    else if(type == Constants.ITEM_NewObject)
      return ", offset=" + index;
    else
      return "";
  }


  public final String toString() {
    return "(type=" + Constants.ITEM_NAMES[type] + printIndex() + ")";
  }


  public StackMapType copy() {
    try {
      return (StackMapType)clone();
    } catch(CloneNotSupportedException e) {}

    return null;
  }


  public final ConstantPool getConstantPool() { return constant_pool; }


  public final void setConstantPool(ConstantPool constant_pool) {
    this.constant_pool = constant_pool;
  }
}
