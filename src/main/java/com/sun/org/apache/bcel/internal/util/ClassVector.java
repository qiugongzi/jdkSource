
package com.sun.org.apache.bcel.internal.util;


import java.util.ArrayList;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;


public class ClassVector implements java.io.Serializable {
  protected ArrayList vec = new ArrayList();

  public void      addElement(JavaClass clazz) { vec.add(clazz); }
  public JavaClass elementAt(int index)        { return (JavaClass)vec.get(index); }
  public void      removeElementAt(int index)  { vec.remove(index); }

  public JavaClass[] toArray() {
    JavaClass[] classes = new JavaClass[vec.size()];
    vec.toArray(classes);
    return classes;
  }
}
