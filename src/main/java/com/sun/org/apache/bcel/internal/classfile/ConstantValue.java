
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public final class ConstantValue extends Attribute {
  private int constantvalue_index;


  public ConstantValue(ConstantValue c) {
    this(c.getNameIndex(), c.getLength(), c.getConstantValueIndex(),
         c.getConstantPool());
  }


  ConstantValue(int name_index, int length, DataInputStream file,
                ConstantPool constant_pool) throws IOException
  {
    this(name_index, length, (int)file.readUnsignedShort(), constant_pool);
  }


  public ConstantValue(int name_index, int length,
                       int constantvalue_index,
                       ConstantPool constant_pool)
  {
    super(Constants.ATTR_CONSTANT_VALUE, name_index, length, constant_pool);
    this.constantvalue_index = constantvalue_index;
  }


  public void accept(Visitor v) {
    v.visitConstantValue(this);
  }

  public final void dump(DataOutputStream file) throws IOException
  {
    super.dump(file);
    file.writeShort(constantvalue_index);
  }

  public final int getConstantValueIndex() { return constantvalue_index; }


  public final void setConstantValueIndex(int constantvalue_index) {
    this.constantvalue_index = constantvalue_index;
  }


  public final String toString() {
    Constant c = constant_pool.getConstant(constantvalue_index);

    String   buf;
    int    i;

    switch(c.getTag()) {
    case Constants.CONSTANT_Long:    buf = "" + ((ConstantLong)c).getBytes();    break;
    case Constants.CONSTANT_Float:   buf = "" + ((ConstantFloat)c).getBytes();   break;
    case Constants.CONSTANT_Double:  buf = "" + ((ConstantDouble)c).getBytes();  break;
    case Constants.CONSTANT_Integer: buf = "" + ((ConstantInteger)c).getBytes(); break;
    case Constants.CONSTANT_String:
      i   = ((ConstantString)c).getStringIndex();
      c   = constant_pool.getConstant(i, Constants.CONSTANT_Utf8);
      buf = "\"" + Utility.convertString(((ConstantUtf8)c).getBytes()) + "\"";
      break;

    default:
      throw new IllegalStateException("Type of ConstValue invalid: " + c);
    }

    return buf;
  }


  public Attribute copy(ConstantPool constant_pool) {
    ConstantValue c = (ConstantValue)clone();
    c.constant_pool = constant_pool;
    return c;
  }
}
