
final class NotCall extends FunctionCall {
    public NotCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        argument().translate(classGen, methodGen);
        il.append(ICONST_1);
        il.append(IXOR);
    }

    public void translateDesynthesized(ClassGenerator classGen,
                                       MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        final Expression exp = argument();
        exp.translateDesynthesized(classGen, methodGen);
        final BranchHandle gotoh = il.append(new GOTO(null));
        _trueList = exp._falseList;
        _falseList = exp._trueList;
        _falseList.add(gotoh);
    }
}
