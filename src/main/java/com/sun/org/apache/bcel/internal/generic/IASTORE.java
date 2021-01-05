
package com.sun.org.apache.bcel.internal.generic;




public class IASTORE extends ArrayInstruction implements StackConsumer {

  public IASTORE() {
    super(com.sun.org.apache.bcel.internal.Constants.IASTORE);
  }



  public void accept(Visitor v) {
    v.visitStackConsumer(this);
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitArrayInstruction(this);
    v.visitIASTORE(this);
  }
}
