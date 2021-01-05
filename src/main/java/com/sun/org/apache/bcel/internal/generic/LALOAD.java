
package com.sun.org.apache.bcel.internal.generic;




public class LALOAD extends ArrayInstruction implements StackProducer {

  public LALOAD() {
    super(com.sun.org.apache.bcel.internal.Constants.LALOAD);
  }



  public void accept(Visitor v) {
    v.visitStackProducer(this);
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitArrayInstruction(this);
    v.visitLALOAD(this);
  }
}
