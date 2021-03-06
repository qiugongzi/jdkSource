
package com.sun.org.apache.bcel.internal.generic;




public class IF_ACMPNE extends IfInstruction {

  IF_ACMPNE() {}

  public IF_ACMPNE(InstructionHandle target) {
    super(com.sun.org.apache.bcel.internal.Constants.IF_ACMPNE, target);
  }


  public IfInstruction negate() {
    return new IF_ACMPEQ(target);
  }



  public void accept(Visitor v) {
    v.visitStackConsumer(this);
    v.visitBranchInstruction(this);
    v.visitIfInstruction(this);
    v.visitIF_ACMPNE(this);
  }
}
