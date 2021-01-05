
package com.sun.org.apache.bcel.internal.generic;




public class L2F extends ConversionInstruction {
  public L2F() {
    super(com.sun.org.apache.bcel.internal.Constants.L2F);
  }



  public void accept(Visitor v) {
    v.visitTypedInstruction(this);
    v.visitStackProducer(this);
    v.visitStackConsumer(this);
    v.visitConversionInstruction(this);
    v.visitL2F(this);
  }
}
