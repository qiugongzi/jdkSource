


package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import jdk.xml.internal.JdkXmlFeatures;
import jdk.xml.internal.JdkXmlUtils;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaLoader;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.DOMInputSource;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SAXInputSource;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.StAXInputSource;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.XMLGrammarPoolImpl;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;


public final class XMLSchemaFactory extends SchemaFactory {

    private static final String SCHEMA_FULL_CHECKING =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING;


    private static final String XMLGRAMMAR_POOL =
        Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;


    private static final String SECURITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;


    private static final String XML_SECURITY_PROPERTY_MANAGER =
            Constants.XML_SECURITY_PROPERTY_MANAGER;


    private final XMLSchemaLoader fXMLSchemaLoader = new XMLSchemaLoader();


    private ErrorHandler fErrorHandler;


    private LSResourceResolver fLSResourceResolver;


    private final DOMEntityResolverWrapper fDOMEntityResolverWrapper;


    private ErrorHandlerWrapper fErrorHandlerWrapper;


    private XMLSecurityManager fSecurityManager;


    private XMLSecurityPropertyManager fSecurityPropertyMgr;


    private XMLGrammarPoolWrapper fXMLGrammarPoolWrapper;

    private final JdkXmlFeatures fXmlFeatures;

    private final boolean fOverrideDefaultParser;


    public XMLSchemaFactory() {
        fErrorHandlerWrapper = new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
        fDOMEntityResolverWrapper = new DOMEntityResolverWrapper();
        fXMLGrammarPoolWrapper = new XMLGrammarPoolWrapper();
        fXMLSchemaLoader.setFeature(SCHEMA_FULL_CHECKING, true);
        fXMLSchemaLoader.setProperty(XMLGRAMMAR_POOL, fXMLGrammarPoolWrapper);
        fXMLSchemaLoader.setEntityResolver(fDOMEntityResolverWrapper);
        fXMLSchemaLoader.setErrorHandler(fErrorHandlerWrapper);

        fSecurityManager = new XMLSecurityManager(true);
        fXMLSchemaLoader.setProperty(SECURITY_MANAGER, fSecurityManager);

        fSecurityPropertyMgr = new XMLSecurityPropertyManager();
        fXMLSchemaLoader.setProperty(XML_SECURITY_PROPERTY_MANAGER,
                fSecurityPropertyMgr);
        fXmlFeatures = new JdkXmlFeatures(fSecurityManager.isSecureProcessing());
        fOverrideDefaultParser = fXmlFeatures.getFeature(
                        JdkXmlFeatures.XmlFeature.JDK_OVERRIDE_PARSER);
        fXMLSchemaLoader.setFeature(JdkXmlUtils.OVERRIDE_PARSER, fOverrideDefaultParser);
    }


