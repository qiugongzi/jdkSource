
package com.sun.org.apache.bcel.internal.generic;




public class LDIV extends ArithmeticInstruction implements ExceptionThrower  {
  public LDIV() {
    super(com.sun.org.apache.bcel.internal.Constants.LDIV);
  }

  public Class[] getExceptions() {
    return new Class[] { com.sun.org.apache.bcel.internal.ExceptionConstants.ARITHMETIC_EXCEPTION };
  }



  public void accept(Visitor v) {
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitArithmeticInstruction(this);
    v.visitLDIV(this);
  }
}
