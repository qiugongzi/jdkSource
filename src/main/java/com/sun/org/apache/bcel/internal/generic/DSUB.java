
package com.sun.org.apache.bcel.internal.generic;




public class DSUB extends ArithmeticInstruction {

  public DSUB() {
    super(com.sun.org.apache.bcel.internal.Constants.DSUB);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitDSUB(this);
  }
}
