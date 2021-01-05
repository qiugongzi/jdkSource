
package com.sun.org.apache.bcel.internal.classfile;



import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.*;


public abstract class Utility {
  private static int consumed_chars;
  private static boolean wide=false;

  public static final String accessToString(int access_flags) {
    return accessToString(access_flags, false);
  }


  public static final String accessToString(int access_flags,
                                            boolean for_class)
  {
    StringBuffer buf = new StringBuffer();

    int p = 0;
    for(int i=0; p < Constants.MAX_ACC_FLAG; i++) { p = pow2(i);

      if((access_flags & p) != 0) {

        if(for_class && ((p == Constants.ACC_SUPER) || (p == Constants.ACC_INTERFACE)))
          continue;

        buf.append(Constants.ACCESS_NAMES[i] + " ");
      }
    }

    return buf.toString().trim();
  }


  public static final String classOrInterface(int access_flags) {
    return ((access_flags & Constants.ACC_INTERFACE) != 0)? "interface" : "class";
  }


  public static final String codeToString(byte[] code,
                                          ConstantPool constant_pool,
                                          int index, int length, boolean verbose)
  {
    StringBuffer buf    = new StringBuffer(code.length * 20); ByteSequence stream = new ByteSequence(code);

    try {
      for(int i=0; i < index; i++) codeToString(stream, constant_pool, verbose);

      for(int i=0; stream.available() > 0; i++) {
        if((length < 0) || (i < length)) {
          String indices = fillup(stream.getIndex() + ":", 6, true, ' ');
          buf.append(indices + codeToString(stream, constant_pool, verbose) + '\n');
        }
      }
    } catch(IOException e) {
      System.out.println(buf.toString());
      e.printStackTrace();
      throw new ClassFormatException("Byte code error: " + e);
    }

    return buf.toString();
  }

  public static final String codeToString(byte[] code,
                                          ConstantPool constant_pool,
                                          int index, int length) {
    return codeToString(code, constant_pool, index, length, true);
  }


