
package com.sun.org.apache.bcel.internal.generic;




public class I2C extends ConversionInstruction {

  public I2C() {
    super(com.sun.org.apache.bcel.internal.Constants.I2C);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitConversionInstruction(this);
    v.visitI2C(this);
  }
}
