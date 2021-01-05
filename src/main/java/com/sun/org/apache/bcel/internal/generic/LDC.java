
package com.sun.org.apache.bcel.internal.generic;


import java.io.*;
import com.sun.org.apache.bcel.internal.util.ByteSequence;


public class LDC extends CPInstruction
  implements PushInstruction, ExceptionThrower, TypedInstruction {

  LDC() {}

  public LDC(int index) {
    super(com.sun.org.apache.bcel.internal.Constants.LDC_W, index);
    setSize();
  }

  protected final void setSize() {
    if(index <= com.sun.org.apache.bcel.internal.Constants.MAX_BYTE) { opcode = com.sun.org.apache.bcel.internal.Constants.LDC;
      length = 2;
    } else {
      opcode = com.sun.org.apache.bcel.internal.Constants.LDC_W;
      length = 3;
    }
  }


  public void dump(DataOutputStream out) throws IOException {
    out.writeByte(opcode);

    if(length == 2)
      out.writeByte(index);
    else out.writeShort(index);
  }


  public final void setIndex(int index) {
    super.setIndex(index);
    setSize();
  }


  protected void initFromFile(ByteSequence bytes, boolean wide)
       throws IOException
  {
    length = 2;
    index  = bytes.readUnsignedByte();
  }

  public Object getValue(ConstantPoolGen cpg) {
    com.sun.org.apache.bcel.internal.classfile.Constant c = cpg.getConstantPool().getConstant(index);

    switch(c.getTag()) {
      case com.sun.org.apache.bcel.internal.Constants.CONSTANT_String:
        int i = ((com.sun.org.apache.bcel.internal.classfile.ConstantString)c).getStringIndex();
        c = cpg.getConstantPool().getConstant(i);
        return ((com.sun.org.apache.bcel.internal.classfile.ConstantUtf8)c).getBytes();

    case com.sun.org.apache.bcel.internal.Constants.CONSTANT_Float:
        return new Float(((com.sun.org.apache.bcel.internal.classfile.ConstantFloat)c).getBytes());

    case com.sun.org.apache.bcel.internal.Constants.CONSTANT_Integer:
        return new Integer(((com.sun.org.apache.bcel.internal.classfile.ConstantInteger)c).getBytes());

    default: throw new RuntimeException("Unknown or invalid constant type at " + index);
      }
  }

  public Type getType(ConstantPoolGen cpg) {
    switch(cpg.getConstantPool().getConstant(index).getTag()) {
    case com.sun.org.apache.bcel.internal.Constants.CONSTANT_String:  return Type.STRING;
    case com.sun.org.apache.bcel.internal.Constants.CONSTANT_Float:   return Type.FLOAT;
    case com.sun.org.apache.bcel.internal.Constants.CONSTANT_Integer: return Type.INT;
    default: throw new RuntimeException("Unknown or invalid constant type at " + index);
    }
  }

  public Class[] getExceptions() {
    return com.sun.org.apache.bcel.internal.ExceptionConstants.EXCS_STRING_RESOLUTION;
  }


  public void accept(Visitor v) {
    v.visitStackProducer(this);
    v.visitPushInstruction(this);
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitCPInstruction(this);
    v.visitLDC(this);
  }
}
