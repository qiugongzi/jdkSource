
package com.sun.org.apache.bcel.internal.classfile;



import com.sun.org.apache.bcel.internal.Constants;
import java.io.*;


public final class ConstantFieldref extends ConstantCP {

  public ConstantFieldref(ConstantFieldref c) {
    super(Constants.CONSTANT_Fieldref, c.getClassIndex(), c.getNameAndTypeIndex());
  }


  ConstantFieldref(DataInputStream file) throws IOException
  {
    super(Constants.CONSTANT_Fieldref, file);
  }


  public ConstantFieldref(int class_index,
                           int name_and_type_index) {
    super(Constants.CONSTANT_Fieldref, class_index, name_and_type_index);
  }


  public void accept(Visitor v) {
    v.visitConstantFieldref(this);
  }
}
