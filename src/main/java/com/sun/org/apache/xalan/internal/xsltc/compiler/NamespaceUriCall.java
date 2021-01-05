
    public void translate(ClassGenerator classGen,
                          MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();


        final int getNamespace = cpg.addInterfaceMethodref(DOM_INTF,
                                                           "getNamespaceName",
                                                           "(I)"+STRING_SIG);
        super.translate(classGen, methodGen);
        il.append(new INVOKEINTERFACE(getNamespace, 2));
    }
}
