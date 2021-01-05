
package com.sun.org.apache.bcel.internal.generic;




public class FMUL extends ArithmeticInstruction {

  public FMUL() {
    super(com.sun.org.apache.bcel.internal.Constants.FMUL);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitFMUL(this);
  }
}
