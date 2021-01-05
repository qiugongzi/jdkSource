
package com.sun.org.apache.bcel.internal.generic;




public class LMUL extends ArithmeticInstruction {
  public LMUL() {
    super(com.sun.org.apache.bcel.internal.Constants.LMUL);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitLMUL(this);
  }
}