  public static final String codeToString(ByteSequence bytes,
                                          ConstantPool constant_pool, boolean verbose)
       throws IOException
  {
    short        opcode = (short)bytes.readUnsignedByte();
    int          default_offset=0, low, high, npairs;
    int          index, vindex, constant;
    int[]        match, jump_table;
    int          no_pad_bytes=0, offset;
    StringBuffer buf = new StringBuffer(Constants.OPCODE_NAMES[opcode]);


    if((opcode == Constants.TABLESWITCH) || (opcode == Constants.LOOKUPSWITCH)) {
      int remainder = bytes.getIndex() % 4;
      no_pad_bytes  = (remainder == 0)? 0 : 4 - remainder;

      for(int i=0; i < no_pad_bytes; i++) {
        byte b;

        if((b=bytes.readByte()) != 0)
          System.err.println("Warning: Padding byte != 0 in " +
                             Constants.OPCODE_NAMES[opcode] + ":" + b);
      }

      default_offset = bytes.readInt();
    }

    switch(opcode) {

    case Constants.TABLESWITCH:
      low  = bytes.readInt();
      high = bytes.readInt();

      offset = bytes.getIndex() - 12 - no_pad_bytes - 1;
      default_offset += offset;

      buf.append("\tdefault = " + default_offset + ", low = " + low +
                 ", high = " + high + "(");

      jump_table = new int[high - low + 1];
      for(int i=0; i < jump_table.length; i++) {
        jump_table[i] = offset + bytes.readInt();
        buf.append(jump_table[i]);

        if(i < jump_table.length - 1)
          buf.append(", ");
      }
      buf.append(")");

      break;


    case Constants.LOOKUPSWITCH: {

      npairs = bytes.readInt();
      offset = bytes.getIndex() - 8 - no_pad_bytes - 1;

      match      = new int[npairs];
      jump_table = new int[npairs];
      default_offset += offset;

      buf.append("\tdefault = " + default_offset + ", npairs = " + npairs +
                 " (");

      for(int i=0; i < npairs; i++) {
        match[i]      = bytes.readInt();

        jump_table[i] = offset + bytes.readInt();

        buf.append("(" + match[i] + ", " + jump_table[i] + ")");

        if(i < npairs - 1)
          buf.append(", ");
      }
      buf.append(")");
    }
    break;


    case Constants.GOTO:      case Constants.IFEQ:      case Constants.IFGE:      case Constants.IFGT:
    case Constants.IFLE:      case Constants.IFLT:      case Constants.JSR: case Constants.IFNE:
    case Constants.IFNONNULL: case Constants.IFNULL:    case Constants.IF_ACMPEQ:
    case Constants.IF_ACMPNE: case Constants.IF_ICMPEQ: case Constants.IF_ICMPGE: case Constants.IF_ICMPGT:
    case Constants.IF_ICMPLE: case Constants.IF_ICMPLT: case Constants.IF_ICMPNE:
      buf.append("\t\t#" + ((bytes.getIndex() - 1) + bytes.readShort()));
      break;


    case Constants.GOTO_W: case Constants.JSR_W:
      buf.append("\t\t#" + ((bytes.getIndex() - 1) + bytes.readInt()));
      break;


    case Constants.ALOAD:  case Constants.ASTORE: case Constants.DLOAD:  case Constants.DSTORE: case Constants.FLOAD:
    case Constants.FSTORE: case Constants.ILOAD:  case Constants.ISTORE: case Constants.LLOAD:  case Constants.LSTORE:
    case Constants.RET:
      if(wide) {
        vindex = bytes.readUnsignedShort();
        wide=false; }
      else
        vindex = bytes.readUnsignedByte();

      buf.append("\t\t%" + vindex);
      break;


    case Constants.WIDE:
      wide      = true;
      buf.append("\t(wide)");
      break;


    case Constants.NEWARRAY:
      buf.append("\t\t<" + Constants.TYPE_NAMES[bytes.readByte()] + ">");
      break;


    case Constants.GETFIELD: case Constants.GETSTATIC: case Constants.PUTFIELD: case Constants.PUTSTATIC:
      index = bytes.readUnsignedShort();
      buf.append("\t\t" +
                 constant_pool.constantToString(index, Constants.CONSTANT_Fieldref) +
                 (verbose? " (" + index + ")" : ""));
      break;


    case Constants.NEW:
    case Constants.CHECKCAST:
      buf.append("\t");
    case Constants.INSTANCEOF:
      index = bytes.readUnsignedShort();
      buf.append("\t<" + constant_pool.constantToString(index,
                                                        Constants.CONSTANT_Class) +
                 ">" + (verbose? " (" + index + ")" : ""));
      break;


    case Constants.INVOKESPECIAL: case Constants.INVOKESTATIC: case Constants.INVOKEVIRTUAL:
      index = bytes.readUnsignedShort();
      buf.append("\t" + constant_pool.constantToString(index,
                                                       Constants.CONSTANT_Methodref) +
                 (verbose? " (" + index + ")" : ""));
      break;

    case Constants.INVOKEINTERFACE:
      index = bytes.readUnsignedShort();
      int nargs = bytes.readUnsignedByte(); buf.append("\t" +
                 constant_pool.constantToString(index,
                                                Constants.CONSTANT_InterfaceMethodref) +
                 (verbose? " (" + index + ")\t" : "") + nargs + "\t" +
                 bytes.readUnsignedByte()); break;


    case Constants.LDC_W: case Constants.LDC2_W:
      index = bytes.readUnsignedShort();

      buf.append("\t\t" + constant_pool.constantToString
                 (index, constant_pool.getConstant(index).getTag()) +
                 (verbose? " (" + index + ")" : ""));
      break;

    case Constants.LDC:
      index = bytes.readUnsignedByte();

      buf.append("\t\t" +
                 constant_pool.constantToString
                 (index, constant_pool.getConstant(index).getTag()) +
                 (verbose? " (" + index + ")" : ""));
      break;


    case Constants.ANEWARRAY:
      index = bytes.readUnsignedShort();

      buf.append("\t\t<" + compactClassName(constant_pool.getConstantString
                                          (index, Constants.CONSTANT_Class), false) +
                 ">" + (verbose? " (" + index + ")": ""));
      break;


    case Constants.MULTIANEWARRAY: {
      index          = bytes.readUnsignedShort();
      int dimensions = bytes.readUnsignedByte();

      buf.append("\t<" + compactClassName(constant_pool.getConstantString
                                          (index, Constants.CONSTANT_Class), false) +
                 ">\t" + dimensions + (verbose? " (" + index + ")" : ""));
    }
    break;


    case Constants.IINC:
      if(wide) {
        vindex   = bytes.readUnsignedShort();
        constant = bytes.readShort();
        wide     = false;
      }
      else {
        vindex   = bytes.readUnsignedByte();
        constant = bytes.readByte();
      }
      buf.append("\t\t%" + vindex + "\t" + constant);
      break;

    default:
      if(Constants.NO_OF_OPERANDS[opcode] > 0) {
        for(int i=0; i < Constants.TYPE_OF_OPERANDS[opcode].length; i++) {
          buf.append("\t\t");
          switch(Constants.TYPE_OF_OPERANDS[opcode][i]) {
          case Constants.T_BYTE:  buf.append(bytes.readByte()); break;
          case Constants.T_SHORT: buf.append(bytes.readShort());       break;
          case Constants.T_INT:   buf.append(bytes.readInt());         break;

          default: System.err.println("Unreachable default case reached!");
            buf.setLength(0);
          }
        }
      }
    }

    return buf.toString();
  }

