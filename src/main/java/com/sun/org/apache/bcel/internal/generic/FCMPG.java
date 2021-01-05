
package com.sun.org.apache.bcel.internal.generic;




public class FCMPG extends Instruction
  implements TypedInstruction, StackProducer, StackConsumer {
  public FCMPG() {
    super(com.sun.org.apache.bcel.internal.Constants.FCMPG, (short)1);
  }


  public Type getType(ConstantPoolGen cp) {
    return Type.FLOAT;
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitFCMPG(this);
  }
}
