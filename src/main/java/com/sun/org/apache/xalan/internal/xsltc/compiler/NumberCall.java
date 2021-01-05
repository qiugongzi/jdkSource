
final class NumberCall extends FunctionCall {

    public NumberCall(QName fname, Vector arguments) {
        super(fname, arguments);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (argumentCount() > 0) {
            argument().typeCheck(stable);
        }
        return _type = Type.Real;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        final InstructionList il = methodGen.getInstructionList();
        Type targ;

        if (argumentCount() == 0) {
            il.append(methodGen.loadContextNode());
            targ = Type.Node;
        }
        else {
            final Expression arg = argument();
            arg.translate(classGen, methodGen);
            arg.startIterator(classGen, methodGen);
            targ = arg.getType();
        }

        if (!targ.identicalTo(Type.Real)) {
            targ.translateTo(classGen, methodGen, Type.Real);
        }
    }
}
