
package com.sun.org.apache.bcel.internal.generic;




public class I2L extends ConversionInstruction {

  public I2L() {
    super(com.sun.org.apache.bcel.internal.Constants.I2L);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitConversionInstruction(this);
    v.visitI2L(this);
  }
}
