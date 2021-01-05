


package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


public class SAXParserImpl extends javax.xml.parsers.SAXParser
    implements JAXPConstants, PSVIProvider {


    private static final String NAMESPACES_FEATURE =
        Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE;


    private static final String NAMESPACE_PREFIXES_FEATURE =
        Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACE_PREFIXES_FEATURE;


    private static final String VALIDATION_FEATURE =
        Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;


    private static final String XMLSCHEMA_VALIDATION_FEATURE =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE;


    private static final String XINCLUDE_FEATURE =
        Constants.XERCES_FEATURE_PREFIX + Constants.XINCLUDE_FEATURE;


    private static final String SECURITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;


    private static final String XML_SECURITY_PROPERTY_MANAGER =
            Constants.XML_SECURITY_PROPERTY_MANAGER;

    private final JAXPSAXParser xmlReader;
    private String schemaLanguage = null;     private final Schema grammar;

    private final XMLComponent fSchemaValidator;
    private final XMLComponentManager fSchemaValidatorComponentManager;
    private final ValidationManager fSchemaValidationManager;
    private final UnparsedEntityHandler fUnparsedEntityHandler;


    private final ErrorHandler fInitErrorHandler;


    private final EntityResolver fInitEntityResolver;

    private final XMLSecurityManager fSecurityManager;
    private final XMLSecurityPropertyManager fSecurityPropertyMgr;


    SAXParserImpl(SAXParserFactoryImpl spf, Map<String, Boolean> features)
        throws SAXException {
        this(spf, features, false);
    }


    SAXParserImpl(SAXParserFactoryImpl spf, Map<String, Boolean> features, boolean secureProcessing)
        throws SAXException
    {
        fSecurityManager = new XMLSecurityManager(secureProcessing);
        fSecurityPropertyMgr = new XMLSecurityPropertyManager();
        xmlReader = new JAXPSAXParser(this, fSecurityPropertyMgr, fSecurityManager);

        xmlReader.setFeature0(NAMESPACES_FEATURE, spf.isNamespaceAware());

        xmlReader.setFeature0(NAMESPACE_PREFIXES_FEATURE, !spf.isNamespaceAware());

        if (spf.isXIncludeAware()) {
            xmlReader.setFeature0(XINCLUDE_FEATURE, true);
        }

        xmlReader.setProperty0(XML_SECURITY_PROPERTY_MANAGER, fSecurityPropertyMgr);

        xmlReader.setProperty0(SECURITY_MANAGER, fSecurityManager);

        if (secureProcessing) {

            if (features != null) {

                Boolean temp = features.get(XMLConstants.FEATURE_SECURE_PROCESSING);
                if (temp != null) {
                    if (temp && Constants.IS_JDK8_OR_ABOVE) {
                        fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD,
                                XMLSecurityPropertyManager.State.FSP, Constants.EXTERNAL_ACCESS_DEFAULT_FSP);
                        fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA,
                                XMLSecurityPropertyManager.State.FSP, Constants.EXTERNAL_ACCESS_DEFAULT_FSP);

                    }
                }
            }
        }

        setFeatures(features);

        if (spf.isValidating()) {
            fInitErrorHandler = new DefaultValidationErrorHandler(xmlReader.getLocale());
            xmlReader.setErrorHandler(fInitErrorHandler);
        }
        else {
            fInitErrorHandler = xmlReader.getErrorHandler();
        }
        xmlReader.setFeature0(VALIDATION_FEATURE, spf.isValidating());

        this.grammar = spf.getSchema();
        if (grammar != null) {
            XMLParserConfiguration config = xmlReader.getXMLParserConfiguration();
            XMLComponent validatorComponent = null;

            if (grammar instanceof XSGrammarPoolContainer) {
                validatorComponent = new XMLSchemaValidator();
                fSchemaValidationManager = new ValidationManager();
                fUnparsedEntityHandler = new UnparsedEntityHandler(fSchemaValidationManager);
                config.setDTDHandler(fUnparsedEntityHandler);
                fUnparsedEntityHandler.setDTDHandler(xmlReader);
                xmlReader.setDTDSource(fUnparsedEntityHandler);
                fSchemaValidatorComponentManager = new SchemaValidatorConfiguration(config,
                        (XSGrammarPoolContainer) grammar, fSchemaValidationManager);
            }

            else {
                validatorComponent = new JAXPValidatorComponent(grammar.newValidatorHandler());
                fSchemaValidationManager = null;
                fUnparsedEntityHandler = null;
                fSchemaValidatorComponentManager = config;
            }
            config.addRecognizedFeatures(validatorComponent.getRecognizedFeatures());
            config.addRecognizedProperties(validatorComponent.getRecognizedProperties());
            config.setDocumentHandler((XMLDocumentHandler) validatorComponent);
            ((XMLDocumentSource)validatorComponent).setDocumentHandler(xmlReader);
            xmlReader.setDocumentSource((XMLDocumentSource) validatorComponent);
            fSchemaValidator = validatorComponent;
        }
        else {
            fSchemaValidationManager = null;
            fUnparsedEntityHandler = null;
            fSchemaValidatorComponentManager = null;
            fSchemaValidator = null;
        }

        fInitEntityResolver = xmlReader.getEntityResolver();
    }


    private void setFeatures(Map<String, Boolean> features)
        throws SAXNotSupportedException, SAXNotRecognizedException {
        if (features != null) {
            for (Map.Entry<String, Boolean> entry : features.entrySet()) {
                xmlReader.setFeature0(entry.getKey(), entry.getValue());
            }
        }
    }

    public Parser getParser() throws SAXException {
        return (Parser) xmlReader;
    }


    public XMLReader getXMLReader() {
        return xmlReader;
    }

    public boolean isNamespaceAware() {
        try {
            return xmlReader.getFeature(NAMESPACES_FEATURE);
        }
        catch (SAXException x) {
            throw new IllegalStateException(x.getMessage());
        }
    }

    public boolean isValidating() {
        try {
            return xmlReader.getFeature(VALIDATION_FEATURE);
        }
        catch (SAXException x) {
            throw new IllegalStateException(x.getMessage());
        }
    }


    public boolean isXIncludeAware() {
        try {
            return xmlReader.getFeature(XINCLUDE_FEATURE);
        }
        catch (SAXException exc) {
            return false;
        }
    }


    public void setProperty(String name, Object value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        xmlReader.setProperty(name, value);
    }


    public Object getProperty(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        return xmlReader.getProperty(name);
    }

    public void parse(InputSource is, DefaultHandler dh)
        throws SAXException, IOException {
        if (is == null) {
            throw new IllegalArgumentException();
        }
        if (dh != null) {
            xmlReader.setContentHandler(dh);
            xmlReader.setEntityResolver(dh);
            xmlReader.setErrorHandler(dh);
            xmlReader.setDTDHandler(dh);
            xmlReader.setDocumentHandler(null);
        }
        xmlReader.parse(is);
    }

    public void parse(InputSource is, HandlerBase hb)
        throws SAXException, IOException {
        if (is == null) {
            throw new IllegalArgumentException();
        }
        if (hb != null) {
            xmlReader.setDocumentHandler(hb);
            xmlReader.setEntityResolver(hb);
            xmlReader.setErrorHandler(hb);
            xmlReader.setDTDHandler(hb);
            xmlReader.setContentHandler(null);
        }
        xmlReader.parse(is);
    }

    public Schema getSchema() {
        return grammar;
    }

    public void reset() {
        try {

            xmlReader.restoreInitState();
        }
        catch (SAXException exc) {
            }

        xmlReader.setContentHandler(null);
        xmlReader.setDTDHandler(null);
        if (xmlReader.getErrorHandler() != fInitErrorHandler) {
            xmlReader.setErrorHandler(fInitErrorHandler);
        }
        if (xmlReader.getEntityResolver() != fInitEntityResolver) {
            xmlReader.setEntityResolver(fInitEntityResolver);
        }
    }



    public ElementPSVI getElementPSVI() {
        return ((PSVIProvider)xmlReader).getElementPSVI();
    }

    public AttributePSVI getAttributePSVI(int index) {
        return ((PSVIProvider)xmlReader).getAttributePSVI(index);
    }

    public AttributePSVI getAttributePSVIByName(String uri, String localname) {
        return ((PSVIProvider)xmlReader).getAttributePSVIByName(uri, localname);
    }


    public static class JAXPSAXParser extends com.sun.org.apache.xerces.internal.parsers.SAXParser {

        private final HashMap fInitFeatures = new HashMap();
        private final HashMap fInitProperties = new HashMap();
        private final SAXParserImpl fSAXParser;
        private XMLSecurityManager fSecurityManager;
        private XMLSecurityPropertyManager fSecurityPropertyMgr;


        public JAXPSAXParser() {
            this(null, null, null);
        }

        JAXPSAXParser(SAXParserImpl saxParser, XMLSecurityPropertyManager securityPropertyMgr,
                XMLSecurityManager securityManager) {
            super();
            fSAXParser = saxParser;
            fSecurityManager = securityManager;
            fSecurityPropertyMgr = securityPropertyMgr;

            if (fSecurityManager == null) {
                fSecurityManager = new XMLSecurityManager(true);
                try {
                    super.setProperty(SECURITY_MANAGER, fSecurityManager);
                } catch (SAXException e) {
                    throw new UnsupportedOperationException(
                    SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                    "property-not-recognized", new Object [] {SECURITY_MANAGER}), e);
                }
            }
            if (fSecurityPropertyMgr == null) {
                fSecurityPropertyMgr = new XMLSecurityPropertyManager();
                try {
                    super.setProperty(XML_SECURITY_PROPERTY_MANAGER, fSecurityPropertyMgr);
                } catch (SAXException e) {
                    throw new UnsupportedOperationException(
                    SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                    "property-not-recognized", new Object [] {SECURITY_MANAGER}), e);
                }
            }
        }


        public synchronized void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
            if (name == null) {
                throw new NullPointerException();
            }
            if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
                try {
                    fSecurityManager.setSecureProcessing(value);
                    setProperty(SECURITY_MANAGER, fSecurityManager);
                }
                catch (SAXNotRecognizedException exc) {
                    if (value) {
                        throw exc;
                    }
                }
                catch (SAXNotSupportedException exc) {
                    if (value) {
                        throw exc;
                    }
                }
                return;
            }
            if (!fInitFeatures.containsKey(name)) {
                boolean current = super.getFeature(name);
                fInitFeatures.put(name, current ? Boolean.TRUE : Boolean.FALSE);
            }

            if (fSAXParser != null && fSAXParser.fSchemaValidator != null) {
                setSchemaValidatorFeature(name, value);
            }
            super.setFeature(name, value);
        }

        public synchronized boolean getFeature(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
            if (name == null) {
                throw new NullPointerException();
            }
            if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
                return fSecurityManager.isSecureProcessing();
            }
            return super.getFeature(name);
        }


        public synchronized void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
            if (name == null) {
                throw new NullPointerException();
            }
            if (fSAXParser != null) {
                if (JAXP_SCHEMA_LANGUAGE.equals(name)) {
                    if (fSAXParser.grammar != null) {
                        throw new SAXNotSupportedException(
                                SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "schema-already-specified", new Object[] {name}));
                    }
                    if ( W3C_XML_SCHEMA.equals(value) ) {
                        if( fSAXParser.isValidating() ) {
                            fSAXParser.schemaLanguage = W3C_XML_SCHEMA;
                            setFeature(XMLSCHEMA_VALIDATION_FEATURE, true);
                            if (!fInitProperties.containsKey(JAXP_SCHEMA_LANGUAGE)) {
                                fInitProperties.put(JAXP_SCHEMA_LANGUAGE, super.getProperty(JAXP_SCHEMA_LANGUAGE));
                            }
                            super.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
                        }

                    }
                    else if (value == null) {
                        fSAXParser.schemaLanguage = null;
                        setFeature(XMLSCHEMA_VALIDATION_FEATURE, false);
                    }
                    else {
                        throw new SAXNotSupportedException(
                            SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "schema-not-supported", null));
                    }
                    return;
                }
                else if (JAXP_SCHEMA_SOURCE.equals(name)) {
                    if (fSAXParser.grammar != null) {
                        throw new SAXNotSupportedException(
                                SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "schema-already-specified", new Object[] {name}));
                    }
                    String val = (String)getProperty(JAXP_SCHEMA_LANGUAGE);
                    if ( val != null && W3C_XML_SCHEMA.equals(val) ) {
                        if (!fInitProperties.containsKey(JAXP_SCHEMA_SOURCE)) {
                            fInitProperties.put(JAXP_SCHEMA_SOURCE, super.getProperty(JAXP_SCHEMA_SOURCE));
                        }
                        super.setProperty(name, value);
                    }
                    else {
                        throw new SAXNotSupportedException(
                            SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                            "jaxp-order-not-supported",
                            new Object[] {JAXP_SCHEMA_LANGUAGE, JAXP_SCHEMA_SOURCE}));
                    }
                    return;
                }
            }

            if (fSAXParser != null && fSAXParser.fSchemaValidator != null) {
                setSchemaValidatorProperty(name, value);
            }

            if (fSecurityManager == null ||
                    !fSecurityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, value)) {
                if (fSecurityPropertyMgr == null ||
                        !fSecurityPropertyMgr.setValue(name, XMLSecurityPropertyManager.State.APIPROPERTY, value)) {
                    if (!fInitProperties.containsKey(name)) {
                        fInitProperties.put(name, super.getProperty(name));
                    }
                    super.setProperty(name, value);
                }
            }

        }

        public synchronized Object getProperty(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
            if (name == null) {
                throw new NullPointerException();
            }
            if (fSAXParser != null && JAXP_SCHEMA_LANGUAGE.equals(name)) {
                return fSAXParser.schemaLanguage;
            }


            String propertyValue = (fSecurityManager != null) ?
                    fSecurityManager.getLimitAsString(name) : null;
            if (propertyValue != null) {
                return propertyValue;
            } else {
                propertyValue = (fSecurityPropertyMgr != null) ?
                    fSecurityPropertyMgr.getValue(name) : null;
                if (propertyValue != null) {
                    return propertyValue;
                }
            }

            return super.getProperty(name);
        }

        synchronized void restoreInitState()
            throws SAXNotRecognizedException, SAXNotSupportedException {
            Iterator iter;
            if (!fInitFeatures.isEmpty()) {
                iter = fInitFeatures.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String name = (String) entry.getKey();
                    boolean value = ((Boolean) entry.getValue()).booleanValue();
                    super.setFeature(name, value);
                }
                fInitFeatures.clear();
            }
            if (!fInitProperties.isEmpty()) {
                iter = fInitProperties.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String name = (String) entry.getKey();
                    Object value = entry.getValue();
                    super.setProperty(name, value);
                }
                fInitProperties.clear();
            }
        }

        public void parse(InputSource inputSource)
            throws SAXException, IOException {
            if (fSAXParser != null && fSAXParser.fSchemaValidator != null) {
                if (fSAXParser.fSchemaValidationManager != null) {
                    fSAXParser.fSchemaValidationManager.reset();
                    fSAXParser.fUnparsedEntityHandler.reset();
                }
                resetSchemaValidator();
            }
            super.parse(inputSource);
        }

        public void parse(String systemId)
            throws SAXException, IOException {
            if (fSAXParser != null && fSAXParser.fSchemaValidator != null) {
                if (fSAXParser.fSchemaValidationManager != null) {
                    fSAXParser.fSchemaValidationManager.reset();
                    fSAXParser.fUnparsedEntityHandler.reset();
                }
                resetSchemaValidator();
            }
            super.parse(systemId);
        }

        XMLParserConfiguration getXMLParserConfiguration() {
            return fConfiguration;
        }

        void setFeature0(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
            super.setFeature(name, value);
        }

        boolean getFeature0(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
            return super.getFeature(name);
        }

        void setProperty0(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
            super.setProperty(name, value);
        }

        Object getProperty0(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
            return super.getProperty(name);
        }

        Locale getLocale() {
            return fConfiguration.getLocale();
        }

        private void setSchemaValidatorFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
            try {
                fSAXParser.fSchemaValidator.setFeature(name, value);
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
        }

        private void setSchemaValidatorProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
            try {
                fSAXParser.fSchemaValidator.setProperty(name, value);
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
        }

        private void resetSchemaValidator() throws SAXException {
            try {
                fSAXParser.fSchemaValidator.reset(fSAXParser.fSchemaValidatorComponentManager);
            }
            catch (XMLConfigurationException e) {
                throw new SAXException(e);
            }
        }
    }
}
