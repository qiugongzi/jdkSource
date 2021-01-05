



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;


final class CastCall extends FunctionCall {


    private String _className;


    private Expression _right;


    public CastCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (argumentCount() != 2) {
            throw new TypeCheckError(new ErrorMsg(ErrorMsg.ILLEGAL_ARG_ERR,
                                                  getName(), this));
        }

        Expression exp = argument(0);
        if (exp instanceof LiteralExpr) {
            _className = ((LiteralExpr) exp).getValue();
            _type = Type.newObjectType(_className);
        }
        else {
            throw new TypeCheckError(new ErrorMsg(ErrorMsg.NEED_LITERAL_ERR,
                                                  getName(), this));
        }

         _right = argument(1);
        Type tright = _right.typeCheck(stable);
        if (tright != Type.Reference &&
            tright instanceof ObjectType == false)
        {
            throw new TypeCheckError(new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
                                                  tright, _type, this));
        }

        return _type;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        _right.translate(classGen, methodGen);
        il.append(new CHECKCAST(cpg.addClass(_className)));
    }
}
