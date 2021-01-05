


package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;


public class SecurityConfiguration extends XIncludeAwareParserConfiguration
{

    protected static final String SECURITY_MANAGER_PROPERTY =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;

    public SecurityConfiguration () {
        this(null, null, null);
    } public SecurityConfiguration (SymbolTable symbolTable) {
        this(symbolTable, null, null);
    } public SecurityConfiguration (SymbolTable symbolTable,
                                         XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    } public SecurityConfiguration (SymbolTable symbolTable,
                                         XMLGrammarPool grammarPool,
                                         XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);

        setProperty(SECURITY_MANAGER_PROPERTY, new XMLSecurityManager(true));
    } }