



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.ArrayList;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.FilterGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NumberType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TestGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;


final class Predicate extends Expression implements Closure {


    private Expression _exp = null;


    private boolean _canOptimize = true;


    private boolean _nthPositionFilter = false;


    private boolean _nthDescendant = false;


    int _ptype = -1;


    private String _className = null;


    private ArrayList _closureVars = null;


    private Closure _parentClosure = null;


    private Expression _value = null;


    private Step _step = null;


    public Predicate(Expression exp) {
        _exp = exp;
        _exp.setParent(this);

    }


    public void setParser(Parser parser) {
        super.setParser(parser);
        _exp.setParser(parser);
    }


    public boolean isNthPositionFilter() {
        return _nthPositionFilter;
    }


    public boolean isNthDescendant() {
        return _nthDescendant;
    }


    public void dontOptimize() {
        _canOptimize = false;
    }


    public boolean hasPositionCall() {
        return _exp.hasPositionCall();
    }


    public boolean hasLastCall() {
        return _exp.hasLastCall();
    }

    public boolean inInnerClass() {
        return (_className != null);
    }


    public Closure getParentClosure() {
        if (_parentClosure == null) {
            SyntaxTreeNode node = getParent();
            do {
                if (node instanceof Closure) {
                    _parentClosure = (Closure) node;
                    break;
                }
                if (node instanceof TopLevelElement) {
                    break;      }
                node = node.getParent();
            } while (node != null);
        }
        return _parentClosure;
    }


    public String getInnerClassName() {
        return _className;
    }


    public void addVariable(VariableRefBase variableRef) {
        if (_closureVars == null) {
            _closureVars = new ArrayList();
        }

        if (!_closureVars.contains(variableRef)) {
            _closureVars.add(variableRef);

            Closure parentClosure = getParentClosure();
            if (parentClosure != null) {
                parentClosure.addVariable(variableRef);
            }
        }
    }

    public int getPosType() {
        if (_ptype == -1) {
            SyntaxTreeNode parent = getParent();
            if (parent instanceof StepPattern) {
                _ptype = ((StepPattern)parent).getNodeType();
            }
            else if (parent instanceof AbsoluteLocationPath) {
                AbsoluteLocationPath path = (AbsoluteLocationPath)parent;
                Expression exp = path.getPath();
                if (exp instanceof Step) {
                    _ptype = ((Step)exp).getNodeType();
                }
            }
            else if (parent instanceof VariableRefBase) {
                final VariableRefBase ref = (VariableRefBase)parent;
                final VariableBase var = ref.getVariable();
                final Expression exp = var.getExpression();
                if (exp instanceof Step) {
                    _ptype = ((Step)exp).getNodeType();
                }
            }
            else if (parent instanceof Step) {
                _ptype = ((Step)parent).getNodeType();
            }
        }
        return _ptype;
    }

    public boolean parentIsPattern() {
        return (getParent() instanceof Pattern);
    }

    public Expression getExpr() {
        return _exp;
    }

    public String toString() {
        return "pred(" + _exp + ')';
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Type texp = _exp.typeCheck(stable);

        if (texp instanceof ReferenceType) {
            _exp = new CastExpr(_exp, texp = Type.Real);
        }

        if (texp instanceof ResultTreeType) {
            _exp = new CastExpr(_exp, Type.Boolean);
            _exp = new CastExpr(_exp, Type.Real);
            texp = _exp.typeCheck(stable);
        }

        if (texp instanceof NumberType) {
            if (texp instanceof IntType == false) {
                _exp = new CastExpr(_exp, Type.Int);
            }

            if (_canOptimize) {
                _nthPositionFilter =
                    !_exp.hasLastCall() && !_exp.hasPositionCall();

                if (_nthPositionFilter) {
                    SyntaxTreeNode parent = getParent();
                    _nthDescendant = (parent instanceof Step) &&
                        (parent.getParent() instanceof AbsoluteLocationPath);
                    return _type = Type.NodeSet;
                }
            }

           _nthPositionFilter = _nthDescendant = false;

           final QName position =
                getParser().getQNameIgnoreDefaultNs("position");
           final PositionCall positionCall =
                new PositionCall(position);
           positionCall.setParser(getParser());
           positionCall.setParent(this);

           _exp = new EqualityExpr(Operators.EQ, positionCall,
                                    _exp);
           if (_exp.typeCheck(stable) != Type.Boolean) {
               _exp = new CastExpr(_exp, Type.Boolean);
           }
           return _type = Type.Boolean;
        }
        else {
            if (texp instanceof BooleanType == false) {
                _exp = new CastExpr(_exp, Type.Boolean);
            }
            return _type = Type.Boolean;
        }
    }


