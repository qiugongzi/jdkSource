
    public void parseContents(Parser parser) {
        sPrefix = getAttribute("stylesheet-prefix");
        rPrefix = getAttribute("result-prefix");
        parser.getSymbolTable().addPrefixAlias(sPrefix,rPrefix);
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return Type.Void;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

    }
}
