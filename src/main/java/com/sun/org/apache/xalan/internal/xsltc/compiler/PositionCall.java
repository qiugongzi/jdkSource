
final class PositionCall extends FunctionCall {

    public PositionCall(QName fname) {
        super(fname);
    }

    public boolean hasPositionCall() {
        return true;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();

        if (methodGen instanceof CompareGenerator) {
            il.append(((CompareGenerator)methodGen).loadCurrentNode());
        }
        else if (methodGen instanceof TestGenerator) {
            il.append(new ILOAD(POSITION_INDEX));
        }
        else {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final int index = cpg.addInterfaceMethodref(NODE_ITERATOR,
                                                       "getPosition",
                                                       "()I");

            il.append(methodGen.loadIterator());
            il.append(new INVOKEINTERFACE(index,1));
        }
    }
}
