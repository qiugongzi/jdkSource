
package com.sun.org.apache.bcel.internal.classfile;



import  com.sun.org.apache.bcel.internal.Constants;
import  com.sun.org.apache.bcel.internal.util.SyntheticRepository;
import  com.sun.org.apache.bcel.internal.util.ClassVector;
import  com.sun.org.apache.bcel.internal.util.ClassQueue;
import  com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;

import  java.io.*;
import  java.util.StringTokenizer;


public class JavaClass extends AccessFlags implements Cloneable, Node {
  private String       file_name;
  private String       package_name;
  private String       source_file_name = "<Unknown>";
  private int          class_name_index;
  private int          superclass_name_index;
  private String       class_name;
  private String       superclass_name;
  private int          major, minor;  private ConstantPool constant_pool; private int[]        interfaces;    private String[]     interface_names;
  private Field[]      fields;        private Method[]     methods;       private Attribute[]  attributes;    private byte         source = HEAP; public static final byte HEAP = 1;
  public static final byte FILE = 2;
  public static final byte ZIP  = 3;

  static boolean debug = false; static char    sep   = '/';   private transient com.sun.org.apache.bcel.internal.util.Repository repository =
    SyntheticRepository.getInstance();


  public JavaClass(int        class_name_index,
                   int        superclass_name_index,
                   String     file_name,
                   int        major,
                   int        minor,
                   int        access_flags,
                   ConstantPool constant_pool,
                   int[]      interfaces,
                   Field[]      fields,
                   Method[]     methods,
                   Attribute[]  attributes,
                   byte          source)
  {
    if(interfaces == null) interfaces = new int[0];
    if(attributes == null)
      this.attributes = new Attribute[0];
    if(fields == null)
      fields = new Field[0];
    if(methods == null)
      methods = new Method[0];

    this.class_name_index      = class_name_index;
    this.superclass_name_index = superclass_name_index;
    this.file_name             = file_name;
    this.major                 = major;
    this.minor                 = minor;
    this.access_flags          = access_flags;
    this.constant_pool         = constant_pool;
    this.interfaces            = interfaces;
    this.fields                = fields;
    this.methods               = methods;
    this.attributes            = attributes;
    this.source                = source;

    for(int i=0; i < attributes.length; i++) {
      if(attributes[i] instanceof SourceFile) {
        source_file_name = ((SourceFile)attributes[i]).getSourceFileName();
        break;
      }
    }


    class_name = constant_pool.getConstantString(class_name_index,
                                                 Constants.CONSTANT_Class);
    class_name = Utility.compactClassName(class_name, false);

    int index = class_name.lastIndexOf('.');
    if(index < 0)
      package_name = "";
    else
      package_name = class_name.substring(0, index);

    if(superclass_name_index > 0) { superclass_name = constant_pool.getConstantString(superclass_name_index,
                                                        Constants.CONSTANT_Class);
      superclass_name = Utility.compactClassName(superclass_name, false);
    }
    else
      superclass_name = "java.lang.Object";

    interface_names = new String[interfaces.length];
    for(int i=0; i < interfaces.length; i++) {
      String str = constant_pool.getConstantString(interfaces[i], Constants.CONSTANT_Class);
      interface_names[i] = Utility.compactClassName(str, false);
    }
  }


  public JavaClass(int        class_name_index,
                   int        superclass_name_index,
                   String     file_name,
                   int        major,
                   int        minor,
                   int        access_flags,
                   ConstantPool constant_pool,
                   int[]      interfaces,
                   Field[]      fields,
                   Method[]     methods,
                   Attribute[]  attributes) {
    this(class_name_index, superclass_name_index, file_name, major, minor, access_flags,
         constant_pool, interfaces, fields, methods, attributes, HEAP);
  }



  public void accept(Visitor v) {
    v.visitJavaClass(this);
  }


  static final void Debug(String str) {
    if(debug)
      System.out.println(str);
  }


  public void dump(File file) throws IOException
  {
    String parent = file.getParent();

    if(parent != null) {
      File dir = new File(parent);

      if(dir != null)
        dir.mkdirs();
    }

    dump(new DataOutputStream(new FileOutputStream(file)));
  }


  public void dump(String file_name) throws IOException
  {
    dump(new File(file_name));
  }


  public byte[] getBytes() {
    ByteArrayOutputStream s  = new ByteArrayOutputStream();
    DataOutputStream      ds = new DataOutputStream(s);

    try {
      dump(ds);
    } catch(IOException e) {
      e.printStackTrace();
    } finally {
      try { ds.close(); } catch(IOException e2) { e2.printStackTrace(); }
    }

    return s.toByteArray();
  }


  public void dump(OutputStream file) throws IOException {
    dump(new DataOutputStream(file));
  }


