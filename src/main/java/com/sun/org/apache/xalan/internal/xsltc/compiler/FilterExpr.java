



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;


class FilterExpr extends Expression {


    private Expression   _primary;


    private final Vector _predicates;

    public FilterExpr(Expression primary, Vector predicates) {
        _primary = primary;
        _predicates = predicates;
        primary.setParent(this);
    }

    protected Expression getExpr() {
        if (_primary instanceof CastExpr)
            return ((CastExpr)_primary).getExpr();
        else
            return _primary;
    }

    public void setParser(Parser parser) {
        super.setParser(parser);
        _primary.setParser(parser);
        if (_predicates != null) {
            final int n = _predicates.size();
            for (int i = 0; i < n; i++) {
                final Expression exp = (Expression)_predicates.elementAt(i);
                exp.setParser(parser);
                exp.setParent(this);
            }
        }
    }

    public String toString() {
        return "filter-expr(" + _primary + ", " + _predicates + ")";
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Type ptype = _primary.typeCheck(stable);
        boolean canOptimize = _primary instanceof KeyCall;

        if (ptype instanceof NodeSetType == false) {
            if (ptype instanceof ReferenceType)  {
                _primary = new CastExpr(_primary, Type.NodeSet);
            }
            else {
                throw new TypeCheckError(this);
            }
        }

        int n = _predicates.size();
        for (int i = 0; i < n; i++) {
            Predicate pred = (Predicate) _predicates.elementAt(i);

            if (!canOptimize) {
                pred.dontOptimize();
            }
            pred.typeCheck(stable);
        }
        return _type = Type.NodeSet;
    }


    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        translateFilterExpr(classGen, methodGen, _predicates == null ? -1 : _predicates.size() - 1);
    }

    private void translateFilterExpr(ClassGenerator classGen,
                                     MethodGenerator methodGen,
                                     int predicateIndex) {
        if (predicateIndex >= 0) {
            translatePredicates(classGen, methodGen, predicateIndex);
        }
        else {
            _primary.translate(classGen, methodGen);
        }
    }


    public void translatePredicates(ClassGenerator classGen,
                                    MethodGenerator methodGen,
                                    int predicateIndex) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        if (predicateIndex < 0) {
            translateFilterExpr(classGen, methodGen, predicateIndex);
        }
        else {
            Predicate predicate = (Predicate) _predicates.get(predicateIndex--);

            translatePredicates(classGen, methodGen, predicateIndex);

            if (predicate.isNthPositionFilter()) {
                int nthIteratorIdx = cpg.addMethodref(NTH_ITERATOR_CLASS,
                                       "<init>",
                                       "("+NODE_ITERATOR_SIG+"I)V");

                LocalVariableGen iteratorTemp
                        = methodGen.addLocalVariable("filter_expr_tmp1",
                                         Util.getJCRefType(NODE_ITERATOR_SIG),
                                         null, null);
                iteratorTemp.setStart(
                        il.append(new ASTORE(iteratorTemp.getIndex())));

                predicate.translate(classGen, methodGen);
                LocalVariableGen predicateValueTemp
                        = methodGen.addLocalVariable("filter_expr_tmp2",
                                         Util.getJCRefType("I"),
                                         null, null);
                predicateValueTemp.setStart(
                        il.append(new ISTORE(predicateValueTemp.getIndex())));

                il.append(new NEW(cpg.addClass(NTH_ITERATOR_CLASS)));
                il.append(DUP);
                iteratorTemp.setEnd(
                        il.append(new ALOAD(iteratorTemp.getIndex())));
                predicateValueTemp.setEnd(
                        il.append(new ILOAD(predicateValueTemp.getIndex())));
                il.append(new INVOKESPECIAL(nthIteratorIdx));
            } else {
                    final int initCNLI = cpg.addMethodref(CURRENT_NODE_LIST_ITERATOR,
                                                      "<init>",
                                                      "("+NODE_ITERATOR_SIG+"Z"+
                                                      CURRENT_NODE_LIST_FILTER_SIG +
                                                      NODE_SIG+TRANSLET_SIG+")V");

                LocalVariableGen nodeIteratorTemp =
                    methodGen.addLocalVariable("filter_expr_tmp1",
                                               Util.getJCRefType(NODE_ITERATOR_SIG),
                                               null, null);
                nodeIteratorTemp.setStart(
                        il.append(new ASTORE(nodeIteratorTemp.getIndex())));

                predicate.translate(classGen, methodGen);
                LocalVariableGen filterTemp =
                    methodGen.addLocalVariable("filter_expr_tmp2",
                                  Util.getJCRefType(CURRENT_NODE_LIST_FILTER_SIG),
                                  null, null);
                filterTemp.setStart(il.append(new ASTORE(filterTemp.getIndex())));

                il.append(new NEW(cpg.addClass(CURRENT_NODE_LIST_ITERATOR)));
                il.append(DUP);

                nodeIteratorTemp.setEnd(
                        il.append(new ALOAD(nodeIteratorTemp.getIndex())));
                il.append(ICONST_1);
                filterTemp.setEnd(il.append(new ALOAD(filterTemp.getIndex())));
                il.append(methodGen.loadCurrentNode());
                il.append(classGen.loadTranslet());
                il.append(new INVOKESPECIAL(initCNLI));
            }
        }
    }
}
