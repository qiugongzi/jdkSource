
package com.sun.org.apache.bcel.internal.generic;




public class DADD extends ArithmeticInstruction {

  public DADD() {
    super(com.sun.org.apache.bcel.internal.Constants.DADD);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitDADD(this);
  }
}
