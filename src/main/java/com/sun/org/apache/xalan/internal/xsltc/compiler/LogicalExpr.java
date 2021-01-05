



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;


final class LogicalExpr extends Expression {

    public static final int OR  = 0;
    public static final int AND = 1;

    private final int  _op;     private Expression _left;   private Expression _right;  private static final String[] Ops = { "or", "and" };


    public LogicalExpr(int op, Expression left, Expression right) {
        _op = op;
        (_left = left).setParent(this);
        (_right = right).setParent(this);
    }


    public boolean hasPositionCall() {
        return (_left.hasPositionCall() || _right.hasPositionCall());
    }


    public boolean hasLastCall() {
            return (_left.hasLastCall() || _right.hasLastCall());
    }


    public Object evaluateAtCompileTime() {
        final Object leftb = _left.evaluateAtCompileTime();
        final Object rightb = _right.evaluateAtCompileTime();

        if (leftb == null || rightb == null) {
            return null;
        }

        if (_op == AND) {
            return (leftb == Boolean.TRUE && rightb == Boolean.TRUE) ?
                Boolean.TRUE : Boolean.FALSE;
        }
        else {
            return (leftb == Boolean.TRUE || rightb == Boolean.TRUE) ?
                Boolean.TRUE : Boolean.FALSE;
        }
    }


    public int getOp() {
        return(_op);
    }


    public void setParser(Parser parser) {
        super.setParser(parser);
        _left.setParser(parser);
        _right.setParser(parser);
    }


    public String toString() {
        return Ops[_op] + '(' + _left + ", " + _right + ')';
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Type tleft = _left.typeCheck(stable);
        Type tright = _right.typeCheck(stable);

        MethodType wantType = new MethodType(Type.Void, tleft, tright);
        MethodType haveType = lookupPrimop(stable, Ops[_op], wantType);

        if (haveType != null) {
            Type arg1 = (Type)haveType.argsType().elementAt(0);
            if (!arg1.identicalTo(tleft))
                _left = new CastExpr(_left, arg1);
            Type arg2 = (Type) haveType.argsType().elementAt(1);
            if (!arg2.identicalTo(tright))
                _right = new CastExpr(_right, arg1);
            return _type = haveType.resultType();
        }
        throw new TypeCheckError(this);
    }


    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        translateDesynthesized(classGen, methodGen);
        synthesize(classGen, methodGen);
    }


    public void translateDesynthesized(ClassGenerator classGen,
                                       MethodGenerator methodGen) {

        final InstructionList il = methodGen.getInstructionList();
        final SyntaxTreeNode parent = getParent();

        if (_op == AND) {

            _left.translateDesynthesized(classGen, methodGen);

            InstructionHandle middle = il.append(NOP);

            _right.translateDesynthesized(classGen, methodGen);

            InstructionHandle after = il.append(NOP);

            _falseList.append(_right._falseList.append(_left._falseList));

            if ((_left instanceof LogicalExpr) &&
                (((LogicalExpr)_left).getOp() == OR)) {
                _left.backPatchTrueList(middle);
            }
            else if (_left instanceof NotCall) {
                _left.backPatchTrueList(middle);
            }
            else {
                _trueList.append(_left._trueList);
            }

            if ((_right instanceof LogicalExpr) &&
                (((LogicalExpr)_right).getOp() == OR)) {
                _right.backPatchTrueList(after);
            }
            else if (_right instanceof NotCall) {
                _right.backPatchTrueList(after);
            }
            else {
                _trueList.append(_right._trueList);
            }
        }
        else {
            _left.translateDesynthesized(classGen, methodGen);

            InstructionHandle ih = il.append(new GOTO(null));

            _right.translateDesynthesized(classGen, methodGen);

            _left._trueList.backPatch(ih);
            _left._falseList.backPatch(ih.getNext());

            _falseList.append(_right._falseList);
            _trueList.add(ih).append(_right._trueList);
        }
    }
}
