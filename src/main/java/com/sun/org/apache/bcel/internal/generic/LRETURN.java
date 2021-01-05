
package com.sun.org.apache.bcel.internal.generic;




public class LRETURN extends ReturnInstruction {
  public LRETURN() {
    super(com.sun.org.apache.bcel.internal.Constants.LRETURN);
  }



  public void accept(Visitor v) {
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitStackConsumer(this);
    v.visitReturnInstruction(this);
    v.visitLRETURN(this);
  }
}
