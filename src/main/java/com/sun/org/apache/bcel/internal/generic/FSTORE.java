
package com.sun.org.apache.bcel.internal.generic;




public class FSTORE extends StoreInstruction {

  FSTORE() {
    super(com.sun.org.apache.bcel.internal.Constants.FSTORE, com.sun.org.apache.bcel.internal.Constants.FSTORE_0);
  }


  public FSTORE(int n) {
    super(com.sun.org.apache.bcel.internal.Constants.FSTORE, com.sun.org.apache.bcel.internal.Constants.FSTORE_0, n);
  }


  public void accept(Visitor v) {
    super.accept(v);
    v.visitFSTORE(this);
  }
}
