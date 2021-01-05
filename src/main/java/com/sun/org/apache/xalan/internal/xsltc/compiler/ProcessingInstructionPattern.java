



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;


final class ProcessingInstructionPattern extends StepPattern {

    private String _name = null;
    private boolean _typeChecked = false;


    public ProcessingInstructionPattern(String name) {
        super(Axis.CHILD, DTM.PROCESSING_INSTRUCTION_NODE, null);
        _name = name;
        }


     public double getDefaultPriority() {
        return (_name != null) ? 0.0 : -0.5;
     }
    public String toString() {
        if (_predicates == null)
            return "processing-instruction("+_name+")";
        else
            return "processing-instruction("+_name+")"+_predicates;
    }

    public void reduceKernelPattern() {
        _typeChecked = true;
    }

    public boolean isWildcard() {
        return false;
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (hasPredicates()) {
            final int n = _predicates.size();
            for (int i = 0; i < n; i++) {
                final Predicate pred = (Predicate)_predicates.elementAt(i);
                pred.typeCheck(stable);
            }
        }
        return Type.NodeSet;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        int gname = cpg.addInterfaceMethodref(DOM_INTF,
                                              "getNodeName",
                                              "(I)Ljava/lang/String;");
        int cmp = cpg.addMethodref(STRING_CLASS,
                                   "equals", "(Ljava/lang/Object;)Z");

        il.append(methodGen.loadCurrentNode());
        il.append(SWAP);

        il.append(methodGen.storeCurrentNode());

        if (!_typeChecked) {
            il.append(methodGen.loadCurrentNode());
            final int getType = cpg.addInterfaceMethodref(DOM_INTF,
                                                          "getExpandedTypeID",
                                                          "(I)I");
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadCurrentNode());
            il.append(new INVOKEINTERFACE(getType, 2));
            il.append(new PUSH(cpg, DTM.PROCESSING_INSTRUCTION_NODE));
            _falseList.add(il.append(new IF_ICMPEQ(null)));
        }

        il.append(new PUSH(cpg, _name));
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadCurrentNode());
        il.append(new INVOKEINTERFACE(gname, 2));
        il.append(new INVOKEVIRTUAL(cmp));
        _falseList.add(il.append(new IFEQ(null)));

        if (hasPredicates()) {
            final int n = _predicates.size();
            for (int i = 0; i < n; i++) {
                Predicate pred = (Predicate)_predicates.elementAt(i);
                Expression exp = pred.getExpr();
                exp.translateDesynthesized(classGen, methodGen);
                _trueList.append(exp._trueList);
                _falseList.append(exp._falseList);
            }
        }

        InstructionHandle restore;
        restore = il.append(methodGen.storeCurrentNode());
        backPatchTrueList(restore);
        BranchHandle skipFalse = il.append(new GOTO(null));

        restore = il.append(methodGen.storeCurrentNode());
        backPatchFalseList(restore);
        _falseList.add(il.append(new GOTO(null)));

        skipFalse.setTarget(il.append(NOP));
    }
}
