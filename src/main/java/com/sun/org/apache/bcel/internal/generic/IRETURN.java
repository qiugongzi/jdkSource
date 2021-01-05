
package com.sun.org.apache.bcel.internal.generic;




public class IRETURN extends ReturnInstruction {

  public IRETURN() {
    super(com.sun.org.apache.bcel.internal.Constants.IRETURN);
  }



  public void accept(Visitor v) {
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitStackConsumer(this);
    v.visitReturnInstruction(this);
    v.visitIRETURN(this);
  }
}
