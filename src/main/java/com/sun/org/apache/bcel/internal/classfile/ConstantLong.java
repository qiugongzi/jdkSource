
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public final class ConstantLong extends Constant implements ConstantObject {
  private long bytes;


  public ConstantLong(long bytes)
  {
    super(Constants.CONSTANT_Long);
    this.bytes = bytes;
  }

  public ConstantLong(ConstantLong c) {
    this(c.getBytes());
  }

  ConstantLong(DataInputStream file) throws IOException
  {
    this(file.readLong());
  }

  public void accept(Visitor v) {
    v.visitConstantLong(this);
  }

  public final void dump(DataOutputStream file) throws IOException
  {
    file.writeByte(tag);
    file.writeLong(bytes);
  }

  public final long getBytes() { return bytes; }

  public final void setBytes(long bytes) {
    this.bytes = bytes;
  }

  public final String toString() {
    return super.toString() + "(bytes = " + bytes + ")";
  }


  public Object getConstantValue(ConstantPool cp) {
    return new Long(bytes);
  }
}
