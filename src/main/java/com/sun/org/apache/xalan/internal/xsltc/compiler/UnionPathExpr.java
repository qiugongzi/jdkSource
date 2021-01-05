



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;


final class UnionPathExpr extends Expression {

    private final Expression _pathExpr;
    private final Expression _rest;
    private boolean _reverse = false;

    private Expression[] _components;

    public UnionPathExpr(Expression pathExpr, Expression rest) {
        _pathExpr = pathExpr;
        _rest     = rest;
    }

    public void setParser(Parser parser) {
        super.setParser(parser);
        final Vector components = new Vector();
        flatten(components);
        final int size = components.size();
        _components = (Expression[])components.toArray(new Expression[size]);
        for (int i = 0; i < size; i++) {
            _components[i].setParser(parser);
            _components[i].setParent(this);
            if (_components[i] instanceof Step) {
                final Step step = (Step)_components[i];
                final int axis = step.getAxis();
                final int type = step.getNodeType();
                if ((axis == Axis.ATTRIBUTE) || (type == DTM.ATTRIBUTE_NODE)) {
                    _components[i] = _components[0];
                    _components[0] = step;
                }
                if (Axis.isReverse(axis)) _reverse = true;
            }
        }
        if (getParent() instanceof Expression) _reverse = false;
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        final int length = _components.length;
        for (int i = 0; i < length; i++) {
            if (_components[i].typeCheck(stable) != Type.NodeSet) {
                _components[i] = new CastExpr(_components[i], Type.NodeSet);
            }
        }
        return _type = Type.NodeSet;
    }

    public String toString() {
        return "union(" + _pathExpr + ", " + _rest + ')';
    }

    private void flatten(Vector components) {
        components.addElement(_pathExpr);
        if (_rest != null) {
            if (_rest instanceof UnionPathExpr) {
                ((UnionPathExpr)_rest).flatten(components);
            }
            else {
                components.addElement(_rest);
            }
        }
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        final int init = cpg.addMethodref(UNION_ITERATOR_CLASS,
                                          "<init>",
                                          "("+DOM_INTF_SIG+")V");
        final int iter = cpg.addMethodref(UNION_ITERATOR_CLASS,
                                          ADD_ITERATOR,
                                          ADD_ITERATOR_SIG);

        il.append(new NEW(cpg.addClass(UNION_ITERATOR_CLASS)));
        il.append(DUP);
        il.append(methodGen.loadDOM());
        il.append(new INVOKESPECIAL(init));

        final int length = _components.length;
        for (int i = 0; i < length; i++) {
            _components[i].translate(classGen, methodGen);
            il.append(new INVOKEVIRTUAL(iter));
        }

        if (_reverse) {
            final int order = cpg.addInterfaceMethodref(DOM_INTF,
                                                        ORDER_ITERATOR,
                                                        ORDER_ITERATOR_SIG);
            il.append(methodGen.loadDOM());
            il.append(SWAP);
            il.append(methodGen.loadContextNode());
            il.append(new INVOKEINTERFACE(order, 3));

        }
    }
}