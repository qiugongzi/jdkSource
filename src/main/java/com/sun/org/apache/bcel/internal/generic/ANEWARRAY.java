
package com.sun.org.apache.bcel.internal.generic;


import com.sun.org.apache.bcel.internal.ExceptionConstants;


public class ANEWARRAY extends CPInstruction
  implements LoadClass, AllocationInstruction, ExceptionThrower, StackProducer {

  ANEWARRAY() {}

  public ANEWARRAY(int index) {
    super(com.sun.org.apache.bcel.internal.Constants.ANEWARRAY, index);
  }

  public Class[] getExceptions(){
    Class[] cs = new Class[1 + ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length];

    System.arraycopy(ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION, 0,
                     cs, 0, ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length);
    cs[ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length] =
      ExceptionConstants.NEGATIVE_ARRAY_SIZE_EXCEPTION;
    return cs;
  }


  public void accept(Visitor v) {
    v.visitLoadClass(this);
    v.visitAllocationInstruction(this);
    v.visitExceptionThrower(this);
    v.visitStackProducer(this);
    v.visitTypedInstruction(this);
    v.visitCPInstruction(this);
    v.visitANEWARRAY(this);
  }

  public ObjectType getLoadClassType(ConstantPoolGen cpg) {
    Type t = getType(cpg);

    if (t instanceof ArrayType){
      t = ((ArrayType) t).getBasicType();
    }

    return (t instanceof ObjectType)? (ObjectType) t : null;
  }
}
