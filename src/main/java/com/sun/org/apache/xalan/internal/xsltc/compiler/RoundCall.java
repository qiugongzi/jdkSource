
final class RoundCall extends FunctionCall {

    public RoundCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();


        argument().translate(classGen, methodGen);
                il.append(new INVOKESTATIC(cpg.addMethodref(BASIS_LIBRARY_CLASS,
                                                            "roundF", "(D)D")));
    }
}
