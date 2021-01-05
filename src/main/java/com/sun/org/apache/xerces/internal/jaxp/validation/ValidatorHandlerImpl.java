


package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;
import jdk.xml.internal.JdkXmlUtils;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.util.AttributesProxy;
import com.sun.org.apache.xerces.internal.util.SAXLocatorWrapper;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.ItemPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.EntityResolver2;


final class ValidatorHandlerImpl extends ValidatorHandler implements
    DTDHandler, EntityState, PSVIProvider, ValidatorHelper, XMLDocumentHandler {

    private static final String NAMESPACE_PREFIXES =
        Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACE_PREFIXES_FEATURE;


    protected static final String STRING_INTERNING =
        Constants.SAX_FEATURE_PREFIX + Constants.STRING_INTERNING_FEATURE;

    private static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    private static final String NAMESPACE_CONTEXT =
        Constants.XERCES_PROPERTY_PREFIX + Constants.NAMESPACE_CONTEXT_PROPERTY;


    private static final String SCHEMA_VALIDATOR =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_VALIDATOR_PROPERTY;


    private static final String SECURITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;


    private static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    private static final String VALIDATION_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATION_MANAGER_PROPERTY;


    private static final String XML_SECURITY_PROPERTY_MANAGER =
            Constants.XML_SECURITY_PROPERTY_MANAGER;

    private XMLErrorReporter fErrorReporter;


    private NamespaceContext fNamespaceContext;


    private XMLSchemaValidator fSchemaValidator;


    private SymbolTable fSymbolTable;


    private ValidationManager fValidationManager;


    private XMLSchemaValidatorComponentManager fComponentManager;


    private final SAXLocatorWrapper fSAXLocatorWrapper = new SAXLocatorWrapper();


    private boolean fNeedPushNSContext = true;


    private HashMap fUnparsedEntities = null;


    private boolean fStringsInternalized = false;


    private final QName fElementQName = new QName();
    private final QName fAttributeQName = new QName();
    private final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
    private final AttributesProxy fAttrAdapter = new AttributesProxy(fAttributes);
    private final XMLString fTempString = new XMLString();

    private ContentHandler fContentHandler = null;



    public ValidatorHandlerImpl(XSGrammarPoolContainer grammarContainer) {
        this(new XMLSchemaValidatorComponentManager(grammarContainer));
        fComponentManager.addRecognizedFeatures(new String [] {NAMESPACE_PREFIXES});
        fComponentManager.setFeature(NAMESPACE_PREFIXES, false);
        setErrorHandler(null);
        setResourceResolver(null);
    }

    public ValidatorHandlerImpl(XMLSchemaValidatorComponentManager componentManager) {
        fComponentManager = componentManager;
        fErrorReporter = (XMLErrorReporter) fComponentManager.getProperty(ERROR_REPORTER);
        fNamespaceContext = (NamespaceContext) fComponentManager.getProperty(NAMESPACE_CONTEXT);
        fSchemaValidator = (XMLSchemaValidator) fComponentManager.getProperty(SCHEMA_VALIDATOR);
        fSymbolTable = (SymbolTable) fComponentManager.getProperty(SYMBOL_TABLE);
        fValidationManager = (ValidationManager) fComponentManager.getProperty(VALIDATION_MANAGER);
    }



    public void setContentHandler(ContentHandler receiver) {
        fContentHandler = receiver;
    }

    public ContentHandler getContentHandler() {
        return fContentHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        fComponentManager.setErrorHandler(errorHandler);
    }

    public ErrorHandler getErrorHandler() {
        return fComponentManager.getErrorHandler();
    }

    public void setResourceResolver(LSResourceResolver resourceResolver) {
        fComponentManager.setResourceResolver(resourceResolver);
    }

    public LSResourceResolver getResourceResolver() {
        return fComponentManager.getResourceResolver();
    }

    public TypeInfoProvider getTypeInfoProvider() {
        return fTypeInfoProvider;
    }

    public boolean getFeature(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            return fComponentManager.getFeature(name);
        }
        catch (XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = e.getType() == Status.NOT_RECOGNIZED ?
                    "feature-not-recognized" : "feature-not-supported";
            throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(fComponentManager.getLocale(),
                    key, new Object [] {identifier}));
        }
    }

    public void setFeature(String name, boolean value)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            fComponentManager.setFeature(name, value);
        }
        catch (XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key;
            if (e.getType() == Status.NOT_ALLOWED) {
                throw new SAXNotSupportedException(
                    SAXMessageFormatter.formatMessage(fComponentManager.getLocale(),
                    "jaxp-secureprocessing-feature", null));
            } else if (e.getType() == Status.NOT_RECOGNIZED) {
                key = "feature-not-recognized";
            } else {
                key = "feature-not-supported";
            }
            throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(fComponentManager.getLocale(),
                    key, new Object [] {identifier}));
        }
    }

    public Object getProperty(String name)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            return fComponentManager.getProperty(name);
        }
        catch (XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = e.getType() == Status.NOT_RECOGNIZED ?
                    "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(fComponentManager.getLocale(),
                    key, new Object [] {identifier}));
        }
    }

    public void setProperty(String name, Object object)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            fComponentManager.setProperty(name, object);
        }
        catch (XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = e.getType() == Status.NOT_RECOGNIZED ?
                    "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(
                    SAXMessageFormatter.formatMessage(fComponentManager.getLocale(),
                    key, new Object [] {identifier}));
        }
    }



    public boolean isEntityDeclared(String name) {
        return false;
    }

    public boolean isEntityUnparsed(String name) {
        if (fUnparsedEntities != null) {
            return fUnparsedEntities.containsKey(name);
        }
        return false;
    }



    public void startDocument(XMLLocator locator, String encoding,
            NamespaceContext namespaceContext, Augmentations augs)
            throws XNIException {
        if (fContentHandler != null) {
            try {
                fContentHandler.startDocument();
            }
            catch (SAXException e) {
                throw new XNIException(e);
            }
        }
    }

    public void xmlDecl(String version, String encoding, String standalone,
            Augmentations augs) throws XNIException {}

    public void doctypeDecl(String rootElement, String publicId,
            String systemId, Augmentations augs) throws XNIException {}

    public void comment(XMLString text, Augmentations augs) throws XNIException {}

    public void processingInstruction(String target, XMLString data,
            Augmentations augs) throws XNIException {
        if (fContentHandler != null) {
            try {
                fContentHandler.processingInstruction(target, data.toString());
            }
            catch (SAXException e) {
                throw new XNIException(e);
            }
        }
    }

    public void startElement(QName element, XMLAttributes attributes,
            Augmentations augs) throws XNIException {
        if (fContentHandler != null) {
            try {
                fTypeInfoProvider.beginStartElement(augs, attributes);
                fContentHandler.startElement((element.uri != null) ? element.uri : XMLSymbols.EMPTY_STRING,
                        element.localpart, element.rawname, fAttrAdapter);
            }
            catch (SAXException e) {
                throw new XNIException(e);
            }
            finally {
                fTypeInfoProvider.finishStartElement();
            }
        }
    }

    public void emptyElement(QName element, XMLAttributes attributes,
            Augmentations augs) throws XNIException {

        startElement(element, attributes, augs);
        endElement(element, augs);
    }

    public void startGeneralEntity(String name,
            XMLResourceIdentifier identifier, String encoding,
            Augmentations augs) throws XNIException {}

    public void textDecl(String version, String encoding, Augmentations augs)
            throws XNIException {}

    public void endGeneralEntity(String name, Augmentations augs)
            throws XNIException {}

    public void characters(XMLString text, Augmentations augs)
            throws XNIException {
        if (fContentHandler != null) {
            if (text.length == 0) {
                return;
            }
            try {
                fContentHandler.characters(text.ch, text.offset, text.length);
            }
            catch (SAXException e) {
                throw new XNIException(e);
            }
        }
    }

    public void ignorableWhitespace(XMLString text, Augmentations augs)
            throws XNIException {
        if (fContentHandler != null) {
            try {
                fContentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
            }
            catch (SAXException e) {
                throw new XNIException(e);
            }
        }
    }

    public void endElement(QName element, Augmentations augs)
            throws XNIException {
        if (fContentHandler != null) {
            try {
                fTypeInfoProvider.beginEndElement(augs);
                fContentHandler.endElement((element.uri != null) ? element.uri : XMLSymbols.EMPTY_STRING,
                        element.localpart, element.rawname);
            }
            catch (SAXException e) {
                throw new XNIException(e);
            }
            finally {
                fTypeInfoProvider.finishEndElement();
            }
        }
    }

    public void startCDATA(Augmentations augs) throws XNIException {}

    public void endCDATA(Augmentations augs) throws XNIException {}

    public void endDocument(Augmentations augs) throws XNIException {
        if (fContentHandler != null) {
            try {
                fContentHandler.endDocument();
            }
            catch (SAXException e) {
                throw new XNIException(e);
            }
        }
    }

    public void setDocumentSource(XMLDocumentSource source) {}

    public XMLDocumentSource getDocumentSource() {
        return fSchemaValidator;
    }



    public void setDocumentLocator(Locator locator) {
        fSAXLocatorWrapper.setLocator(locator);
        if (fContentHandler != null) {
            fContentHandler.setDocumentLocator(locator);
        }
    }

    public void startDocument() throws SAXException {
        fComponentManager.reset();
        fSchemaValidator.setDocumentHandler(this);
        fValidationManager.setEntityState(this);
        fTypeInfoProvider.finishStartElement(); fNeedPushNSContext = true;
        if (fUnparsedEntities != null && !fUnparsedEntities.isEmpty()) {
            fUnparsedEntities.clear();
        }
        fErrorReporter.setDocumentLocator(fSAXLocatorWrapper);
        try {
            fSchemaValidator.startDocument(fSAXLocatorWrapper, fSAXLocatorWrapper.getEncoding(), fNamespaceContext, null);
        }
        catch (XMLParseException e) {
            throw Util.toSAXParseException(e);
        }
        catch (XNIException e) {
            throw Util.toSAXException(e);
        }
    }

    public void endDocument() throws SAXException {
        fSAXLocatorWrapper.setLocator(null);
        try {
            fSchemaValidator.endDocument(null);
        }
        catch (XMLParseException e) {
            throw Util.toSAXParseException(e);
        }
        catch (XNIException e) {
            throw Util.toSAXException(e);
        }
    }

    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        String prefixSymbol;
        String uriSymbol;
        if (!fStringsInternalized) {
            prefixSymbol = (prefix != null) ? fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING;
            uriSymbol = (uri != null && uri.length() > 0) ? fSymbolTable.addSymbol(uri) : null;
        }
        else {
            prefixSymbol = (prefix != null) ? prefix : XMLSymbols.EMPTY_STRING;
            uriSymbol = (uri != null && uri.length() > 0) ? uri : null;
        }
        if (fNeedPushNSContext) {
            fNeedPushNSContext = false;
            fNamespaceContext.pushContext();
        }
        fNamespaceContext.declarePrefix(prefixSymbol, uriSymbol);
        if (fContentHandler != null) {
            fContentHandler.startPrefixMapping(prefix, uri);
        }
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        if (fContentHandler != null) {
            fContentHandler.endPrefixMapping(prefix);
        }
    }

    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        if (fNeedPushNSContext) {
            fNamespaceContext.pushContext();
        }
        fNeedPushNSContext = true;

        fillQName(fElementQName, uri, localName, qName);

        if (atts instanceof Attributes2) {
            fillXMLAttributes2((Attributes2) atts);
        }
        else {
            fillXMLAttributes(atts);
        }

        try {
            fSchemaValidator.startElement(fElementQName, fAttributes, null);
        }
        catch (XMLParseException e) {
            throw Util.toSAXParseException(e);
        }
        catch (XNIException e) {
            throw Util.toSAXException(e);
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        fillQName(fElementQName, uri, localName, qName);
        try {
            fSchemaValidator.endElement(fElementQName, null);
        }
        catch (XMLParseException e) {
            throw Util.toSAXParseException(e);
        }
        catch (XNIException e) {
            throw Util.toSAXException(e);
        }
        finally {
            fNamespaceContext.popContext();
        }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        try {
            fTempString.setValues(ch, start, length);
            fSchemaValidator.characters(fTempString, null);
        }
        catch (XMLParseException e) {
            throw Util.toSAXParseException(e);
        }
        catch (XNIException e) {
            throw Util.toSAXException(e);
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        try {
            fTempString.setValues(ch, start, length);
            fSchemaValidator.ignorableWhitespace(fTempString, null);
        }
        catch (XMLParseException e) {
            throw Util.toSAXParseException(e);
        }
        catch (XNIException e) {
            throw Util.toSAXException(e);
        }
    }

    public void processingInstruction(String target, String data)
            throws SAXException {

        if (fContentHandler != null) {
            fContentHandler.processingInstruction(target, data);
        }
    }

    public void skippedEntity(String name) throws SAXException {
        if (fContentHandler != null) {
            fContentHandler.skippedEntity(name);
        }
    }



    public void notationDecl(String name, String publicId,
            String systemId) throws SAXException {}

    public void unparsedEntityDecl(String name, String publicId,
            String systemId, String notationName) throws SAXException {
        if (fUnparsedEntities == null) {
            fUnparsedEntities = new HashMap();
        }
        fUnparsedEntities.put(name, name);
    }



    public void validate(Source source, Result result)
        throws SAXException, IOException {
        if (result instanceof SAXResult || result == null) {
            final SAXSource saxSource = (SAXSource) source;
            final SAXResult saxResult = (SAXResult) result;

            if (result != null) {
                setContentHandler(saxResult.getHandler());
            }

            try {
                XMLReader reader = saxSource.getXMLReader();
                if( reader==null ) {
                    reader = JdkXmlUtils.getXMLReader(fComponentManager.getFeature(JdkXmlUtils.OVERRIDE_PARSER),
                            fComponentManager.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING));

                    try {
                        if (reader instanceof com.sun.org.apache.xerces.internal.parsers.SAXParser) {
                           XMLSecurityManager securityManager =
                                   (XMLSecurityManager) fComponentManager.getProperty(SECURITY_MANAGER);
                           if (securityManager != null) {
                               try {
                                   reader.setProperty(SECURITY_MANAGER, securityManager);
                               }
                               catch (SAXException exc) {}
                           }
                           try {
                               XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)
                                       fComponentManager.getProperty(XML_SECURITY_PROPERTY_MANAGER);
                               reader.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD,
                                       spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD));
                           } catch (SAXException exc) {
                               XMLSecurityManager.printWarning(reader.getClass().getName(),
                                       XMLConstants.ACCESS_EXTERNAL_DTD, exc);
                           }
                        }
                    } catch( Exception e ) {
                        throw new FactoryConfigurationError(e);
                    }
                }

                try {
                    fStringsInternalized = reader.getFeature(STRING_INTERNING);
                }
                catch (SAXException exc) {
                    fStringsInternalized = false;
                }

                ErrorHandler errorHandler = fComponentManager.getErrorHandler();
                reader.setErrorHandler(errorHandler != null ? errorHandler : DraconianErrorHandler.getInstance());
                reader.setEntityResolver(fResolutionForwarder);
                fResolutionForwarder.setEntityResolver(fComponentManager.getResourceResolver());
                reader.setContentHandler(this);
                reader.setDTDHandler(this);

                InputSource is = saxSource.getInputSource();
                reader.parse(is);
            }
            finally {
                setContentHandler(null);
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(fComponentManager.getLocale(),
                "SourceResultMismatch",
                new Object [] {source.getClass().getName(), result.getClass().getName()}));
    }



    public ElementPSVI getElementPSVI() {
        return fTypeInfoProvider.getElementPSVI();
    }

    public AttributePSVI getAttributePSVI(int index) {
        return fTypeInfoProvider.getAttributePSVI(index);
    }

    public AttributePSVI getAttributePSVIByName(String uri, String localname) {
        return fTypeInfoProvider.getAttributePSVIByName(uri, localname);
    }

    private void fillQName(QName toFill, String uri, String localpart, String raw) {
        if (!fStringsInternalized) {
            uri = (uri != null && uri.length() > 0) ? fSymbolTable.addSymbol(uri) : null;
            localpart = (localpart != null) ? fSymbolTable.addSymbol(localpart) : XMLSymbols.EMPTY_STRING;
            raw = (raw != null) ? fSymbolTable.addSymbol(raw) : XMLSymbols.EMPTY_STRING;
        }
        else {
            if (uri != null && uri.length() == 0) {
                uri = null;
            }
            if (localpart == null) {
                localpart = XMLSymbols.EMPTY_STRING;
            }
            if (raw == null) {
                raw = XMLSymbols.EMPTY_STRING;
            }
        }
        String prefix = XMLSymbols.EMPTY_STRING;
        int prefixIdx = raw.indexOf(':');
        if (prefixIdx != -1) {
            prefix = fSymbolTable.addSymbol(raw.substring(0, prefixIdx));
        }
        toFill.setValues(prefix, localpart, raw, uri);
    }


    private void fillXMLAttributes(Attributes att) {
        fAttributes.removeAllAttributes();
        final int len = att.getLength();
        for (int i = 0; i < len; ++i) {
            fillXMLAttribute(att, i);
            fAttributes.setSpecified(i, true);
        }
    }


    private void fillXMLAttributes2(Attributes2 att) {
        fAttributes.removeAllAttributes();
        final int len = att.getLength();
        for (int i = 0; i < len; ++i) {
            fillXMLAttribute(att, i);
            fAttributes.setSpecified(i, att.isSpecified(i));
            if (att.isDeclared(i)) {
                fAttributes.getAugmentations(i).putItem(Constants.ATTRIBUTE_DECLARED, Boolean.TRUE);
            }
        }
    }


    private void fillXMLAttribute(Attributes att, int index) {
        fillQName(fAttributeQName, att.getURI(index), att.getLocalName(index), att.getQName(index));
        String type = att.getType(index);
        fAttributes.addAttributeNS(fAttributeQName, (type != null) ? type : XMLSymbols.fCDATASymbol, att.getValue(index));
    }


    private final XMLSchemaTypeInfoProvider fTypeInfoProvider = new XMLSchemaTypeInfoProvider();
    private class XMLSchemaTypeInfoProvider extends TypeInfoProvider {


        private Augmentations fElementAugs;


        private XMLAttributes fAttributes;


        private boolean fInStartElement = false;


        private boolean fInEndElement = false;


        void beginStartElement(Augmentations elementAugs, XMLAttributes attributes) {
            fInStartElement = true;
            fElementAugs = elementAugs;
            fAttributes = attributes;
        }


        void finishStartElement() {
            fInStartElement = false;
            fElementAugs = null;
            fAttributes = null;
        }


        void beginEndElement(Augmentations elementAugs) {
            fInEndElement = true;
            fElementAugs = elementAugs;
        }


        void finishEndElement() {
            fInEndElement = false;
            fElementAugs = null;
        }


        private void checkState(boolean forElementInfo) {
            if (! (fInStartElement || (fInEndElement && forElementInfo))) {
                throw new IllegalStateException(JAXPValidationMessageFormatter.formatMessage(fComponentManager.getLocale(),
                        "TypeInfoProviderIllegalState", null));
            }
        }

        public TypeInfo getAttributeTypeInfo(int index) {
            checkState(false);
            return getAttributeType(index);
        }

        private TypeInfo getAttributeType( int index ) {
            checkState(false);
            if( index<0 || fAttributes.getLength()<=index )
                throw new IndexOutOfBoundsException(Integer.toString(index));
            Augmentations augs = fAttributes.getAugmentations(index);
            if (augs == null) return null;
            AttributePSVI psvi = (AttributePSVI)augs.getItem(Constants.ATTRIBUTE_PSVI);
            return getTypeInfoFromPSVI(psvi);
        }

        public TypeInfo getAttributeTypeInfo(String attributeUri, String attributeLocalName) {
            checkState(false);
            return getAttributeTypeInfo(fAttributes.getIndex(attributeUri,attributeLocalName));
        }

        public TypeInfo getAttributeTypeInfo(String attributeQName) {
            checkState(false);
            return getAttributeTypeInfo(fAttributes.getIndex(attributeQName));
        }

        public TypeInfo getElementTypeInfo() {
            checkState(true);
            if (fElementAugs == null) return null;
            ElementPSVI psvi = (ElementPSVI)fElementAugs.getItem(Constants.ELEMENT_PSVI);
            return getTypeInfoFromPSVI(psvi);
        }

        private TypeInfo getTypeInfoFromPSVI( ItemPSVI psvi ) {
            if(psvi==null)  return null;

            if( psvi.getValidity()== ElementPSVI.VALIDITY_VALID ) {
                XSTypeDefinition t = psvi.getMemberTypeDefinition();
                if (t != null) {
                    return (t instanceof TypeInfo) ? (TypeInfo) t : null;
                }
            }

            XSTypeDefinition t = psvi.getTypeDefinition();
            if (t != null) {
                return (t instanceof TypeInfo) ? (TypeInfo) t : null;
            }
            return null;
        }

        public boolean isIdAttribute(int index) {
            checkState(false);
            XSSimpleType type = (XSSimpleType)getAttributeType(index);
            if(type==null)  return false;
            return type.isIDType();
        }

        public boolean isSpecified(int index) {
            checkState(false);
            return fAttributes.isSpecified(index);
        }



        ElementPSVI getElementPSVI() {
            return (fElementAugs != null) ? (ElementPSVI) fElementAugs.getItem(Constants.ELEMENT_PSVI) : null;
        }

        AttributePSVI getAttributePSVI(int index) {
            if (fAttributes != null) {
                Augmentations augs = fAttributes.getAugmentations(index);
                if (augs != null) {
                    return (AttributePSVI) augs.getItem(Constants.ATTRIBUTE_PSVI);
                }
            }
            return null;
        }

        AttributePSVI getAttributePSVIByName(String uri, String localname) {
            if (fAttributes != null) {
                Augmentations augs = fAttributes.getAugmentations(uri, localname);
                if (augs != null) {
                    return (AttributePSVI) augs.getItem(Constants.ATTRIBUTE_PSVI);
                }
            }
            return null;
        }
    }


    private final ResolutionForwarder fResolutionForwarder = new ResolutionForwarder(null);
    static final class ResolutionForwarder
        implements EntityResolver2 {

        private static final String XML_TYPE = "http:protected LSResourceResolver fEntityResolver;

        public ResolutionForwarder() {}


        public ResolutionForwarder(LSResourceResolver entityResolver) {
            setEntityResolver(entityResolver);
        }

        public void setEntityResolver(LSResourceResolver entityResolver) {
            fEntityResolver = entityResolver;
        } public LSResourceResolver getEntityResolver() {
            return fEntityResolver;
        } public InputSource getExternalSubset(String name, String baseURI)
                throws SAXException, IOException {
            return null;
        }


        public InputSource resolveEntity(String name, String publicId,
                String baseURI, String systemId) throws SAXException, IOException {
            if (fEntityResolver != null) {
                LSInput lsInput = fEntityResolver.resolveResource(XML_TYPE, null, publicId, systemId, baseURI);
                if (lsInput != null) {
                    final String pubId = lsInput.getPublicId();
                    final String sysId = lsInput.getSystemId();
                    final String baseSystemId = lsInput.getBaseURI();
                    final Reader charStream = lsInput.getCharacterStream();
                    final InputStream byteStream = lsInput.getByteStream();
                    final String data = lsInput.getStringData();
                    final String encoding = lsInput.getEncoding();


                    InputSource inputSource = new InputSource();
                    inputSource.setPublicId(pubId);
                    inputSource.setSystemId((baseSystemId != null) ? resolveSystemId(systemId, baseSystemId) : systemId);

                    if (charStream != null) {
                        inputSource.setCharacterStream(charStream);
                    }
                    else if (byteStream != null) {
                        inputSource.setByteStream(byteStream);
                    }
                    else if (data != null && data.length() != 0) {
                        inputSource.setCharacterStream(new StringReader(data));
                    }
                    inputSource.setEncoding(encoding);
                    return inputSource;
                }
            }
            return null;
        }


        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {
            return resolveEntity(null, publicId, null, systemId);
        }


        private String resolveSystemId(String systemId, String baseURI) {
            try {
                return XMLEntityManager.expandSystemId(systemId, baseURI, false);
            }
            catch (URI.MalformedURIException ex) {
                return systemId;
            }
        }
    }
}
