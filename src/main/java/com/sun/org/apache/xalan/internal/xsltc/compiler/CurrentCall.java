
final class CurrentCall extends FunctionCall {
    public CurrentCall(QName fname) {
        super(fname);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        methodGen.getInstructionList().append(methodGen.loadCurrentNode());
    }
}
