
package com.sun.org.apache.bcel.internal.classfile;




public interface AttributeReader {

  public Attribute createAttribute(int name_index,
                                   int length,
                                   java.io.DataInputStream file,
                                   ConstantPool constant_pool);
}
