
package com.sun.org.apache.bcel.internal.util;


import java.util.LinkedList;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;


public class ClassQueue implements java.io.Serializable {
  protected LinkedList vec  = new LinkedList();

  public void enqueue(JavaClass clazz) { vec.addLast(clazz); }

  public JavaClass dequeue()                {
    return (JavaClass)vec.removeFirst();
  }

  public boolean empty() { return vec.isEmpty(); }

  public String toString() {
    return vec.toString();
  }
}
