
package com.sun.org.apache.bcel.internal.generic;




public class D2F extends ConversionInstruction {

  public D2F() {
    super(com.sun.org.apache.bcel.internal.Constants.D2F);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitConversionInstruction(this);
    v.visitD2F(this);
  }
}
