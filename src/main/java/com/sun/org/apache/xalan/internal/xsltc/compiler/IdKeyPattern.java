



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;


abstract class IdKeyPattern extends LocationPathPattern {

    protected RelativePathPattern _left = null;;
    private String _index = null;
    private String _value = null;;

    public IdKeyPattern(String index, String value) {
        _index = index;
        _value = value;
    }

    public String getIndexName() {
        return(_index);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return Type.NodeSet;
    }

    public boolean isWildcard() {
        return false;
    }

    public void setLeft(RelativePathPattern left) {
        _left = left;
    }

    public StepPattern getKernelPattern() {
        return(null);
    }

    public void reduceKernelPattern() { }

    public String toString() {
        return "id/keyPattern(" + _index + ", " + _value + ')';
    }


    public void translate(ClassGenerator classGen,
                          MethodGenerator methodGen) {

        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        final int getKeyIndex = cpg.addMethodref(TRANSLET_CLASS,
                                                 "getKeyIndex",
                                                 "(Ljava/lang/String;)"+
                                                 KEY_INDEX_SIG);

        final int lookupId = cpg.addMethodref(KEY_INDEX_CLASS,
                                              "containsID",
                                              "(ILjava/lang/Object;)I");
        final int lookupKey = cpg.addMethodref(KEY_INDEX_CLASS,
                                               "containsKey",
                                               "(ILjava/lang/Object;)I");
        final int getNodeIdent = cpg.addInterfaceMethodref(DOM_INTF,
                                                           "getNodeIdent",
                                                           "(I)"+NODE_SIG);

        il.append(classGen.loadTranslet());
        il.append(new PUSH(cpg,_index));
        il.append(new INVOKEVIRTUAL(getKeyIndex));

        il.append(SWAP);
        il.append(new PUSH(cpg,_value));
        if (this instanceof IdPattern)
        {
            il.append(new INVOKEVIRTUAL(lookupId));
        }
        else
        {
            il.append(new INVOKEVIRTUAL(lookupKey));
        }

        _trueList.add(il.append(new IFNE(null)));
        _falseList.add(il.append(new GOTO(null)));
    }

}
