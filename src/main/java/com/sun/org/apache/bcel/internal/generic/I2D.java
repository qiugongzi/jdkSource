
package com.sun.org.apache.bcel.internal.generic;




public class I2D extends ConversionInstruction {

  public I2D() {
    super(com.sun.org.apache.bcel.internal.Constants.I2D);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitConversionInstruction(this);
    v.visitI2D(this);
  }
}
