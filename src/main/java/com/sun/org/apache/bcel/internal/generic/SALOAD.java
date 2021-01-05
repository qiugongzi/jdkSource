
package com.sun.org.apache.bcel.internal.generic;




public class SALOAD extends ArrayInstruction implements StackProducer {
  public SALOAD() {
    super(com.sun.org.apache.bcel.internal.Constants.SALOAD);
  }



  public void accept(Visitor v) {
    v.visitStackProducer(this);
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitArrayInstruction(this);
    v.visitSALOAD(this);
  }
}
