
package com.sun.org.apache.bcel.internal.generic;




public abstract class JsrInstruction extends BranchInstruction
  implements UnconditionalBranch, TypedInstruction, StackProducer
{
  JsrInstruction(short opcode, InstructionHandle target) {
    super(opcode, target);
  }


  JsrInstruction(){}


  public Type getType(ConstantPoolGen cp) {
    return new ReturnaddressType(physicalSuccessor());
  }


  public InstructionHandle physicalSuccessor(){
    InstructionHandle ih = this.target;

    while(ih.getPrev() != null)
      ih = ih.getPrev();

    while(ih.getInstruction() != this)
      ih = ih.getNext();

    InstructionHandle toThis = ih;

    while(ih != null){
        ih = ih.getNext();
        if ((ih != null) && (ih.getInstruction() == this))
        throw new RuntimeException("physicalSuccessor() called on a shared JsrInstruction.");
    }

    return toThis.getNext();
  }
}
