



package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;


public final class NodeSetType extends Type {
    protected NodeSetType() {}

    public String toString() {
        return "node-set";
    }

    public boolean identicalTo(Type other) {
        return this == other;
    }

    public String toSignature() {
        return NODE_ITERATOR_SIG;
    }

    public com.sun.org.apache.bcel.internal.generic.Type toJCType() {
        return new com.sun.org.apache.bcel.internal.generic.ObjectType(NODE_ITERATOR);
    }


    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            Type type) {
        if (type == Type.String) {
            translateTo(classGen, methodGen, (StringType) type);
        }
        else if (type == Type.Boolean) {
            translateTo(classGen, methodGen, (BooleanType) type);
        }
        else if (type == Type.Real) {
            translateTo(classGen, methodGen, (RealType) type);
        }
        else if (type == Type.Node) {
            translateTo(classGen, methodGen, (NodeType) type);
        }
        else if (type == Type.Reference) {
            translateTo(classGen, methodGen, (ReferenceType) type);
        }
        else if (type == Type.Object) {
            translateTo(classGen, methodGen, (ObjectType) type);
        }
        else {
            ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
                                        toString(), type.toString());
            classGen.getParser().reportError(Constants.FATAL, err);
        }
    }


    public void translateFrom(ClassGenerator classGen,
        MethodGenerator methodGen, Class clazz)
    {

        InstructionList il = methodGen.getInstructionList();
        ConstantPoolGen cpg = classGen.getConstantPool();
        if (clazz.getName().equals("org.w3c.dom.NodeList")) {
           il.append(classGen.loadTranslet());   il.append(methodGen.loadDOM());       final int convert = cpg.addMethodref(BASIS_LIBRARY_CLASS,
                                        "nodeList2Iterator",
                                        "("
                                         + "Lorg/w3c/dom/NodeList;"
                                         + TRANSLET_INTF_SIG
                                         + DOM_INTF_SIG
                                         + ")" + NODE_ITERATOR_SIG );
           il.append(new INVOKESTATIC(convert));
        }
        else if (clazz.getName().equals("org.w3c.dom.Node")) {
           il.append(classGen.loadTranslet());   il.append(methodGen.loadDOM());       final int convert = cpg.addMethodref(BASIS_LIBRARY_CLASS,
                                        "node2Iterator",
                                        "("
                                         + "Lorg/w3c/dom/Node;"
                                         + TRANSLET_INTF_SIG
                                         + DOM_INTF_SIG
                                         + ")" + NODE_ITERATOR_SIG );
           il.append(new INVOKESTATIC(convert));
        }
        else {
            ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
                toString(), clazz.getName());
            classGen.getParser().reportError(Constants.FATAL, err);
        }
    }



    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            BooleanType type) {
        final InstructionList il = methodGen.getInstructionList();
        FlowList falsel = translateToDesynthesized(classGen, methodGen, type);
        il.append(ICONST_1);
        final BranchHandle truec = il.append(new GOTO(null));
        falsel.backPatch(il.append(ICONST_0));
        truec.setTarget(il.append(NOP));
    }


    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            StringType type) {
        final InstructionList il = methodGen.getInstructionList();
        getFirstNode(classGen, methodGen);
        il.append(DUP);
        final BranchHandle falsec = il.append(new IFLT(null));
        Type.Node.translateTo(classGen, methodGen, type);
        final BranchHandle truec = il.append(new GOTO(null));
        falsec.setTarget(il.append(POP));
        il.append(new PUSH(classGen.getConstantPool(), ""));
        truec.setTarget(il.append(NOP));
    }


    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            RealType type) {
        translateTo(classGen, methodGen, Type.String);
        Type.String.translateTo(classGen, methodGen, Type.Real);
    }


    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            NodeType type) {
        getFirstNode(classGen, methodGen);
    }


    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            ObjectType type) {
            methodGen.getInstructionList().append(NOP);
    }


    public FlowList translateToDesynthesized(ClassGenerator classGen,
                                             MethodGenerator methodGen,
                                             BooleanType type) {
        final InstructionList il = methodGen.getInstructionList();
        getFirstNode(classGen, methodGen);
        return new FlowList(il.append(new IFLT(null)));
    }


    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            ReferenceType type) {
        methodGen.getInstructionList().append(NOP);
    }


    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen,
                            Class clazz) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final String className = clazz.getName();

        il.append(methodGen.loadDOM());
        il.append(SWAP);

        if (className.equals("org.w3c.dom.Node")) {
            int index = cpg.addInterfaceMethodref(DOM_INTF,
                                                  MAKE_NODE,
                                                  MAKE_NODE_SIG2);
            il.append(new INVOKEINTERFACE(index, 2));
        }
        else if (className.equals("org.w3c.dom.NodeList") ||
                 className.equals("java.lang.Object")) {
            int index = cpg.addInterfaceMethodref(DOM_INTF,
                                                  MAKE_NODE_LIST,
                                                  MAKE_NODE_LIST_SIG2);
            il.append(new INVOKEINTERFACE(index, 2));
        }
        else if (className.equals("java.lang.String")) {
            int next = cpg.addInterfaceMethodref(NODE_ITERATOR,
                                                 "next", "()I");
            int index = cpg.addInterfaceMethodref(DOM_INTF,
                                                 GET_NODE_VALUE,
                                                 "(I)"+STRING_SIG);

            il.append(new INVOKEINTERFACE(next, 1));
            il.append(new INVOKEINTERFACE(index, 2));

        }
        else {
            ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
                                        toString(), className);
            classGen.getParser().reportError(Constants.FATAL, err);
        }
    }


    private void getFirstNode(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref(NODE_ITERATOR,
                                                                NEXT,
                                                                NEXT_SIG), 1));
    }


    public void translateBox(ClassGenerator classGen,
                             MethodGenerator methodGen) {
        translateTo(classGen, methodGen, Type.Reference);
    }


    public void translateUnBox(ClassGenerator classGen,
                               MethodGenerator methodGen) {
        methodGen.getInstructionList().append(NOP);
    }


    public String getClassName() {
        return(NODE_ITERATOR);
    }


    public Instruction LOAD(int slot) {
        return new ALOAD(slot);
    }

    public Instruction STORE(int slot) {
        return new ASTORE(slot);
    }
}