    public boolean isSchemaLanguageSupported(String schemaLanguage) {
        if (schemaLanguage == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                    "SchemaLanguageNull", null));
        }
        if (schemaLanguage.length() == 0) {
            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                    "SchemaLanguageLengthZero", null));
        }
        return schemaLanguage.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    public LSResourceResolver getResourceResolver() {
        return fLSResourceResolver;
    }

    public void setResourceResolver(LSResourceResolver resourceResolver) {
        fLSResourceResolver = resourceResolver;
        fDOMEntityResolverWrapper.setEntityResolver(resourceResolver);
        fXMLSchemaLoader.setEntityResolver(fDOMEntityResolverWrapper);
    }

    public ErrorHandler getErrorHandler() {
        return fErrorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        fErrorHandler = errorHandler;
        fErrorHandlerWrapper.setErrorHandler(errorHandler != null ? errorHandler : DraconianErrorHandler.getInstance());
        fXMLSchemaLoader.setErrorHandler(fErrorHandlerWrapper);
    }

    public Schema newSchema( Source[] schemas ) throws SAXException {

        XMLGrammarPoolImplExtension pool = new XMLGrammarPoolImplExtension();
        fXMLGrammarPoolWrapper.setGrammarPool(pool);

        XMLInputSource[] xmlInputSources = new XMLInputSource[schemas.length];
        InputStream inputStream;
        Reader reader;
        for( int i=0; i<schemas.length; i++ ) {
            Source source = schemas[i];
            if (source instanceof StreamSource) {
                StreamSource streamSource = (StreamSource) source;
                String publicId = streamSource.getPublicId();
                String systemId = streamSource.getSystemId();
                inputStream = streamSource.getInputStream();
                reader = streamSource.getReader();
                xmlInputSources[i] = new XMLInputSource(publicId, systemId, null);
                xmlInputSources[i].setByteStream(inputStream);
                xmlInputSources[i].setCharacterStream(reader);
            }
            else if (source instanceof SAXSource) {
                SAXSource saxSource = (SAXSource) source;
                InputSource inputSource = saxSource.getInputSource();
                if (inputSource == null) {
                    throw new SAXException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                            "SAXSourceNullInputSource", null));
                }
                xmlInputSources[i] = new SAXInputSource(saxSource.getXMLReader(), inputSource);
            }
            else if (source instanceof DOMSource) {
                DOMSource domSource = (DOMSource) source;
                Node node = domSource.getNode();
                String systemID = domSource.getSystemId();
                xmlInputSources[i] = new DOMInputSource(node, systemID);
            }
             else if (source instanceof StAXSource) {
                StAXSource staxSource = (StAXSource) source;
                XMLEventReader eventReader = staxSource.getXMLEventReader();
                if (eventReader != null) {
                    xmlInputSources[i] = new StAXInputSource(eventReader);
                }
                else {
                    xmlInputSources[i] = new StAXInputSource(staxSource.getXMLStreamReader());
                }
            }
            else if (source == null) {
                throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                        "SchemaSourceArrayMemberNull", null));
            }
            else {
                throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                        "SchemaFactorySourceUnrecognized",
                        new Object [] {source.getClass().getName()}));
            }
        }

        try {
            fXMLSchemaLoader.loadGrammar(xmlInputSources);
        }
        catch (XNIException e) {
            throw Util.toSAXException(e);
        }
        catch (IOException e) {
            SAXParseException se = new SAXParseException(e.getMessage(),null,e);
            fErrorHandler.error(se);
            throw se; }

        fXMLGrammarPoolWrapper.setGrammarPool(null);

        final int grammarCount = pool.getGrammarCount();
        AbstractXMLSchema schema = null;
        if (grammarCount > 1) {
            schema = new XMLSchema(new ReadOnlyGrammarPool(pool));
        }
        else if (grammarCount == 1) {
            Grammar[] grammars = pool.retrieveInitialGrammarSet(XMLGrammarDescription.XML_SCHEMA);
            schema = new SimpleXMLSchema(grammars[0]);
        }
        else {
            schema = new EmptyXMLSchema();
        }
        propagateFeatures(schema);
        propagateProperties(schema);
        return schema;
    }

    public Schema newSchema() throws SAXException {
        AbstractXMLSchema schema = new WeakReferenceXMLSchema();
        propagateFeatures(schema);
        propagateProperties(schema);
        return schema;
    }

    public boolean getFeature(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                    "FeatureNameNull", null));
        }
        if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            return (fSecurityManager != null && fSecurityManager.isSecureProcessing());
        }
        try {
            return fXMLSchemaLoader.getFeature(name);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(
                        SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                        "feature-not-recognized", new Object [] {identifier}));
            }
            else {
                throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                        "feature-not-supported", new Object [] {identifier}));
            }
        }
    }

    public Object getProperty(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                    "ProperyNameNull", null));
        }
        if (name.equals(SECURITY_MANAGER)) {
            return fSecurityManager;
        }
        else if (name.equals(XMLGRAMMAR_POOL)) {
            throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                    "property-not-supported", new Object [] {name}));
        }

        int index = fXmlFeatures.getIndex(name);
        if (index > -1) {
            return fXmlFeatures.getFeature(index);
        }
        try {
            return fXMLSchemaLoader.getProperty(name);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(
                        SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                        "property-not-recognized", new Object [] {identifier}));
            }
            else {
                throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                        "property-not-supported", new Object [] {identifier}));
            }
        }
    }

    public void setFeature(String name, boolean value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                    "FeatureNameNull", null));
        }
        if (name.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            if (System.getSecurityManager() != null && (!value)) {
                throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(null,
                        "jaxp-secureprocessing-feature", null));
            }

            fSecurityManager.setSecureProcessing(value);
            if (value) {
                if (Constants.IS_JDK8_OR_ABOVE) {
                    fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD,
                            XMLSecurityPropertyManager.State.FSP, Constants.EXTERNAL_ACCESS_DEFAULT_FSP);
                    fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA,
                            XMLSecurityPropertyManager.State.FSP, Constants.EXTERNAL_ACCESS_DEFAULT_FSP);
                }
            }

            fXMLSchemaLoader.setProperty(SECURITY_MANAGER, fSecurityManager);
            return;
        } else if (name.equals(Constants.ORACLE_FEATURE_SERVICE_MECHANISM)) {
            if (System.getSecurityManager() != null)
                return;
        }

        if ((fXmlFeatures != null) &&
                    fXmlFeatures.setFeature(name, JdkXmlFeatures.State.APIPROPERTY, value)) {
            if (name.equals(JdkXmlUtils.OVERRIDE_PARSER)
                    || name.equals(Constants.ORACLE_FEATURE_SERVICE_MECHANISM)) {
                fXMLSchemaLoader.setFeature(name, value);
            }
            return;
        }
        try {
            fXMLSchemaLoader.setFeature(name, value);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(
                        SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                        "feature-not-recognized", new Object [] {identifier}));
            }
            else {
                throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                        "feature-not-supported", new Object [] {identifier}));
            }
        }
    }

    public void setProperty(String name, Object object)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                    "ProperyNameNull", null));
        }
        if (name.equals(SECURITY_MANAGER)) {
            fSecurityManager = XMLSecurityManager.convert(object, fSecurityManager);
            fXMLSchemaLoader.setProperty(SECURITY_MANAGER, fSecurityManager);
            return;
        } else if (name.equals(Constants.XML_SECURITY_PROPERTY_MANAGER)) {
            if (object == null) {
                fSecurityPropertyMgr = new XMLSecurityPropertyManager();
            } else {
                fSecurityPropertyMgr = (XMLSecurityPropertyManager)object;
            }
            fXMLSchemaLoader.setProperty(Constants.XML_SECURITY_PROPERTY_MANAGER, fSecurityPropertyMgr);
            return;
        }
        else if (name.equals(XMLGRAMMAR_POOL)) {
            throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                    "property-not-supported", new Object [] {name}));
        }
        try {
            if (fSecurityManager == null ||
                    !fSecurityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, object)) {
                if (fSecurityPropertyMgr == null ||
                        !fSecurityPropertyMgr.setValue(name, XMLSecurityPropertyManager.State.APIPROPERTY, object)) {
                    fXMLSchemaLoader.setProperty(name, object);
                }
            }
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(
                        SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                        "property-not-recognized", new Object [] {identifier}));
            }
            else {
                throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(fXMLSchemaLoader.getLocale(),
                        "property-not-supported", new Object [] {identifier}));
            }
        }
    }

    private void propagateFeatures(AbstractXMLSchema schema) {
        schema.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING,
                (fSecurityManager != null && fSecurityManager.isSecureProcessing()));
        schema.setFeature(JdkXmlUtils.OVERRIDE_PARSER, fOverrideDefaultParser);
        String[] features = fXMLSchemaLoader.getRecognizedFeatures();
        for (int i = 0; i < features.length; ++i) {
            boolean state = fXMLSchemaLoader.getFeature(features[i]);
            schema.setFeature(features[i], state);
        }
    }

    private void propagateProperties(AbstractXMLSchema schema) {
        String[] properties = fXMLSchemaLoader.getRecognizedProperties();
        for (int i = 0; i < properties.length; ++i) {
            Object state = fXMLSchemaLoader.getProperty(properties[i]);
            schema.setProperty(properties[i], state);
        }
    }



    static class XMLGrammarPoolImplExtension extends XMLGrammarPoolImpl {


        public XMLGrammarPoolImplExtension() {
            super();
        }


        public XMLGrammarPoolImplExtension(int initialCapacity) {
            super(initialCapacity);
        }


        int getGrammarCount() {
            return fGrammarCount;
        }

    } static class XMLGrammarPoolWrapper implements XMLGrammarPool {

        private XMLGrammarPool fGrammarPool;



        public Grammar[] retrieveInitialGrammarSet(String grammarType) {
            return fGrammarPool.retrieveInitialGrammarSet(grammarType);
        }

        public void cacheGrammars(String grammarType, Grammar[] grammars) {
            fGrammarPool.cacheGrammars(grammarType, grammars);
        }

        public Grammar retrieveGrammar(XMLGrammarDescription desc) {
            return fGrammarPool.retrieveGrammar(desc);
        }

        public void lockPool() {
            fGrammarPool.lockPool();
        }

        public void unlockPool() {
            fGrammarPool.unlockPool();
        }

        public void clear() {
            fGrammarPool.clear();
        }



        void setGrammarPool(XMLGrammarPool grammarPool) {
            fGrammarPool = grammarPool;
        }

        XMLGrammarPool getGrammarPool() {
            return fGrammarPool;
        }

    } }