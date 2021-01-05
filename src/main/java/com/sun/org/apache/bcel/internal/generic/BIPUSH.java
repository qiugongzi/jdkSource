
package com.sun.org.apache.bcel.internal.generic;



import java.io.*;
import com.sun.org.apache.bcel.internal.util.ByteSequence;


public class BIPUSH extends Instruction implements ConstantPushInstruction {
  private byte b;


  BIPUSH() {}


  public BIPUSH(byte b) {
    super(com.sun.org.apache.bcel.internal.Constants.BIPUSH, (short)2);
    this.b = b;
  }


  public void dump(DataOutputStream out) throws IOException {
    super.dump(out);
    out.writeByte(b);
  }


  public String toString(boolean verbose) {
    return super.toString(verbose) + " " + b;
  }


  protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException
  {
    length = 2;
    b      = bytes.readByte();
  }

  public Number getValue() { return new Integer(b); }


  public Type getType(ConstantPoolGen cp) {
    return Type.BYTE;
  }


  public void accept(Visitor v) {
    v.visitPushInstruction(this);
    v.visitStackProducer(this);
    v.visitTypedInstruction(this);
    v.visitConstantPushInstruction(this);
    v.visitBIPUSH(this);
  }
}
