
package com.sun.org.apache.bcel.internal.generic;




public class ALOAD extends LoadInstruction {

  ALOAD() {
    super(com.sun.org.apache.bcel.internal.Constants.ALOAD, com.sun.org.apache.bcel.internal.Constants.ALOAD_0);
  }


  public ALOAD(int n) {
    super(com.sun.org.apache.bcel.internal.Constants.ALOAD, com.sun.org.apache.bcel.internal.Constants.ALOAD_0, n);
  }


  public void accept(Visitor v) {
    super.accept(v);
    v.visitALOAD(this);
  }
}
