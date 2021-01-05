
package com.sun.org.apache.bcel.internal.generic;



import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import java.io.*;
import com.sun.org.apache.bcel.internal.util.ByteSequence;


public abstract class Instruction implements Cloneable, Serializable {
  protected short length = 1;  protected short opcode = -1; private static InstructionComparator cmp = InstructionComparator.DEFAULT;


  Instruction() {}

  public Instruction(short opcode, short length) {
    this.length = length;
    this.opcode = opcode;
  }


  public void dump(DataOutputStream out) throws IOException {
    out.writeByte(opcode); }


  public String getName() {
    return Constants.OPCODE_NAMES[opcode];
  }


  public String toString(boolean verbose) {
    if(verbose)
      return getName() + "[" + opcode + "](" + length + ")";
    else
      return getName();
  }


  public String toString() {
    return toString(true);
  }


  public String toString(ConstantPool cp) {
    return toString(false);
  }


  public Instruction copy() {
    Instruction i = null;

    if(InstructionConstants.INSTRUCTIONS[this.getOpcode()] != null)
      i = this;
    else {
      try {
        i = (Instruction)clone();
      } catch(CloneNotSupportedException e) {
        System.err.println(e);
      }
    }

    return i;
  }


  protected void initFromFile(ByteSequence bytes, boolean wide)
    throws IOException
  {}


  public static final Instruction readInstruction(ByteSequence bytes)
    throws IOException
  {
    boolean     wide   = false;
    short       opcode = (short)bytes.readUnsignedByte();
    Instruction obj    = null;

    if(opcode == Constants.WIDE) { wide = true;
      opcode  = (short)bytes.readUnsignedByte();
    }

    if(InstructionConstants.INSTRUCTIONS[opcode] != null)
      return InstructionConstants.INSTRUCTIONS[opcode]; Class clazz;

    try {
      clazz = Class.forName(className(opcode));
    } catch (ClassNotFoundException cnfe){
      throw new ClassGenException("Illegal opcode detected.");
    }

    try {
      obj = (Instruction)clazz.newInstance();

      if(wide && !((obj instanceof LocalVariableInstruction) ||
                   (obj instanceof IINC) ||
                   (obj instanceof RET)))
        throw new Exception("Illegal opcode after wide: " + opcode);

      obj.setOpcode(opcode);
      obj.initFromFile(bytes, wide); } catch(Exception e) { throw new ClassGenException(e.toString()); }

    return obj;
  }

  private static final String className(short opcode) {
    String name = Constants.OPCODE_NAMES[opcode].toUpperCase();


    try {
      int  len = name.length();
      char ch1 = name.charAt(len - 2), ch2 = name.charAt(len - 1);

      if((ch1 == '_') && (ch2 >= '0')  && (ch2 <= '5'))
        name = name.substring(0, len - 2);

      if(name.equals("ICONST_M1")) name = "ICONST";
    } catch(StringIndexOutOfBoundsException e) { System.err.println(e); }

    return "com.sun.org.apache.bcel.internal.generic." + name;
  }


  public int consumeStack(ConstantPoolGen cpg) {
    return Constants.CONSUME_STACK[opcode];
  }


  public int produceStack(ConstantPoolGen cpg) {
    return Constants.PRODUCE_STACK[opcode];
  }


  public short getOpcode()    { return opcode; }


  public int getLength()   { return length; }


  private void setOpcode(short opcode) { this.opcode = opcode; }


  void dispose() {}


  public abstract void accept(Visitor v);


  public static InstructionComparator getComparator() { return cmp; }


  public static void setComparator(InstructionComparator c) { cmp = c; }


  public boolean equals(Object that) {
    return (that instanceof Instruction)?
      cmp.equals(this, (Instruction)that) : false;
  }
}
