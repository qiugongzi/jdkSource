


package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;


class VariableBase extends TopLevelElement {

    protected QName       _name;             protected String      _escapedName;      protected Type        _type;             protected boolean     _isLocal;          protected LocalVariableGen _local;       protected Instruction _loadInstruction;  protected Instruction _storeInstruction; protected Expression  _select;           protected String      select;            protected Vector<VariableRefBase> _refs = new Vector<>(2);

    protected boolean    _ignore = false;


    public void disable() {
        _ignore = true;
    }


    public void addReference(VariableRefBase vref) {
        _refs.addElement(vref);
    }


    public void copyReferences(VariableBase var) {
        final int size = _refs.size();
        for (int i = 0; i < size; i++) {
            var.addReference(_refs.get(i));
        }
    }


    public void mapRegister(MethodGenerator methodGen) {
        if (_local == null) {
            final InstructionList il = methodGen.getInstructionList();
            final String name = getEscapedName(); final com.sun.org.apache.bcel.internal.generic.Type varType = _type.toJCType();
            _local = methodGen.addLocalVariable2(name, varType, il.getEnd());
        }
    }


    public void unmapRegister(ClassGenerator classGen, MethodGenerator methodGen) {
        if (_local != null) {
            if (_type instanceof ResultTreeType) {
                final ConstantPoolGen cpg = classGen.getConstantPool();
                final InstructionList il = methodGen.getInstructionList();
                if (classGen.getStylesheet().callsNodeset() && classGen.getDOMClass().equals(MULTI_DOM_CLASS)) {
                    final int removeDA = cpg.addMethodref(MULTI_DOM_CLASS, "removeDOMAdapter", "(" + DOM_ADAPTER_SIG + ")V");
                    il.append(methodGen.loadDOM());
                    il.append(new CHECKCAST(cpg.addClass(MULTI_DOM_CLASS)));
                    il.append(loadInstruction());
                    il.append(new CHECKCAST(cpg.addClass(DOM_ADAPTER_CLASS)));
                    il.append(new INVOKEVIRTUAL(removeDA));
                }
                final int release = cpg.addInterfaceMethodref(DOM_IMPL_CLASS, "release", "()V");
                il.append(loadInstruction());
                il.append(new INVOKEINTERFACE(release, 1));
            }

            _local.setEnd(methodGen.getInstructionList().getEnd());
            methodGen.removeLocalVariable(_local);
            _refs = null;
            _local = null;
        }
    }


    public Instruction loadInstruction() {
        if (_loadInstruction == null) {
            _loadInstruction = _type.LOAD(_local.getIndex());
        }
        return _loadInstruction;
    }


    public Instruction storeInstruction() {
        if (_storeInstruction == null) {
            _storeInstruction = _type.STORE(_local.getIndex());
        }
        return _storeInstruction;
    }


    public Expression getExpression() {
        return(_select);
    }


    public String toString() {
        return("variable("+_name+")");
    }


    public void display(int indent) {
        indent(indent);
        System.out.println("Variable " + _name);
        if (_select != null) {
            indent(indent + IndentIncrement);
            System.out.println("select " + _select.toString());
        }
        displayContents(indent + IndentIncrement);
    }


    public Type getType() {
        return _type;
    }


    public QName getName() {
        return _name;
    }


    public String getEscapedName() {
        return _escapedName;
    }


    public void setName(QName name) {
        _name = name;
        _escapedName = Util.escape(name.getStringRep());
    }


    public boolean isLocal() {
        return _isLocal;
    }


    public void parseContents(Parser parser) {
        String name = getAttribute("name");

        if (name.length() > 0) {
            if (!XML11Char.isXML11ValidQName(name)) {
                ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_QNAME_ERR, name, this);
                parser.reportError(Constants.ERROR, err);
            }
            setName(parser.getQNameIgnoreDefaultNs(name));
        }
        else
            reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "name");

        VariableBase other = parser.lookupVariable(_name);
        if ((other != null) && (other.getParent() == getParent())) {
            reportError(this, parser, ErrorMsg.VARIABLE_REDEF_ERR, name);
        }

        select = getAttribute("select");
        if (select.length() > 0) {
            _select = getParser().parseExpression(this, "select", null);
            if (_select.isDummy()) {
                reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "select");
                return;
            }
        }

        parseChildren(parser);
    }


    public void translateValue(ClassGenerator classGen,
                               MethodGenerator methodGen) {
        if (_select != null) {
            _select.translate(classGen, methodGen);
            if (_select.getType() instanceof NodeSetType) {
                final ConstantPoolGen cpg = classGen.getConstantPool();
                final InstructionList il = methodGen.getInstructionList();

                final int initCNI = cpg.addMethodref(CACHED_NODE_LIST_ITERATOR_CLASS,
                                            "<init>",
                                            "("
                                            +NODE_ITERATOR_SIG
                                            +")V");
                il.append(new NEW(cpg.addClass(CACHED_NODE_LIST_ITERATOR_CLASS)));
                il.append(DUP_X1);
                il.append(SWAP);

                il.append(new INVOKESPECIAL(initCNI));
            }
            _select.startIterator(classGen, methodGen);
        }
        else if (hasContents()) {
            compileResultTree(classGen, methodGen);
        }
        else {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final InstructionList il = methodGen.getInstructionList();
            il.append(new PUSH(cpg, Constants.EMPTYSTRING));
        }
    }

}
