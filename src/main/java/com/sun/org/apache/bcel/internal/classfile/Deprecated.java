
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public final class Deprecated extends Attribute {
  private byte[] bytes;


  public Deprecated(Deprecated c) {
    this(c.getNameIndex(), c.getLength(), c.getBytes(), c.getConstantPool());
  }


  public Deprecated(int name_index, int length, byte[] bytes,
                    ConstantPool constant_pool)
  {
    super(Constants.ATTR_DEPRECATED, name_index, length, constant_pool);
    this.bytes = bytes;
  }


  Deprecated(int name_index, int length, DataInputStream file,
             ConstantPool constant_pool) throws IOException
  {
    this(name_index, length, (byte [])null, constant_pool);

    if(length > 0) {
      bytes = new byte[length];
      file.readFully(bytes);
      System.err.println("Deprecated attribute with length > 0");
    }
  }


  public void accept(Visitor v) {
    v.visitDeprecated(this);
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
    return Constants.ATTRIBUTE_NAMES[Constants.ATTR_DEPRECATED];
  }


  public Attribute copy(ConstantPool constant_pool) {
    Deprecated c = (Deprecated)clone();

    if(bytes != null)
      c.bytes = (byte[])bytes.clone();

    c.constant_pool = constant_pool;
    return c;
  }
}
