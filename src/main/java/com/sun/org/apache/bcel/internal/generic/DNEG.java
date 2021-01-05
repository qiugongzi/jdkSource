
package com.sun.org.apache.bcel.internal.generic;




public class DNEG extends ArithmeticInstruction {
  public DNEG() {
    super(com.sun.org.apache.bcel.internal.Constants.DNEG);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitDNEG(this);
  }
}
