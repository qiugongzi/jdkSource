
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public final class ConstantUtf8 extends Constant {
  private String bytes;


  public ConstantUtf8(ConstantUtf8 c) {
    this(c.getBytes());
  }


  ConstantUtf8(DataInputStream file) throws IOException
  {
    super(Constants.CONSTANT_Utf8);

    bytes = file.readUTF();
  }


  public ConstantUtf8(String bytes)
  {
    super(Constants.CONSTANT_Utf8);

    if(bytes == null)
      throw new IllegalArgumentException("bytes must not be null!");

    this.bytes  = bytes;
  }


  public void accept(Visitor v) {
    v.visitConstantUtf8(this);
  }


  public final void dump(DataOutputStream file) throws IOException
  {
    file.writeByte(tag);
    file.writeUTF(bytes);
  }


  public final String getBytes() { return bytes; }


  public final void setBytes(String bytes) {
    this.bytes = bytes;
  }


  public final String toString()
  {
    return super.toString() + "(\"" + Utility.replace(bytes, "\n", "\\n") + "\")";
  }
}
