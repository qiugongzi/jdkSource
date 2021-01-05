
package com.sun.org.apache.bcel.internal.generic;




public class D2L extends ConversionInstruction {

  public D2L() {
    super(com.sun.org.apache.bcel.internal.Constants.D2L);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitConversionInstruction(this);
    v.visitD2L(this);
  }
}
