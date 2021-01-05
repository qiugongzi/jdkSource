
package com.sun.org.apache.bcel.internal.generic;




public class DUP2_X2 extends StackInstruction {
  public DUP2_X2() {
    super(com.sun.org.apache.bcel.internal.Constants.DUP2_X2);
  }



  public void accept(Visitor v) {
    v.visitStackInstruction(this);
    v.visitDUP2_X2(this);
  }
}
