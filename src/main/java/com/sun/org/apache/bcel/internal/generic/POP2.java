
package com.sun.org.apache.bcel.internal.generic;




public class POP2 extends StackInstruction implements PopInstruction {
  public POP2() {
    super(com.sun.org.apache.bcel.internal.Constants.POP2);
  }



  public void accept(Visitor v) {
    v.visitStackConsumer(this);
    v.visitPopInstruction(this);
    v.visitStackInstruction(this);
    v.visitPOP2(this);
  }
}
