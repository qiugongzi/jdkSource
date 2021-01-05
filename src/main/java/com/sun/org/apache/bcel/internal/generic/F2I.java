
package com.sun.org.apache.bcel.internal.generic;




public class F2I extends ConversionInstruction {

  public F2I() {
    super(com.sun.org.apache.bcel.internal.Constants.F2I);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitConversionInstruction(this);
    v.visitF2I(this);
  }
}
