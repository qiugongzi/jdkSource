
package com.sun.org.apache.bcel.internal.generic;



import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.*;
import java.util.Objects;


public class LocalVariableGen
  implements InstructionTargeter, NamedAndTyped, Cloneable,
             java.io.Serializable
{
  private final int   index;
  private String      name;
  private Type        type;
  private InstructionHandle start, end;


  public LocalVariableGen(int index, String name, Type type,
                          InstructionHandle start, InstructionHandle end) {
    if((index < 0) || (index > Constants.MAX_SHORT))
      throw new ClassGenException("Invalid index index: " + index);

    this.name  = name;
    this.type  = type;
    this.index  = index;
    setStart(start);
    setEnd(end);
  }


  public LocalVariable getLocalVariable(ConstantPoolGen cp) {
    int start_pc        = start.getPosition();
    int length          = end.getPosition() - start_pc;

    if(length > 0)
      length += end.getInstruction().getLength();

    int name_index      = cp.addUtf8(name);
    int signature_index = cp.addUtf8(type.getSignature());

    return new LocalVariable(start_pc, length, name_index,
                             signature_index, index, cp.getConstantPool());
  }

  public int         getIndex()                  { return index; }
  @Override
  public void        setName(String name)        { this.name = name; }
  @Override
  public String      getName()                   { return name; }
  @Override
  public void        setType(Type type)          { this.type = type; }
  @Override
  public Type        getType()                   { return type; }

  public InstructionHandle getStart()                  { return start; }
  public InstructionHandle getEnd()                    { return end; }


  void notifyTargetChanging() {
    BranchInstruction.notifyTargetChanging(this.start, this);
    if (this.end != this.start) {
        BranchInstruction.notifyTargetChanging(this.end, this);
    }
  }


  void notifyTargetChanged() {
    BranchInstruction.notifyTargetChanged(this.start, this);
    if (this.end != this.start) {
        BranchInstruction.notifyTargetChanged(this.end, this);
    }
  }

  public final void setStart(InstructionHandle start) {

    notifyTargetChanging();

    this.start = start;

    notifyTargetChanged();
  }

  public final void setEnd(InstructionHandle end) {
    notifyTargetChanging();

    this.end = end;

    notifyTargetChanged();

  }


  @Override
  public void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih) {
    boolean targeted = false;

    if(start == old_ih) {
      targeted = true;
      setStart(new_ih);
    }

    if(end == old_ih) {
      targeted = true;
      setEnd(new_ih);
    }

    if(!targeted)
      throw new ClassGenException("Not targeting " + old_ih + ", but {" + start + ", " +
                                  end + "}");
  }


  @Override
  public boolean containsTarget(InstructionHandle ih) {
    return (start == ih) || (end == ih);
  }


  @Override
  public boolean equals(Object o) {
    if (o==this)
      return true;

    if(!(o instanceof LocalVariableGen))
      return false;

    LocalVariableGen l = (LocalVariableGen)o;
    return (l.index == index) && (l.start == start) && (l.end == end);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + this.index;
    hash = 59 * hash + Objects.hashCode(this.start);
    hash = 59 * hash + Objects.hashCode(this.end);
    return hash;
  }

  @Override
  public String toString() {
    return "LocalVariableGen(" + name +  ", " + type +  ", " + start + ", " + end + ")";
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
