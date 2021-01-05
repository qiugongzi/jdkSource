
package com.sun.org.apache.bcel.internal.util;



import com.sun.org.apache.bcel.internal.classfile.*;
import java.io.*;
import java.util.BitSet;


final class CodeHTML implements com.sun.org.apache.bcel.internal.Constants {
  private String        class_name;     private Method[]      methods;        private PrintWriter   file;           private BitSet        goto_set;
  private ConstantPool  constant_pool;
  private ConstantHTML  constant_html;
  private static boolean wide=false;

  CodeHTML(String dir, String class_name,
           Method[] methods, ConstantPool constant_pool,
           ConstantHTML constant_html) throws IOException
  {
    this.class_name     = class_name;
    this.methods        = methods;
    this.constant_pool  = constant_pool;
    this.constant_html = constant_html;

    file = new PrintWriter(new FileOutputStream(dir + class_name + "_code.html"));
    file.println("<HTML><BODY BGCOLOR=\"#C0C0C0\">");

    for(int i=0; i < methods.length; i++)
      writeMethod(methods[i], i);

    file.println("</BODY></HTML>");
    file.close();
  }


  private final String codeToHTML(ByteSequence bytes, int method_number)
       throws IOException
  {
    short        opcode = (short)bytes.readUnsignedByte();
    StringBuffer buf;
    String       name, signature;
    int          default_offset=0, low, high;
    int          index, class_index, vindex, constant;
    int[]        jump_table;
    int          no_pad_bytes=0, offset;

    buf = new StringBuffer("<TT>" + OPCODE_NAMES[opcode] + "</TT></TD><TD>");


    if((opcode == TABLESWITCH) || (opcode == LOOKUPSWITCH)) {
      int remainder = bytes.getIndex() % 4;
      no_pad_bytes  = (remainder == 0)? 0 : 4 - remainder;

      for(int i=0; i < no_pad_bytes; i++)
        bytes.readByte();

      default_offset = bytes.readInt();
    }

    switch(opcode) {
    case TABLESWITCH:
      low  = bytes.readInt();
      high = bytes.readInt();

      offset = bytes.getIndex() - 12 - no_pad_bytes - 1;
      default_offset += offset;

      buf.append("<TABLE BORDER=1><TR>");

      jump_table = new int[high - low + 1];
      for(int i=0; i < jump_table.length; i++) {
        jump_table[i] = offset + bytes.readInt();

        buf.append("<TH>" + (low + i) + "</TH>");
      }
      buf.append("<TH>default</TH></TR>\n<TR>");

      for(int i=0; i < jump_table.length; i++)
        buf.append("<TD><A HREF=\"#code" + method_number + "@" +
                   jump_table[i] + "\">" + jump_table[i] + "</A></TD>");
      buf.append("<TD><A HREF=\"#code" + method_number + "@" +
                 default_offset + "\">" + default_offset + "</A></TD></TR>\n</TABLE>\n");

      break;


    case LOOKUPSWITCH:
      int npairs = bytes.readInt();
      offset = bytes.getIndex() - 8 - no_pad_bytes - 1;
      jump_table = new int[npairs];
      default_offset += offset;

      buf.append("<TABLE BORDER=1><TR>");

      for(int i=0; i < npairs; i++) {
        int match = bytes.readInt();

        jump_table[i] = offset + bytes.readInt();
        buf.append("<TH>" + match + "</TH>");
      }
      buf.append("<TH>default</TH></TR>\n<TR>");

      for(int i=0; i < npairs; i++)
        buf.append("<TD><A HREF=\"#code" + method_number + "@" +
                   jump_table[i] + "\">" + jump_table[i] + "</A></TD>");
      buf.append("<TD><A HREF=\"#code" + method_number + "@" +
                 default_offset + "\">" + default_offset + "</A></TD></TR>\n</TABLE>\n");
      break;


    case GOTO:      case IFEQ:      case IFGE:      case IFGT:
    case IFLE:      case IFLT:
    case IFNE:      case IFNONNULL: case IFNULL:    case IF_ACMPEQ:
    case IF_ACMPNE: case IF_ICMPEQ: case IF_ICMPGE: case IF_ICMPGT:
    case IF_ICMPLE: case IF_ICMPLT: case IF_ICMPNE: case JSR:

      index = (int)(bytes.getIndex() + bytes.readShort() - 1);

      buf.append("<A HREF=\"#code" + method_number + "@" + index + "\">" + index + "</A>");
      break;


    case GOTO_W: case JSR_W:
      int windex = bytes.getIndex() + bytes.readInt() - 1;
      buf.append("<A HREF=\"#code" + method_number + "@" + windex + "\">" +
                 windex + "</A>");
      break;


    case ALOAD:  case ASTORE: case DLOAD:  case DSTORE: case FLOAD:
    case FSTORE: case ILOAD:  case ISTORE: case LLOAD:  case LSTORE:
    case RET:
      if(wide) {
        vindex = bytes.readShort();
        wide=false; }
      else
        vindex = bytes.readUnsignedByte();

      buf.append("%" + vindex);
      break;


    case WIDE:
      wide      = true;
      buf.append("(wide)");
      break;


    case NEWARRAY:
      buf.append("<FONT COLOR=\"#00FF00\">" + TYPE_NAMES[bytes.readByte()] + "</FONT>");
      break;


    case GETFIELD: case GETSTATIC: case PUTFIELD: case PUTSTATIC:
      index = bytes.readShort();
      ConstantFieldref c1 = (ConstantFieldref)constant_pool.getConstant(index, CONSTANT_Fieldref);

      class_index = c1.getClassIndex();
      name = constant_pool.getConstantString(class_index, CONSTANT_Class);
      name = Utility.compactClassName(name, false);

      index = c1.getNameAndTypeIndex();
      String field_name = constant_pool.constantToString(index, CONSTANT_NameAndType);

      if(name.equals(class_name)) { buf.append("<A HREF=\"" + class_name + "_methods.html#field" + field_name +
                   "\" TARGET=Methods>" + field_name + "</A>\n");
      }
      else
        buf.append(constant_html.referenceConstant(class_index) + "." + field_name);

      break;


    case CHECKCAST: case INSTANCEOF: case NEW:
      index = bytes.readShort();
      buf.append(constant_html.referenceConstant(index));
      break;


    case INVOKESPECIAL: case INVOKESTATIC: case INVOKEVIRTUAL: case INVOKEINTERFACE:
      int m_index = bytes.readShort();
      String str;

      if(opcode == INVOKEINTERFACE) { int nargs    = bytes.readUnsignedByte(); int reserved = bytes.readUnsignedByte(); ConstantInterfaceMethodref c=(ConstantInterfaceMethodref)constant_pool.getConstant(m_index, CONSTANT_InterfaceMethodref);

        class_index = c.getClassIndex();
        str = constant_pool.constantToString(c);
        index = c.getNameAndTypeIndex();
      }
      else {
        ConstantMethodref c = (ConstantMethodref)constant_pool.getConstant(m_index, CONSTANT_Methodref);
        class_index = c.getClassIndex();

        str  = constant_pool.constantToString(c);
        index = c.getNameAndTypeIndex();
      }

      name = Class2HTML.referenceClass(class_index);
      str = Class2HTML.toHTML(constant_pool.constantToString(constant_pool.getConstant(index, CONSTANT_NameAndType)));

      ConstantNameAndType c2 = (ConstantNameAndType)constant_pool.
        getConstant(index, CONSTANT_NameAndType);
      signature = constant_pool.constantToString(c2.getSignatureIndex(),
                                                 CONSTANT_Utf8);
      String[] args = Utility.methodSignatureArgumentTypes(signature, false);
      String   type = Utility.methodSignatureReturnType(signature, false);

      buf.append(name + ".<A HREF=\"" + class_name + "_cp.html#cp" + m_index +
                 "\" TARGET=ConstantPool>" + str + "</A>" + "(");

      for(int i=0; i < args.length; i++) {
        buf.append(Class2HTML.referenceType(args[i]));

        if(i < args.length - 1)
          buf.append(", ");
      }
      buf.append("):" + Class2HTML.referenceType(type));

      break;


    case LDC_W: case LDC2_W:
      index = bytes.readShort();

      buf.append("<A HREF=\"" + class_name + "_cp.html#cp" + index +
                 "\" TARGET=\"ConstantPool\">" +
                 Class2HTML.toHTML(constant_pool.constantToString(index,
                                                                  constant_pool.
                                                                  getConstant(index).getTag()))+
                 "</a>");
      break;

    case LDC:
      index = bytes.readUnsignedByte();
      buf.append("<A HREF=\"" + class_name + "_cp.html#cp" + index +
                 "\" TARGET=\"ConstantPool\">" +
                 Class2HTML.toHTML(constant_pool.constantToString(index,
                                                                  constant_pool.
                                                                  getConstant(index).getTag()))+
                 "</a>");
      break;


    case ANEWARRAY:
      index = bytes.readShort();

      buf.append(constant_html.referenceConstant(index));
      break;


    case MULTIANEWARRAY:
      index = bytes.readShort();
      int dimensions = bytes.readByte();
      buf.append(constant_html.referenceConstant(index) + ":" + dimensions + "-dimensional");
      break;


    case IINC:
      if(wide) {
        vindex   = bytes.readShort();
        constant = bytes.readShort();
        wide     = false;
      }
      else {
        vindex   = bytes.readUnsignedByte();
        constant = bytes.readByte();
      }
      buf.append("%" + vindex + " " + constant);
      break;

    default:
      if(NO_OF_OPERANDS[opcode] > 0) {
        for(int i=0; i < TYPE_OF_OPERANDS[opcode].length; i++) {
          switch(TYPE_OF_OPERANDS[opcode][i]) {
          case T_BYTE:
            buf.append(bytes.readUnsignedByte());
            break;

          case T_SHORT: buf.append(bytes.readShort());
            break;

          case T_INT:
            buf.append(bytes.readInt());
            break;

          default: System.err.println("Unreachable default case reached!");
            System.exit(-1);
          }
          buf.append("&nbsp;");
        }
      }
    }

    buf.append("</TD>");
    return buf.toString();
  }


