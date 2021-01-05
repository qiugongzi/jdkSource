



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;


final class KeyCall extends FunctionCall {


    private Expression _name;


    private Expression _value;


    private Type _valueType; private QName _resolvedQName = null;


    public KeyCall(QName fname, Vector arguments) {
        super(fname, arguments);
        switch(argumentCount()) {
        case 1:
            _name = null;
            _value = argument(0);
            break;
        case 2:
            _name = argument(0);
            _value = argument(1);
            break;
        default:
            _name = _value = null;
            break;
        }
    }


    public void addParentDependency() {
        if (_resolvedQName == null) return;

        SyntaxTreeNode node = this;
        while (node != null && node instanceof TopLevelElement == false) {
            node = node.getParent();
        }

        TopLevelElement parent = (TopLevelElement) node;
        if (parent != null) {
            parent.addDependency(getSymbolTable().getKey(_resolvedQName));
        }
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        final Type returnType = super.typeCheck(stable);

        if (_name != null) {
            final Type nameType = _name.typeCheck(stable);

            if (_name instanceof LiteralExpr) {
                final LiteralExpr literal = (LiteralExpr) _name;
                _resolvedQName =
                    getParser().getQNameIgnoreDefaultNs(literal.getValue());
            }
            else if (nameType instanceof StringType == false) {
                _name = new CastExpr(_name, Type.String);
            }
        }

        _valueType = _value.typeCheck(stable);

        if (_valueType != Type.NodeSet
                && _valueType != Type.Reference
                && _valueType != Type.String) {
            _value = new CastExpr(_value, Type.String);
            _valueType = _value.typeCheck(stable);
        }

        addParentDependency();

        return returnType;
    }


    public void translate(ClassGenerator classGen,
                          MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        final int getKeyIndex = cpg.addMethodref(TRANSLET_CLASS,
                                                 "getKeyIndex",
                                                 "(Ljava/lang/String;)"+
                                                 KEY_INDEX_SIG);

        final int keyDom = cpg.addMethodref(KEY_INDEX_CLASS,
                                            "setDom",
                                            "(" + DOM_INTF_SIG + "I)V");

        final int getKeyIterator =
                        cpg.addMethodref(KEY_INDEX_CLASS,
                                         "getKeyIndexIterator",
                                         "(" + _valueType.toSignature() + "Z)"
                                             + KEY_INDEX_ITERATOR_SIG);

        il.append(classGen.loadTranslet());
        if (_name == null) {
            il.append(new PUSH(cpg,"##id"));
        } else if (_resolvedQName != null) {
            il.append(new PUSH(cpg, _resolvedQName.toString()));
        } else {
            _name.translate(classGen, methodGen);
        }

        il.append(new INVOKEVIRTUAL(getKeyIndex));
        il.append(DUP);
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadCurrentNode());
        il.append(new INVOKEVIRTUAL(keyDom));

        _value.translate(classGen, methodGen);
        il.append((_name != null) ? ICONST_1: ICONST_0);
        il.append(new INVOKEVIRTUAL(getKeyIterator));
    }
}