  public static final String codeToString(ByteSequence bytes, ConstantPool constant_pool)
    throws IOException
  {
    return codeToString(bytes, constant_pool, true);
  }


  public static final String compactClassName(String str) {
    return compactClassName(str, true);
  }


  public static final String compactClassName(String str,
                                              String prefix,
                                              boolean chopit)
  {
    int len = prefix.length();

    str = str.replace('/', '.'); if(chopit) {
      if(str.startsWith(prefix) &&
         (str.substring(len).indexOf('.') == -1))
        str = str.substring(len);
    }

    return str;
  }


  public static final String compactClassName(String str, boolean chopit) {
    return compactClassName(str, "java.lang.", chopit);
  }

  private static final boolean is_digit(char ch) {
    return (ch >= '0') && (ch <= '9');
  }

  private static final boolean is_space(char ch) {
    return (ch == ' ') || (ch == '\t') || (ch == '\r') || (ch == '\n');
  }


  public static final int setBit(int flag, int i) {
    return flag | pow2(i);
  }


  public static final int clearBit(int flag, int i) {
    int bit = pow2(i);
    return (flag & bit) == 0? flag : flag ^ bit;
  }


  public static final boolean isSet(int flag, int i) {
    return (flag & pow2(i)) != 0;
  }


  public final static String methodTypeToSignature(String ret, String[] argv)
    throws ClassFormatException
  {
    StringBuffer buf = new StringBuffer("(");
    String       str;

    if(argv != null)
      for(int i=0; i < argv.length; i++) {
        str = getSignature(argv[i]);

        if(str.endsWith("V")) throw new ClassFormatException("Invalid type: " + argv[i]);

        buf.append(str);
      }

    str = getSignature(ret);

    buf.append(")" + str);

    return buf.toString();
  }


  public static final String[] methodSignatureArgumentTypes(String signature)
    throws ClassFormatException
  {
    return methodSignatureArgumentTypes(signature, true);
  }


  public static final String[] methodSignatureArgumentTypes(String signature,
                                                            boolean chopit)
    throws ClassFormatException
  {
    ArrayList vec = new ArrayList();
    int       index;
    String[]  types;

    try { if(signature.charAt(0) != '(')
        throw new ClassFormatException("Invalid method signature: " + signature);

      index = 1; while(signature.charAt(index) != ')') {
        vec.add(signatureToString(signature.substring(index), chopit));
        index += consumed_chars; }
    } catch(StringIndexOutOfBoundsException e) { throw new ClassFormatException("Invalid method signature: " + signature);
    }

    types = new String[vec.size()];
    vec.toArray(types);
    return types;
  }

  public static final String methodSignatureReturnType(String signature)
       throws ClassFormatException
  {
    return methodSignatureReturnType(signature, true);
  }

  public static final String methodSignatureReturnType(String signature,
                                                       boolean chopit)
       throws ClassFormatException
  {
    int    index;
    String type;

    try {
      index = signature.lastIndexOf(')') + 1;
      type = signatureToString(signature.substring(index), chopit);
    } catch(StringIndexOutOfBoundsException e) { throw new ClassFormatException("Invalid method signature: " + signature);
    }

    return type;
  }


  public static final String methodSignatureToString(String signature,
                                                     String name,
                                                     String access) {
    return methodSignatureToString(signature, name, access, true);
  }

  public static final String methodSignatureToString(String signature,
                                                     String name,
                                                     String access,
                                                     boolean chopit) {
    return methodSignatureToString(signature, name, access, chopit, null);
  }


