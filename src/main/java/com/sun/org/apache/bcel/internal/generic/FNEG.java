
package com.sun.org.apache.bcel.internal.generic;




public class FNEG extends ArithmeticInstruction {
  public FNEG() {
    super(com.sun.org.apache.bcel.internal.Constants.FNEG);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitFNEG(this);
  }
}
