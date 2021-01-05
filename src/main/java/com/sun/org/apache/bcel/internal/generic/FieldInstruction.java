
package com.sun.org.apache.bcel.internal.generic;



import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantCP;
import com.sun.org.apache.bcel.internal.classfile.*;


public abstract class FieldInstruction extends FieldOrMethod
  implements TypedInstruction {

  FieldInstruction() {}


  protected FieldInstruction(short opcode, int index) {
    super(opcode, index);
  }


  public String toString(ConstantPool cp) {
    return com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[opcode] + " " +
      cp.constantToString(index, com.sun.org.apache.bcel.internal.Constants.CONSTANT_Fieldref);
  }


  protected int getFieldSize(ConstantPoolGen cpg) {
    return getType(cpg).getSize();
  }


  public Type getType(ConstantPoolGen cpg) {
    return getFieldType(cpg);
  }


  public Type getFieldType(ConstantPoolGen cpg) {
    return Type.getType(getSignature(cpg));
  }


  public String getFieldName(ConstantPoolGen cpg) {
    return getName(cpg);
  }
}
