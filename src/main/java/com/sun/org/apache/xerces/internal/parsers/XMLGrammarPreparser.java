


package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class XMLGrammarPreparser {

    private final static String CONTINUE_AFTER_FATAL_ERROR =
        Constants.XERCES_FEATURE_PREFIX + Constants.CONTINUE_AFTER_FATAL_ERROR_FEATURE;


    protected static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    protected static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    protected static final String ERROR_HANDLER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_HANDLER_PROPERTY;


    protected static final String ENTITY_RESOLVER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;


    protected static final String GRAMMAR_POOL =
        Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;

    private static final Map<String, String> KNOWN_LOADERS;

    static {
        Map<String, String> loaders = new HashMap<>();
        loaders.put(XMLGrammarDescription.XML_SCHEMA,
            "com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader");
        loaders.put(XMLGrammarDescription.XML_DTD,
            "com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDLoader");
        KNOWN_LOADERS = Collections.unmodifiableMap(loaders);
    }


    private static final String[] RECOGNIZED_PROPERTIES = {
        SYMBOL_TABLE,
        ERROR_REPORTER,
        ERROR_HANDLER,
        ENTITY_RESOLVER,
        GRAMMAR_POOL,
    };

    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityResolver fEntityResolver;
    protected XMLGrammarPool fGrammarPool;

    protected Locale fLocale;

    private Map<String, XMLGrammarLoader> fLoaders;

    public XMLGrammarPreparser() {
        this(new SymbolTable());
    } public XMLGrammarPreparser (SymbolTable symbolTable) {
        fSymbolTable = symbolTable;

        fLoaders = new HashMap<>();
        fErrorReporter = new XMLErrorReporter();
        setLocale(Locale.getDefault());
        fEntityResolver = new XMLEntityManager();
        } public boolean registerPreparser(String grammarType, XMLGrammarLoader loader) {
        if(loader == null) { if(KNOWN_LOADERS.containsKey(grammarType)) {
                String loaderName = (String)KNOWN_LOADERS.get(grammarType);
                try {
                    XMLGrammarLoader gl = (XMLGrammarLoader)(ObjectFactory.newInstance(loaderName, true));
                    fLoaders.put(grammarType, gl);
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
            return false;
        }
        fLoaders.put(grammarType, loader);
        return true;
    } public Grammar preparseGrammar(String type, XMLInputSource
                is) throws XNIException, IOException {
        if(fLoaders.containsKey(type)) {
            XMLGrammarLoader gl = fLoaders.get(type);
            gl.setProperty(SYMBOL_TABLE, fSymbolTable);
            gl.setProperty(ENTITY_RESOLVER, fEntityResolver);
            gl.setProperty(ERROR_REPORTER, fErrorReporter);
            if(fGrammarPool != null) {
                try {
                    gl.setProperty(GRAMMAR_POOL, fGrammarPool);
                } catch(Exception e) {
                    }
            }
            return gl.loadGrammar(is);
        }
        return null;
    } public void setLocale(Locale locale) {
        fLocale = locale;
        fErrorReporter.setLocale(locale);
    } public Locale getLocale() {
        return fLocale;
    } public void setErrorHandler(XMLErrorHandler errorHandler) {
        fErrorReporter.setProperty(ERROR_HANDLER, errorHandler);
    } public XMLErrorHandler getErrorHandler() {
        return fErrorReporter.getErrorHandler();
    } public void setEntityResolver(XMLEntityResolver entityResolver) {
        fEntityResolver = entityResolver;
    } public XMLEntityResolver getEntityResolver() {
        return fEntityResolver;
    } public void setGrammarPool(XMLGrammarPool grammarPool) {
        fGrammarPool = grammarPool;
    } public XMLGrammarPool getGrammarPool() {
        return fGrammarPool;
    } public XMLGrammarLoader getLoader(String type) {
        return fLoaders.get(type);
    } public void setFeature(String featureId, boolean value) {
        for (Map.Entry<String, XMLGrammarLoader> entry : fLoaders.entrySet()) {
            try {
                XMLGrammarLoader gl = entry.getValue();
                gl.setFeature(featureId, value);
            } catch(Exception e) {
                }
        }
        if(featureId.equals(CONTINUE_AFTER_FATAL_ERROR)) {
            fErrorReporter.setFeature(CONTINUE_AFTER_FATAL_ERROR, value);
        }
    } public void setProperty(String propId, Object value) {
        for (Map.Entry<String, XMLGrammarLoader> entry : fLoaders.entrySet()) {
            try {
                XMLGrammarLoader gl = entry.getValue();
                gl.setProperty(propId, value);
            } catch(Exception e) {
                }
        }
    } public boolean getFeature(String type, String featureId) {
        XMLGrammarLoader gl = fLoaders.get(type);
        return gl.getFeature(featureId);
    } public Object getProperty(String type, String propertyId) {
        XMLGrammarLoader gl = fLoaders.get(type);
        return gl.getProperty(propertyId);
    } }