
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  java.io.*;


public final class InnerClasses extends Attribute {
  private InnerClass[] inner_classes;
  private int          number_of_classes;


  public InnerClasses(InnerClasses c) {
    this(c.getNameIndex(), c.getLength(), c.getInnerClasses(),
         c.getConstantPool());
  }


  public InnerClasses(int name_index, int length,
                      InnerClass[] inner_classes,
                      ConstantPool constant_pool)
  {
    super(Constants.ATTR_INNER_CLASSES, name_index, length, constant_pool);
    setInnerClasses(inner_classes);
  }


  InnerClasses(int name_index, int length, DataInputStream file,
               ConstantPool constant_pool) throws IOException
  {
    this(name_index, length, (InnerClass[])null, constant_pool);

    number_of_classes = file.readUnsignedShort();
    inner_classes = new InnerClass[number_of_classes];

    for(int i=0; i < number_of_classes; i++)
      inner_classes[i] = new InnerClass(file);
  }

  public void accept(Visitor v) {
    v.visitInnerClasses(this);
  }

  public final void dump(DataOutputStream file) throws IOException
  {
    super.dump(file);
    file.writeShort(number_of_classes);

    for(int i=0; i < number_of_classes; i++)
      inner_classes[i].dump(file);
  }


  public final InnerClass[] getInnerClasses() { return inner_classes; }


  public final void setInnerClasses(InnerClass[] inner_classes) {
    this.inner_classes = inner_classes;
    number_of_classes = (inner_classes == null)? 0 : inner_classes.length;
  }


  public final String toString() {
    StringBuffer buf = new StringBuffer();

    for(int i=0; i < number_of_classes; i++)
      buf.append(inner_classes[i].toString(constant_pool) + "\n");

    return buf.toString();
  }


  public Attribute copy(ConstantPool constant_pool) {
    InnerClasses c = (InnerClasses)clone();

    c.inner_classes = new InnerClass[number_of_classes];
    for(int i=0; i < number_of_classes; i++)
      c.inner_classes[i] = inner_classes[i].copy();

    c.constant_pool = constant_pool;
    return c;
  }
}
