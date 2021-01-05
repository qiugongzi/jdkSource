
package com.sun.org.apache.bcel.internal.generic;




public class IMPDEP1 extends Instruction {
  public IMPDEP1() {
    super(com.sun.org.apache.bcel.internal.Constants.IMPDEP1, (short)1);
  }



  public void accept(Visitor v) {
    v.visitIMPDEP1(this);
  }
}
