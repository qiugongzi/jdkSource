
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public final class ConstantClass extends Constant implements ConstantObject {
  private int name_index; public ConstantClass(ConstantClass c) {
    this(c.getNameIndex());
  }


  ConstantClass(DataInputStream file) throws IOException
  {
    this(file.readUnsignedShort());
  }


  public ConstantClass(int name_index) {
    super(Constants.CONSTANT_Class);
    this.name_index = name_index;
  }


  public void accept(Visitor v) {
    v.visitConstantClass(this);
  }


  public final void dump(DataOutputStream file) throws IOException
  {
    file.writeByte(tag);
    file.writeShort(name_index);
  }


  public final int getNameIndex() { return name_index; }


  public final void setNameIndex(int name_index) {
    this.name_index = name_index;
  }



  public Object getConstantValue(ConstantPool cp) {
    Constant c = cp.getConstant(name_index, Constants.CONSTANT_Utf8);
    return ((ConstantUtf8)c).getBytes();
  }


  public String getBytes(ConstantPool cp) {
    return (String)getConstantValue(cp);
  }


  public final String toString() {
    return super.toString() + "(name_index = " + name_index + ")";
  }
}