  public void dump(DataOutputStream file) throws IOException
  {
    file.writeInt(0xcafebabe);
    file.writeShort(minor);
    file.writeShort(major);

    constant_pool.dump(file);

    file.writeShort(access_flags);
    file.writeShort(class_name_index);
    file.writeShort(superclass_name_index);

    file.writeShort(interfaces.length);
    for(int i=0; i < interfaces.length; i++)
      file.writeShort(interfaces[i]);

    file.writeShort(fields.length);
    for(int i=0; i < fields.length; i++)
      fields[i].dump(file);

    file.writeShort(methods.length);
    for(int i=0; i < methods.length; i++)
      methods[i].dump(file);

    if(attributes != null) {
      file.writeShort(attributes.length);
      for(int i=0; i < attributes.length; i++)
        attributes[i].dump(file);
    }
    else
      file.writeShort(0);

    file.close();
  }


  public Attribute[] getAttributes() { return attributes; }


  public String getClassName()       { return class_name; }


  public String getPackageName()       { return package_name; }


  public int getClassNameIndex()   { return class_name_index; }


  public ConstantPool getConstantPool() { return constant_pool; }


  public Field[] getFields()         { return fields; }


  public String getFileName()        { return file_name; }


  public String[] getInterfaceNames()  { return interface_names; }


  public int[] getInterfaceIndices()     { return interfaces; }


  public int  getMajor()           { return major; }


  public Method[] getMethods()       { return methods; }


  public Method getMethod(java.lang.reflect.Method m) {
    for(int i = 0; i < methods.length; i++) {
      Method method = methods[i];

      if(m.getName().equals(method.getName()) &&
         (m.getModifiers() == method.getModifiers()) &&
         Type.getSignature(m).equals(method.getSignature())) {
        return method;
      }
    }

    return null;
  }


  public int  getMinor()           { return minor; }


  public String getSourceFileName()  { return source_file_name; }


  public String getSuperclassName()  { return superclass_name; }


  public int getSuperclassNameIndex() { return superclass_name_index; }

  static {
    String debug = null, sep = null;

    try {
      debug = SecuritySupport.getSystemProperty("JavaClass.debug");
      sep = SecuritySupport.getSystemProperty("file.separator");
    }
    catch (SecurityException e) {
        }

    if(debug != null)
      JavaClass.debug = new Boolean(debug).booleanValue();

    if(sep != null)
      try {
        JavaClass.sep = sep.charAt(0);
      } catch(StringIndexOutOfBoundsException e) {} }


  public void setAttributes(Attribute[] attributes) {
    this.attributes = attributes;
  }


  public void setClassName(String class_name) {
    this.class_name = class_name;
  }


  public void setClassNameIndex(int class_name_index) {
    this.class_name_index = class_name_index;
  }


  public void setConstantPool(ConstantPool constant_pool) {
    this.constant_pool = constant_pool;
  }


  public void setFields(Field[] fields) {
    this.fields = fields;
  }


  public void setFileName(String file_name) {
    this.file_name = file_name;
  }


  public void setInterfaceNames(String[] interface_names) {
    this.interface_names = interface_names;
  }


  public void setInterfaces(int[] interfaces) {
    this.interfaces = interfaces;
  }


  public void setMajor(int major) {
    this.major = major;
  }


  public void setMethods(Method[] methods) {
    this.methods = methods;
  }


  public void setMinor(int minor) {
    this.minor = minor;
  }


  public void setSourceFileName(String source_file_name) {
    this.source_file_name = source_file_name;
  }


  public void setSuperclassName(String superclass_name) {
    this.superclass_name = superclass_name;
  }


  public void setSuperclassNameIndex(int superclass_name_index) {
    this.superclass_name_index = superclass_name_index;
  }


  public String toString() {
    String access = Utility.accessToString(access_flags, true);
    access = access.equals("")? "" : (access + " ");

    StringBuffer buf = new StringBuffer(access +
                                        Utility.classOrInterface(access_flags) +
                                        " " +
                                        class_name + " extends " +
                                        Utility.compactClassName(superclass_name,
                                                                 false) + '\n');
    int size = interfaces.length;

    if(size > 0) {
      buf.append("implements\t\t");

      for(int i=0; i < size; i++) {
        buf.append(interface_names[i]);
        if(i < size - 1)
          buf.append(", ");
      }

      buf.append('\n');
    }

    buf.append("filename\t\t" + file_name + '\n');
    buf.append("compiled from\t\t" + source_file_name + '\n');
    buf.append("compiler version\t" + major + "." + minor + '\n');
    buf.append("access flags\t\t" + access_flags + '\n');
    buf.append("constant pool\t\t" + constant_pool.getLength() + " entries\n");
    buf.append("ACC_SUPER flag\t\t" + isSuper() + "\n");

    if(attributes.length > 0) {
      buf.append("\nAttribute(s):\n");
      for(int i=0; i < attributes.length; i++)
        buf.append(indent(attributes[i]));
    }

    if(fields.length > 0) {
      buf.append("\n" + fields.length + " fields:\n");
      for(int i=0; i < fields.length; i++)
        buf.append("\t" + fields[i] + '\n');
    }

    if(methods.length > 0) {
      buf.append("\n" + methods.length + " methods:\n");
      for(int i=0; i < methods.length; i++)
        buf.append("\t" + methods[i] + '\n');
    }

    return buf.toString();
  }

