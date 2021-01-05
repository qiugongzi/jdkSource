
final class FloorCall extends FunctionCall {
    public FloorCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        argument().translate(classGen, methodGen);
        methodGen.getInstructionList()
            .append(new INVOKESTATIC(classGen.getConstantPool()
                                     .addMethodref(MATH_CLASS,
                                                   "floor", "(D)D")));
    }
}
