



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Iterator;
import java.util.Vector;


final class ForEach extends Instruction {

    private Expression _select;
    private Type       _type;

    public void display(int indent) {
        indent(indent);
        Util.println("ForEach");
        indent(indent + IndentIncrement);
        Util.println("select " + _select.toString());
        displayContents(indent + IndentIncrement);
    }

    public void parseContents(Parser parser) {
        _select = parser.parseExpression(this, "select", null);

        parseChildren(parser);

        if (_select.isDummy()) {
            reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "select");
        }
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        _type = _select.typeCheck(stable);

        if (_type instanceof ReferenceType || _type instanceof NodeType) {
            _select = new CastExpr(_select, Type.NodeSet);
            typeCheckContents(stable);
            return Type.Void;
        }
        if (_type instanceof NodeSetType||_type instanceof ResultTreeType) {
            typeCheckContents(stable);
            return Type.Void;
        }
        throw new TypeCheckError(this);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.loadIterator());

        final Vector sortObjects = new Vector();
        Iterator<SyntaxTreeNode> children = elements();
        while (children.hasNext()) {
            final SyntaxTreeNode child = children.next();
            if (child instanceof Sort) {
                sortObjects.addElement(child);
            }
        }

        if ((_type != null) && (_type instanceof ResultTreeType)) {
            il.append(methodGen.loadDOM());

            if (sortObjects.size() > 0) {
                ErrorMsg msg = new ErrorMsg(ErrorMsg.RESULT_TREE_SORT_ERR,this);
                getParser().reportError(WARNING, msg);
            }

            _select.translate(classGen, methodGen);
            _type.translateTo(classGen, methodGen, Type.NodeSet);
            il.append(SWAP);
            il.append(methodGen.storeDOM());
        }
        else {
            if (sortObjects.size() > 0) {
                Sort.translateSortIterator(classGen, methodGen,
                                           _select, sortObjects);
            }
            else {
                _select.translate(classGen, methodGen);
            }

            if (_type instanceof ReferenceType == false) {
                il.append(methodGen.loadContextNode());
                il.append(methodGen.setStartNode());
            }
        }


        il.append(methodGen.storeIterator());

        initializeVariables(classGen, methodGen);

        final BranchHandle nextNode = il.append(new GOTO(null));
        final InstructionHandle loop = il.append(NOP);

        translateContents(classGen, methodGen);

        nextNode.setTarget(il.append(methodGen.loadIterator()));
        il.append(methodGen.nextNode());
        il.append(DUP);
        il.append(methodGen.storeCurrentNode());
        il.append(new IFGT(loop));

        if ((_type != null) && (_type instanceof ResultTreeType)) {
            il.append(methodGen.storeDOM());
        }

        il.append(methodGen.storeIterator());
        il.append(methodGen.storeCurrentNode());
    }


    public void initializeVariables(ClassGenerator classGen,
                                   MethodGenerator methodGen) {
        final int n = elementCount();
        for (int i = 0; i < n; i++) {
            final SyntaxTreeNode child = getContents().get(i);
            if (child instanceof Variable) {
                Variable var = (Variable)child;
                var.initialize(classGen, methodGen);
            }
        }
    }

}
