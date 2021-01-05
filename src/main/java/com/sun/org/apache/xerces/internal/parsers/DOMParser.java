


package com.sun.org.apache.xerces.internal.parsers;

import java.io.IOException;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.EntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.EntityResolver2Wrapper;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.helpers.LocatorImpl;


public class DOMParser
    extends AbstractDOMParser {

    protected static final String USE_ENTITY_RESOLVER2 =
        Constants.SAX_FEATURE_PREFIX + Constants.USE_ENTITY_RESOLVER2_FEATURE;

    protected static final String REPORT_WHITESPACE =
            Constants.SUN_SCHEMA_FEATURE_PREFIX + Constants.SUN_REPORT_IGNORED_ELEMENT_CONTENT_WHITESPACE;


    private static final String XML_SECURITY_PROPERTY_MANAGER =
            Constants.XML_SECURITY_PROPERTY_MANAGER;

    private static final String[] RECOGNIZED_FEATURES = {
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

    protected boolean fUseEntityResolver2 = true;

    public DOMParser(XMLParserConfiguration config) {
        super(config);
    } public DOMParser() {
        this(null, null);
    } public DOMParser(SymbolTable symbolTable) {
        this(symbolTable, null);
    } public DOMParser(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
        super(new XIncludeAwareParserConfiguration());

        fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
        if (symbolTable != null) {
            fConfiguration.setProperty(SYMBOL_TABLE, symbolTable);
        }
        if (grammarPool != null) {
            fConfiguration.setProperty(XMLGRAMMAR_POOL, grammarPool);
        }

        fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);

    } public void parse(String systemId) throws SAXException, IOException {

        XMLInputSource source = new XMLInputSource(null, systemId, null);
        try {
            parse(source);
        }

        catch (XMLParseException e) {
            Exception ex = e.getException();
            if (ex == null) {
                LocatorImpl locatorImpl = new LocatorImpl();
                locatorImpl.setPublicId(e.getPublicId());
                locatorImpl.setSystemId(e.getExpandedSystemId());
                locatorImpl.setLineNumber(e.getLineNumber());
                locatorImpl.setColumnNumber(e.getColumnNumber());
                throw new SAXParseException(e.getMessage(), locatorImpl);
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
        catch (XNIException e) {
            e.printStackTrace();
            Exception ex = e.getException();
            if (ex == null) {
                throw new SAXException(e.getMessage());
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }

    } public void parse(InputSource inputSource)
        throws SAXException, IOException {

        try {
            XMLInputSource xmlInputSource =
                new XMLInputSource(inputSource.getPublicId(),
                                   inputSource.getSystemId(),
                                   null);
            xmlInputSource.setByteStream(inputSource.getByteStream());
            xmlInputSource.setCharacterStream(inputSource.getCharacterStream());
            xmlInputSource.setEncoding(inputSource.getEncoding());
            parse(xmlInputSource);
        }

        catch (XMLParseException e) {
            Exception ex = e.getException();
            if (ex == null) {
                LocatorImpl locatorImpl = new LocatorImpl();
                locatorImpl.setPublicId(e.getPublicId());
                locatorImpl.setSystemId(e.getExpandedSystemId());
                locatorImpl.setLineNumber(e.getLineNumber());
                locatorImpl.setColumnNumber(e.getColumnNumber());
                throw new SAXParseException(e.getMessage(), locatorImpl);
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
        catch (XNIException e) {
            Exception ex = e.getException();
            if (ex == null) {
                throw new SAXException(e.getMessage());
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }

    } public void setEntityResolver(EntityResolver resolver) {

        try {
            XMLEntityResolver xer = (XMLEntityResolver) fConfiguration.getProperty(ENTITY_RESOLVER);
            if (fUseEntityResolver2 && resolver instanceof EntityResolver2) {
                if (xer instanceof EntityResolver2Wrapper) {
                    EntityResolver2Wrapper er2w = (EntityResolver2Wrapper) xer;
                    er2w.setEntityResolver((EntityResolver2) resolver);
                }
                else {
                    fConfiguration.setProperty(ENTITY_RESOLVER,
                            new EntityResolver2Wrapper((EntityResolver2) resolver));
                }
            }
            else {
                if (xer instanceof EntityResolverWrapper) {
                    EntityResolverWrapper erw = (EntityResolverWrapper) xer;
                    erw.setEntityResolver(resolver);
                }
                else {
                    fConfiguration.setProperty(ENTITY_RESOLVER,
                            new EntityResolverWrapper(resolver));
                }
            }
        }
        catch (XMLConfigurationException e) {
            }

    } public EntityResolver getEntityResolver() {

        EntityResolver entityResolver = null;
        try {
            XMLEntityResolver xmlEntityResolver =
                (XMLEntityResolver)fConfiguration.getProperty(ENTITY_RESOLVER);
            if (xmlEntityResolver != null) {
                if (xmlEntityResolver instanceof EntityResolverWrapper) {
                    entityResolver =
                        ((EntityResolverWrapper) xmlEntityResolver).getEntityResolver();
                }
                else if (xmlEntityResolver instanceof EntityResolver2Wrapper) {
                    entityResolver =
                        ((EntityResolver2Wrapper) xmlEntityResolver).getEntityResolver();
                }
            }
        }
        catch (XMLConfigurationException e) {
            }
        return entityResolver;

    } public void setErrorHandler(ErrorHandler errorHandler) {

        try {
            XMLErrorHandler xeh = (XMLErrorHandler) fConfiguration.getProperty(ERROR_HANDLER);
            if (xeh instanceof ErrorHandlerWrapper) {
                ErrorHandlerWrapper ehw = (ErrorHandlerWrapper) xeh;
                ehw.setErrorHandler(errorHandler);
            }
            else {
                fConfiguration.setProperty(ERROR_HANDLER,
                        new ErrorHandlerWrapper(errorHandler));
            }
        }
        catch (XMLConfigurationException e) {
            }

    } public ErrorHandler getErrorHandler() {

        ErrorHandler errorHandler = null;
        try {
            XMLErrorHandler xmlErrorHandler =
                (XMLErrorHandler)fConfiguration.getProperty(ERROR_HANDLER);
            if (xmlErrorHandler != null &&
                xmlErrorHandler instanceof ErrorHandlerWrapper) {
                errorHandler = ((ErrorHandlerWrapper)xmlErrorHandler).getErrorHandler();
            }
        }
        catch (XMLConfigurationException e) {
            }
        return errorHandler;

    } public void setFeature(String featureId, boolean state)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        try {

            if (featureId.equals(USE_ENTITY_RESOLVER2)) {
                if (state != fUseEntityResolver2) {
                    fUseEntityResolver2 = state;
                    setEntityResolver(getEntityResolver());
                }
                return;
            }

            fConfiguration.setFeature(featureId, state);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                    "feature-not-recognized", new Object [] {identifier}));
            }
            else {
                throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                    "feature-not-supported", new Object [] {identifier}));
            }
        }

    } public boolean getFeature(String featureId)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        try {

            if (featureId.equals(USE_ENTITY_RESOLVER2)) {
                return fUseEntityResolver2;
            }

            return fConfiguration.getFeature(featureId);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                    "feature-not-recognized", new Object [] {identifier}));
            }
            else {
                throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                    "feature-not-supported", new Object [] {identifier}));
            }
        }

    } public void setProperty(String propertyId, Object value)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        if (propertyId.equals(Constants.SECURITY_MANAGER)) {
            securityManager = XMLSecurityManager.convert(value, securityManager);
            setProperty0(Constants.SECURITY_MANAGER, securityManager);
            return;
        }
        if (propertyId.equals(Constants.XML_SECURITY_PROPERTY_MANAGER)) {
            if (value == null) {
                securityPropertyManager = new XMLSecurityPropertyManager();
            } else {
                securityPropertyManager = (XMLSecurityPropertyManager)value;
            }
            setProperty0(Constants.XML_SECURITY_PROPERTY_MANAGER, securityPropertyManager);
            return;
        }

        if (securityManager == null) {
            securityManager = new XMLSecurityManager(true);
            setProperty0(Constants.SECURITY_MANAGER, securityManager);
        }

        if (securityPropertyManager == null) {
            securityPropertyManager = new XMLSecurityPropertyManager();
            setProperty0(Constants.XML_SECURITY_PROPERTY_MANAGER, securityPropertyManager);
        }
        int index = securityPropertyManager.getIndex(propertyId);

        if (index > -1) {

            securityPropertyManager.setValue(index, XMLSecurityPropertyManager.State.APIPROPERTY, (String)value);
        } else {
            if (!securityManager.setLimit(propertyId, XMLSecurityManager.State.APIPROPERTY, value)) {
                setProperty0(propertyId, value);
            }
        }
    }

    public void setProperty0(String propertyId, Object value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            fConfiguration.setProperty(propertyId, value);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                    "property-not-recognized", new Object [] {identifier}));
            }
            else {
                throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                    "property-not-supported", new Object [] {identifier}));
            }
        }

    } public Object getProperty(String propertyId)
        throws SAXNotRecognizedException, SAXNotSupportedException {

       if (propertyId.equals(CURRENT_ELEMENT_NODE)) {
           boolean deferred = false;
           try {
               deferred = getFeature(DEFER_NODE_EXPANSION);
           }
           catch (XMLConfigurationException e){
               }
           if (deferred) {
               throw new SAXNotSupportedException("Current element node cannot be queried when node expansion is deferred.");
           }
           return (fCurrentNode!=null &&
                   fCurrentNode.getNodeType() == Node.ELEMENT_NODE)? fCurrentNode:null;
       }

        try {
            XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)
                    fConfiguration.getProperty(XML_SECURITY_PROPERTY_MANAGER);
            int index = spm.getIndex(propertyId);
            if (index > -1) {
                return spm.getValueByIndex(index);
            }

            return fConfiguration.getProperty(propertyId);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                    "property-not-recognized", new Object [] {identifier}));
            }
            else {
                throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                    "property-not-supported", new Object [] {identifier}));
            }
        }

    } public XMLParserConfiguration getXMLParserConfiguration() {
        return fConfiguration;
    } }