
package com.sun.org.apache.bcel.internal.generic;




public class RETURN extends ReturnInstruction {
  public RETURN() {
    super(com.sun.org.apache.bcel.internal.Constants.RETURN);
  }



  public void accept(Visitor v) {
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitStackConsumer(this);
    v.visitReturnInstruction(this);
    v.visitRETURN(this);
  }
}
