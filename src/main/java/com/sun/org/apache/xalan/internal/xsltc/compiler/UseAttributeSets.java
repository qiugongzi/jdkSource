



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.StringTokenizer;
import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;


final class UseAttributeSets extends Instruction {

    private final static String ATTR_SET_NOT_FOUND =
        "";

    private final Vector _sets = new Vector(2);


    public UseAttributeSets(String setNames, Parser parser) {
        setParser(parser);
        addAttributeSets(setNames);
    }


    public void addAttributeSets(String setNames) {
        if ((setNames != null) && (!setNames.equals(Constants.EMPTYSTRING))) {
            final StringTokenizer tokens = new StringTokenizer(setNames);
            while (tokens.hasMoreTokens()) {
                final QName qname =
                    getParser().getQNameIgnoreDefaultNs(tokens.nextToken());
                _sets.add(qname);
            }
        }
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return Type.Void;
    }


    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final SymbolTable symbolTable = getParser().getSymbolTable();

        for (int i=0; i<_sets.size(); i++) {
            final QName name = (QName)_sets.elementAt(i);
            final AttributeSet attrs = symbolTable.lookupAttributeSet(name);
            if (attrs != null) {
                final String methodName = attrs.getMethodName();
                il.append(classGen.loadTranslet());
                il.append(methodGen.loadDOM());
                il.append(methodGen.loadIterator());
                il.append(methodGen.loadHandler());
                il.append(methodGen.loadCurrentNode());
                final int method = cpg.addMethodref(classGen.getClassName(),
                                                    methodName, ATTR_SET_SIG);
                il.append(new INVOKESPECIAL(method));
            }
            else {
                final Parser parser = getParser();
                final String atrs = name.toString();
                reportError(this, parser, ErrorMsg.ATTRIBSET_UNDEF_ERR, atrs);
            }
        }
    }
}
