
    protected XMLGrammarParser(SymbolTable symbolTable) {
        super(new XIncludeAwareParserConfiguration());
        fConfiguration.setProperty(Constants.XERCES_PROPERTY_PREFIX+Constants.SYMBOL_TABLE_PROPERTY, symbolTable);
    }

}
