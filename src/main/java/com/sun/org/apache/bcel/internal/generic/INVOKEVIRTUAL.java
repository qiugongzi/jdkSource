
package com.sun.org.apache.bcel.internal.generic;


import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.ExceptionConstants;


public class INVOKEVIRTUAL extends InvokeInstruction {

  INVOKEVIRTUAL() {}

  public INVOKEVIRTUAL(int index) {
    super(Constants.INVOKEVIRTUAL, index);
  }

  public Class[] getExceptions() {
    Class[] cs = new Class[4 + ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length];

    System.arraycopy(ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION, 0,
                     cs, 0, ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length);

    cs[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length+3] = ExceptionConstants.UNSATISFIED_LINK_ERROR;
    cs[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length+2] = ExceptionConstants.ABSTRACT_METHOD_ERROR;
    cs[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length+1] = ExceptionConstants.INCOMPATIBLE_CLASS_CHANGE_ERROR;
    cs[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length]   = ExceptionConstants.NULL_POINTER_EXCEPTION;

    return cs;
  }



  public void accept(Visitor v) {
    v.visitExceptionThrower(this);
    v.visitTypedInstruction(this);
    v.visitStackConsumer(this);
    v.visitStackProducer(this);
    v.visitLoadClass(this);
    v.visitCPInstruction(this);
    v.visitFieldOrMethod(this);
    v.visitInvokeInstruction(this);
    v.visitINVOKEVIRTUAL(this);
  }
}
