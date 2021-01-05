
package com.sun.org.apache.bcel.internal.generic;




public class D2I extends ConversionInstruction {

  public D2I() {
    super(com.sun.org.apache.bcel.internal.Constants.D2I);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitConversionInstruction(this);
    v.visitD2I(this);
  }
}
