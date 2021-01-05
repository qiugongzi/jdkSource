
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public abstract class Constant implements Cloneable, Node, Serializable {

  protected byte tag;

  Constant(byte tag) { this.tag = tag; }


  public abstract void accept(Visitor v);

  public abstract void dump(DataOutputStream file) throws IOException;


  public final byte getTag() { return tag; }


  public String toString() {
    return Constants.CONSTANT_NAMES[tag] + "[" + tag + "]";
  }


  public Constant copy() {
    try {
      return (Constant)super.clone();
    } catch(CloneNotSupportedException e) {}

    return null;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }


  static final Constant readConstant(DataInputStream file)
    throws IOException, ClassFormatException
  {
    byte b = file.readByte(); switch(b) {
    case Constants.CONSTANT_Class:              return new ConstantClass(file);
    case Constants.CONSTANT_Fieldref:           return new ConstantFieldref(file);
    case Constants.CONSTANT_Methodref:          return new ConstantMethodref(file);
    case Constants.CONSTANT_InterfaceMethodref: return new
                                        ConstantInterfaceMethodref(file);
    case Constants.CONSTANT_String:             return new ConstantString(file);
    case Constants.CONSTANT_Integer:            return new ConstantInteger(file);
    case Constants.CONSTANT_Float:              return new ConstantFloat(file);
    case Constants.CONSTANT_Long:               return new ConstantLong(file);
    case Constants.CONSTANT_Double:             return new ConstantDouble(file);
    case Constants.CONSTANT_NameAndType:        return new ConstantNameAndType(file);
    case Constants.CONSTANT_Utf8:               return new ConstantUtf8(file);
    default:
      throw new ClassFormatException("Invalid byte tag in constant pool: " + b);
    }
  }
}
