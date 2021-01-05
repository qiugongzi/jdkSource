
package com.sun.org.apache.bcel.internal.generic;




public class LREM extends ArithmeticInstruction implements ExceptionThrower {
  public LREM() {
    super(com.sun.org.apache.bcel.internal.Constants.LREM);
  }

  public Class[] getExceptions() { return new Class[] { com.sun.org.apache.bcel.internal.ExceptionConstants.ARITHMETIC_EXCEPTION }; }



  public void accept(Visitor v) {
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitLREM(this);
  }
}
