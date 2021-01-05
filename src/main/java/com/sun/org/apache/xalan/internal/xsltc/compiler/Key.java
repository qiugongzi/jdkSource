



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IFGE;
import com.sun.org.apache.bcel.internal.generic.IFGT;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.utils.XML11Char;


final class Key extends TopLevelElement {


    private QName _name;


    private Pattern _match;


    private Expression _use;


    private Type _useType;


    public void parseContents(Parser parser) {

        final String name = getAttribute("name");
        if (!XML11Char.isXML11ValidQName(name)){
            ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_QNAME_ERR, name, this);
            parser.reportError(Constants.ERROR, err);
        }

        _name = parser.getQNameIgnoreDefaultNs(name);
        getSymbolTable().addKey(_name, this);

        _match = parser.parsePattern(this, "match", null);
        _use = parser.parseExpression(this, "use", null);

        if (_name == null) {
            reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "name");
            return;
        }
        if (_match.isDummy()) {
            reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "match");
            return;
        }
        if (_use.isDummy()) {
            reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "use");
            return;
        }
    }


    public String getName() {
        return _name.toString();
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        _match.typeCheck(stable);

        _useType = _use.typeCheck(stable);
        if (_useType instanceof StringType == false &&
            _useType instanceof NodeSetType == false)
        {
            _use = new CastExpr(_use, Type.String);
        }

        return Type.Void;
    }


    public void traverseNodeSet(ClassGenerator classGen,
                                MethodGenerator methodGen,
                                int buildKeyIndex) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        final int getNodeValue = cpg.addInterfaceMethodref(DOM_INTF,
                                                           GET_NODE_VALUE,
                                                           "(I)"+STRING_SIG);

        final int getNodeIdent = cpg.addInterfaceMethodref(DOM_INTF,
                                                           "getNodeIdent",
                                                           "(I)"+NODE_SIG);

        final int keyDom = cpg.addMethodref(TRANSLET_CLASS,
                                         "setKeyIndexDom",
                                         "("+STRING_SIG+DOM_INTF_SIG+")V");


        final LocalVariableGen parentNode =
            methodGen.addLocalVariable("parentNode",
                                       Util.getJCRefType("I"),
                                       null, null);

        parentNode.setStart(il.append(new ISTORE(parentNode.getIndex())));

        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.loadIterator());

        _use.translate(classGen, methodGen);
        _use.startIterator(classGen, methodGen);
        il.append(methodGen.storeIterator());

        final BranchHandle nextNode = il.append(new GOTO(null));
        final InstructionHandle loop = il.append(NOP);

        il.append(classGen.loadTranslet());
        il.append(new PUSH(cpg, _name.toString()));
        parentNode.setEnd(il.append(new ILOAD(parentNode.getIndex())));

        il.append(methodGen.loadDOM());
        il.append(methodGen.loadCurrentNode());
        il.append(new INVOKEINTERFACE(getNodeValue, 2));

        il.append(new INVOKEVIRTUAL(buildKeyIndex));

        il.append(classGen.loadTranslet());
        il.append(new PUSH(cpg, getName()));
        il.append(methodGen.loadDOM());
        il.append(new INVOKEVIRTUAL(keyDom));

        nextNode.setTarget(il.append(methodGen.loadIterator()));
        il.append(methodGen.nextNode());

        il.append(DUP);
        il.append(methodGen.storeCurrentNode());
        il.append(new IFGE(loop)); il.append(methodGen.storeIterator());
        il.append(methodGen.storeCurrentNode());
    }


    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int current = methodGen.getLocalIndex("current");

        final int key = cpg.addMethodref(TRANSLET_CLASS,
                                         "buildKeyIndex",
                                         "("+STRING_SIG+"I"+STRING_SIG+")V");

        final int keyDom = cpg.addMethodref(TRANSLET_CLASS,
                                         "setKeyIndexDom",
                                         "("+STRING_SIG+DOM_INTF_SIG+")V");

        final int getNodeIdent = cpg.addInterfaceMethodref(DOM_INTF,
                                                           "getNodeIdent",
                                                           "(I)"+NODE_SIG);

        final int git = cpg.addInterfaceMethodref(DOM_INTF,
                                                  "getAxisIterator",
                                                  "(I)"+NODE_ITERATOR_SIG);

        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.loadIterator());

        il.append(methodGen.loadDOM());
        il.append(new PUSH(cpg,Axis.DESCENDANT));
        il.append(new INVOKEINTERFACE(git, 2));

        il.append(methodGen.loadCurrentNode());
        il.append(methodGen.setStartNode());
        il.append(methodGen.storeIterator());

        final BranchHandle nextNode = il.append(new GOTO(null));
        final InstructionHandle loop = il.append(NOP);

        il.append(methodGen.loadCurrentNode());
        _match.translate(classGen, methodGen);
        _match.synthesize(classGen, methodGen); final BranchHandle skipNode = il.append(new IFEQ(null));

        if (_useType instanceof NodeSetType) {
            il.append(methodGen.loadCurrentNode());
            traverseNodeSet(classGen, methodGen, key);
        }
        else {
            il.append(classGen.loadTranslet());
            il.append(DUP);
            il.append(new PUSH(cpg, _name.toString()));
            il.append(DUP_X1);
            il.append(methodGen.loadCurrentNode());
            _use.translate(classGen, methodGen);
            il.append(new INVOKEVIRTUAL(key));

            il.append(methodGen.loadDOM());
            il.append(new INVOKEVIRTUAL(keyDom));
        }

        final InstructionHandle skip = il.append(NOP);

        il.append(methodGen.loadIterator());
        il.append(methodGen.nextNode());
        il.append(DUP);
        il.append(methodGen.storeCurrentNode());
        il.append(new IFGT(loop));

        il.append(methodGen.storeIterator());
        il.append(methodGen.storeCurrentNode());

        nextNode.setTarget(skip);
        skipNode.setTarget(skip);
    }
}
