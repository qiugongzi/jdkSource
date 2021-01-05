
package com.sun.org.apache.bcel.internal.generic;




public class IUSHR extends ArithmeticInstruction {
  public IUSHR() {
    super(com.sun.org.apache.bcel.internal.Constants.IUSHR);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitIUSHR(this);
  }
}
