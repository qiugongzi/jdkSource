
package com.sun.org.apache.bcel.internal.generic;



import com.sun.org.apache.bcel.internal.classfile.*;


public final class CodeExceptionGen
  implements InstructionTargeter, Cloneable, java.io.Serializable {
  private InstructionHandle start_pc;
  private InstructionHandle end_pc;
  private InstructionHandle handler_pc;
  private ObjectType        catch_type;


  public CodeExceptionGen(InstructionHandle start_pc, InstructionHandle end_pc,
                          InstructionHandle handler_pc, ObjectType catch_type) {
    setStartPC(start_pc);
    setEndPC(end_pc);
    setHandlerPC(handler_pc);
    this.catch_type = catch_type;
  }


  public CodeException getCodeException(ConstantPoolGen cp) {
    return new CodeException(start_pc.getPosition(),
                             end_pc.getPosition() + end_pc.getInstruction().getLength(),
                             handler_pc.getPosition(),
                             (catch_type == null)? 0 : cp.addClass(catch_type));
  }


  public final void setStartPC(InstructionHandle start_pc) {
    BranchInstruction.notifyTargetChanging(this.start_pc, this);
    this.start_pc = start_pc;
    BranchInstruction.notifyTargetChanged(this.start_pc, this);
  }


  public final void setEndPC(InstructionHandle end_pc) {
    BranchInstruction.notifyTargetChanging(this.end_pc, this);
    this.end_pc = end_pc;
    BranchInstruction.notifyTargetChanged(this.end_pc, this);
  }


  public final void setHandlerPC(InstructionHandle handler_pc) {
    BranchInstruction.notifyTargetChanging(this.handler_pc, this);
    this.handler_pc = handler_pc;
    BranchInstruction.notifyTargetChanged(this.handler_pc, this);
  }


  @Override
  public void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih) {
    boolean targeted = false;

    if(start_pc == old_ih) {
      targeted = true;
      setStartPC(new_ih);
    }

    if(end_pc == old_ih) {
      targeted = true;
      setEndPC(new_ih);
    }

    if(handler_pc == old_ih) {
      targeted = true;
      setHandlerPC(new_ih);
    }

    if(!targeted)
      throw new ClassGenException("Not targeting " + old_ih + ", but {" + start_pc + ", " +
                                  end_pc + ", " + handler_pc + "}");
  }


  @Override
  public boolean containsTarget(InstructionHandle ih) {
    return (start_pc == ih) || (end_pc == ih) || (handler_pc == ih);
  }


  public void              setCatchType(ObjectType catch_type)        { this.catch_type = catch_type; }

  public ObjectType        getCatchType()                             { return catch_type; }


  public InstructionHandle getStartPC()                               { return start_pc; }


  public InstructionHandle getEndPC()                                 { return end_pc; }


  public InstructionHandle getHandlerPC()                             { return handler_pc; }

  @Override
  public String toString() {
    return "CodeExceptionGen(" + start_pc + ", " + end_pc + ", " + handler_pc + ")";
  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch(CloneNotSupportedException e) {
      System.err.println(e);
      return null;
    }
  }
}
