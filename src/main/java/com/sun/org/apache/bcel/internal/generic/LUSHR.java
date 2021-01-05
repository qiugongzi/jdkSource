
package com.sun.org.apache.bcel.internal.generic;




public class LUSHR extends ArithmeticInstruction {
  public LUSHR() {
    super(com.sun.org.apache.bcel.internal.Constants.LUSHR);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitLUSHR(this);
  }
}
