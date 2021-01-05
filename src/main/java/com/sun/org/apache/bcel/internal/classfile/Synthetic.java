
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public final class Synthetic extends Attribute {
  private byte[] bytes;


  public Synthetic(Synthetic c) {
    this(c.getNameIndex(), c.getLength(), c.getBytes(), c.getConstantPool());
  }


  public Synthetic(int name_index, int length, byte[] bytes,
                   ConstantPool constant_pool)
  {
    super(Constants.ATTR_SYNTHETIC, name_index, length, constant_pool);
    this.bytes         = bytes;
  }


  Synthetic(int name_index, int length, DataInputStream file,
            ConstantPool constant_pool) throws IOException
  {
    this(name_index, length, (byte [])null, constant_pool);

    if(length > 0) {
      bytes = new byte[length];
      file.readFully(bytes);
      System.err.println("Synthetic attribute with length > 0");
    }
  }

  public void accept(Visitor v) {
    v.visitSynthetic(this);
  }

  public final void dump(DataOutputStream file) throws IOException
  {
    super.dump(file);
    if(length > 0)
      file.write(bytes, 0, length);
  }

  public final byte[] getBytes() { return bytes; }


  public final void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }


  public final String toString() {
    StringBuffer buf = new StringBuffer("Synthetic");

    if(length > 0)
      buf.append(" " + Utility.toHexString(bytes));

    return buf.toString();
  }


  public Attribute copy(ConstantPool constant_pool) {
    Synthetic c = (Synthetic)clone();

    if(bytes != null)
      c.bytes = (byte[])bytes.clone();

    c.constant_pool = constant_pool;
    return c;
  }
}
