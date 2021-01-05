



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;


final class ProcessingInstruction extends Instruction {

    private AttributeValue _name; private boolean _isLiteral = false;  public void parseContents(Parser parser) {
        final String name  = getAttribute("name");

        if (name.length() > 0) {
            _isLiteral = Util.isLiteral(name);
            if (_isLiteral) {
                if (!XML11Char.isXML11ValidNCName(name)) {
                    ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_NCNAME_ERR, name, this);
                    parser.reportError(Constants.ERROR, err);
                }
            }
            _name = AttributeValue.create(this, name, parser);
        }
        else
            reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "name");

        if (name.equals("xml")) {
            reportError(this, parser, ErrorMsg.ILLEGAL_PI_ERR, "xml");
        }
        parseChildren(parser);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        _name.typeCheck(stable);
        typeCheckContents(stable);
        return Type.Void;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        if (!_isLiteral) {
            LocalVariableGen nameValue =
                    methodGen.addLocalVariable2("nameValue",
            Util.getJCRefType(STRING_SIG),
                                                null);

            _name.translate(classGen, methodGen);
            nameValue.setStart(il.append(new ASTORE(nameValue.getIndex())));
            il.append(new ALOAD(nameValue.getIndex()));

            final int check = cpg.addMethodref(BASIS_LIBRARY_CLASS, "checkNCName",
                                "("
                                +STRING_SIG
                                +")V");
                                il.append(new INVOKESTATIC(check));

            il.append(methodGen.loadHandler());
            il.append(DUP);     nameValue.setEnd(il.append(new ALOAD(nameValue.getIndex())));
        } else {
            il.append(methodGen.loadHandler());
            il.append(DUP);     _name.translate(classGen, methodGen);}

        il.append(classGen.loadTranslet());
        il.append(new GETFIELD(cpg.addFieldref(TRANSLET_CLASS,
                                               "stringValueHandler",
                                               STRING_VALUE_HANDLER_SIG)));
        il.append(DUP);
        il.append(methodGen.storeHandler());

        translateContents(classGen, methodGen);

        il.append(new INVOKEVIRTUAL(cpg.addMethodref(STRING_VALUE_HANDLER,
                                                     "getValueOfPI",
                                                     "()" + STRING_SIG)));
        final int processingInstruction =
            cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE,
                                      "processingInstruction",
                                      "(" + STRING_SIG + STRING_SIG + ")V");
        il.append(new INVOKEINTERFACE(processingInstruction, 3));
        il.append(methodGen.storeHandler());
    }
}
