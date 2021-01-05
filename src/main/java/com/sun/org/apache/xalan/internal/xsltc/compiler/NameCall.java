
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        final int getName = cpg.addInterfaceMethodref(DOM_INTF,
                                                      GET_NODE_NAME,
                                                      GET_NODE_NAME_SIG);
        super.translate(classGen, methodGen);
        il.append(new INVOKEINTERFACE(getName, 2));
    }
}