  private final void findGotos(ByteSequence bytes, Method method, Code code)
       throws IOException
  {
    int index;
    goto_set = new BitSet(bytes.available());
    int opcode;



    if(code != null) {
      CodeException[] ce  = code.getExceptionTable();
      int             len = ce.length;

      for(int i=0; i < len; i++) {
        goto_set.set(ce[i].getStartPC());
        goto_set.set(ce[i].getEndPC());
        goto_set.set(ce[i].getHandlerPC());
      }

      Attribute[] attributes = code.getAttributes();
      for(int i=0; i < attributes.length; i++) {
        if(attributes[i].getTag() == ATTR_LOCAL_VARIABLE_TABLE) {
          LocalVariable[] vars = ((LocalVariableTable)attributes[i]).getLocalVariableTable();

          for(int j=0; j < vars.length; j++) {
            int  start = vars[j].getStartPC();
            int  end   = (int)(start + vars[j].getLength());
            goto_set.set(start);
            goto_set.set(end);
          }
          break;
        }
      }
    }

    for(int i=0; bytes.available() > 0; i++) {
      opcode = bytes.readUnsignedByte();
      switch(opcode) {
      case TABLESWITCH: case LOOKUPSWITCH:
        int remainder = bytes.getIndex() % 4;
        int no_pad_bytes  = (remainder == 0)? 0 : 4 - remainder;
        int default_offset, offset;

        for(int j=0; j < no_pad_bytes; j++)
          bytes.readByte();

        default_offset = bytes.readInt();

        if(opcode == TABLESWITCH) {
          int low = bytes.readInt();
          int high = bytes.readInt();

          offset = bytes.getIndex() - 12 - no_pad_bytes - 1;
          default_offset += offset;
          goto_set.set(default_offset);

          for(int j=0; j < (high - low + 1); j++) {
            index = offset + bytes.readInt();
            goto_set.set(index);
          }
        }
        else { int npairs = bytes.readInt();

          offset = bytes.getIndex() - 8 - no_pad_bytes - 1;
          default_offset += offset;
          goto_set.set(default_offset);

          for(int j=0; j < npairs; j++) {
            int match = bytes.readInt();

            index = offset + bytes.readInt();
            goto_set.set(index);
          }
        }
        break;

      case GOTO:      case IFEQ:      case IFGE:      case IFGT:
      case IFLE:      case IFLT:
      case IFNE:      case IFNONNULL: case IFNULL:    case IF_ACMPEQ:
      case IF_ACMPNE: case IF_ICMPEQ: case IF_ICMPGE: case IF_ICMPGT:
      case IF_ICMPLE: case IF_ICMPLT: case IF_ICMPNE: case JSR:
        index = bytes.getIndex() + bytes.readShort() - 1;

        goto_set.set(index);
        break;

      case GOTO_W: case JSR_W:
        index = bytes.getIndex() + bytes.readInt() - 1;
        goto_set.set(index);
        break;

      default:
        bytes.unreadByte();
        codeToHTML(bytes, 0); }
    }
  }


