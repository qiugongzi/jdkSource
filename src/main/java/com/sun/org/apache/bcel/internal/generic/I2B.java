
package com.sun.org.apache.bcel.internal.generic;




public class I2B extends ConversionInstruction {

  public I2B() {
    super(com.sun.org.apache.bcel.internal.Constants.I2B);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitConversionInstruction(this);
    v.visitI2B(this);
  }
}
