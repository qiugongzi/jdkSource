
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public final class ConstantDouble extends Constant implements ConstantObject {
  private double bytes;


  public ConstantDouble(double bytes) {
    super(Constants.CONSTANT_Double);
    this.bytes = bytes;
  }


  public ConstantDouble(ConstantDouble c) {
    this(c.getBytes());
  }


  ConstantDouble(DataInputStream file) throws IOException
  {
    this(file.readDouble());
  }


  public void accept(Visitor v) {
    v.visitConstantDouble(this);
  }

  public final void dump(DataOutputStream file) throws IOException
  {
    file.writeByte(tag);
    file.writeDouble(bytes);
  }

  public final double getBytes() { return bytes; }

  public final void setBytes(double bytes) {
    this.bytes = bytes;
  }

  public final String toString()
  {
    return super.toString() + "(bytes = " + bytes + ")";
  }


  public Object getConstantValue(ConstantPool cp) {
    return new Double(bytes);
  }
}
