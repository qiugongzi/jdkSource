



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;


class NameBase extends FunctionCall {

    private Expression _param = null;
    private Type       _paramType = Type.Node;


    public NameBase(QName fname) {
        super(fname);
    }


    public NameBase(QName fname, Vector arguments) {
        super(fname, arguments);
        _param = argument(0);
    }



    public Type typeCheck(SymbolTable stable) throws TypeCheckError {

        switch(argumentCount()) {
        case 0:
            _paramType = Type.Node;
            break;
        case 1:
            _paramType = _param.typeCheck(stable);
            break;
        default:
            throw new TypeCheckError(this);
        }

        if ((_paramType != Type.NodeSet) &&
            (_paramType != Type.Node) &&
            (_paramType != Type.Reference)) {
            throw new TypeCheckError(this);
        }

        return (_type = Type.String);
    }

    public Type getType() {
        return _type;
    }


    public void translate(ClassGenerator classGen,
                          MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        il.append(methodGen.loadDOM());

        if (argumentCount() == 0) {
            il.append(methodGen.loadContextNode());
        }
        else if (_paramType == Type.Node) {
            _param.translate(classGen, methodGen);
        }
        else if (_paramType == Type.Reference) {
            _param.translate(classGen, methodGen);
            il.append(new INVOKESTATIC(cpg.addMethodref
                                       (BASIS_LIBRARY_CLASS,
                                        "referenceToNodeSet",
                                        "("
                                        + OBJECT_SIG
                                        + ")"
                                        + NODE_ITERATOR_SIG)));
            il.append(methodGen.nextNode());
        }
        else {
            _param.translate(classGen, methodGen);
            _param.startIterator(classGen, methodGen);
            il.append(methodGen.nextNode());
        }
    }
}
