
package com.sun.org.apache.bcel.internal.generic;




public class FLOAD extends LoadInstruction {

  FLOAD() {
    super(com.sun.org.apache.bcel.internal.Constants.FLOAD, com.sun.org.apache.bcel.internal.Constants.FLOAD_0);
  }


  public FLOAD(int n) {
    super(com.sun.org.apache.bcel.internal.Constants.FLOAD, com.sun.org.apache.bcel.internal.Constants.FLOAD_0, n);
  }


  public void accept(Visitor v) {
    super.accept(v);
    v.visitFLOAD(this);
  }
}
