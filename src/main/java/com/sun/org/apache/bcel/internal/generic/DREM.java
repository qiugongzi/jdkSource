
package com.sun.org.apache.bcel.internal.generic;




public class DREM extends ArithmeticInstruction {

  public DREM() {
    super(com.sun.org.apache.bcel.internal.Constants.DREM);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitDREM(this);
  }
}
