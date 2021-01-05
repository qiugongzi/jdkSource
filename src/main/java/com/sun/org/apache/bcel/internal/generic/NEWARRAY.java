
package com.sun.org.apache.bcel.internal.generic;


import java.io.*;
import com.sun.org.apache.bcel.internal.util.ByteSequence;


public class NEWARRAY extends Instruction
  implements AllocationInstruction, ExceptionThrower, StackProducer {
  private byte type;


  NEWARRAY() {}

  public NEWARRAY(byte type) {
    super(com.sun.org.apache.bcel.internal.Constants.NEWARRAY, (short)2);
    this.type = type;
  }

  public NEWARRAY(BasicType type) {
      this(type.getType());
  }


  public void dump(DataOutputStream out) throws IOException {
    out.writeByte(opcode);
    out.writeByte(type);
  }


  public final byte getTypecode() { return type; }


  public final Type getType() {
    return new ArrayType(BasicType.getType(type), 1);
  }


  public String toString(boolean verbose) {
    return super.toString(verbose) + " " + com.sun.org.apache.bcel.internal.Constants.TYPE_NAMES[type];
  }

  protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException
  {
    type   = bytes.readByte();
    length = 2;
  }

  public Class[] getExceptions() {
    return new Class[] { com.sun.org.apache.bcel.internal.ExceptionConstants.NEGATIVE_ARRAY_SIZE_EXCEPTION };
  }


  public void accept(Visitor v) {
    v.visitAllocationInstruction(this);
    v.visitExceptionThrower(this);
    v.visitStackProducer(this);
    v.visitNEWARRAY(this);
  }
}
