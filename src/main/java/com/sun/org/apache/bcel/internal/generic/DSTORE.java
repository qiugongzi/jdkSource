
package com.sun.org.apache.bcel.internal.generic;




public class DSTORE extends StoreInstruction {

  DSTORE() {
    super(com.sun.org.apache.bcel.internal.Constants.DSTORE, com.sun.org.apache.bcel.internal.Constants.DSTORE_0);
  }


  public DSTORE(int n) {
    super(com.sun.org.apache.bcel.internal.Constants.DSTORE, com.sun.org.apache.bcel.internal.Constants.DSTORE_0, n);
  }


  public void accept(Visitor v) {
    super.accept(v);
    v.visitDSTORE(this);
  }
}
