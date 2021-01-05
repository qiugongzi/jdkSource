
package com.sun.org.apache.bcel.internal.generic;


import java.io.*;


public class JSR extends JsrInstruction implements VariableLengthInstruction {

  JSR() {}

  public JSR(InstructionHandle target) {
    super(com.sun.org.apache.bcel.internal.Constants.JSR, target);
  }


  public void dump(DataOutputStream out) throws IOException {
    index = getTargetOffset();
    if(opcode == com.sun.org.apache.bcel.internal.Constants.JSR)
      super.dump(out);
    else { index = getTargetOffset();
      out.writeByte(opcode);
      out.writeInt(index);
    }
  }

  protected int updatePosition(int offset, int max_offset) {
    int i = getTargetOffset(); position += offset; if(Math.abs(i) >= (32767 - max_offset)) { opcode  = com.sun.org.apache.bcel.internal.Constants.JSR_W;
      length = 5;
      return 2; }

    return 0;
  }


  public void accept(Visitor v) {
    v.visitStackProducer(this);
    v.visitVariableLengthInstruction(this);
    v.visitBranchInstruction(this);
    v.visitJsrInstruction(this);
    v.visitJSR(this);
  }
}
