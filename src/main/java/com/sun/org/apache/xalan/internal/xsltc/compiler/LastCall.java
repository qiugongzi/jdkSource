
final class LastCall extends FunctionCall {

    public LastCall(QName fname) {
        super(fname);
    }

    public boolean hasPositionCall() {
        return true;
    }

    public boolean hasLastCall() {
        return true;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();

        if (methodGen instanceof CompareGenerator) {
            il.append(((CompareGenerator)methodGen).loadLastNode());
        }
        else if (methodGen instanceof TestGenerator) {
            il.append(new ILOAD(LAST_INDEX));
        }
        else {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final int getLast = cpg.addInterfaceMethodref(NODE_ITERATOR,
                                                          "getLast",
                                                          "()I");
            il.append(methodGen.loadIterator());
            il.append(new INVOKEINTERFACE(getLast, 1));
        }
    }
}
