
package com.sun.org.apache.bcel.internal.generic;




public class ISTORE extends StoreInstruction {

  ISTORE() {
    super(com.sun.org.apache.bcel.internal.Constants.ISTORE, com.sun.org.apache.bcel.internal.Constants.ISTORE_0);
  }


  public ISTORE(int n) {
    super(com.sun.org.apache.bcel.internal.Constants.ISTORE, com.sun.org.apache.bcel.internal.Constants.ISTORE_0, n);
  }


  public void accept(Visitor v) {
    super.accept(v);
    v.visitISTORE(this);
  }
}
