
package com.sun.org.apache.bcel.internal.generic;




public class I2S extends ConversionInstruction {
  public I2S() {
    super(com.sun.org.apache.bcel.internal.Constants.I2S);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitConversionInstruction(this);
    v.visitI2S(this);
  }
}
