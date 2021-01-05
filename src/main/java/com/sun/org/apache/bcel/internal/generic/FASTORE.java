
package com.sun.org.apache.bcel.internal.generic;




public class FASTORE extends ArrayInstruction implements StackConsumer {

  public FASTORE() {
    super(com.sun.org.apache.bcel.internal.Constants.FASTORE);
  }



  public void accept(Visitor v) {
    v.visitStackConsumer(this);
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitArrayInstruction(this);
    v.visitFASTORE(this);
  }
}
