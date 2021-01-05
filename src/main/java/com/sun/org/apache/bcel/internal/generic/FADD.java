
package com.sun.org.apache.bcel.internal.generic;




public class FADD extends ArithmeticInstruction {

  public FADD() {
    super(com.sun.org.apache.bcel.internal.Constants.FADD);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitFADD(this);
  }
}
