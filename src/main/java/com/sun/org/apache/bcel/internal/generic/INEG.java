
package com.sun.org.apache.bcel.internal.generic;




public class INEG extends ArithmeticInstruction {
  public INEG() {
    super(com.sun.org.apache.bcel.internal.Constants.INEG);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitINEG(this);
  }
}
