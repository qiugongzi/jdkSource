
package com.sun.org.apache.bcel.internal.util;



import java.lang.reflect.*;


public class JavaWrapper {
  private java.lang.ClassLoader loader;

  private static java.lang.ClassLoader getClassLoader() {
    String s = SecuritySupport.getSystemProperty("bcel.classloader");

    if((s == null) || "".equals(s))
      s = "com.sun.org.apache.bcel.internal.util.ClassLoader";

    try {
      return (java.lang.ClassLoader)Class.forName(s).newInstance();
    } catch(Exception e) {
      throw new RuntimeException(e.toString());
    }
  }

  public JavaWrapper(java.lang.ClassLoader loader) {
    this.loader = loader;
  }

  public JavaWrapper() {
    this(getClassLoader());
  }


  public void runMain(String class_name, String[] argv) throws ClassNotFoundException
  {
    Class   cl    = loader.loadClass(class_name);
    Method method = null;

    try {
      method = cl.getMethod("_main",  new Class[] { argv.getClass() });


      int   m = method.getModifiers();
      Class r = method.getReturnType();

      if(!(Modifier.isPublic(m) && Modifier.isStatic(m)) ||
         Modifier.isAbstract(m) || (r != Void.TYPE))
        throw new NoSuchMethodException();
    } catch(NoSuchMethodException no) {
      System.out.println("In class " + class_name +
                         ": public static void _main(String[] argv) is not defined");
      return;
    }

    try {
      method.invoke(null, new Object[] { argv });
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }


  public static void _main(String[] argv) throws Exception {

    if(argv.length == 0) {
      System.out.println("Missing class name.");
      return;
    }

    String class_name = argv[0];
    String[] new_argv = new String[argv.length - 1];
    System.arraycopy(argv, 1, new_argv, 0, new_argv.length);

    JavaWrapper wrapper = new JavaWrapper();
    wrapper.runMain(class_name, new_argv);
  }
}
