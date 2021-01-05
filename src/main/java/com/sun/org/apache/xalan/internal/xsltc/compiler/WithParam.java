


package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;


final class WithParam extends Instruction {


    private QName _name;


    protected String _escapedName;


    private Expression _select;


    private LocalVariableGen _domAdapter;


    private boolean _doParameterOptimization = false;


    public void display(int indent) {
        indent(indent);
        Util.println("with-param " + _name);
        if (_select != null) {
            indent(indent + IndentIncrement);
            Util.println("select " + _select.toString());
        }
        displayContents(indent + IndentIncrement);
    }


    public String getEscapedName() {
        return _escapedName;
    }


    public QName getName() {
        return _name;
    }


    public void setName(QName name) {
        _name = name;
        _escapedName = Util.escape(name.getStringRep());
    }


    public void setDoParameterOptimization(boolean flag) {
        _doParameterOptimization = flag;
    }


    public void parseContents(Parser parser) {
        final String name = getAttribute("name");
        if (name.length() > 0) {
            if (!XML11Char.isXML11ValidQName(name)) {
                ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_QNAME_ERR, name,
                                            this);
                parser.reportError(Constants.ERROR, err);
            }
            setName(parser.getQNameIgnoreDefaultNs(name));
        } else {
            reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "name");
        }

        final String select = getAttribute("select");
        if (select.length() > 0) {
            _select = parser.parseExpression(this, "select", null);
        }

        parseChildren(parser);
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (_select != null) {
            final Type tselect = _select.typeCheck(stable);
            if (tselect instanceof ReferenceType == false) {
                _select = new CastExpr(_select, Type.Reference);
            }
        } else {
            typeCheckContents(stable);
        }
        return Type.Void;
    }


    public void translateValue(ClassGenerator classGen,
                               MethodGenerator methodGen)
    {
        if (_select != null) {
            _select.translate(classGen, methodGen);
            _select.startIterator(classGen, methodGen);
        } else if (hasContents()) {
            final InstructionList il = methodGen.getInstructionList();
            compileResultTree(classGen, methodGen);
            _domAdapter = methodGen.addLocalVariable2("@" + _escapedName,
                                                      Type.ResultTree.toJCType(),
                                                      il.getEnd());
            il.append(DUP);
            il.append(new ASTORE(_domAdapter.getIndex()));
        } else {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final InstructionList il = methodGen.getInstructionList();
            il.append(new PUSH(cpg, Constants.EMPTYSTRING));
        }
    }


    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        if (_doParameterOptimization) {
            translateValue(classGen, methodGen);
            return;
        }

        String name = Util.escape(getEscapedName());

        il.append(classGen.loadTranslet());

        il.append(new PUSH(cpg, name)); translateValue(classGen, methodGen);
        il.append(new PUSH(cpg, false));
        il.append(new INVOKEVIRTUAL(cpg.addMethodref(TRANSLET_CLASS,
                                                     ADD_PARAMETER,
                                                     ADD_PARAMETER_SIG)));
        il.append(POP); }


    public void releaseResultTree(ClassGenerator classGen,
                                  MethodGenerator methodGen)
    {
        if (_domAdapter != null) {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final InstructionList il = methodGen.getInstructionList();
            if (classGen.getStylesheet().callsNodeset() &&
                classGen.getDOMClass().equals(MULTI_DOM_CLASS))
            {
                final int removeDA =
                    cpg.addMethodref(MULTI_DOM_CLASS, "removeDOMAdapter",
                                     "(" + DOM_ADAPTER_SIG + ")V");
                il.append(methodGen.loadDOM());
                il.append(new CHECKCAST(cpg.addClass(MULTI_DOM_CLASS)));
                il.append(new ALOAD(_domAdapter.getIndex()));
                il.append(new CHECKCAST(cpg.addClass(DOM_ADAPTER_CLASS)));
                il.append(new INVOKEVIRTUAL(removeDA));
            }
            final int release =
                cpg.addInterfaceMethodref(DOM_IMPL_CLASS, "release", "()V");
            il.append(new ALOAD(_domAdapter.getIndex()));
            il.append(new INVOKEINTERFACE(release, 1));
            _domAdapter.setEnd(il.getEnd());
            methodGen.removeLocalVariable(_domAdapter);
            _domAdapter = null;
        }
    }
}
