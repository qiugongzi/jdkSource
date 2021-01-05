
package com.sun.org.apache.bcel.internal.util;



import com.sun.org.apache.bcel.internal.classfile.JavaClass;


public interface Repository extends java.io.Serializable {

  public void storeClass(JavaClass clazz);


  public void removeClass(JavaClass clazz);


  public JavaClass findClass(String className);


  public JavaClass loadClass(String className)
    throws java.lang.ClassNotFoundException;


  public JavaClass loadClass(Class clazz)
    throws java.lang.ClassNotFoundException;


  public void clear();
}
