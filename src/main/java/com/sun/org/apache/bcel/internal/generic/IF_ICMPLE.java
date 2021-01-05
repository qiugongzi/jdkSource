
package com.sun.org.apache.bcel.internal.generic;




public class IF_ICMPLE extends IfInstruction {

  IF_ICMPLE() {}

  public IF_ICMPLE(InstructionHandle target) {
    super(com.sun.org.apache.bcel.internal.Constants.IF_ICMPLE, target);
  }


  public IfInstruction negate() {
    return new IF_ICMPGT(target);
  }



  public void accept(Visitor v) {
    v.visitStackConsumer(this);
    v.visitBranchInstruction(this);
    v.visitIfInstruction(this);
    v.visitIF_ICMPLE(this);
  }
}
