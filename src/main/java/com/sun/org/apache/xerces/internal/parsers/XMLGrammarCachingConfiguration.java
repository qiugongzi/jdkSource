


package com.sun.org.apache.xerces.internal.parsers;

import java.io.IOException;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDLoader;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;


public class XMLGrammarCachingConfiguration
    extends XIncludeAwareParserConfiguration {

    public static final int BIG_PRIME = 2039;

    protected static final SynchronizedSymbolTable fStaticSymbolTable =
            new SynchronizedSymbolTable(BIG_PRIME);

    protected static final XMLGrammarPoolImpl fStaticGrammarPool =
            new XMLGrammarPoolImpl();

    protected static final String SCHEMA_FULL_CHECKING =
            Constants.XERCES_FEATURE_PREFIX+Constants.SCHEMA_FULL_CHECKING;

    protected XMLSchemaLoader fSchemaLoader;

    protected XMLDTDLoader fDTDLoader;

    public XMLGrammarCachingConfiguration() {
        this(fStaticSymbolTable, fStaticGrammarPool, null);
    } public XMLGrammarCachingConfiguration(SymbolTable symbolTable) {
        this(symbolTable, fStaticGrammarPool, null);
    } public XMLGrammarCachingConfiguration(SymbolTable symbolTable,
                                       XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    } public XMLGrammarCachingConfiguration(SymbolTable symbolTable,
                                       XMLGrammarPool grammarPool,
                                       XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);

        fSchemaLoader = new XMLSchemaLoader(fSymbolTable);
        fSchemaLoader.setProperty(XMLGRAMMAR_POOL, fGrammarPool);

        fDTDLoader = new XMLDTDLoader(fSymbolTable, fGrammarPool);
    } public void lockGrammarPool() {
        fGrammarPool.lockPool();
    } public void clearGrammarPool() {
        fGrammarPool.clear();
    } public void unlockGrammarPool() {
        fGrammarPool.unlockPool();
    } public Grammar parseGrammar(String type, String uri)
                              throws XNIException, IOException {
        XMLInputSource source = new XMLInputSource(null, uri, null);
        return parseGrammar(type, source);

    }


    public Grammar parseGrammar(String type, XMLInputSource
                is) throws XNIException, IOException {
        if(type.equals(XMLGrammarDescription.XML_SCHEMA)) {
            return parseXMLSchema(is);
        } else if(type.equals(XMLGrammarDescription.XML_DTD)) {
            return parseDTD(is);
        }
        return null;
    } SchemaGrammar parseXMLSchema(XMLInputSource is)
                throws IOException {
        XMLEntityResolver resolver = getEntityResolver();
        if(resolver != null) {
            fSchemaLoader.setEntityResolver(resolver);
        }
        if (fErrorReporter.getMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN) == null) {
            fErrorReporter.putMessageFormatter(XSMessageFormatter.SCHEMA_DOMAIN, new XSMessageFormatter());
        }
        fSchemaLoader.setProperty(ERROR_REPORTER, fErrorReporter);

        String propPrefix = Constants.XERCES_PROPERTY_PREFIX;
        String propName = propPrefix + Constants.SCHEMA_LOCATION;
        fSchemaLoader.setProperty(propName, getProperty(propName));
        propName = propPrefix + Constants.SCHEMA_NONS_LOCATION;
        fSchemaLoader.setProperty(propName, getProperty(propName));
        propName = Constants.JAXP_PROPERTY_PREFIX+Constants.SCHEMA_SOURCE;
        fSchemaLoader.setProperty(propName, getProperty(propName));
        fSchemaLoader.setFeature(SCHEMA_FULL_CHECKING, getFeature(SCHEMA_FULL_CHECKING));

        SchemaGrammar grammar = (SchemaGrammar)fSchemaLoader.loadGrammar(is);
        if (grammar != null) {
            fGrammarPool.cacheGrammars(XMLGrammarDescription.XML_SCHEMA,
                                      new Grammar[]{grammar});
        }

        return grammar;

    } DTDGrammar parseDTD(XMLInputSource is)
                throws IOException {
        XMLEntityResolver resolver = getEntityResolver();
        if(resolver != null) {
            fDTDLoader.setEntityResolver(resolver);
        }
        fDTDLoader.setProperty(ERROR_REPORTER, fErrorReporter);

        DTDGrammar grammar = (DTDGrammar)fDTDLoader.loadGrammar(is);
        if (grammar != null) {
            fGrammarPool.cacheGrammars(XMLGrammarDescription.XML_DTD,
                                      new Grammar[]{grammar});
        }

        return grammar;

    } }