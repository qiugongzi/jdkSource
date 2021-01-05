
package com.sun.org.apache.bcel.internal.generic;




public class DRETURN extends ReturnInstruction {

  public DRETURN() {
    super(com.sun.org.apache.bcel.internal.Constants.DRETURN);
  }



  public void accept(Visitor v) {
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitStackConsumer(this);
    v.visitReturnInstruction(this);
    v.visitDRETURN(this);
  }
}
