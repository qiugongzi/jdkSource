



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;


final class LocalNameCall extends NameBase {


    public LocalNameCall(QName fname) {
        super(fname);
    }


    public LocalNameCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }


    public void translate(ClassGenerator classGen,
                          MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        final int getNodeName = cpg.addInterfaceMethodref(DOM_INTF,
                                                          "getNodeName",
                                                          "(I)"+STRING_SIG);

        final int getLocalName = cpg.addMethodref(BASIS_LIBRARY_CLASS,
                                                  "getLocalName",
                                                  "(Ljava/lang/String;)"+
                                                  "Ljava/lang/String;");
        super.translate(classGen, methodGen);
        il.append(new INVOKEINTERFACE(getNodeName, 2));
        il.append(new INVOKESTATIC(getLocalName));
    }
}
