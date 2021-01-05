
package com.sun.org.apache.bcel.internal.generic;




public class BREAKPOINT extends Instruction {
  public BREAKPOINT() {
    super(com.sun.org.apache.bcel.internal.Constants.BREAKPOINT, (short)1);
  }


  public void accept(Visitor v) {
    v.visitBREAKPOINT(this);
  }
}
