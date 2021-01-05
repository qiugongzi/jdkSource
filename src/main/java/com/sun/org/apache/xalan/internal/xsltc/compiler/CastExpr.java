



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.SIPUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MultiHashtable;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.dtm.Axis;


final class CastExpr extends Expression {
    private final Expression _left;


    private static final MultiHashtable<Type, Type> InternalTypeMap = new MultiHashtable<>();

    static {
        InternalTypeMap.put(Type.Boolean, Type.Boolean);
        InternalTypeMap.put(Type.Boolean, Type.Real);
        InternalTypeMap.put(Type.Boolean, Type.String);
        InternalTypeMap.put(Type.Boolean, Type.Reference);
        InternalTypeMap.put(Type.Boolean, Type.Object);

        InternalTypeMap.put(Type.Real, Type.Real);
        InternalTypeMap.put(Type.Real, Type.Int);
        InternalTypeMap.put(Type.Real, Type.Boolean);
        InternalTypeMap.put(Type.Real, Type.String);
        InternalTypeMap.put(Type.Real, Type.Reference);
        InternalTypeMap.put(Type.Real, Type.Object);

        InternalTypeMap.put(Type.Int, Type.Int);
        InternalTypeMap.put(Type.Int, Type.Real);
        InternalTypeMap.put(Type.Int, Type.Boolean);
        InternalTypeMap.put(Type.Int, Type.String);
        InternalTypeMap.put(Type.Int, Type.Reference);
        InternalTypeMap.put(Type.Int, Type.Object);

        InternalTypeMap.put(Type.String, Type.String);
        InternalTypeMap.put(Type.String, Type.Boolean);
        InternalTypeMap.put(Type.String, Type.Real);
        InternalTypeMap.put(Type.String, Type.Reference);
        InternalTypeMap.put(Type.String, Type.Object);

        InternalTypeMap.put(Type.NodeSet, Type.NodeSet);
        InternalTypeMap.put(Type.NodeSet, Type.Boolean);
        InternalTypeMap.put(Type.NodeSet, Type.Real);
        InternalTypeMap.put(Type.NodeSet, Type.String);
        InternalTypeMap.put(Type.NodeSet, Type.Node);
        InternalTypeMap.put(Type.NodeSet, Type.Reference);
        InternalTypeMap.put(Type.NodeSet, Type.Object);

        InternalTypeMap.put(Type.Node, Type.Node);
        InternalTypeMap.put(Type.Node, Type.Boolean);
        InternalTypeMap.put(Type.Node, Type.Real);
        InternalTypeMap.put(Type.Node, Type.String);
        InternalTypeMap.put(Type.Node, Type.NodeSet);
        InternalTypeMap.put(Type.Node, Type.Reference);
        InternalTypeMap.put(Type.Node, Type.Object);

        InternalTypeMap.put(Type.ResultTree, Type.ResultTree);
        InternalTypeMap.put(Type.ResultTree, Type.Boolean);
        InternalTypeMap.put(Type.ResultTree, Type.Real);
        InternalTypeMap.put(Type.ResultTree, Type.String);
        InternalTypeMap.put(Type.ResultTree, Type.NodeSet);
        InternalTypeMap.put(Type.ResultTree, Type.Reference);
        InternalTypeMap.put(Type.ResultTree, Type.Object);

        InternalTypeMap.put(Type.Reference, Type.Reference);
        InternalTypeMap.put(Type.Reference, Type.Boolean);
        InternalTypeMap.put(Type.Reference, Type.Int);
        InternalTypeMap.put(Type.Reference, Type.Real);
        InternalTypeMap.put(Type.Reference, Type.String);
        InternalTypeMap.put(Type.Reference, Type.Node);
        InternalTypeMap.put(Type.Reference, Type.NodeSet);
        InternalTypeMap.put(Type.Reference, Type.ResultTree);
        InternalTypeMap.put(Type.Reference, Type.Object);

        InternalTypeMap.put(Type.Object, Type.String);

        InternalTypeMap.put(Type.Void, Type.String);

        InternalTypeMap.makeUnmodifiable();
    }

    private boolean _typeTest = false;


    public CastExpr(Expression left, Type type) throws TypeCheckError {
        _left = left;
        _type = type;           if ((_left instanceof Step) && (_type == Type.Boolean)) {
            Step step = (Step)_left;
            if ((step.getAxis() == Axis.SELF) && (step.getNodeType() != -1))
                _typeTest = true;
        }

        setParser(left.getParser());
        setParent(left.getParent());
        left.setParent(this);
        typeCheck(left.getParser().getSymbolTable());
    }

    public Expression getExpr() {
        return _left;
    }


    public boolean hasPositionCall() {
        return(_left.hasPositionCall());
    }

    public boolean hasLastCall() {
        return(_left.hasLastCall());
    }

    public String toString() {
        return "cast(" + _left + ", " + _type + ")";
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Type tleft = _left.getType();
        if (tleft == null) {
            tleft = _left.typeCheck(stable);
        }
        if (tleft instanceof NodeType) {
            tleft = Type.Node;  }
        else if (tleft instanceof ResultTreeType) {
            tleft = Type.ResultTree; }
        if (InternalTypeMap.maps(tleft, _type) != null) {
            return _type;
        }
        throw new TypeCheckError(new ErrorMsg(
            ErrorMsg.DATA_CONVERSION_ERR, tleft.toString(), _type.toString()));
    }

    public void translateDesynthesized(ClassGenerator classGen,
                                       MethodGenerator methodGen) {
        FlowList fl;
        final Type ltype = _left.getType();

        if (_typeTest) {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final InstructionList il = methodGen.getInstructionList();

            final int idx = cpg.addInterfaceMethodref(DOM_INTF,
                                                      "getExpandedTypeID",
                                                      "(I)I");
            il.append(new SIPUSH((short)((Step)_left).getNodeType()));
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadContextNode());
            il.append(new INVOKEINTERFACE(idx, 2));
            _falseList.add(il.append(new IF_ICMPNE(null)));
        }
        else {

            _left.translate(classGen, methodGen);
            if (_type != ltype) {
                _left.startIterator(classGen, methodGen);
                if (_type instanceof BooleanType) {
                    fl = ltype.translateToDesynthesized(classGen, methodGen,
                                                        _type);
                    if (fl != null) {
                        _falseList.append(fl);
                    }
                }
                else {
                    ltype.translateTo(classGen, methodGen, _type);
                }
            }
        }
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final Type ltype = _left.getType();
        _left.translate(classGen, methodGen);
        if (_type.identicalTo(ltype) == false) {
            _left.startIterator(classGen, methodGen);
            ltype.translateTo(classGen, methodGen, _type);
        }
    }
}
