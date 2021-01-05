
package com.sun.org.apache.bcel.internal.generic;




public class ARRAYLENGTH extends Instruction
  implements ExceptionThrower, StackProducer {

  public ARRAYLENGTH() {
    super(com.sun.org.apache.bcel.internal.Constants.ARRAYLENGTH, (short)1);
  }


  public Class[] getExceptions() {
    return new Class[] { com.sun.org.apache.bcel.internal.ExceptionConstants.NULL_POINTER_EXCEPTION };
  }



  public void accept(Visitor v) {
    v.visitExceptionThrower(this);
    v.visitStackProducer(this);
    v.visitARRAYLENGTH(this);
  }
}
