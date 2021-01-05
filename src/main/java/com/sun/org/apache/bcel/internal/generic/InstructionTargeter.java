
package com.sun.org.apache.bcel.internal.generic;




public interface InstructionTargeter {
  public boolean containsTarget(InstructionHandle ih);
  public void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih);
}