    private void compileFilter(ClassGenerator classGen,
                               MethodGenerator methodGen) {
        TestGenerator testGen;
        LocalVariableGen local;
        FilterGenerator filterGen;

        _className = getXSLTC().getHelperClassName();
        filterGen = new FilterGenerator(_className,
                                        "java.lang.Object",
                                        toString(),
                                        ACC_PUBLIC | ACC_SUPER,
                                        new String[] {
                                            CURRENT_NODE_LIST_FILTER
                                        },
                                        classGen.getStylesheet());

        final ConstantPoolGen cpg = filterGen.getConstantPool();
        final int length = (_closureVars == null) ? 0 : _closureVars.size();

        for (int i = 0; i < length; i++) {
            VariableBase var = ((VariableRefBase) _closureVars.get(i)).getVariable();

            filterGen.addField(new Field(ACC_PUBLIC,
                                        cpg.addUtf8(var.getEscapedName()),
                                        cpg.addUtf8(var.getType().toSignature()),
                                        null, cpg.getConstantPool()));
        }

        final InstructionList il = new InstructionList();
        testGen = new TestGenerator(ACC_PUBLIC | ACC_FINAL,
                                    com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN,
                                    new com.sun.org.apache.bcel.internal.generic.Type[] {
                                        com.sun.org.apache.bcel.internal.generic.Type.INT,
                                        com.sun.org.apache.bcel.internal.generic.Type.INT,
                                        com.sun.org.apache.bcel.internal.generic.Type.INT,
                                        com.sun.org.apache.bcel.internal.generic.Type.INT,
                                        Util.getJCRefType(TRANSLET_SIG),
                                        Util.getJCRefType(NODE_ITERATOR_SIG)
                                    },
                                    new String[] {
                                        "node",
                                        "position",
                                        "last",
                                        "current",
                                        "translet",
                                        "iterator"
                                    },
                                    "test", _className, il, cpg);

        local = testGen.addLocalVariable("document",
                                         Util.getJCRefType(DOM_INTF_SIG),
                                         null, null);
        final String className = classGen.getClassName();
        il.append(filterGen.loadTranslet());
        il.append(new CHECKCAST(cpg.addClass(className)));
        il.append(new GETFIELD(cpg.addFieldref(className,
                                               DOM_FIELD, DOM_INTF_SIG)));
        local.setStart(il.append(new ASTORE(local.getIndex())));

        testGen.setDomIndex(local.getIndex());

        _exp.translate(filterGen, testGen);
        il.append(IRETURN);

        filterGen.addEmptyConstructor(ACC_PUBLIC);
        filterGen.addMethod(testGen);

        getXSLTC().dumpClass(filterGen.getJavaClass());
    }


    public boolean isBooleanTest() {
        return (_exp instanceof BooleanExpr);
    }


    public boolean isNodeValueTest() {
        if (!_canOptimize) return false;
        return (getStep() != null && getCompareValue() != null);
    }


    public Step getStep() {
        if (_step != null) {
            return _step;
        }

        if (_exp == null) {
            return null;
        }

        if (_exp instanceof EqualityExpr) {
            EqualityExpr exp = (EqualityExpr)_exp;
            Expression left = exp.getLeft();
            Expression right = exp.getRight();

            if (left instanceof CastExpr) {
                left = ((CastExpr) left).getExpr();
            }
            if (left instanceof Step) {
                _step = (Step) left;
            }

            if (right instanceof CastExpr) {
                right = ((CastExpr)right).getExpr();
            }
            if (right instanceof Step) {
                _step = (Step)right;
            }
        }
        return _step;
    }


    public Expression getCompareValue() {
        if (_value != null) {
            return _value;
        }

        if (_exp == null) {
            return null;
        }

        if (_exp instanceof EqualityExpr) {
            EqualityExpr exp = (EqualityExpr) _exp;
            Expression left = exp.getLeft();
            Expression right = exp.getRight();

            if (left instanceof LiteralExpr) {
                _value = left;
                return _value;
            }
            if (left instanceof VariableRefBase &&
                left.getType() == Type.String)
            {
                _value = left;
                return _value;
            }

            if (right instanceof LiteralExpr) {
                _value = right;
                return _value;
            }
            if (right instanceof VariableRefBase &&
                right.getType() == Type.String)
            {
                _value = right;
                return _value;
            }
        }
        return null;
    }


    public void translateFilter(ClassGenerator classGen,
                                MethodGenerator methodGen)
    {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        compileFilter(classGen, methodGen);

        il.append(new NEW(cpg.addClass(_className)));
        il.append(DUP);
        il.append(new INVOKESPECIAL(cpg.addMethodref(_className,
                                                     "<init>", "()V")));

        final int length = (_closureVars == null) ? 0 : _closureVars.size();

        for (int i = 0; i < length; i++) {
            VariableRefBase varRef = (VariableRefBase) _closureVars.get(i);
            VariableBase var = varRef.getVariable();
            Type varType = var.getType();

            il.append(DUP);

            Closure variableClosure = _parentClosure;
            while (variableClosure != null) {
                if (variableClosure.inInnerClass()) break;
                variableClosure = variableClosure.getParentClosure();
            }

            if (variableClosure != null) {
                il.append(ALOAD_0);
                il.append(new GETFIELD(
                    cpg.addFieldref(variableClosure.getInnerClassName(),
                        var.getEscapedName(), varType.toSignature())));
            }
            else {
                il.append(var.loadInstruction());
            }

            il.append(new PUTFIELD(
                    cpg.addFieldref(_className, var.getEscapedName(),
                        varType.toSignature())));
        }
    }


    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        if (_nthPositionFilter || _nthDescendant) {
            _exp.translate(classGen, methodGen);
        }
        else if (isNodeValueTest() && (getParent() instanceof Step)) {
            _value.translate(classGen, methodGen);
            il.append(new CHECKCAST(cpg.addClass(STRING_CLASS)));
            il.append(new PUSH(cpg, ((EqualityExpr)_exp).getOp()));
        }
        else {
            translateFilter(classGen, methodGen);
        }
    }
}
