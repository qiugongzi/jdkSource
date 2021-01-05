


package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.EntityResolver2Wrapper;
import com.sun.org.apache.xerces.internal.util.EntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import java.io.IOException;
import java.util.Locale;
import javax.xml.XMLConstants;
import org.xml.sax.AttributeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.LocatorImpl;


public abstract class AbstractSAXParser
    extends AbstractXMLDocumentParser
    implements PSVIProvider, Parser, XMLReader {

    protected static final String NAMESPACES =
        Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE;


    protected static final String NAMESPACE_PREFIXES =
        Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACE_PREFIXES_FEATURE;


    protected static final String STRING_INTERNING =
        Constants.SAX_FEATURE_PREFIX + Constants.STRING_INTERNING_FEATURE;


    protected static final String ALLOW_UE_AND_NOTATION_EVENTS =
        Constants.SAX_FEATURE_PREFIX + Constants.ALLOW_DTD_EVENTS_AFTER_ENDDTD_FEATURE;


    private static final String[] RECOGNIZED_FEATURES = {
        NAMESPACES,
        NAMESPACE_PREFIXES,
        STRING_INTERNING,
    };

    protected static final String LEXICAL_HANDLER =
        Constants.SAX_PROPERTY_PREFIX + Constants.LEXICAL_HANDLER_PROPERTY;


    protected static final String DECLARATION_HANDLER =
        Constants.SAX_PROPERTY_PREFIX + Constants.DECLARATION_HANDLER_PROPERTY;


    protected static final String DOM_NODE =
        Constants.SAX_PROPERTY_PREFIX + Constants.DOM_NODE_PROPERTY;


    private static final String SECURITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;


    private static final String[] RECOGNIZED_PROPERTIES = {
        LEXICAL_HANDLER,
        DECLARATION_HANDLER,
        DOM_NODE,
    };

    protected boolean fNamespaces;


    protected boolean fNamespacePrefixes = false;


    protected boolean fLexicalHandlerParameterEntities = true;


    protected boolean fStandalone;


    protected boolean fResolveDTDURIs = true;


    protected boolean fUseEntityResolver2 = true;


    protected boolean fXMLNSURIs = false;

    protected ContentHandler fContentHandler;


    protected DocumentHandler fDocumentHandler;


    protected NamespaceContext fNamespaceContext;


    protected org.xml.sax.DTDHandler fDTDHandler;


    protected DeclHandler fDeclHandler;


    protected LexicalHandler fLexicalHandler;

    protected QName fQName = new QName();

    protected boolean fParseInProgress = false;

    protected String fVersion;

    private final AttributesProxy fAttributesProxy = new AttributesProxy();
    private Augmentations fAugmentations = null;


    private static final int BUFFER_SIZE = 20;
    private char[] fCharBuffer =  new char[BUFFER_SIZE];

    protected SymbolHash fDeclaredAttrs = null;

    protected AbstractSAXParser(XMLParserConfiguration config) {
        super(config);

        config.addRecognizedFeatures(RECOGNIZED_FEATURES);
        config.addRecognizedProperties(RECOGNIZED_PROPERTIES);

        try {
            config.setFeature(ALLOW_UE_AND_NOTATION_EVENTS, false);
        }
        catch (XMLConfigurationException e) {
            }
    } public void startDocument(XMLLocator locator, String encoding,
                              NamespaceContext namespaceContext, Augmentations augs)
        throws XNIException {

        fNamespaceContext = namespaceContext;

        try {
            if (fDocumentHandler != null) {
                if (locator != null) {
                    fDocumentHandler.setDocumentLocator(new LocatorProxy(locator));
                }
                fDocumentHandler.startDocument();
            }

            if (fContentHandler != null) {
                if (locator != null) {
                    fContentHandler.setDocumentLocator(new LocatorProxy(locator));
                }
                fContentHandler.startDocument();
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void xmlDecl(String version, String encoding, String standalone, Augmentations augs)
        throws XNIException {
        fVersion = version;
        fStandalone = "yes".equals(standalone);
    } public void doctypeDecl(String rootElement,
                            String publicId, String systemId, Augmentations augs)
        throws XNIException {
        fInDTD = true;

        try {
            if (fLexicalHandler != null) {
                fLexicalHandler.startDTD(rootElement, publicId, systemId);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

        if(fDeclHandler != null) {
            fDeclaredAttrs = new SymbolHash();
        }

    } public void startGeneralEntity(String name, XMLResourceIdentifier identifier,
                                   String encoding, Augmentations augs)
        throws XNIException {

        try {
            if (augs != null && Boolean.TRUE.equals(augs.getItem(Constants.ENTITY_SKIPPED))) {
                if (fContentHandler != null) {
                    fContentHandler.skippedEntity(name);
                }
            }
            else {
                if (fLexicalHandler != null) {
                    fLexicalHandler.startEntity(name);
                }
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void endGeneralEntity(String name, Augmentations augs) throws XNIException {

        try {
            if (augs == null || !Boolean.TRUE.equals(augs.getItem(Constants.ENTITY_SKIPPED))) {
                if (fLexicalHandler != null) {
                    fLexicalHandler.endEntity(name);
                }
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException {

        try {
            if (fDocumentHandler != null) {
                fAttributesProxy.setAttributes(attributes);
                fDocumentHandler.startElement(element.rawname, fAttributesProxy);
            }

            if (fContentHandler != null) {

                if (fNamespaces) {
                    startNamespaceMapping();

                    int len = attributes.getLength();
                    if (!fNamespacePrefixes) {
                        for (int i = len - 1; i >= 0; --i) {
                            attributes.getName(i, fQName);
                            if ((fQName.prefix == XMLSymbols.PREFIX_XMLNS) ||
                               (fQName.rawname == XMLSymbols.PREFIX_XMLNS)) {
                                attributes.removeAttributeAt(i);
                            }
                        }
                    }
                    else if (!fXMLNSURIs) {
                        for (int i = len - 1; i >= 0; --i) {
                            attributes.getName(i, fQName);
                            if ((fQName.prefix == XMLSymbols.PREFIX_XMLNS) ||
                               (fQName.rawname == XMLSymbols.PREFIX_XMLNS)) {
                                fQName.prefix = "";
                                fQName.uri = "";
                                fQName.localpart = "";
                                attributes.setName(i, fQName);
                            }
                        }
                    }
                }

                fAugmentations = augs;

                String uri = element.uri != null ? element.uri : "";
                String localpart = fNamespaces ? element.localpart : "";
                fAttributesProxy.setAttributes(attributes);
                fContentHandler.startElement(uri, localpart, element.rawname,
                                             fAttributesProxy);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void characters(XMLString text, Augmentations augs) throws XNIException {

        if (text.length == 0) {
            return;
        }


        try {
            if (fDocumentHandler != null) {
                fDocumentHandler.characters(text.ch, text.offset, text.length);
            }

            if (fContentHandler != null) {
                fContentHandler.characters(text.ch, text.offset, text.length);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {

        try {
            if (fDocumentHandler != null) {
                fDocumentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
            }

            if (fContentHandler != null) {
                fContentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void endElement(QName element, Augmentations augs) throws XNIException {


        try {
            if (fDocumentHandler != null) {
                fDocumentHandler.endElement(element.rawname);
            }

            if (fContentHandler != null) {
                fAugmentations = augs;
                String uri = element.uri != null ? element.uri : "";
                String localpart = fNamespaces ? element.localpart : "";
                fContentHandler.endElement(uri, localpart,
                                           element.rawname);
                if (fNamespaces) {
                    endNamespaceMapping();
                }
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void startCDATA(Augmentations augs) throws XNIException {

        try {
            if (fLexicalHandler != null) {
                fLexicalHandler.startCDATA();
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void endCDATA(Augmentations augs) throws XNIException {

        try {
            if (fLexicalHandler != null) {
                fLexicalHandler.endCDATA();
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void comment(XMLString text, Augmentations augs) throws XNIException {

        try {
            if (fLexicalHandler != null) {
                fLexicalHandler.comment(text.ch, 0, text.length);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void processingInstruction(String target, XMLString data, Augmentations augs)
        throws XNIException {

        try {
            if (fDocumentHandler != null) {
                fDocumentHandler.processingInstruction(target,
                                                       data.toString());
            }

            if (fContentHandler != null) {
                fContentHandler.processingInstruction(target, data.toString());
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void endDocument(Augmentations augs) throws XNIException {

        try {
            if (fDocumentHandler != null) {
                fDocumentHandler.endDocument();
            }

            if (fContentHandler != null) {
                fContentHandler.endDocument();
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void startExternalSubset(XMLResourceIdentifier identifier,
                                    Augmentations augs) throws XNIException {
        startParameterEntity("[dtd]", null, null, augs);
    }


    public void endExternalSubset(Augmentations augs) throws XNIException {
        endParameterEntity("[dtd]", augs);
    }


    public void startParameterEntity(String name,
                                     XMLResourceIdentifier identifier,
                                     String encoding, Augmentations augs)
        throws XNIException {

        try {
            if (augs != null && Boolean.TRUE.equals(augs.getItem(Constants.ENTITY_SKIPPED))) {
                if (fContentHandler != null) {
                    fContentHandler.skippedEntity(name);
                }
            }
            else {
                if (fLexicalHandler != null && fLexicalHandlerParameterEntities) {
                    fLexicalHandler.startEntity(name);
                }
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void endParameterEntity(String name, Augmentations augs) throws XNIException {

        try {
            if (augs == null || !Boolean.TRUE.equals(augs.getItem(Constants.ENTITY_SKIPPED))) {
                if (fLexicalHandler != null && fLexicalHandlerParameterEntities) {
                    fLexicalHandler.endEntity(name);
                }
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void elementDecl(String name, String contentModel, Augmentations augs)
        throws XNIException {

        try {
            if (fDeclHandler != null) {
                fDeclHandler.elementDecl(name, contentModel);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void attributeDecl(String elementName, String attributeName,
                              String type, String[] enumeration,
                              String defaultType, XMLString defaultValue,
                              XMLString nonNormalizedDefaultValue, Augmentations augs) throws XNIException {

        try {
            if (fDeclHandler != null) {
                String elemAttr = new StringBuffer(elementName).append("<").append(attributeName).toString();
                if(fDeclaredAttrs.get(elemAttr) != null) {
                    return;
                }
                fDeclaredAttrs.put(elemAttr, Boolean.TRUE);
                if (type.equals("NOTATION") ||
                    type.equals("ENUMERATION")) {

                    StringBuffer str = new StringBuffer();
                    if (type.equals("NOTATION")) {
                      str.append(type);
                      str.append(" (");
                    }
                    else {
                      str.append("(");
                    }
                    for (int i = 0; i < enumeration.length; i++) {
                        str.append(enumeration[i]);
                        if (i < enumeration.length - 1) {
                            str.append('|');
                        }
                    }
                    str.append(')');
                    type = str.toString();
                }
                String value = (defaultValue==null) ? null : defaultValue.toString();
                fDeclHandler.attributeDecl(elementName, attributeName,
                                           type, defaultType, value);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void internalEntityDecl(String name, XMLString text,
                                   XMLString nonNormalizedText,
                                   Augmentations augs) throws XNIException {

        try {
            if (fDeclHandler != null) {
                fDeclHandler.internalEntityDecl(name, text.toString());
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void externalEntityDecl(String name, XMLResourceIdentifier identifier,
                                   Augmentations augs) throws XNIException {
        try {
            if (fDeclHandler != null) {
                String publicId = identifier.getPublicId();
                String systemId = fResolveDTDURIs ?
                    identifier.getExpandedSystemId() : identifier.getLiteralSystemId();
                fDeclHandler.externalEntityDecl(name, publicId, systemId);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void unparsedEntityDecl(String name, XMLResourceIdentifier identifier,
                                   String notation,
                                   Augmentations augs) throws XNIException {
        try {
            if (fDTDHandler != null) {
                String publicId = identifier.getPublicId();
                String systemId = fResolveDTDURIs ?
                    identifier.getExpandedSystemId() : identifier.getLiteralSystemId();
                fDTDHandler.unparsedEntityDecl(name, publicId, systemId, notation);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void notationDecl(String name, XMLResourceIdentifier identifier,
                             Augmentations augs) throws XNIException {
        try {
            if (fDTDHandler != null) {
                String publicId = identifier.getPublicId();
                String systemId = fResolveDTDURIs ?
                    identifier.getExpandedSystemId() : identifier.getLiteralSystemId();
                fDTDHandler.notationDecl(name, publicId, systemId);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }

    } public void endDTD(Augmentations augs) throws XNIException {
        fInDTD = false;

        try {
            if (fLexicalHandler != null) {
                fLexicalHandler.endDTD();
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
        if(fDeclaredAttrs != null) {
            fDeclaredAttrs.clear();
        }

    } public void parse(String systemId) throws SAXException, IOException {

        XMLInputSource source = new XMLInputSource(null, systemId, null);
        try {
            parse(source);
        }

        catch (XMLParseException e) {
            Exception ex = e.getException();
            if (ex == null) {
                LocatorImpl locatorImpl = new LocatorImpl(){
                    public String getXMLVersion() {
                        return fVersion;
                    }
                    public String getEncoding() {
                        return null;
                    }
                };
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
                LocatorImpl locatorImpl = new LocatorImpl() {
                    public String getXMLVersion() {
                        return fVersion;
                    }
                    public String getEncoding() {
                        return null;
                    }
                };
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

    } public void setLocale(Locale locale) throws SAXException {
        fConfiguration.setLocale(locale);

    } public void setDTDHandler(DTDHandler dtdHandler) {
        fDTDHandler = dtdHandler;
    } public void setDocumentHandler(DocumentHandler documentHandler) {
        fDocumentHandler = documentHandler;
    } public void setContentHandler(ContentHandler contentHandler) {
        fContentHandler = contentHandler;
    } public ContentHandler getContentHandler() {
        return fContentHandler;
    } public DTDHandler getDTDHandler() {
        return fDTDHandler;
    } public void setFeature(String featureId, boolean state)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        try {
            if (featureId.startsWith(Constants.SAX_FEATURE_PREFIX)) {
                final int suffixLength = featureId.length() - Constants.SAX_FEATURE_PREFIX.length();

                if (suffixLength == Constants.NAMESPACES_FEATURE.length() &&
                    featureId.endsWith(Constants.NAMESPACES_FEATURE)) {
                    fConfiguration.setFeature(featureId, state);
                    fNamespaces = state;
                    return;
                }

                if (suffixLength == Constants.NAMESPACE_PREFIXES_FEATURE.length() &&
                    featureId.endsWith(Constants.NAMESPACE_PREFIXES_FEATURE)) {
                    fConfiguration.setFeature(featureId, state);
                    fNamespacePrefixes = state;
                    return;
                }

                if (suffixLength == Constants.STRING_INTERNING_FEATURE.length() &&
                    featureId.endsWith(Constants.STRING_INTERNING_FEATURE)) {
                    if (!state) {
                        throw new SAXNotSupportedException(
                            SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                            "false-not-supported", new Object [] {featureId}));
                    }
                    return;
                }

                if (suffixLength == Constants.LEXICAL_HANDLER_PARAMETER_ENTITIES_FEATURE.length() &&
                    featureId.endsWith(Constants.LEXICAL_HANDLER_PARAMETER_ENTITIES_FEATURE)) {
                    fLexicalHandlerParameterEntities = state;
                    return;
                }

                if (suffixLength == Constants.RESOLVE_DTD_URIS_FEATURE.length() &&
                    featureId.endsWith(Constants.RESOLVE_DTD_URIS_FEATURE)) {
                    fResolveDTDURIs = state;
                    return;
                }

                if (suffixLength == Constants.UNICODE_NORMALIZATION_CHECKING_FEATURE.length() &&
                    featureId.endsWith(Constants.UNICODE_NORMALIZATION_CHECKING_FEATURE)) {
                    if (state) {
                        throw new SAXNotSupportedException(
                            SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                            "true-not-supported", new Object [] {featureId}));
                    }
                    return;
                }

                if (suffixLength == Constants.XMLNS_URIS_FEATURE.length() &&
                    featureId.endsWith(Constants.XMLNS_URIS_FEATURE)) {
                    fXMLNSURIs = state;
                    return;
                }

                if (suffixLength == Constants.USE_ENTITY_RESOLVER2_FEATURE.length() &&
                    featureId.endsWith(Constants.USE_ENTITY_RESOLVER2_FEATURE)) {
                    if (state != fUseEntityResolver2) {
                        fUseEntityResolver2 = state;
                        setEntityResolver(getEntityResolver());
                    }
                    return;
                }

                if ((suffixLength == Constants.IS_STANDALONE_FEATURE.length() &&
                    featureId.endsWith(Constants.IS_STANDALONE_FEATURE)) ||
                    (suffixLength == Constants.USE_ATTRIBUTES2_FEATURE.length() &&
                    featureId.endsWith(Constants.USE_ATTRIBUTES2_FEATURE)) ||
                    (suffixLength == Constants.USE_LOCATOR2_FEATURE.length() &&
                    featureId.endsWith(Constants.USE_LOCATOR2_FEATURE)) ||
                    (suffixLength == Constants.XML_11_FEATURE.length() &&
                    featureId.endsWith(Constants.XML_11_FEATURE))) {
                    throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                        "feature-read-only", new Object [] {featureId}));
                }


                }
            else if (featureId.equals(XMLConstants.FEATURE_SECURE_PROCESSING)) {
                if (state) {
                    if (fConfiguration.getProperty(SECURITY_MANAGER )==null) {
                        fConfiguration.setProperty(SECURITY_MANAGER, new XMLSecurityManager());
                    }
                }
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
            if (featureId.startsWith(Constants.SAX_FEATURE_PREFIX)) {
                final int suffixLength = featureId.length() - Constants.SAX_FEATURE_PREFIX.length();

                if (suffixLength == Constants.NAMESPACE_PREFIXES_FEATURE.length() &&
                    featureId.endsWith(Constants.NAMESPACE_PREFIXES_FEATURE)) {
                    boolean state = fConfiguration.getFeature(featureId);
                    return state;
                }
                if (suffixLength == Constants.STRING_INTERNING_FEATURE.length() &&
                    featureId.endsWith(Constants.STRING_INTERNING_FEATURE)) {
                    return true;
                }

                if (suffixLength == Constants.IS_STANDALONE_FEATURE.length() &&
                    featureId.endsWith(Constants.IS_STANDALONE_FEATURE)) {
                    return fStandalone;
                }

                if (suffixLength == Constants.XML_11_FEATURE.length() &&
                    featureId.endsWith(Constants.XML_11_FEATURE)) {
                    return (fConfiguration instanceof XML11Configurable);
                }

                if (suffixLength == Constants.LEXICAL_HANDLER_PARAMETER_ENTITIES_FEATURE.length() &&
                    featureId.endsWith(Constants.LEXICAL_HANDLER_PARAMETER_ENTITIES_FEATURE)) {
                    return fLexicalHandlerParameterEntities;
                }

                if (suffixLength == Constants.RESOLVE_DTD_URIS_FEATURE.length() &&
                    featureId.endsWith(Constants.RESOLVE_DTD_URIS_FEATURE)) {
                    return fResolveDTDURIs;
                }

                if (suffixLength == Constants.XMLNS_URIS_FEATURE.length() &&
                    featureId.endsWith(Constants.XMLNS_URIS_FEATURE)) {
                    return fXMLNSURIs;
                }

                if (suffixLength == Constants.UNICODE_NORMALIZATION_CHECKING_FEATURE.length() &&
                    featureId.endsWith(Constants.UNICODE_NORMALIZATION_CHECKING_FEATURE)) {
                    return false;
                }

                if (suffixLength == Constants.USE_ENTITY_RESOLVER2_FEATURE.length() &&
                    featureId.endsWith(Constants.USE_ENTITY_RESOLVER2_FEATURE)) {
                    return fUseEntityResolver2;
                }

                if ((suffixLength == Constants.USE_ATTRIBUTES2_FEATURE.length() &&
                    featureId.endsWith(Constants.USE_ATTRIBUTES2_FEATURE)) ||
                    (suffixLength == Constants.USE_LOCATOR2_FEATURE.length() &&
                    featureId.endsWith(Constants.USE_LOCATOR2_FEATURE))) {
                    return true;
                }


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

        try {
            if (propertyId.startsWith(Constants.SAX_PROPERTY_PREFIX)) {
                final int suffixLength = propertyId.length() - Constants.SAX_PROPERTY_PREFIX.length();

                if (suffixLength == Constants.LEXICAL_HANDLER_PROPERTY.length() &&
                    propertyId.endsWith(Constants.LEXICAL_HANDLER_PROPERTY)) {
                    try {
                        setLexicalHandler((LexicalHandler)value);
                    }
                    catch (ClassCastException e) {
                        throw new SAXNotSupportedException(
                            SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                            "incompatible-class", new Object [] {propertyId, "org.xml.sax.ext.LexicalHandler"}));
                    }
                    return;
                }
                if (suffixLength == Constants.DECLARATION_HANDLER_PROPERTY.length() &&
                    propertyId.endsWith(Constants.DECLARATION_HANDLER_PROPERTY)) {
                    try {
                        setDeclHandler((DeclHandler)value);
                    }
                    catch (ClassCastException e) {
                        throw new SAXNotSupportedException(
                            SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                            "incompatible-class", new Object [] {propertyId, "org.xml.sax.ext.DeclHandler"}));
                    }
                    return;
                }
                if ((suffixLength == Constants.DOM_NODE_PROPERTY.length() &&
                    propertyId.endsWith(Constants.DOM_NODE_PROPERTY)) ||
                    (suffixLength == Constants.DOCUMENT_XML_VERSION_PROPERTY.length() &&
                    propertyId.endsWith(Constants.DOCUMENT_XML_VERSION_PROPERTY))) {
                    throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                        "property-read-only", new Object [] {propertyId}));
                }
                }

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

        try {
            if (propertyId.startsWith(Constants.SAX_PROPERTY_PREFIX)) {
                final int suffixLength = propertyId.length() - Constants.SAX_PROPERTY_PREFIX.length();

                if (suffixLength == Constants.DOCUMENT_XML_VERSION_PROPERTY.length() &&
                    propertyId.endsWith(Constants.DOCUMENT_XML_VERSION_PROPERTY)) {
                    return fVersion;
                }

                if (suffixLength == Constants.LEXICAL_HANDLER_PROPERTY.length() &&
                    propertyId.endsWith(Constants.LEXICAL_HANDLER_PROPERTY)) {
                    return getLexicalHandler();
                }
                if (suffixLength == Constants.DECLARATION_HANDLER_PROPERTY.length() &&
                    propertyId.endsWith(Constants.DECLARATION_HANDLER_PROPERTY)) {
                    return getDeclHandler();
                }

                if (suffixLength == Constants.DOM_NODE_PROPERTY.length() &&
                    propertyId.endsWith(Constants.DOM_NODE_PROPERTY)) {
                    throw new SAXNotSupportedException(
                        SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                        "dom-node-read-not-supported", null));
                }

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

    } protected void setDeclHandler(DeclHandler handler)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        if (fParseInProgress) {
            throw new SAXNotSupportedException(
                SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                "property-not-parsing-supported",
                new Object [] {"http:}
        fDeclHandler = handler;

    } protected DeclHandler getDeclHandler()
        throws SAXNotRecognizedException, SAXNotSupportedException {
        return fDeclHandler;
    } protected void setLexicalHandler(LexicalHandler handler)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        if (fParseInProgress) {
            throw new SAXNotSupportedException(
                SAXMessageFormatter.formatMessage(fConfiguration.getLocale(),
                "property-not-parsing-supported",
                new Object [] {"http:}
        fLexicalHandler = handler;

    } protected LexicalHandler getLexicalHandler()
        throws SAXNotRecognizedException, SAXNotSupportedException {
        return fLexicalHandler;
    } protected final void startNamespaceMapping() throws SAXException{
        int count = fNamespaceContext.getDeclaredPrefixCount();
        if (count > 0) {
            String prefix = null;
            String uri = null;
            for (int i = 0; i < count; i++) {
                prefix = fNamespaceContext.getDeclaredPrefixAt(i);
                uri = fNamespaceContext.getURI(prefix);
                fContentHandler.startPrefixMapping(prefix,
                    (uri == null) ? "" : uri);
            }
        }
    }


    protected final void endNamespaceMapping() throws SAXException {
        int count = fNamespaceContext.getDeclaredPrefixCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                fContentHandler.endPrefixMapping(fNamespaceContext.getDeclaredPrefixAt(i));
            }
        }
    }

    public void reset() throws XNIException {
        super.reset();

        fInDTD = false;
        fVersion = "1.0";
        fStandalone = false;

        fNamespaces = fConfiguration.getFeature(NAMESPACES);
        fNamespacePrefixes = fConfiguration.getFeature(NAMESPACE_PREFIXES);
        fAugmentations = null;
        fDeclaredAttrs = null;

    } protected class LocatorProxy
        implements Locator2 {

        protected XMLLocator fLocator;

        public LocatorProxy(XMLLocator locator) {
            fLocator = locator;
        }

        public String getPublicId() {
            return fLocator.getPublicId();
        }


        public String getSystemId() {
            return fLocator.getExpandedSystemId();
        }

        public int getLineNumber() {
            return fLocator.getLineNumber();
        }


        public int getColumnNumber() {
            return fLocator.getColumnNumber();
        }

        public String getXMLVersion() {
            return fLocator.getXMLVersion();
        }

        public String getEncoding() {
            return fLocator.getEncoding();
        }

    } protected static final class AttributesProxy
        implements AttributeList, Attributes2 {

        protected XMLAttributes fAttributes;

        public void setAttributes(XMLAttributes attributes) {
            fAttributes = attributes;
        } public int getLength() {
            return fAttributes.getLength();
        }

        public String getName(int i) {
            return fAttributes.getQName(i);
        }

        public String getQName(int index) {
            return fAttributes.getQName(index);
        }

        public String getURI(int index) {
            String uri= fAttributes.getURI(index);
            return uri != null ? uri : "";
        }

        public String getLocalName(int index) {
            return fAttributes.getLocalName(index);
        }

        public String getType(int i) {
            return fAttributes.getType(i);
        }

        public String getType(String name) {
            return fAttributes.getType(name);
        }

        public String getType(String uri, String localName) {
            return uri.equals("") ? fAttributes.getType(null, localName) :
                                    fAttributes.getType(uri, localName);
        }

        public String getValue(int i) {
            return fAttributes.getValue(i);
        }

        public String getValue(String name) {
            return fAttributes.getValue(name);
        }

        public String getValue(String uri, String localName) {
            return uri.equals("") ? fAttributes.getValue(null, localName) :
                                    fAttributes.getValue(uri, localName);
        }

        public int getIndex(String qName) {
            return fAttributes.getIndex(qName);
        }

        public int getIndex(String uri, String localPart) {
            return uri.equals("") ? fAttributes.getIndex(null, localPart) :
                                    fAttributes.getIndex(uri, localPart);
        }

        public boolean isDeclared(int index) {
            if (index < 0 || index >= fAttributes.getLength()) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return Boolean.TRUE.equals(
                fAttributes.getAugmentations(index).getItem(
                Constants.ATTRIBUTE_DECLARED));
        }

        public boolean isDeclared(String qName) {
            int index = getIndex(qName);
            if (index == -1) {
                throw new IllegalArgumentException(qName);
            }
            return Boolean.TRUE.equals(
                fAttributes.getAugmentations(index).getItem(
                Constants.ATTRIBUTE_DECLARED));
        }

        public boolean isDeclared(String uri, String localName) {
            int index = getIndex(uri, localName);
            if (index == -1) {
                throw new IllegalArgumentException(localName);
            }
            return Boolean.TRUE.equals(
                fAttributes.getAugmentations(index).getItem(
                Constants.ATTRIBUTE_DECLARED));
        }

        public boolean isSpecified(int index) {
            if (index < 0 || index >= fAttributes.getLength()) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return fAttributes.isSpecified(index);
        }

        public boolean isSpecified(String qName) {
            int index = getIndex(qName);
            if (index == -1) {
                throw new IllegalArgumentException(qName);
            }
            return fAttributes.isSpecified(index);
        }

        public boolean isSpecified(String uri, String localName) {
            int index = getIndex(uri, localName);
            if (index == -1) {
                throw new IllegalArgumentException(localName);
            }
            return fAttributes.isSpecified(index);
        }

    } public ElementPSVI getElementPSVI(){
        return (fAugmentations != null)?(ElementPSVI)fAugmentations.getItem(Constants.ELEMENT_PSVI):null;
    }


    public AttributePSVI getAttributePSVI(int index){

        return (AttributePSVI)fAttributesProxy.fAttributes.getAugmentations(index).getItem(Constants.ATTRIBUTE_PSVI);
    }


    public AttributePSVI getAttributePSVIByName(String uri,
                                                String localname){
        return (AttributePSVI)fAttributesProxy.fAttributes.getAugmentations(uri, localname).getItem(Constants.ATTRIBUTE_PSVI);
    }

}