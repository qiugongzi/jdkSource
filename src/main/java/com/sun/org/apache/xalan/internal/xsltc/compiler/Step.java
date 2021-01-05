



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;


final class Step extends RelativeLocationPath {


    private int _axis;


    private Vector _predicates;


    private boolean _hadPredicates = false;


    private int _nodeType;

    public Step(int axis, int nodeType, Vector predicates) {
        _axis = axis;
        _nodeType = nodeType;
        _predicates = predicates;
    }


    public void setParser(Parser parser) {
        super.setParser(parser);
        if (_predicates != null) {
            final int n = _predicates.size();
            for (int i = 0; i < n; i++) {
                final Predicate exp = (Predicate)_predicates.elementAt(i);
                exp.setParser(parser);
                exp.setParent(this);
            }
        }
    }


    public int getAxis() {
        return _axis;
    }


    public void setAxis(int axis) {
        _axis = axis;
    }


    public int getNodeType() {
        return _nodeType;
    }


    public Vector getPredicates() {
        return _predicates;
    }


    public void addPredicates(Vector predicates) {
        if (_predicates == null) {
            _predicates = predicates;
        }
        else {
            _predicates.addAll(predicates);
        }
    }


    private boolean hasParentPattern() {
        final SyntaxTreeNode parent = getParent();
        return (parent instanceof ParentPattern ||
                parent instanceof ParentLocationPath ||
                parent instanceof UnionPathExpr ||
                parent instanceof FilterParentPath);
    }


    private boolean hasParentLocationPath() {
        return getParent() instanceof ParentLocationPath;
    }


    private boolean hasPredicates() {
        return _predicates != null && _predicates.size() > 0;
    }


    private boolean isPredicate() {
        SyntaxTreeNode parent = this;
        while (parent != null) {
            parent = parent.getParent();
            if (parent instanceof Predicate) return true;
        }
        return false;
    }


    public boolean isAbbreviatedDot() {
        return _nodeType == NodeTest.ANODE && _axis == Axis.SELF;
    }



    public boolean isAbbreviatedDDot() {
        return _nodeType == NodeTest.ANODE && _axis == Axis.PARENT;
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {

        _hadPredicates = hasPredicates();

        if (isAbbreviatedDot()) {
            _type = (hasParentPattern() || hasPredicates() || hasParentLocationPath()) ?
                Type.NodeSet : Type.Node;
        }
        else {
            _type = Type.NodeSet;
        }

        if (_predicates != null) {
            final int n = _predicates.size();
            for (int i = 0; i < n; i++) {
                final Expression pred = (Expression)_predicates.elementAt(i);
                pred.typeCheck(stable);
            }
        }

        return _type;
    }


    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        translateStep(classGen, methodGen, hasPredicates() ? _predicates.size() - 1 : -1);
    }

    private void translateStep(ClassGenerator classGen,
                               MethodGenerator methodGen,
                               int predicateIndex) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        if (predicateIndex >= 0) {
            translatePredicates(classGen, methodGen, predicateIndex);
        } else {
            int star = 0;
            String name = null;
            final XSLTC xsltc = getParser().getXSLTC();

            if (_nodeType >= DTM.NTYPES) {
                final Vector ni = xsltc.getNamesIndex();

                name = (String)ni.elementAt(_nodeType-DTM.NTYPES);
                star = name.lastIndexOf('*');
            }

            if (_axis == Axis.ATTRIBUTE && _nodeType != NodeTest.ATTRIBUTE
                && _nodeType != NodeTest.ANODE && !hasParentPattern()
                && star == 0)
            {
                int iter = cpg.addInterfaceMethodref(DOM_INTF,
                                                     "getTypedAxisIterator",
                                                     "(II)"+NODE_ITERATOR_SIG);
                il.append(methodGen.loadDOM());
                il.append(new PUSH(cpg, Axis.ATTRIBUTE));
                il.append(new PUSH(cpg, _nodeType));
                il.append(new INVOKEINTERFACE(iter, 3));
                return;
            }

            SyntaxTreeNode parent = getParent();
            if (isAbbreviatedDot()) {
                if (_type == Type.Node) {
                    il.append(methodGen.loadContextNode());
                }
                else {
                    if (parent instanceof ParentLocationPath){
                        int init = cpg.addMethodref(SINGLETON_ITERATOR,
                                                    "<init>",
                                                    "("+NODE_SIG+")V");
                        il.append(new NEW(cpg.addClass(SINGLETON_ITERATOR)));
                        il.append(DUP);
                        il.append(methodGen.loadContextNode());
                        il.append(new INVOKESPECIAL(init));
                    } else {
                        int git = cpg.addInterfaceMethodref(DOM_INTF,
                                                "getAxisIterator",
                                                "(I)"+NODE_ITERATOR_SIG);
                        il.append(methodGen.loadDOM());
                        il.append(new PUSH(cpg, _axis));
                        il.append(new INVOKEINTERFACE(git, 2));
                    }
                }
                return;
            }

            public void translatePredicates(ClassGenerator classGen,
                                    MethodGenerator methodGen,
                                    int predicateIndex) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        int idx = 0;

        if (predicateIndex < 0) {
            translateStep(classGen, methodGen, predicateIndex);
        }
        else {
            final Predicate predicate = (Predicate) _predicates.get(predicateIndex--);

            if (predicate.isNodeValueTest()) {
                Step step = predicate.getStep();

                il.append(methodGen.loadDOM());
                if (step.isAbbreviatedDot()) {
                    translateStep(classGen, methodGen, predicateIndex);
                    il.append(new ICONST(DOM.RETURN_CURRENT));
                }
                else {
                    ParentLocationPath path = new ParentLocationPath(this, step);
                    _parent = step._parent = path;      try {
                        path.typeCheck(getParser().getSymbolTable());
                    }
                    catch (TypeCheckError e) { }
                    translateStep(classGen, methodGen, predicateIndex);
                    path.translateStep(classGen, methodGen);
                    il.append(new ICONST(DOM.RETURN_PARENT));
                }
                predicate.translate(classGen, methodGen);
                idx = cpg.addInterfaceMethodref(DOM_INTF,
                                                GET_NODE_VALUE_ITERATOR,
                                                GET_NODE_VALUE_ITERATOR_SIG);
                il.append(new INVOKEINTERFACE(idx, 5));
            }
            public String toString() {
        final StringBuffer buffer = new StringBuffer("step(\"");
        buffer.append(Axis.getNames(_axis)).append("\", ").append(_nodeType);
        if (_predicates != null) {
            final int n = _predicates.size();
            for (int i = 0; i < n; i++) {
                final Predicate pred = (Predicate)_predicates.elementAt(i);
                buffer.append(", ").append(pred.toString());
            }
        }
        return buffer.append(')').toString();
    }
}
