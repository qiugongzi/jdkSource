



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;


final class FilterParentPath extends Expression {

    private Expression _filterExpr;
    private Expression _path;
    private boolean _hasDescendantAxis = false;

    public FilterParentPath(Expression filterExpr, Expression path) {
        (_path = path).setParent(this);
        (_filterExpr = filterExpr).setParent(this);
    }

    public void setParser(Parser parser) {
        super.setParser(parser);
        _filterExpr.setParser(parser);
        _path.setParser(parser);
    }

    public String toString() {
        return "FilterParentPath(" + _filterExpr + ", " + _path + ')';
    }

    public void setDescendantAxis() {
        _hasDescendantAxis = true;
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        final Type ftype = _filterExpr.typeCheck(stable);
        if (ftype instanceof NodeSetType == false) {
            if (ftype instanceof ReferenceType)  {
                _filterExpr = new CastExpr(_filterExpr, Type.NodeSet);
            }

            else if (ftype instanceof NodeType)  {
                _filterExpr = new CastExpr(_filterExpr, Type.NodeSet);
            }
            else {
                throw new TypeCheckError(this);
            }
        }

        final Type ptype = _path.typeCheck(stable);
        if (!(ptype instanceof NodeSetType)) {
            _path = new CastExpr(_path, Type.NodeSet);
        }

        return _type = Type.NodeSet;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int initSI = cpg.addMethodref(STEP_ITERATOR_CLASS,
                                            "<init>",
                                            "("
                                            +NODE_ITERATOR_SIG
                                            +NODE_ITERATOR_SIG
                                            +")V");

        _filterExpr.translate(classGen, methodGen);
        LocalVariableGen filterTemp =
                methodGen.addLocalVariable("filter_parent_path_tmp1",
                                           Util.getJCRefType(NODE_ITERATOR_SIG),
                                           null, null);
        filterTemp.setStart(il.append(new ASTORE(filterTemp.getIndex())));

        _path.translate(classGen, methodGen);
        LocalVariableGen pathTemp =
                methodGen.addLocalVariable("filter_parent_path_tmp2",
                                           Util.getJCRefType(NODE_ITERATOR_SIG),
                                           null, null);
        pathTemp.setStart(il.append(new ASTORE(pathTemp.getIndex())));

        il.append(new NEW(cpg.addClass(STEP_ITERATOR_CLASS)));
        il.append(DUP);
        filterTemp.setEnd(il.append(new ALOAD(filterTemp.getIndex())));
        pathTemp.setEnd(il.append(new ALOAD(pathTemp.getIndex())));

        il.append(new INVOKESPECIAL(initSI));

        if (_hasDescendantAxis) {
            final int incl = cpg.addMethodref(NODE_ITERATOR_BASE,
                                              "includeSelf",
                                              "()" + NODE_ITERATOR_SIG);
            il.append(new INVOKEVIRTUAL(incl));
        }

        SyntaxTreeNode parent = getParent();

        boolean parentAlreadyOrdered =
            (parent instanceof RelativeLocationPath)
                || (parent instanceof FilterParentPath)
                || (parent instanceof KeyCall)
                || (parent instanceof CurrentCall)
                || (parent instanceof DocumentCall);

        if (!parentAlreadyOrdered) {
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
