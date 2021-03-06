
package com.sun.org.apache.bcel.internal.generic;




public class POP extends StackInstruction implements PopInstruction {
  public POP() {
    super(com.sun.org.apache.bcel.internal.Constants.POP);
  }



  public void accept(Visitor v) {
    v.visitStackConsumer(this);
    v.visitPopInstruction(this);
    v.visitStackInstruction(this);
    v.visitPOP(this);
  }
}
