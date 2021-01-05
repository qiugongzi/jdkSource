
package com.sun.org.apache.bcel.internal.generic;




public class IF_ACMPEQ extends IfInstruction {

  IF_ACMPEQ() {}

  public IF_ACMPEQ(InstructionHandle target) {
    super(com.sun.org.apache.bcel.internal.Constants.IF_ACMPEQ, target);
  }


  public IfInstruction negate() {
    return new IF_ACMPNE(target);
  }


  public void accept(Visitor v) {
    v.visitStackConsumer(this);
    v.visitBranchInstruction(this);
    v.visitIfInstruction(this);
    v.visitIF_ACMPEQ(this);
  }
}
