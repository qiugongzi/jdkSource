
package com.sun.org.apache.bcel.internal.generic;




public class AALOAD extends ArrayInstruction implements StackProducer {

  public AALOAD() {
    super(com.sun.org.apache.bcel.internal.Constants.AALOAD);
  }



  public void accept(Visitor v) {
    v.visitStackProducer(this);
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitArrayInstruction(this);
    v.visitAALOAD(this);
  }
}
