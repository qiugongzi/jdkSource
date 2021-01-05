
final class StringLengthCall extends FunctionCall {
    public StringLengthCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (argumentCount() > 0) {
            argument().translate(classGen, methodGen);
        }
        else {
            il.append(methodGen.loadContextNode());
            Type.Node.translateTo(classGen, methodGen, Type.String);
        }
        il.append(new INVOKESTATIC(cpg.addMethodref(BASIS_LIBRARY_CLASS,
                                                     "getStringLength",
                                                     "(Ljava/lang/String;)I")));
    }
}
