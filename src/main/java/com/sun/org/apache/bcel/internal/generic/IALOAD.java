
package com.sun.org.apache.bcel.internal.generic;




public class IALOAD extends ArrayInstruction implements StackProducer {

  public IALOAD() {
    super(com.sun.org.apache.bcel.internal.Constants.IALOAD);
  }



  public void accept(Visitor v) {
    v.visitStackProducer(this);
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitArrayInstruction(this);
    v.visitIALOAD(this);
  }
}