  private void writeMethod(Method method, int method_number)
       throws IOException
  {
    String       signature = method.getSignature();
    String[]     args      = Utility.methodSignatureArgumentTypes(signature, false);
    String       type      = Utility.methodSignatureReturnType(signature, false);
    String       name      = method.getName();
    String       html_name = Class2HTML.toHTML(name);
    String       access    = Utility.accessToString(method.getAccessFlags());
    access = Utility.replace(access, " ", "&nbsp;");
    Attribute[]  attributes= method.getAttributes();

    file.print("<P><B><FONT COLOR=\"#FF0000\">" + access + "</FONT>&nbsp;" +
               "<A NAME=method" + method_number + ">" + Class2HTML.referenceType(type) +
               "</A>&nbsp<A HREF=\"" + class_name + "_methods.html#method" + method_number +
               "\" TARGET=Methods>" + html_name + "</A>(");

    for(int i=0; i < args.length; i++) {
      file.print(Class2HTML.referenceType(args[i]));
      if(i < args.length - 1)
        file.print(",&nbsp;");
    }

    file.println(")</B></P>");

    Code c=null;
    byte[] code=null;

    if(attributes.length > 0) {
      file.print("<H4>Attributes</H4><UL>\n");
      for(int i=0; i < attributes.length; i++) {
        byte tag = attributes[i].getTag();

        if(tag != ATTR_UNKNOWN)
          file.print("<LI><A HREF=\"" + class_name + "_attributes.html#method" + method_number + "@" + i +
                     "\" TARGET=Attributes>" + ATTRIBUTE_NAMES[tag] + "</A></LI>\n");
        else
          file.print("<LI>" + attributes[i] + "</LI>");

        if(tag == ATTR_CODE) {
          c = (Code)attributes[i];
          Attribute[] attributes2 = c.getAttributes();
          code                                                          = c.getCode();

          file.print("<UL>");
          for(int j=0; j < attributes2.length; j++) {
            tag = attributes2[j].getTag();
            file.print("<LI><A HREF=\"" + class_name + "_attributes.html#" +
                       "method" + method_number + "@" + i + "@" + j + "\" TARGET=Attributes>" +
                       ATTRIBUTE_NAMES[tag] + "</A></LI>\n");

          }
          file.print("</UL>");
        }
      }
      file.println("</UL>");
    }

    if(code != null) { ByteSequence stream = new ByteSequence(code);
      stream.mark(stream.available());
      findGotos(stream, method, c);
      stream.reset();

      file.println("<TABLE BORDER=0><TR><TH ALIGN=LEFT>Byte<BR>offset</TH>" +
                   "<TH ALIGN=LEFT>Instruction</TH><TH ALIGN=LEFT>Argument</TH>");

      for(int i=0; stream.available() > 0; i++) {
        int offset = stream.getIndex();
        String str = codeToHTML(stream, method_number);
        String anchor = "";


        if(goto_set.get(offset))
          anchor = "<A NAME=code" + method_number + "@" + offset +  "></A>";

        String anchor2;
        if(stream.getIndex() == code.length) anchor2 = "<A NAME=code" + method_number + "@" + code.length + ">" + offset + "</A>";
        else
          anchor2 = "" + offset;

        file.println("<TR VALIGN=TOP><TD>" + anchor2 + "</TD><TD>" + anchor + str + "</TR>");
      }

      file.println("<TR><TD> </A></TD></TR>");
      file.println("</TABLE>");
    }

  }
}
