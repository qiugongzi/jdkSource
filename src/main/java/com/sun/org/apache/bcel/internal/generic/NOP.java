
package com.sun.org.apache.bcel.internal.generic;




public class NOP extends Instruction {
  public NOP() {
    super(com.sun.org.apache.bcel.internal.Constants.NOP, (short)1);
  }



  public void accept(Visitor v) {
    v.visitNOP(this);
  }
}