  public static final String methodSignatureToString(String signature,
                                                     String name,
                                                     String access,
                                                     boolean chopit,
                                                     LocalVariableTable vars)
    throws ClassFormatException
  {
    StringBuffer buf = new StringBuffer("(");
    String       type;
    int          index;
    int          var_index = (access.indexOf("static") >= 0)? 0 : 1;

    try { if(signature.charAt(0) != '(')
        throw new ClassFormatException("Invalid method signature: " + signature);

      index = 1; while(signature.charAt(index) != ')') {
        String param_type = signatureToString(signature.substring(index), chopit);
        buf.append(param_type);

        if(vars != null) {
          LocalVariable l = vars.getLocalVariable(var_index);

          if(l != null)
            buf.append(" " + l.getName());
        } else
          buf.append(" arg" + var_index);

        if("double".equals(param_type) || "long".equals(param_type))
          var_index += 2;
        else
          var_index++;

        buf.append(", ");
        index += consumed_chars; }

      index++; type = signatureToString(signature.substring(index), chopit);

    } catch(StringIndexOutOfBoundsException e) { throw new ClassFormatException("Invalid method signature: " + signature);
    }

    if(buf.length() > 1) buf.setLength(buf.length() - 2);

    buf.append(")");

    return access + ((access.length() > 0)? " " : "") + type + " " + name + buf.toString();
  }

  private static final int pow2(int n) {
    return 1 << n;
  }


  public static final String replace(String str, String old, String new_) {
    int          index, old_index;
    StringBuffer buf = new StringBuffer();

    try {
      if((index = str.indexOf(old)) != -1) { old_index = 0;                       while((index = str.indexOf(old, old_index)) != -1) {
          buf.append(str.substring(old_index, index)); buf.append(new_);                            old_index = index + old.length(); }

        buf.append(str.substring(old_index)); str = buf.toString();
      }
    } catch(StringIndexOutOfBoundsException e) { System.err.println(e);
    }

    return str;
  }


  public static final String signatureToString(String signature) {
    return signatureToString(signature, true);
  }


  public static final String signatureToString(String signature,
                                               boolean chopit)
  {
    consumed_chars = 1; try {
      switch(signature.charAt(0)) {
      case 'B' : return "byte";
      case 'C' : return "char";
      case 'D' : return "double";
      case 'F' : return "float";
      case 'I' : return "int";
      case 'J' : return "long";

      case 'L' : { int    index = signature.indexOf(';'); if(index < 0)
          throw new ClassFormatException("Invalid signature: " + signature);

        consumed_chars = index + 1; return compactClassName(signature.substring(1, index), chopit);
      }

      case 'S' : return "short";
      case 'Z' : return "boolean";

      case '[' : { int          n;
        StringBuffer buf, brackets;
        String       type;
        char         ch;
        int          consumed_chars; brackets = new StringBuffer(); for(n=0; signature.charAt(n) == '['; n++)
          brackets.append("[]");

        consumed_chars = n; type = signatureToString(signature.substring(n), chopit);

        Utility.consumed_chars += consumed_chars;
        return type + brackets.toString();
      }

      case 'V' : return "void";

      default  : throw new ClassFormatException("Invalid signature: `" +
                                            signature + "'");
      }
    } catch(StringIndexOutOfBoundsException e) { throw new ClassFormatException("Invalid signature: " + e + ":" + signature);
    }
  }


  public static String getSignature(String type) {
    StringBuffer buf        = new StringBuffer();
    char[]       chars      = type.toCharArray();
    boolean      char_found = false, delim = false;
    int          index      = -1;

  loop:
    for(int i=0; i < chars.length; i++) {
      switch(chars[i]) {
      case ' ': case '\t': case '\n': case '\r': case '\f':
        if(char_found)
          delim = true;
        break;

      case '[':
        if(!char_found)
          throw new RuntimeException("Illegal type: " + type);

        index = i;
        break loop;

      default:
        char_found = true;
        if(!delim)
          buf.append(chars[i]);
      }
    }

    int brackets = 0;

    if(index > 0)
      brackets = countBrackets(type.substring(index));

    type = buf.toString();
    buf.setLength(0);

    for(int i=0; i < brackets; i++)
      buf.append('[');

    boolean found = false;

    for(int i=Constants.T_BOOLEAN; (i <= Constants.T_VOID) && !found; i++) {
      if(Constants.TYPE_NAMES[i].equals(type)) {
        found = true;
        buf.append(Constants.SHORT_TYPE_NAMES[i]);
      }
    }

    if(!found) buf.append('L' + type.replace('.', '/') + ';');

    return buf.toString();
  }

