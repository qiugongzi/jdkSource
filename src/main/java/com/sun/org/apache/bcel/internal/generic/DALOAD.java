
package com.sun.org.apache.bcel.internal.generic;




public class DALOAD extends ArrayInstruction implements StackProducer {

  public DALOAD() {
    super(com.sun.org.apache.bcel.internal.Constants.DALOAD);
  }



  public void accept(Visitor v) {
    v.visitStackProducer(this);
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitArrayInstruction(this);
    v.visitDALOAD(this);
  }
}
