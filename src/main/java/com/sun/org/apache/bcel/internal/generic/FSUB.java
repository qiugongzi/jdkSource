
package com.sun.org.apache.bcel.internal.generic;




public class FSUB extends ArithmeticInstruction {

  public FSUB() {
    super(com.sun.org.apache.bcel.internal.Constants.FSUB);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitFSUB(this);
  }
}
