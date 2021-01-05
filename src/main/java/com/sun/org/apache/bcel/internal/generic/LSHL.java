
package com.sun.org.apache.bcel.internal.generic;




public class LSHL extends ArithmeticInstruction {
  public LSHL() {
    super(com.sun.org.apache.bcel.internal.Constants.LSHL);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitLSHL(this);
  }
}
