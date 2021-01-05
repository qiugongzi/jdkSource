
package com.sun.org.apache.bcel.internal.generic;


import java.io.*;
import com.sun.org.apache.bcel.internal.util.ByteSequence;


public class GOTO_W extends GotoInstruction {

  GOTO_W() {}

  public GOTO_W(InstructionHandle target) {
    super(com.sun.org.apache.bcel.internal.Constants.GOTO_W, target);
    length = 5;
  }


  public void dump(DataOutputStream out) throws IOException {
    index = getTargetOffset();
    out.writeByte(opcode);
    out.writeInt(index);
  }


  protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException
  {
    index  = bytes.readInt();
    length = 5;
  }


  public void accept(Visitor v) {
    v.visitUnconditionalBranch(this);
    v.visitBranchInstruction(this);
    v.visitGotoInstruction(this);
    v.visitGOTO_W(this);
  }
}
