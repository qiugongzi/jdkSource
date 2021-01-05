
package com.sun.org.apache.bcel.internal.util;



import java.io.*;

import java.util.Map;
import java.util.HashMap;

import com.sun.org.apache.bcel.internal.classfile.*;


public class SyntheticRepository implements Repository {
  private static final String DEFAULT_PATH = ClassPath.getClassPath();

  private static HashMap _instances = new HashMap(); private ClassPath _path = null;
  private HashMap   _loadedClasses = new HashMap(); private SyntheticRepository(ClassPath path) {
    _path = path;
  }

  public static SyntheticRepository getInstance() {
    return getInstance(ClassPath.SYSTEM_CLASS_PATH);
  }

  public static SyntheticRepository getInstance(ClassPath classPath) {
    SyntheticRepository rep = (SyntheticRepository)_instances.get(classPath);

    if(rep == null) {
      rep = new SyntheticRepository(classPath);
      _instances.put(classPath, rep);
    }

    return rep;
  }


  public void storeClass(JavaClass clazz) {
    _loadedClasses.put(clazz.getClassName(), clazz);
    clazz.setRepository(this);
 }


  public void removeClass(JavaClass clazz) {
    _loadedClasses.remove(clazz.getClassName());
  }


  public JavaClass findClass(String className) {
    return (JavaClass)_loadedClasses.get(className);
  }


  public JavaClass loadClass(String className)
    throws ClassNotFoundException
  {
    if(className == null || className.equals("")) {
      throw new IllegalArgumentException("Invalid class name " + className);
    }

    className = className.replace('/', '.'); try {
      return loadClass(_path.getInputStream(className), className);
    } catch(IOException e) {
      throw new ClassNotFoundException("Exception while looking for class " +
                                       className + ": " + e.toString());
    }
  }


  public JavaClass loadClass(Class clazz) throws ClassNotFoundException {
    String className = clazz.getName();
    String name      = className;
    int    i         = name.lastIndexOf('.');

    if(i > 0) {
      name = name.substring(i + 1);
    }

    return loadClass(clazz.getResourceAsStream(name + ".class"), className);
  }

  private JavaClass loadClass(InputStream is, String className)
    throws ClassNotFoundException
  {
    JavaClass clazz = findClass(className);

    if(clazz != null) {
      return clazz;
    }

    try {
      if(is != null) {
        ClassParser parser = new ClassParser(is, className);
        clazz = parser.parse();

        storeClass(clazz);

        return clazz;
      }
    } catch(IOException e) {
      throw new ClassNotFoundException("Exception while looking for class " +
                                       className + ": " + e.toString());
    }

    throw new ClassNotFoundException("SyntheticRepository could not load " +
                                     className);
  }


  public void clear() {
    _loadedClasses.clear();
  }
}
