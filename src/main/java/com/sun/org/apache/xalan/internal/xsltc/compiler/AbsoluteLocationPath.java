



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;


final class AbsoluteLocationPath extends Expression {
    private Expression _path;   public AbsoluteLocationPath() {
        _path = null;
    }

    public AbsoluteLocationPath(Expression path) {
        _path = path;
        if (path != null) {
            _path.setParent(this);
        }
    }

    public void setParser(Parser parser) {
        super.setParser(parser);
        if (_path != null) {
            _path.setParser(parser);
        }
    }

    public Expression getPath() {
        return(_path);
    }

    public String toString() {
        return "AbsoluteLocationPath(" +
            (_path != null ? _path.toString() : "null") + ')';
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (_path != null) {
            final Type ptype = _path.typeCheck(stable);
            if (ptype instanceof NodeType) {            _path = new CastExpr(_path, Type.NodeSet);
            }
        }
        return _type = Type.NodeSet;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (_path != null) {
            final int initAI = cpg.addMethodref(ABSOLUTE_ITERATOR,
                                                "<init>",
                                                "("
                                                + NODE_ITERATOR_SIG
                                                + ")V");

            _path.translate(classGen, methodGen);
            LocalVariableGen relPathIterator
                    = methodGen.addLocalVariable("abs_location_path_tmp",
                                       Util.getJCRefType(NODE_ITERATOR_SIG),
                                       null, null);
            relPathIterator.setStart(
                    il.append(new ASTORE(relPathIterator.getIndex())));

            il.append(new NEW(cpg.addClass(ABSOLUTE_ITERATOR)));
            il.append(DUP);
            relPathIterator.setEnd(
                    il.append(new ALOAD(relPathIterator.getIndex())));

            il.append(new INVOKESPECIAL(initAI));
        }
        else {
            final int gitr = cpg.addInterfaceMethodref(DOM_INTF,
                                                       "getIterator",
                                                       "()"+NODE_ITERATOR_SIG);
            il.append(methodGen.loadDOM());
            il.append(new INVOKEINTERFACE(gitr, 1));
        }
    }
}
