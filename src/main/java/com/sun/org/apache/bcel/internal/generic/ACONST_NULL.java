
package com.sun.org.apache.bcel.internal.generic;




public class ACONST_NULL extends Instruction
  implements PushInstruction, TypedInstruction {

  public ACONST_NULL() {
    super(com.sun.org.apache.bcel.internal.Constants.ACONST_NULL, (short)1);
  }


  public Type getType(ConstantPoolGen cp) {
    return Type.NULL;
  }



  public void accept(Visitor v) {
    v.visitStackProducer(this);
    v.visitPushInstruction(this);
    v.visitTypedInstruction(this);
    v.visitACONST_NULL(this);
  }
}
