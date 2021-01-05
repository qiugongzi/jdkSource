


package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


public class SAXParser
    extends AbstractSAXParser {

    protected static final String NOTIFY_BUILTIN_REFS =
        Constants.XERCES_FEATURE_PREFIX + Constants.NOTIFY_BUILTIN_REFS_FEATURE;

    protected static final String REPORT_WHITESPACE =
            Constants.SUN_SCHEMA_FEATURE_PREFIX + Constants.SUN_REPORT_IGNORED_ELEMENT_CONTENT_WHITESPACE;


    private static final String[] RECOGNIZED_FEATURES = {
        NOTIFY_BUILTIN_REFS,
        REPORT_WHITESPACE
    };

    protected static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    protected static final String XMLGRAMMAR_POOL =
        Constants.XERCES_PROPERTY_PREFIX+Constants.XMLGRAMMAR_POOL_PROPERTY;


    private static final String[] RECOGNIZED_PROPERTIES = {
        SYMBOL_TABLE,
        XMLGRAMMAR_POOL,
    };


    public SAXParser(XMLParserConfiguration config) {
        super(config);
    } public SAXParser() {
        this(null, null);
    } public SAXParser(SymbolTable symbolTable) {
        this(symbolTable, null);
    } public SAXParser(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
        super(new XIncludeAwareParserConfiguration());

        fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
        fConfiguration.setFeature(NOTIFY_BUILTIN_REFS, true);

        fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
        if (symbolTable != null) {
            fConfiguration.setProperty(SYMBOL_TABLE, symbolTable);
        }
        if (grammarPool != null) {
            fConfiguration.setProperty(XMLGRAMMAR_POOL, grammarPool);
        }

    } public void setProperty(String name, Object value)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        if (name.equals(Constants.SECURITY_MANAGER)) {
            securityManager = XMLSecurityManager.convert(value, securityManager);
            super.setProperty(Constants.SECURITY_MANAGER, securityManager);
            return;
        }
        if (name.equals(Constants.XML_SECURITY_PROPERTY_MANAGER)) {
            if (value == null) {
                securityPropertyManager = new XMLSecurityPropertyManager();
            } else {
                securityPropertyManager = (XMLSecurityPropertyManager)value;
            }
            super.setProperty(Constants.XML_SECURITY_PROPERTY_MANAGER, securityPropertyManager);
            return;
        }

        if (securityManager == null) {
            securityManager = new XMLSecurityManager(true);
            super.setProperty(Constants.SECURITY_MANAGER, securityManager);
        }

        if (securityPropertyManager == null) {
            securityPropertyManager = new XMLSecurityPropertyManager();
            super.setProperty(Constants.XML_SECURITY_PROPERTY_MANAGER, securityPropertyManager);
        }

        int index = securityPropertyManager.getIndex(name);
        if (index > -1) {

            securityPropertyManager.setValue(index, XMLSecurityPropertyManager.State.APIPROPERTY, (String)value);
        } else {
            if (!securityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, value)) {
                super.setProperty(name, value);
            }
        }
    }
}