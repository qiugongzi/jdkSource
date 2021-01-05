


package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;

import java.util.Vector;


final class CallTemplate extends Instruction {


    private QName _name;


    private SyntaxTreeNode[] _parameters = null;


    private Template _calleeTemplate = null;

    public void display(int indent) {
        indent(indent);
        System.out.print("CallTemplate");
        Util.println(" name " + _name);
        displayContents(indent + IndentIncrement);
    }

    public boolean hasWithParams() {
        return elementCount() > 0;
    }

    public void parseContents(Parser parser) {
        final String name = getAttribute("name");
        if (name.length() > 0) {
            if (!XML11Char.isXML11ValidQName(name)) {
                ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_QNAME_ERR, name, this);
                parser.reportError(Constants.ERROR, err);
            }
            _name = parser.getQNameIgnoreDefaultNs(name);
        }
        else {
            reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "name");
        }
        parseChildren(parser);
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        final Template template = stable.lookupTemplate(_name);
        if (template != null) {
            typeCheckContents(stable);
        }
        else {
            ErrorMsg err = new ErrorMsg(ErrorMsg.TEMPLATE_UNDEF_ERR,_name,this);
            throw new TypeCheckError(err);
        }
        return Type.Void;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final Stylesheet stylesheet = classGen.getStylesheet();
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        if (stylesheet.hasLocalParams() || hasContents()) {
            _calleeTemplate = getCalleeTemplate();

            if (_calleeTemplate != null) {
                buildParameterList();
            }
            else {
                final int push = cpg.addMethodref(TRANSLET_CLASS,
                                                  PUSH_PARAM_FRAME,
                                                  PUSH_PARAM_FRAME_SIG);
                il.append(classGen.loadTranslet());
                il.append(new INVOKEVIRTUAL(push));
                translateContents(classGen, methodGen);
            }
        }

        final String className = stylesheet.getClassName();
        String methodName = Util.escape(_name.toString());

        il.append(classGen.loadTranslet());
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadIterator());
        il.append(methodGen.loadHandler());
        il.append(methodGen.loadCurrentNode());

        StringBuffer methodSig = new StringBuffer("(" + DOM_INTF_SIG
            + NODE_ITERATOR_SIG + TRANSLET_OUTPUT_SIG + NODE_SIG);

        if (_calleeTemplate != null) {
            int numParams = _parameters.length;

            for (int i = 0; i < numParams; i++) {
                SyntaxTreeNode node = _parameters[i];
                methodSig.append(OBJECT_SIG);   if (node instanceof Param) {
                    il.append(ACONST_NULL);
                }
                else {  node.translate(classGen, methodGen);
                }
            }
        }

        methodSig.append(")V");
        il.append(new INVOKEVIRTUAL(cpg.addMethodref(className,
                                                     methodName,
                                                     methodSig.toString())));

        if (_parameters != null) {
            for (int i = 0; i < _parameters.length; i++) {
                if (_parameters[i] instanceof WithParam) {
                    ((WithParam)_parameters[i]).releaseResultTree(classGen, methodGen);
                }
            }
        }

        if (_calleeTemplate == null && (stylesheet.hasLocalParams() || hasContents())) {
            final int pop = cpg.addMethodref(TRANSLET_CLASS,
                                             POP_PARAM_FRAME,
                                             POP_PARAM_FRAME_SIG);
            il.append(classGen.loadTranslet());
            il.append(new INVOKEVIRTUAL(pop));
        }
    }


    public Template getCalleeTemplate() {
        Template foundTemplate
            = getXSLTC().getParser().getSymbolTable().lookupTemplate(_name);

        return foundTemplate.isSimpleNamedTemplate() ? foundTemplate : null;
    }


    private void buildParameterList() {
        Vector<Param> defaultParams = _calleeTemplate.getParameters();
        int numParams = defaultParams.size();
        _parameters = new SyntaxTreeNode[numParams];
        for (int i = 0; i < numParams; i++) {
            _parameters[i] = defaultParams.elementAt(i);
        }

        int count = elementCount();
        for (int i = 0; i < count; i++) {
            Object node = elementAt(i);

            if (node instanceof WithParam) {
                WithParam withParam = (WithParam)node;
                QName name = withParam.getName();

                for (int k = 0; k < numParams; k++) {
                    SyntaxTreeNode parm = _parameters[k];
                    if (parm instanceof Param
                        && ((Param)parm).getName().equals(name)) {
                        withParam.setDoParameterOptimization(true);
                        _parameters[k] = withParam;
                        break;
                    }
                    else if (parm instanceof WithParam
                        && ((WithParam)parm).getName().equals(name)) {
                        withParam.setDoParameterOptimization(true);
                        _parameters[k] = withParam;
                        break;
                    }
                }
            }
        }
     }
}
