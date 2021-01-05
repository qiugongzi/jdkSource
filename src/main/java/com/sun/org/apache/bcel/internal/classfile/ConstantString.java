
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public final class ConstantString extends Constant implements ConstantObject {
  private int string_index; public ConstantString(ConstantString c) {
    this(c.getStringIndex());
  }

  ConstantString(DataInputStream file) throws IOException
  {
    this((int)file.readUnsignedShort());
  }

  public ConstantString(int string_index)
  {
    super(Constants.CONSTANT_String);
    this.string_index = string_index;
  }

  public void accept(Visitor v) {
    v.visitConstantString(this);
  }

  public final void dump(DataOutputStream file) throws IOException
  {
    file.writeByte(tag);
    file.writeShort(string_index);
  }

  public final int getStringIndex() { return string_index; }

  public final void setStringIndex(int string_index) {
    this.string_index = string_index;
  }

  public final String toString()
  {
    return super.toString() + "(string_index = " + string_index + ")";
  }


  public Object getConstantValue(ConstantPool cp) {
    Constant c = cp.getConstant(string_index, Constants.CONSTANT_Utf8);
    return ((ConstantUtf8)c).getBytes();
  }


  public String getBytes(ConstantPool cp) {
    return (String)getConstantValue(cp);
  }
}
