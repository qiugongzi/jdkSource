
package com.sun.org.apache.bcel.internal.util;



import java.util.Hashtable;
import java.io.*;
import com.sun.org.apache.bcel.internal.*;
import com.sun.org.apache.bcel.internal.classfile.*;


public class ClassLoader extends java.lang.ClassLoader {
  private Hashtable classes = new Hashtable(); private String[] ignored_packages = {
    "java.", "javax.", "sun."
  };
  private Repository repository = SyntheticRepository.getInstance();
  private java.lang.ClassLoader deferTo = ClassLoader.getSystemClassLoader();

  public ClassLoader() {
  }

  public ClassLoader(java.lang.ClassLoader deferTo) {
    this.deferTo = deferTo;
    this.repository = new ClassLoaderRepository(deferTo);
  }


  public ClassLoader(String[] ignored_packages) {
    addIgnoredPkgs(ignored_packages);
  }

  public ClassLoader(java.lang.ClassLoader deferTo, String [] ignored_packages) {
    this.deferTo = deferTo;
    this.repository = new ClassLoaderRepository(deferTo);

    addIgnoredPkgs(ignored_packages);
  }

  private void addIgnoredPkgs(String[] ignored_packages) {
    String[] new_p = new String[ignored_packages.length + this.ignored_packages.length];

    System.arraycopy(this.ignored_packages, 0, new_p, 0, this.ignored_packages.length);
    System.arraycopy(ignored_packages, 0, new_p, this.ignored_packages.length,
                     ignored_packages.length);

    this.ignored_packages = new_p;
  }

  protected Class loadClass(String class_name, boolean resolve)
    throws ClassNotFoundException
  {
    Class cl = null;


    if((cl=(Class)classes.get(class_name)) == null) {

      for(int i=0; i < ignored_packages.length; i++) {
        if(class_name.startsWith(ignored_packages[i])) {
          cl = deferTo.loadClass(class_name);
          break;
        }
      }

      if(cl == null) {
        JavaClass clazz = null;


        if(class_name.indexOf("$$BCEL$$") >= 0)
          clazz = createClass(class_name);
        else { if ((clazz = repository.loadClass(class_name)) != null) {
            clazz = modifyClass(clazz);
          }
          else
            throw new ClassNotFoundException(class_name);
        }

        if(clazz != null) {
          byte[] bytes  = clazz.getBytes();
          cl = defineClass(class_name, bytes, 0, bytes.length);
        } else cl = Class.forName(class_name);
      }

      if(resolve)
        resolveClass(cl);
    }

    classes.put(class_name, cl);

    return cl;
  }


  protected JavaClass modifyClass(JavaClass clazz) {
    return clazz;
  }


  protected JavaClass createClass(String class_name) {
    int    index     = class_name.indexOf("$$BCEL$$");
    String real_name = class_name.substring(index + 8);

    JavaClass clazz = null;
    try {
      byte[]      bytes  = Utility.decode(real_name, true);
      ClassParser parser = new ClassParser(new ByteArrayInputStream(bytes), "foo");

      clazz = parser.parse();
    } catch(Throwable e) {
      e.printStackTrace();
      return null;
    }

    ConstantPool cp = clazz.getConstantPool();

    ConstantClass cl = (ConstantClass)cp.getConstant(clazz.getClassNameIndex(),
                                                     Constants.CONSTANT_Class);
    ConstantUtf8 name = (ConstantUtf8)cp.getConstant(cl.getNameIndex(),
                                                     Constants.CONSTANT_Utf8);
    name.setBytes(class_name.replace('.', '/'));

    return clazz;
  }
}
