
package com.sun.org.apache.bcel.internal.generic;


import java.io.*;
import com.sun.org.apache.bcel.internal.util.ByteSequence;


public class JSR_W extends JsrInstruction {

  JSR_W() {}

  public JSR_W(InstructionHandle target) {
    super(com.sun.org.apache.bcel.internal.Constants.JSR_W, target);
    length = 5;
  }


  public void dump(DataOutputStream out) throws IOException {
    index = getTargetOffset();
    out.writeByte(opcode);
    out.writeInt(index);
  }


  protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException
  {
    index = bytes.readInt();
    length = 5;
  }


  public void accept(Visitor v) {
    v.visitStackProducer(this);
    v.visitBranchInstruction(this);
    v.visitJsrInstruction(this);
    v.visitJSR_W(this);
  }
}
