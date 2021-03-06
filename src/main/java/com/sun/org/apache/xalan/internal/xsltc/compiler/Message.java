



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;


final class Message extends Instruction {
    private boolean _terminate = false;

    public void parseContents(Parser parser) {
        String termstr = getAttribute("terminate");
        if (termstr != null) {
            _terminate = termstr.equals("yes");
        }
        parseChildren(parser);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        typeCheckContents(stable);
        return Type.Void;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        il.append(classGen.loadTranslet());

        switch (elementCount()) {
            case 0:
                il.append(new PUSH(cpg, ""));
            break;
            case 1:
                SyntaxTreeNode child = (SyntaxTreeNode) elementAt(0);
                if (child instanceof Text) {
                    il.append(new PUSH(cpg, ((Text) child).getText()));
                    break;
                }
                default:
                il.append(methodGen.loadHandler());

                il.append(new NEW(cpg.addClass(STREAM_XML_OUTPUT)));
                il.append(methodGen.storeHandler());

                il.append(new NEW(cpg.addClass(STRING_WRITER)));
                il.append(DUP);
                il.append(DUP);
                il.append(new INVOKESPECIAL(
                    cpg.addMethodref(STRING_WRITER, "<init>", "()V")));

                il.append(methodGen.loadHandler());
                il.append(new INVOKESPECIAL(
                    cpg.addMethodref(STREAM_XML_OUTPUT, "<init>",
                                     "()V")));

                il.append(methodGen.loadHandler());
                il.append(SWAP);
                il.append(new INVOKEINTERFACE(
                    cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                              "setWriter",
                                              "("+WRITER_SIG+")V"), 2));

                il.append(methodGen.loadHandler());
                il.append(new PUSH(cpg, "UTF-8"));   il.append(new INVOKEINTERFACE(
                    cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                              "setEncoding",
                                              "("+STRING_SIG+")V"), 2));

                il.append(methodGen.loadHandler());
                il.append(ICONST_1);
                il.append(new INVOKEINTERFACE(
                    cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                              "setOmitXMLDeclaration",
                                              "(Z)V"), 2));

                il.append(methodGen.loadHandler());
                il.append(new INVOKEINTERFACE(
                    cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                              "startDocument",
                                              "()V"), 1));

                translateContents(classGen, methodGen);

                il.append(methodGen.loadHandler());
                il.append(new INVOKEINTERFACE(
                    cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                              "endDocument",
                                              "()V"), 1));

                il.append(new INVOKEVIRTUAL(
                    cpg.addMethodref(STRING_WRITER, "toString",
                                     "()" + STRING_SIG)));

                il.append(SWAP);
                il.append(methodGen.storeHandler());
            break;
        }

        il.append(new INVOKEVIRTUAL(cpg.addMethodref(TRANSLET_CLASS,
                                                     "displayMessage",
                                                     "("+STRING_SIG+")V")));

        if (_terminate == true) {
            final int einit = cpg.addMethodref("java.lang.RuntimeException",
                                               "<init>",
                                               "(Ljava/lang/String;)V");
            il.append(new NEW(cpg.addClass("java.lang.RuntimeException")));
            il.append(DUP);
            il.append(new PUSH(cpg,"Termination forced by an " +
                                   "xsl:message instruction"));
            il.append(new INVOKESPECIAL(einit));
            il.append(ATHROW);
        }
    }

}
