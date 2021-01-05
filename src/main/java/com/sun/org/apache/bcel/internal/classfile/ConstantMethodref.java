
package com.sun.org.apache.bcel.internal.classfile;



import com.sun.org.apache.bcel.internal.Constants;
import java.io.*;


public final class ConstantMethodref extends ConstantCP {

  public ConstantMethodref(ConstantMethodref c) {
    super(Constants.CONSTANT_Methodref, c.getClassIndex(), c.getNameAndTypeIndex());
  }


  ConstantMethodref(DataInputStream file) throws IOException
  {
    super(Constants.CONSTANT_Methodref, file);
  }


  public ConstantMethodref(int class_index,
                           int name_and_type_index) {
    super(Constants.CONSTANT_Methodref, class_index, name_and_type_index);
  }


  public void accept(Visitor v) {
    v.visitConstantMethodref(this);
  }
}
