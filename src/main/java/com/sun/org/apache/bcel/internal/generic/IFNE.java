
package com.sun.org.apache.bcel.internal.generic;




public class IFNE extends IfInstruction {

  IFNE() {}

  public IFNE(InstructionHandle target) {
    super(com.sun.org.apache.bcel.internal.Constants.IFNE, target);
  }


  public IfInstruction negate() {
    return new IFEQ(target);
  }



  public void accept(Visitor v) {
    v.visitStackConsumer(this);
    v.visitBranchInstruction(this);
    v.visitIfInstruction(this);
    v.visitIFNE(this);
  }
}
