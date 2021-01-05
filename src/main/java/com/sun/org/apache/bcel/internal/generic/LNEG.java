
package com.sun.org.apache.bcel.internal.generic;




public class LNEG extends ArithmeticInstruction {
  public LNEG() {
    super(com.sun.org.apache.bcel.internal.Constants.LNEG);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitLNEG(this);
  }
}
