
package com.sun.org.apache.bcel.internal.generic;


import com.sun.org.apache.bcel.internal.Constants;

public abstract class ConversionInstruction extends Instruction
  implements TypedInstruction, StackProducer, StackConsumer {

  ConversionInstruction() {}


  protected ConversionInstruction(short opcode) {
    super(opcode, (short)1);
  }


  public Type getType(ConstantPoolGen cp) {
    switch(opcode) {
    case Constants.D2I: case Constants.F2I: case Constants.L2I:
      return Type.INT;
    case Constants.D2F: case Constants.I2F: case Constants.L2F:
      return Type.FLOAT;
    case Constants.D2L: case Constants.F2L: case Constants.I2L:
      return Type.LONG;
    case Constants.F2D:  case Constants.I2D: case Constants.L2D:
        return Type.DOUBLE;
    case Constants.I2B:
      return Type.BYTE;
    case Constants.I2C:
      return Type.CHAR;
    case Constants.I2S:
      return Type.SHORT;

    default: throw new ClassGenException("Unknown type " + opcode);
    }
  }
}