  private static final String indent(Object obj) {
    StringTokenizer tok = new StringTokenizer(obj.toString(), "\n");
    StringBuffer buf = new StringBuffer();

    while(tok.hasMoreTokens())
      buf.append("\t" + tok.nextToken() + "\n");

    return buf.toString();
  }


  public JavaClass copy() {
    JavaClass c = null;

    try {
      c = (JavaClass)clone();
    } catch(CloneNotSupportedException e) {}

    c.constant_pool   = constant_pool.copy();
    c.interfaces      = (int[])interfaces.clone();
    c.interface_names = (String[])interface_names.clone();

    c.fields = new Field[fields.length];
    for(int i=0; i < fields.length; i++)
      c.fields[i] = fields[i].copy(c.constant_pool);

    c.methods = new Method[methods.length];
    for(int i=0; i < methods.length; i++)
      c.methods[i] = methods[i].copy(c.constant_pool);

    c.attributes = new Attribute[attributes.length];
    for(int i=0; i < attributes.length; i++)
      c.attributes[i] = attributes[i].copy(c.constant_pool);

    return c;
  }

  public final boolean isSuper() {
    return (access_flags & Constants.ACC_SUPER) != 0;
  }

  public final boolean isClass() {
    return (access_flags & Constants.ACC_INTERFACE) == 0;
  }


  public final byte getSource() {
    return source;
  }




  public com.sun.org.apache.bcel.internal.util.Repository getRepository() {
    return repository;
  }


  public void setRepository(com.sun.org.apache.bcel.internal.util.Repository repository) {
    this.repository = repository;
  }


  public final boolean instanceOf(JavaClass super_class) {
    if(this.equals(super_class))
      return true;

    JavaClass[] super_classes = getSuperClasses();

    for(int i=0; i < super_classes.length; i++) {
      if(super_classes[i].equals(super_class)) {
        return true;
      }
    }

    if(super_class.isInterface()) {
      return implementationOf(super_class);
    }

    return false;
  }


  public boolean implementationOf(JavaClass inter) {
    if(!inter.isInterface()) {
      throw new IllegalArgumentException(inter.getClassName() + " is no interface");
    }

    if(this.equals(inter)) {
      return true;
    }

    JavaClass[] super_interfaces = getAllInterfaces();

    for(int i=0; i < super_interfaces.length; i++) {
      if(super_interfaces[i].equals(inter)) {
        return true;
      }
    }

    return false;
  }


  public JavaClass getSuperClass() {
    if("java.lang.Object".equals(getClassName())) {
      return null;
    }

    try {
      return repository.loadClass(getSuperclassName());
    } catch(ClassNotFoundException e) {
      System.err.println(e);
      return null;
    }
  }


  public JavaClass[] getSuperClasses() {
    JavaClass   clazz = this;
    ClassVector vec   = new ClassVector();

    for(clazz = clazz.getSuperClass(); clazz != null;
        clazz = clazz.getSuperClass())
    {
      vec.addElement(clazz);
    }

    return vec.toArray();
  }


  public JavaClass[] getInterfaces() {
    String[]    interfaces = getInterfaceNames();
    JavaClass[] classes    = new JavaClass[interfaces.length];

    try {
      for(int i = 0; i < interfaces.length; i++) {
        classes[i] = repository.loadClass(interfaces[i]);
      }
    } catch(ClassNotFoundException e) {
      System.err.println(e);
      return null;
    }

    return classes;
  }


  public JavaClass[] getAllInterfaces() {
    ClassQueue  queue = new ClassQueue();
    ClassVector vec   = new ClassVector();

    queue.enqueue(this);

    while(!queue.empty()) {
      JavaClass clazz = queue.dequeue();

      JavaClass   souper     = clazz.getSuperClass();
      JavaClass[] interfaces = clazz.getInterfaces();

      if(clazz.isInterface()) {
        vec.addElement(clazz);
      } else {
        if(souper != null) {
          queue.enqueue(souper);
        }
      }

      for(int i = 0; i < interfaces.length; i++) {
        queue.enqueue(interfaces[i]);
      }
    }

    return vec.toArray();
  }
}
