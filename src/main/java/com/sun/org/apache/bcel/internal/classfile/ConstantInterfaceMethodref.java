
package com.sun.org.apache.bcel.internal.classfile;



import com.sun.org.apache.bcel.internal.Constants;
import java.io.*;


public final class ConstantInterfaceMethodref extends ConstantCP {

  public ConstantInterfaceMethodref(ConstantInterfaceMethodref c) {
    super(Constants.CONSTANT_InterfaceMethodref, c.getClassIndex(), c.getNameAndTypeIndex());
  }


  ConstantInterfaceMethodref(DataInputStream file) throws IOException
  {
    super(Constants.CONSTANT_InterfaceMethodref, file);
  }


  public ConstantInterfaceMethodref(int class_index,
                                    int name_and_type_index) {
    super(Constants.CONSTANT_InterfaceMethodref, class_index, name_and_type_index);
  }


  public void accept(Visitor v) {
    v.visitConstantInterfaceMethodref(this);
  }
}
