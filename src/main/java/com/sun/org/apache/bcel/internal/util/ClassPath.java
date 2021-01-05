
package com.sun.org.apache.bcel.internal.util;



import java.util.*;
import java.util.zip.*;
import java.io.*;


public class ClassPath implements Serializable {
  public static final ClassPath SYSTEM_CLASS_PATH = new ClassPath();

  private PathEntry[] paths;
  private String      class_path;


  public ClassPath(String class_path) {
    this.class_path = class_path;

    ArrayList vec = new ArrayList();

    for(StringTokenizer tok=new StringTokenizer(class_path,
                            SecuritySupport.getSystemProperty("path.separator"));
        tok.hasMoreTokens();)
    {
      String path = tok.nextToken();

      if(!path.equals("")) {
        File file = new File(path);

        try {
          if(SecuritySupport.getFileExists(file)) {
            if(file.isDirectory())
              vec.add(new Dir(path));
            else
              vec.add(new Zip(new ZipFile(file)));
          }
        } catch(IOException e) {
          System.err.println("CLASSPATH component " + file + ": " + e);
        }
      }
    }

    paths = new PathEntry[vec.size()];
    vec.toArray(paths);
  }


  public ClassPath() {
    this("");
  }


  public String toString() {
    return class_path;
  }

  public int hashCode() {
    return class_path.hashCode();
  }

  public boolean equals(Object o) {
    if(o instanceof ClassPath) {
      return class_path.equals(((ClassPath)o).class_path);
    }

    return false;
  }

  private static final void getPathComponents(String path, ArrayList list) {
    if(path != null) {
      StringTokenizer tok = new StringTokenizer(path, File.pathSeparator);

      while(tok.hasMoreTokens()) {
        String name = tok.nextToken();
        File   file = new File(name);

        if(SecuritySupport.getFileExists(file)) {
          list.add(name);
        }
      }
    }
  }


  public static final String getClassPath() {

    String class_path, boot_path, ext_path;

    try {
      class_path = SecuritySupport.getSystemProperty("java.class.path");
      boot_path  = SecuritySupport.getSystemProperty("sun.boot.class.path");
      ext_path   = SecuritySupport.getSystemProperty("java.ext.dirs");
    }
    catch (SecurityException e) {
        return "";
    }

    ArrayList list = new ArrayList();

    getPathComponents(class_path, list);
    getPathComponents(boot_path, list);

    ArrayList dirs = new ArrayList();
    getPathComponents(ext_path, dirs);

    for(Iterator e = dirs.iterator(); e.hasNext(); ) {
      File ext_dir = new File((String)e.next());
      String[] extensions = SecuritySupport.getFileList(ext_dir, new FilenameFilter() {
        public boolean accept(File dir, String name) {
          name = name.toLowerCase();
          return name.endsWith(".zip") || name.endsWith(".jar");
        }
      });

      if(extensions != null)
        for(int i=0; i < extensions.length; i++)
          list.add(ext_path + File.separatorChar + extensions[i]);
    }

    StringBuffer buf = new StringBuffer();

    for(Iterator e = list.iterator(); e.hasNext(); ) {
      buf.append((String)e.next());

      if(e.hasNext())
        buf.append(File.pathSeparatorChar);
    }

    return buf.toString().intern();
  }


  public InputStream getInputStream(String name) throws IOException {
    return getInputStream(name, ".class");
  }


  public InputStream getInputStream(String name, String suffix) throws IOException {
    InputStream is = null;

    try {
      is = getClass().getClassLoader().getResourceAsStream(name + suffix);
    } catch(Exception e) { }

    if(is != null)
      return is;

    return getClassFile(name, suffix).getInputStream();
  }


  public ClassFile getClassFile(String name, String suffix) throws IOException {
    for(int i=0; i < paths.length; i++) {
      ClassFile cf;

      if((cf = paths[i].getClassFile(name, suffix)) != null)
        return cf;
    }

    throw new IOException("Couldn't find: " + name + suffix);
  }


  public ClassFile getClassFile(String name) throws IOException {
    return getClassFile(name, ".class");
  }


  public byte[] getBytes(String name, String suffix) throws IOException {
    InputStream is = getInputStream(name, suffix);

    if(is == null)
      throw new IOException("Couldn't find: " + name + suffix);

    DataInputStream dis   = new DataInputStream(is);
    byte[]          bytes = new byte[is.available()];
    dis.readFully(bytes);
    dis.close(); is.close();

    return bytes;
  }


  public byte[] getBytes(String name) throws IOException {
    return getBytes(name, ".class");
  }


  public String getPath(String name) throws IOException {
    int    index  = name.lastIndexOf('.');
    String suffix = "";

    if(index > 0) {
      suffix = name.substring(index);
      name   = name.substring(0, index);
    }

    return getPath(name, suffix);
  }


  public String getPath(String name, String suffix) throws IOException {
    return getClassFile(name, suffix).getPath();
  }

  private static abstract class PathEntry implements Serializable {
    abstract ClassFile getClassFile(String name, String suffix) throws IOException;
  }


  public interface ClassFile {

    public abstract InputStream getInputStream() throws IOException;


    public abstract String getPath();


    public abstract String getBase();


    public abstract long getTime();


    public abstract long getSize();
  }

  private static class Dir extends PathEntry {
    private String dir;

    Dir(String d) { dir = d; }

    ClassFile getClassFile(String name, String suffix) throws IOException {
      final File file = new File(dir + File.separatorChar +
                                 name.replace('.', File.separatorChar) + suffix);

      return SecuritySupport.getFileExists(file)? new ClassFile() {
        public InputStream getInputStream() throws IOException { return new FileInputStream(file); }

        public String      getPath()        { try {
          return file.getCanonicalPath();
        } catch(IOException e) { return null; }

        }
        public long        getTime()        { return file.lastModified(); }
        public long        getSize()        { return file.length(); }
        public String getBase() {  return dir;  }

      } : null;
    }

    public String toString() { return dir; }
  }

  private static class Zip extends PathEntry {
    private ZipFile zip;

    Zip(ZipFile z) { zip = z; }

    ClassFile getClassFile(String name, String suffix) throws IOException {
      final ZipEntry entry = zip.getEntry(name.replace('.', '/') + suffix);

      return (entry != null)? new ClassFile() {
        public InputStream getInputStream() throws IOException { return zip.getInputStream(entry); }
        public String      getPath()        { return entry.toString(); }
        public long        getTime()        { return entry.getTime(); }
        public long        getSize()       { return entry.getSize(); }
        public String getBase() {
          return zip.getName();
        }
      } : null;
    }
  }
}
