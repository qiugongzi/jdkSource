
package com.sun.org.apache.bcel.internal.classfile;


import  com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.Type;
import java.io.*;


public final class Field extends FieldOrMethod {

  public Field(Field c) {
    super(c);
  }


  Field(DataInputStream file, ConstantPool constant_pool)
       throws IOException, ClassFormatException
  {
    super(file, constant_pool);
  }


  public Field(int access_flags, int name_index, int signature_index,
               Attribute[] attributes, ConstantPool constant_pool)
  {
    super(access_flags, name_index, signature_index, attributes, constant_pool);
  }


  public void accept(Visitor v) {
    v.visitField(this);
  }


  public final ConstantValue getConstantValue() {
    for(int i=0; i < attributes_count; i++)
      if(attributes[i].getTag() == Constants.ATTR_CONSTANT_VALUE)
        return (ConstantValue)attributes[i];

    return null;
  }


  public final String toString() {
    String name, signature, access; access    = Utility.accessToString(access_flags);
    access    = access.equals("")? "" : (access + " ");
    signature = Utility.signatureToString(getSignature());
    name      = getName();

    StringBuffer  buf = new StringBuffer(access + signature + " " + name);
    ConstantValue cv  = getConstantValue();

    if(cv != null)
      buf.append(" = " + cv);

    for(int i=0; i < attributes_count; i++) {
      Attribute a = attributes[i];

      if(!(a instanceof ConstantValue))
        buf.append(" [" + a.toString() + "]");
    }

    return buf.toString();
  }


  public final Field copy(ConstantPool constant_pool) {
    return (Field)copy_(constant_pool);
  }


  public Type getType() {
    return Type.getReturnType(getSignature());
  }
}
