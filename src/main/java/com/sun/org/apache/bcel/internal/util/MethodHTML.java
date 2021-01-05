
package com.sun.org.apache.bcel.internal.util;



import com.sun.org.apache.bcel.internal.classfile.*;
import java.io.*;


final class MethodHTML implements com.sun.org.apache.bcel.internal.Constants {
  private String        class_name;     private PrintWriter   file;           private ConstantHTML  constant_html;
  private AttributeHTML attribute_html;

  MethodHTML(String dir, String class_name,
             Method[] methods, Field[] fields,
             ConstantHTML constant_html, AttributeHTML attribute_html) throws IOException
  {
    this.class_name     = class_name;
    this.attribute_html = attribute_html;
    this.constant_html  = constant_html;

    file = new PrintWriter(new FileOutputStream(dir + class_name + "_methods.html"));

    file.println("<HTML><BODY BGCOLOR=\"#C0C0C0\"><TABLE BORDER=0>");
    file.println("<TR><TH ALIGN=LEFT>Access&nbsp;flags</TH><TH ALIGN=LEFT>Type</TH>" +
                 "<TH ALIGN=LEFT>Field&nbsp;name</TH></TR>");
    for(int i=0; i < fields.length; i++)
      writeField(fields[i]);
    file.println("</TABLE>");

    file.println("<TABLE BORDER=0><TR><TH ALIGN=LEFT>Access&nbsp;flags</TH>" +
                 "<TH ALIGN=LEFT>Return&nbsp;type</TH><TH ALIGN=LEFT>Method&nbsp;name</TH>" +
                 "<TH ALIGN=LEFT>Arguments</TH></TR>");
    for(int i=0; i < methods.length; i++)
      writeMethod(methods[i], i);

    file.println("</TABLE></BODY></HTML>");
    file.close();
  }


  private void writeField(Field field) throws IOException {
    String       type   = Utility.signatureToString(field.getSignature());
    String       name   = field.getName();
    String       access = Utility.accessToString(field.getAccessFlags());
    Attribute[]  attributes;

    access = Utility.replace(access, " ", "&nbsp;");

    file.print("<TR><TD><FONT COLOR=\"#FF0000\">" + access + "</FONT></TD>\n<TD>" +
               Class2HTML.referenceType(type) + "</TD><TD><A NAME=\"field" + name + "\">" +
               name + "</A></TD>");

    attributes = field.getAttributes();

    for(int i=0; i < attributes.length; i++)
      attribute_html.writeAttribute(attributes[i], name + "@" + i);

    for(int i=0; i < attributes.length; i++) {
      if(attributes[i].getTag() == ATTR_CONSTANT_VALUE) { String str = ((ConstantValue)attributes[i]).toString();

        file.print("<TD>= <A HREF=\"" + class_name + "_attributes.html#" +
                   name + "@" + i + "\" TARGET=\"Attributes\">" + str + "</TD>\n");
        break;
      }
    }

    file.println("</TR>");
  }

  private final void writeMethod(Method method, int method_number) throws IOException {
    String       signature      = method.getSignature();
    String[]     args           = Utility.methodSignatureArgumentTypes(signature, false);
    String       type           = Utility.methodSignatureReturnType(signature, false);
    String       name           = method.getName(), html_name;
    String       access         = Utility.accessToString(method.getAccessFlags());
    Attribute[]  attributes     = method.getAttributes();


    access      = Utility.replace(access, " ", "&nbsp;");
    html_name   = Class2HTML.toHTML(name);

    file.print("<TR VALIGN=TOP><TD><FONT COLOR=\"#FF0000\"><A NAME=method" + method_number + ">" +
               access + "</A></FONT></TD>");

    file.print("<TD>" + Class2HTML.referenceType(type) + "</TD><TD>" +
               "<A HREF=" + class_name + "_code.html#method" + method_number +
               " TARGET=Code>" + html_name + "</A></TD>\n<TD>(");

    for(int i=0; i < args.length; i++) {
      file.print(Class2HTML.referenceType(args[i]));
      if(i < args.length - 1)
        file.print(", ");
    }

    file.print(")</TD></TR>");

    for(int i=0; i < attributes.length; i++) {
      attribute_html.writeAttribute(attributes[i], "method" + method_number + "@" + i,
                                    method_number);

      byte tag = attributes[i].getTag();
      if(tag == ATTR_EXCEPTIONS) {
        file.print("<TR VALIGN=TOP><TD COLSPAN=2></TD><TH ALIGN=LEFT>throws</TH><TD>");
        int[] exceptions = ((ExceptionTable)attributes[i]).getExceptionIndexTable();

        for(int j=0; j < exceptions.length; j++) {
          file.print(constant_html.referenceConstant(exceptions[j]));

          if(j < exceptions.length - 1)
            file.print(", ");
        }
        file.println("</TD></TR>");
      } else if(tag == ATTR_CODE) {
        Attribute[] c_a = ((Code)attributes[i]).getAttributes();

        for(int j=0; j < c_a.length; j++)
          attribute_html.writeAttribute(c_a[j], "method" + method_number + "@" + i + "@" + j,
                                        method_number);
      }
    }
  }
}
