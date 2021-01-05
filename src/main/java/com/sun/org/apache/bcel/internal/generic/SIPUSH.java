
package com.sun.org.apache.bcel.internal.generic;


import java.io.*;
import com.sun.org.apache.bcel.internal.util.ByteSequence;


public class SIPUSH extends Instruction implements ConstantPushInstruction {
  private short b;


  SIPUSH() {}

  public SIPUSH(short b) {
    super(com.sun.org.apache.bcel.internal.Constants.SIPUSH, (short)3);
    this.b = b;
  }


  public void dump(DataOutputStream out) throws IOException {
    super.dump(out);
    out.writeShort(b);
  }


  public String toString(boolean verbose) {
    return super.toString(verbose) + " " + b;
  }


  protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException
  {
    length = 3;
    b      = bytes.readShort();
  }

  public Number getValue() { return new Integer(b); }


  public Type getType(ConstantPoolGen cp) {
    return Type.SHORT;
  }


  public void accept(Visitor v) {
    v.visitPushInstruction(this);
    v.visitStackProducer(this);
    v.visitTypedInstruction(this);
    v.visitConstantPushInstruction(this);
    v.visitSIPUSH(this);
  }
}