  private static int countBrackets(String brackets) {
    char[]  chars = brackets.toCharArray();
    int     count = 0;
    boolean open  = false;

    for(int i=0; i<chars.length; i++) {
      switch(chars[i]) {
      case '[':
        if(open)
          throw new RuntimeException("Illegally nested brackets:" + brackets);
        open = true;
        break;

      case ']':
        if(!open)
          throw new RuntimeException("Illegally nested brackets:" + brackets);
        open = false;
        count++;
        break;

      default:
        }
    }

    if(open)
      throw new RuntimeException("Illegally nested brackets:" + brackets);

    return count;
  }


  public static final byte typeOfMethodSignature(String signature)
    throws ClassFormatException
  {
    int index;

    try {
      if(signature.charAt(0) != '(')
        throw new ClassFormatException("Invalid method signature: " + signature);

      index = signature.lastIndexOf(')') + 1;
      return typeOfSignature(signature.substring(index));
    } catch(StringIndexOutOfBoundsException e) {
      throw new ClassFormatException("Invalid method signature: " + signature);
    }
  }


  public static final byte typeOfSignature(String signature)
    throws ClassFormatException
  {
    try {
      switch(signature.charAt(0)) {
      case 'B' : return Constants.T_BYTE;
      case 'C' : return Constants.T_CHAR;
      case 'D' : return Constants.T_DOUBLE;
      case 'F' : return Constants.T_FLOAT;
      case 'I' : return Constants.T_INT;
      case 'J' : return Constants.T_LONG;
      case 'L' : return Constants.T_REFERENCE;
      case '[' : return Constants.T_ARRAY;
      case 'V' : return Constants.T_VOID;
      case 'Z' : return Constants.T_BOOLEAN;
      case 'S' : return Constants.T_SHORT;
      default:
        throw new ClassFormatException("Invalid method signature: " + signature);
      }
    } catch(StringIndexOutOfBoundsException e) {
      throw new ClassFormatException("Invalid method signature: " + signature);
    }
  }


  public static short searchOpcode(String name) {
    name = name.toLowerCase();

    for(short i=0; i < Constants.OPCODE_NAMES.length; i++)
      if(Constants.OPCODE_NAMES[i].equals(name))
        return i;

    return -1;
  }


  private static final short byteToShort(byte b) {
    return (b < 0)? (short)(256 + b) : (short)b;
  }


  public static final String toHexString(byte[] bytes) {
    StringBuffer buf = new StringBuffer();

    for(int i=0; i < bytes.length; i++) {
      short  b   = byteToShort(bytes[i]);
      String hex = Integer.toString(b, 0x10);

      if(b < 0x10) buf.append('0');

      buf.append(hex);

      if(i < bytes.length - 1)
        buf.append(' ');
    }

    return buf.toString();
  }


  public static final String format(int i, int length, boolean left_justify, char fill) {
    return fillup(Integer.toString(i), length, left_justify, fill);
  }


  public static final String fillup(String str, int length, boolean left_justify, char fill) {
    int    len = length - str.length();
    char[] buf = new char[(len < 0)? 0 : len];

    for(int j=0; j < buf.length; j++)
      buf[j] = fill;

    if(left_justify)
      return str + new String(buf);
    else
      return new String(buf) + str;
  }

  static final boolean equals(byte[] a, byte[] b) {
    int size;

    if((size=a.length) != b.length)
      return false;

    for(int i=0; i < size; i++)
      if(a[i] != b[i])
        return false;

    return true;
  }

  public static final void printArray(PrintStream out, Object[] obj) {
    out.println(printArray(obj, true));
  }

  public static final void printArray(PrintWriter out, Object[] obj) {
    out.println(printArray(obj, true));
  }

  public static final String printArray(Object[] obj) {
    return printArray(obj, true);
  }

  public static final String printArray(Object[] obj, boolean braces) {
    return printArray(obj, braces, false);
  }

  public static final String printArray(Object[] obj, boolean braces,
                                        boolean quote) {
    if(obj == null)
      return null;

    StringBuffer buf = new StringBuffer();
    if(braces)
      buf.append('{');

    for(int i=0; i < obj.length; i++) {
      if(obj[i] != null) {
        buf.append((quote? "\"" : "") + obj[i].toString() + (quote? "\"" : ""));
      } else {
        buf.append("null");
      }

      if(i < obj.length - 1) {
        buf.append(", ");
      }
    }

    if(braces)
      buf.append('}');

    return buf.toString();
  }


