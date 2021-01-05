
package com.sun.org.apache.bcel.internal.generic;




public class FDIV extends ArithmeticInstruction {

  public FDIV() {
    super(com.sun.org.apache.bcel.internal.Constants.FDIV);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitFDIV(this);
  }
}
