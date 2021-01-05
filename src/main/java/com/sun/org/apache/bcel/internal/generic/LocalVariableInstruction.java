
package com.sun.org.apache.bcel.internal.generic;


import java.io.*;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.Constants;


public abstract class LocalVariableInstruction extends Instruction
  implements TypedInstruction, IndexedInstruction {
  protected int     n         = -1; private short     c_tag     = -1; private short     canon_tag = -1; private final boolean wide() { return n > Constants.MAX_BYTE; }


  LocalVariableInstruction(short canon_tag, short c_tag) {
    super();
    this.canon_tag = canon_tag;
    this.c_tag     = c_tag;
  }


  LocalVariableInstruction() {
  }


  protected LocalVariableInstruction(short opcode, short c_tag, int n) {
    super(opcode, (short)2);

    this.c_tag = c_tag;
    canon_tag  = opcode;

    setIndex(n);
  }


  public void dump(DataOutputStream out) throws IOException {
    if(wide()) out.writeByte(Constants.WIDE);

    out.writeByte(opcode);

    if(length > 1) { if(wide())
        out.writeShort(n);
      else
        out.writeByte(n);
    }
  }


  public String toString(boolean verbose) {
    if(((opcode >= Constants.ILOAD_0) &&
        (opcode <= Constants.ALOAD_3)) ||
       ((opcode >= Constants.ISTORE_0) &&
        (opcode <= Constants.ASTORE_3)))
      return super.toString(verbose);
    else
      return super.toString(verbose) + " " + n;
  }


  protected void initFromFile(ByteSequence bytes, boolean wide)
    throws IOException
  {
    if(wide) {
      n         = bytes.readUnsignedShort();
      length    = 4;
    } else if(((opcode >= Constants.ILOAD) &&
               (opcode <= Constants.ALOAD)) ||
              ((opcode >= Constants.ISTORE) &&
               (opcode <= Constants.ASTORE))) {
      n      = bytes.readUnsignedByte();
      length = 2;
    } else if(opcode <= Constants.ALOAD_3) { n      = (opcode - Constants.ILOAD_0) % 4;
      length = 1;
    } else { n      = (opcode - Constants.ISTORE_0) % 4;
      length = 1;
    }
 }


  public final int getIndex() { return n; }


  public void setIndex(int n) {
    if((n < 0) || (n > Constants.MAX_SHORT))
      throw new ClassGenException("Illegal value: " + n);

    this.n = n;

    if(n >= 0 && n <= 3) { opcode = (short)(c_tag + n);
      length = 1;
    } else {
      opcode = canon_tag;

      if(wide()) length = 4;
      else
        length = 2;
    }
  }


  public short getCanonicalTag() {
    return canon_tag;
  }


  public Type getType(ConstantPoolGen cp) {
    switch(canon_tag) {
    case Constants.ILOAD: case Constants.ISTORE:
      return Type.INT;
    case Constants.LLOAD: case Constants.LSTORE:
      return Type.LONG;
    case Constants.DLOAD: case Constants.DSTORE:
      return Type.DOUBLE;
    case Constants.FLOAD: case Constants.FSTORE:
      return Type.FLOAT;
    case Constants.ALOAD: case Constants.ASTORE:
      return Type.OBJECT;

    default: throw new ClassGenException("Oops: unknown case in switch" + canon_tag);
    }
  }
}
