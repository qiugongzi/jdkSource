
package com.sun.org.apache.bcel.internal.generic;




public class DDIV extends ArithmeticInstruction {

  public DDIV() {
    super(com.sun.org.apache.bcel.internal.Constants.DDIV);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitDDIV(this);
  }
}
