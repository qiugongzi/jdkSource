
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public class ConstantPool implements Cloneable, Node, Serializable {
  private int        constant_pool_count;
  private Constant[] constant_pool;


  public ConstantPool(Constant[] constant_pool)
  {
    setConstantPool(constant_pool);
  }


  ConstantPool(DataInputStream file) throws IOException, ClassFormatException
  {
    byte tag;

    constant_pool_count = file.readUnsignedShort();
    constant_pool       = new Constant[constant_pool_count];


    for(int i=1; i < constant_pool_count; i++) {
      constant_pool[i] = Constant.readConstant(file);


      tag = constant_pool[i].getTag();
      if((tag == Constants.CONSTANT_Double) || (tag == Constants.CONSTANT_Long))
        i++;
    }
  }


  public void accept(Visitor v) {
    v.visitConstantPool(this);
  }


  public String constantToString(Constant c)
       throws ClassFormatException
  {
    String   str;
    int      i;
    byte     tag = c.getTag();

    switch(tag) {
    case Constants.CONSTANT_Class:
      i   = ((ConstantClass)c).getNameIndex();
      c   = getConstant(i, Constants.CONSTANT_Utf8);
      str = Utility.compactClassName(((ConstantUtf8)c).getBytes(), false);
      break;

    case Constants.CONSTANT_String:
      i   = ((ConstantString)c).getStringIndex();
      c   = getConstant(i, Constants.CONSTANT_Utf8);
      str = "\"" + escape(((ConstantUtf8)c).getBytes()) + "\"";
      break;

    case Constants.CONSTANT_Utf8:    str = ((ConstantUtf8)c).getBytes();         break;
    case Constants.CONSTANT_Double:  str = "" + ((ConstantDouble)c).getBytes();  break;
    case Constants.CONSTANT_Float:   str = "" + ((ConstantFloat)c).getBytes();   break;
    case Constants.CONSTANT_Long:    str = "" + ((ConstantLong)c).getBytes();    break;
    case Constants.CONSTANT_Integer: str = "" + ((ConstantInteger)c).getBytes(); break;

    case Constants.CONSTANT_NameAndType:
      str = (constantToString(((ConstantNameAndType)c).getNameIndex(),
                              Constants.CONSTANT_Utf8) + " " +
             constantToString(((ConstantNameAndType)c).getSignatureIndex(),
                              Constants.CONSTANT_Utf8));
      break;

    case Constants.CONSTANT_InterfaceMethodref: case Constants.CONSTANT_Methodref:
    case Constants.CONSTANT_Fieldref:
      str = (constantToString(((ConstantCP)c).getClassIndex(),
                              Constants.CONSTANT_Class) + "." +
             constantToString(((ConstantCP)c).getNameAndTypeIndex(),
                              Constants.CONSTANT_NameAndType));
      break;

    default: throw new RuntimeException("Unknown constant type " + tag);
    }

    return str;
  }

  private static final String escape(String str) {
    int          len = str.length();
    StringBuffer buf = new StringBuffer(len + 5);
    char[]       ch  = str.toCharArray();

    for(int i=0; i < len; i++) {
      switch(ch[i]) {
      case '\n' : buf.append("\\n"); break;
      case '\r' : buf.append("\\r"); break;
      case '\t' : buf.append("\\t"); break;
      case '\b' : buf.append("\\b"); break;
      case '"'  : buf.append("\\\""); break;
      default: buf.append(ch[i]);
      }
    }

    return buf.toString();
  }



  public String constantToString(int index, byte tag)
       throws ClassFormatException
  {
    Constant c = getConstant(index, tag);
    return constantToString(c);
  }


  public void dump(DataOutputStream file) throws IOException
  {
    file.writeShort(constant_pool_count);

    for(int i=1; i < constant_pool_count; i++)
      if(constant_pool[i] != null)
        constant_pool[i].dump(file);
  }


  public Constant getConstant(int index) {
    if (index >= constant_pool.length || index < 0)
      throw new ClassFormatException("Invalid constant pool reference: " +
                                 index + ". Constant pool size is: " +
                                 constant_pool.length);
    return constant_pool[index];
  }


  public Constant getConstant(int index, byte tag)
       throws ClassFormatException
  {
    Constant c;

    c = getConstant(index);

    if(c == null)
      throw new ClassFormatException("Constant pool at index " + index + " is null.");

    if(c.getTag() == tag)
      return c;
    else
      throw new ClassFormatException("Expected class `" + Constants.CONSTANT_NAMES[tag] +
                                 "' at index " + index + " and got " + c);
  }


  public Constant[] getConstantPool() { return constant_pool;  }

  public String getConstantString(int index, byte tag)
       throws ClassFormatException
  {
    Constant c;
    int    i;

    c = getConstant(index, tag);


    switch(tag) {
    case Constants.CONSTANT_Class:  i = ((ConstantClass)c).getNameIndex();    break;
    case Constants.CONSTANT_String: i = ((ConstantString)c).getStringIndex(); break;
    default:
      throw new RuntimeException("getConstantString called with illegal tag " + tag);
    }

    c = getConstant(i, Constants.CONSTANT_Utf8);
    return ((ConstantUtf8)c).getBytes();
  }

  public int getLength()
  {
    return constant_pool_count;
  }


  public void setConstant(int index, Constant constant) {
    constant_pool[index] = constant;
  }


  public void setConstantPool(Constant[] constant_pool) {
    this.constant_pool = constant_pool;
    constant_pool_count = (constant_pool == null)? 0 : constant_pool.length;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();

    for(int i=1; i < constant_pool_count; i++)
      buf.append(i + ")" + constant_pool[i] + "\n");

    return buf.toString();
  }


  public ConstantPool copy() {
    ConstantPool c = null;

    try {
      c = (ConstantPool)clone();
    } catch(CloneNotSupportedException e) {}

    c.constant_pool = new Constant[constant_pool_count];

    for(int i=1; i < constant_pool_count; i++) {
      if(constant_pool[i] != null)
        c.constant_pool[i] = constant_pool[i].copy();
    }

    return c;
  }
}
