
package com.sun.org.apache.bcel.internal.generic;




public class F2L extends ConversionInstruction {

  public F2L() {
    super(com.sun.org.apache.bcel.internal.Constants.F2L);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitConversionInstruction(this);
    v.visitF2L(this);
  }
}
