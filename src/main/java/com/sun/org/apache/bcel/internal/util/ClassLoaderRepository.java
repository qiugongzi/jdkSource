
package com.sun.org.apache.bcel.internal.util;



import java.io.*;

import java.util.Map;
import java.util.HashMap;

import com.sun.org.apache.bcel.internal.classfile.*;


public class ClassLoaderRepository
  implements Repository
{
  private java.lang.ClassLoader loader;
  private HashMap loadedClasses =
    new HashMap(); public ClassLoaderRepository( java.lang.ClassLoader loader ) {
    this.loader = loader;
  }


  public void storeClass( JavaClass clazz ) {
    loadedClasses.put( clazz.getClassName(),
                       clazz );
    clazz.setRepository( this );
  }


  public void removeClass(JavaClass clazz) {
    loadedClasses.remove(clazz.getClassName());
  }


  public JavaClass findClass( String className ) {
    if ( loadedClasses.containsKey( className )) {
      return (JavaClass) loadedClasses.get( className );
    } else {
      return null;
    }
  }


  public JavaClass loadClass( String className )
    throws ClassNotFoundException
  {
    String classFile = className.replace('.', '/');

    JavaClass RC = findClass( className );
    if (RC != null) { return RC; }

    try {
      InputStream is =
        loader.getResourceAsStream( classFile + ".class" );

      if(is == null) {
        throw new ClassNotFoundException(className + " not found.");
      }

      ClassParser parser = new ClassParser( is, className );
      RC = parser.parse();

      storeClass( RC );

      return RC;
    } catch (IOException e) {
      throw new ClassNotFoundException( e.toString() );
    }
  }

  public JavaClass loadClass(Class clazz) throws ClassNotFoundException {
    return loadClass(clazz.getName());
  }


  public void clear() {
    loadedClasses.clear();
  }
}
