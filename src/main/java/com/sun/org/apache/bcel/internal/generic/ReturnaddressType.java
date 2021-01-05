
package com.sun.org.apache.bcel.internal.generic;


import com.sun.org.apache.bcel.internal.Constants;
import java.util.Objects;


public class ReturnaddressType extends Type {

  public static final ReturnaddressType NO_TARGET = new ReturnaddressType();
  private InstructionHandle returnTarget;


  private ReturnaddressType(){
    super(Constants.T_ADDRESS, "<return address>");
  }


  public ReturnaddressType(InstructionHandle returnTarget) {
    super(Constants.T_ADDRESS, "<return address targeting "+returnTarget+">");
        this.returnTarget = returnTarget;
  }

  @Override
  public int hashCode() {
      return Objects.hashCode(this.returnTarget);
  }


  @Override
  public boolean equals(Object rat){
    if(!(rat instanceof ReturnaddressType))
      return false;

    return ((ReturnaddressType)rat).returnTarget.equals(this.returnTarget);
  }


  public InstructionHandle getTarget(){
    return returnTarget;
  }
}
