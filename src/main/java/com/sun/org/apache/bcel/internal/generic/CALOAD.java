
package com.sun.org.apache.bcel.internal.generic;




public class CALOAD extends ArrayInstruction implements StackProducer {

  public CALOAD() {
    super(com.sun.org.apache.bcel.internal.Constants.CALOAD);
  }



  public void accept(Visitor v) {
    v.visitStackProducer(this);
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitArrayInstruction(this);
    v.visitCALOAD(this);
  }
}