  public static boolean isJavaIdentifierPart(char ch) {
    return ((ch >= 'a') && (ch <= 'z')) ||
      ((ch >= 'A') && (ch <= 'Z')) ||
      ((ch >= '0') && (ch <= '9')) ||
      (ch == '_');
  }


  public static String encode(byte[] bytes, boolean compress) throws IOException {
    if(compress) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      GZIPOutputStream      gos  = new GZIPOutputStream(baos);

      gos.write(bytes, 0, bytes.length);
      gos.close();
      baos.close();

      bytes = baos.toByteArray();
    }

    CharArrayWriter caw = new CharArrayWriter();
    JavaWriter      jw  = new JavaWriter(caw);

    for(int i=0; i < bytes.length; i++) {
      int in = bytes[i] & 0x000000ff; jw.write(in);
    }

    return caw.toString();
  }


  public static byte[] decode(String s, boolean uncompress) throws IOException {
    char[] chars = s.toCharArray();

    CharArrayReader car = new CharArrayReader(chars);
    JavaReader      jr  = new JavaReader(car);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    int ch;

    while((ch = jr.read()) >= 0) {
      bos.write(ch);
    }

    bos.close();
    car.close();
    jr.close();

    byte[] bytes = bos.toByteArray();

    if(uncompress) {
      GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));

      byte[] tmp   = new byte[bytes.length * 3]; int    count = 0;
      int    b;

      while((b = gis.read()) >= 0)
        tmp[count++] = (byte)b;

      bytes = new byte[count];
      System.arraycopy(tmp, 0, bytes, 0, count);
    }

    return bytes;
  }

  private static final int   FREE_CHARS  = 48;
  private static       int[] CHAR_MAP    = new int[FREE_CHARS];
  private static       int[] MAP_CHAR    = new int[256]; private static final char  ESCAPE_CHAR = '$';

  static {
    int j = 0, k = 0;
    for(int i='A'; i <= 'Z'; i++) {
      CHAR_MAP[j] = i;
      MAP_CHAR[i] = j;
      j++;
    }

    for(int i='g'; i <= 'z'; i++) {
      CHAR_MAP[j] = i;
      MAP_CHAR[i] = j;
      j++;
    }

    CHAR_MAP[j]   = '$';
    MAP_CHAR['$'] = j;
    j++;

    CHAR_MAP[j]   = '_';
    MAP_CHAR['_'] = j;
  }


  private static class JavaReader extends FilterReader {
    public JavaReader(Reader in) {
      super(in);
    }

    public int read() throws IOException {
      int b = in.read();

      if(b != ESCAPE_CHAR) {
        return b;
      } else {
        int i = in.read();

        if(i < 0)
          return -1;

        if(((i >= '0') && (i <= '9')) || ((i >= 'a') && (i <= 'f'))) { int j = in.read();

          if(j < 0)
            return -1;

          char[] tmp = { (char)i, (char)j };
          int    s   = Integer.parseInt(new String(tmp), 16);

          return s;
        } else { return MAP_CHAR[i];
        }
      }
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
      for(int i=0; i < len; i++)
        cbuf[off + i] = (char)read();

      return len;
    }
  }


  private static class JavaWriter extends FilterWriter {
    public JavaWriter(Writer out) {
      super(out);
    }

    public void write(int b) throws IOException {
      if(isJavaIdentifierPart((char)b) && (b != ESCAPE_CHAR)) {
        out.write(b);
      } else {
        out.write(ESCAPE_CHAR); if(b >= 0 && b < FREE_CHARS) {
          out.write(CHAR_MAP[b]);
        } else { char[] tmp = Integer.toHexString(b).toCharArray();

          if(tmp.length == 1) {
            out.write('0');
            out.write(tmp[0]);
          } else {
            out.write(tmp[0]);
            out.write(tmp[1]);
          }
        }
      }
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
      for(int i=0; i < len; i++)
        write(cbuf[off + i]);
    }

    public void write(String str, int off, int len) throws IOException {
      write(str.toCharArray(), off, len);
    }
  }


  public static final String convertString(String label) {
    char[]       ch  = label.toCharArray();
    StringBuffer buf = new StringBuffer();

    for(int i=0; i < ch.length; i++) {
      switch(ch[i]) {
      case '\n':
        buf.append("\\n"); break;
      case '\r':
        buf.append("\\r"); break;
      case '\"':
        buf.append("\\\""); break;
      case '\'':
        buf.append("\\'"); break;
      case '\\':
        buf.append("\\\\"); break;
      default:
        buf.append(ch[i]); break;
      }
    }

    return buf.toString();
  }
}
