
package com.sun.org.apache.bcel.internal.generic;


import java.io.*;
import com.sun.org.apache.bcel.internal.util.ByteSequence;


public class RET extends Instruction implements IndexedInstruction, TypedInstruction {
  private boolean wide;
  private int     index; RET() {}

  public RET(int index) {
    super(com.sun.org.apache.bcel.internal.Constants.RET, (short)2);
    setIndex(index);   }


  public void dump(DataOutputStream out) throws IOException {
    if(wide)
      out.writeByte(com.sun.org.apache.bcel.internal.Constants.WIDE);

    out.writeByte(opcode);

    if(wide)
      out.writeShort(index);
    else
      out.writeByte(index);
  }

  private final void setWide() {
    if(wide = index > com.sun.org.apache.bcel.internal.Constants.MAX_BYTE)
      length = 4; else
      length = 2;
  }


  protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException
  {
    this.wide = wide;

    if(wide) {
      index  = bytes.readUnsignedShort();
      length = 4;
    } else {
      index = bytes.readUnsignedByte();
      length = 2;
    }
  }


  public final int getIndex() { return index; }


  public final void setIndex(int n) {
    if(n < 0)
      throw new ClassGenException("Negative index value: " + n);

    index = n;
    setWide();
  }


  public String toString(boolean verbose) {
    return super.toString(verbose) + " " + index;
  }


  public Type getType(ConstantPoolGen cp) {
      return ReturnaddressType.NO_TARGET;
  }


  public void accept(Visitor v) {
    v.visitRET(this);
  }
}
