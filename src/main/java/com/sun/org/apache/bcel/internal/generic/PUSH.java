
package com.sun.org.apache.bcel.internal.generic;


import com.sun.org.apache.bcel.internal.Constants;
import java.io.*;


public final class PUSH
  implements CompoundInstruction, VariableLengthInstruction, InstructionConstants
{
  private Instruction instruction;


  public PUSH(ConstantPoolGen cp, int value) {
    if((value >= -1) && (value <= 5)) instruction = INSTRUCTIONS[Constants.ICONST_0 + value];
    else if((value >= -128) && (value <= 127)) instruction = new BIPUSH((byte)value);
    else if((value >= -32768) && (value <= 32767)) instruction = new SIPUSH((short)value);
    else instruction = new LDC(cp.addInteger(value));
  }


  public PUSH(ConstantPoolGen cp, boolean value) {
    instruction = INSTRUCTIONS[Constants.ICONST_0 + (value? 1 : 0)];
  }


  public PUSH(ConstantPoolGen cp, float value) {
    if(value == 0.0)
      instruction = FCONST_0;
    else if(value == 1.0)
      instruction = FCONST_1;
    else if(value == 2.0)
      instruction = FCONST_2;
    else instruction = new LDC(cp.addFloat(value));
  }


  public PUSH(ConstantPoolGen cp, long value) {
    if(value == 0)
      instruction = LCONST_0;
    else if(value == 1)
      instruction = LCONST_1;
    else instruction = new LDC2_W(cp.addLong(value));
  }


  public PUSH(ConstantPoolGen cp, double value) {
    if(value == 0.0)
      instruction = DCONST_0;
    else if(value == 1.0)
      instruction = DCONST_1;
    else instruction = new LDC2_W(cp.addDouble(value));
  }


  public PUSH(ConstantPoolGen cp, String value) {
    if(value == null)
      instruction = ACONST_NULL;
    else instruction = new LDC(cp.addString(value));
  }


  public PUSH(ConstantPoolGen cp, Number value) {
    if((value instanceof Integer) || (value instanceof Short) || (value instanceof Byte))
      instruction = new PUSH(cp, value.intValue()).instruction;
    else if(value instanceof Double)
      instruction = new PUSH(cp, value.doubleValue()).instruction;
    else if(value instanceof Float)
      instruction = new PUSH(cp, value.floatValue()).instruction;
    else if(value instanceof Long)
      instruction = new PUSH(cp, value.longValue()).instruction;
    else
      throw new ClassGenException("What's this: " + value);
  }


  public PUSH(ConstantPoolGen cp, Character value) {
    this(cp, (int)value.charValue());
  }


  public PUSH(ConstantPoolGen cp, Boolean value) {
    this(cp, value.booleanValue());
  }

  public final InstructionList getInstructionList() {
    return new InstructionList(instruction);
  }

  public final Instruction getInstruction() {
    return instruction;
  }


  public String toString() {
    return instruction.toString() + " (PUSH)";
  }
}
