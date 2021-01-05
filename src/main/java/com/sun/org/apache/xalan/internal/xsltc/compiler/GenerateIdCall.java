
final class GenerateIdCall extends FunctionCall {
    public GenerateIdCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        if (argumentCount() == 0) {
           il.append(methodGen.loadContextNode());
        }
        else {
            argument().translate(classGen, methodGen);
        }
        final ConstantPoolGen cpg = classGen.getConstantPool();
        il.append(new INVOKESTATIC(cpg.addMethodref(BASIS_LIBRARY_CLASS,
                                                    "generate_idF",

                                                    GET_NODE_NAME_SIG)));
    }
}
