
package com.sun.org.apache.bcel.internal.generic;




public class IFEQ extends IfInstruction {

  IFEQ() {}

  public IFEQ(InstructionHandle target) {
    super(com.sun.org.apache.bcel.internal.Constants.IFEQ, target);
  }


  public IfInstruction negate() {
    return new IFNE(target);
  }



  public void accept(Visitor v) {
    v.visitStackConsumer(this);
    v.visitBranchInstruction(this);
    v.visitIfInstruction(this);
    v.visitIFEQ(this);
  }
}
