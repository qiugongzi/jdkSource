
package com.sun.org.apache.bcel.internal.generic;




public class BALOAD extends ArrayInstruction implements StackProducer {

  public BALOAD() {
    super(com.sun.org.apache.bcel.internal.Constants.BALOAD);
  }



  public void accept(Visitor v) {
    v.visitStackProducer(this);
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitArrayInstruction(this);
    v.visitBALOAD(this);
  }
}
