


package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;


public class XMLNamespaceBinder
    implements XMLComponent, XMLDocumentFilter {

    protected static final String NAMESPACES =
        Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE;

    protected static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    protected static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;

    private static final String[] RECOGNIZED_FEATURES = {
        NAMESPACES,
    };


    private static final Boolean[] FEATURE_DEFAULTS = {
        null,
    };


    private static final String[] RECOGNIZED_PROPERTIES = {
        SYMBOL_TABLE,
        ERROR_REPORTER,
    };


    private static final Object[] PROPERTY_DEFAULTS = {
        null,
        null,
    };

    protected boolean fNamespaces;

    protected SymbolTable fSymbolTable;


    protected XMLErrorReporter fErrorReporter;

    protected XMLDocumentHandler fDocumentHandler;

    protected XMLDocumentSource fDocumentSource;

    protected boolean fOnlyPassPrefixMappingEvents;

    private NamespaceContext fNamespaceContext;

    private QName fAttributeQName = new QName();

    public XMLNamespaceBinder() {
    } public void setOnlyPassPrefixMappingEvents(boolean onlyPassPrefixMappingEvents) {
        fOnlyPassPrefixMappingEvents = onlyPassPrefixMappingEvents;
    } public boolean getOnlyPassPrefixMappingEvents() {
        return fOnlyPassPrefixMappingEvents;
    } public void reset(XMLComponentManager componentManager)
        throws XNIException {

        fNamespaces = componentManager.getFeature(NAMESPACES, true);

        fSymbolTable = (SymbolTable)componentManager.getProperty(SYMBOL_TABLE);
        fErrorReporter = (XMLErrorReporter)componentManager.getProperty(ERROR_REPORTER);

    } public String[] getRecognizedFeatures() {
        return (String[])(RECOGNIZED_FEATURES.clone());
    } public void setFeature(String featureId, boolean state)
        throws XMLConfigurationException {
    } public String[] getRecognizedProperties() {
        return (String[])(RECOGNIZED_PROPERTIES.clone());
    } public void setProperty(String propertyId, Object value)
        throws XMLConfigurationException {

        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
                final int suffixLength = propertyId.length() - Constants.XERCES_PROPERTY_PREFIX.length();

            if (suffixLength == Constants.SYMBOL_TABLE_PROPERTY.length() &&
                propertyId.endsWith(Constants.SYMBOL_TABLE_PROPERTY)) {
                fSymbolTable = (SymbolTable)value;
            }
            else if (suffixLength == Constants.ERROR_REPORTER_PROPERTY.length() &&
                propertyId.endsWith(Constants.ERROR_REPORTER_PROPERTY)) {
                fErrorReporter = (XMLErrorReporter)value;
            }
            return;
        }

    } public Boolean getFeatureDefault(String featureId) {
        for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
            if (RECOGNIZED_FEATURES[i].equals(featureId)) {
                return FEATURE_DEFAULTS[i];
            }
        }
        return null;
    } public Object getPropertyDefault(String propertyId) {
        for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
            if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    } public void setDocumentHandler(XMLDocumentHandler documentHandler) {
        fDocumentHandler = documentHandler;
    } public XMLDocumentHandler getDocumentHandler() {
        return fDocumentHandler;
    } public void setDocumentSource(XMLDocumentSource source){
        fDocumentSource = source;
    } public XMLDocumentSource getDocumentSource (){
        return fDocumentSource;
    } public void startGeneralEntity(String name,
                                   XMLResourceIdentifier identifier,
                                   String encoding, Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            fDocumentHandler.startGeneralEntity(name, identifier, encoding, augs);
        }
    } public void textDecl(String version, String encoding, Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            fDocumentHandler.textDecl(version, encoding, augs);
        }
    } public void startDocument(XMLLocator locator, String encoding,
                                NamespaceContext namespaceContext, Augmentations augs)
                                      throws XNIException {
                fNamespaceContext = namespaceContext;

                if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
                        fDocumentHandler.startDocument(locator, encoding, namespaceContext, augs);
                }
        } public void xmlDecl(String version, String encoding, String standalone, Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
        }
    } public void doctypeDecl(String rootElement,
                            String publicId, String systemId, Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            fDocumentHandler.doctypeDecl(rootElement, publicId, systemId, augs);
        }
    } public void comment(XMLString text, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            fDocumentHandler.comment(text, augs);
        }
    } public void processingInstruction(String target, XMLString data, Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            fDocumentHandler.processingInstruction(target, data, augs);
        }
    } public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException {

        if (fNamespaces) {
            handleStartElement(element, attributes, augs, false);
        }
        else if (fDocumentHandler != null) {
            fDocumentHandler.startElement(element, attributes, augs);
        }


    } public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException {

        if (fNamespaces) {
            handleStartElement(element, attributes, augs, true);
            handleEndElement(element, augs, true);
        }
        else if (fDocumentHandler != null) {
            fDocumentHandler.emptyElement(element, attributes, augs);
        }

    } public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            fDocumentHandler.characters(text, augs);
        }
    } public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            fDocumentHandler.ignorableWhitespace(text, augs);
        }
    } public void endElement(QName element, Augmentations augs) throws XNIException {

        if (fNamespaces) {
            handleEndElement(element, augs, false);
        }
        else if (fDocumentHandler != null) {
            fDocumentHandler.endElement(element, augs);
        }

    } public void startCDATA(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            fDocumentHandler.startCDATA(augs);
        }
    } public void endCDATA(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            fDocumentHandler.endCDATA(augs);
        }
    } public void endDocument(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            fDocumentHandler.endDocument(augs);
        }
    } public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            fDocumentHandler.endGeneralEntity(name, augs);
        }
    } protected void handleStartElement(QName element, XMLAttributes attributes,
                                      Augmentations augs,
                                      boolean isEmpty) throws XNIException {

        fNamespaceContext.pushContext();

        if (element.prefix == XMLSymbols.PREFIX_XMLNS) {
            fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                       "ElementXMLNSPrefix",
                                       new Object[]{element.rawname},
                                       XMLErrorReporter.SEVERITY_FATAL_ERROR);
        }

        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            String localpart = attributes.getLocalName(i);
            String prefix = attributes.getPrefix(i);
            if (prefix == XMLSymbols.PREFIX_XMLNS ||
                prefix == XMLSymbols.EMPTY_STRING && localpart == XMLSymbols.PREFIX_XMLNS) {

                String uri = fSymbolTable.addSymbol(attributes.getValue(i));

                if (prefix == XMLSymbols.PREFIX_XMLNS && localpart == XMLSymbols.PREFIX_XMLNS) {
                    fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                               "CantBindXMLNS",
                                               new Object[]{attributes.getQName(i)},
                                               XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }

                if (uri == NamespaceContext.XMLNS_URI) {
                    fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                               "CantBindXMLNS",
                                               new Object[]{attributes.getQName(i)},
                                               XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }

                if (localpart == XMLSymbols.PREFIX_XML) {
                    if (uri != NamespaceContext.XML_URI) {
                        fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                                   "CantBindXML",
                                                   new Object[]{attributes.getQName(i)},
                                                   XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }
                else {
                    if (uri ==NamespaceContext.XML_URI) {
                        fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                                   "CantBindXML",
                                                   new Object[]{attributes.getQName(i)},
                                                   XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    }
                }

                prefix = localpart != XMLSymbols.PREFIX_XMLNS ? localpart : XMLSymbols.EMPTY_STRING;

                if(prefixBoundToNullURI(uri, localpart)) {
                    fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                               "EmptyPrefixedAttName",
                                               new Object[]{attributes.getQName(i)},
                                               XMLErrorReporter.SEVERITY_FATAL_ERROR);
                    continue;
                }

                fNamespaceContext.declarePrefix(prefix, uri.length() != 0 ? uri : null);

            }
        }

        String prefix = element.prefix != null
                      ? element.prefix : XMLSymbols.EMPTY_STRING;
        element.uri = fNamespaceContext.getURI(prefix);
        if (element.prefix == null && element.uri != null) {
            element.prefix = XMLSymbols.EMPTY_STRING;
        }
        if (element.prefix != null && element.uri == null) {
            fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                       "ElementPrefixUnbound",
                                       new Object[]{element.prefix, element.rawname},
                                       XMLErrorReporter.SEVERITY_FATAL_ERROR);
        }

        for (int i = 0; i < length; i++) {
            attributes.getName(i, fAttributeQName);
            String aprefix = fAttributeQName.prefix != null
                           ? fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
            String arawname = fAttributeQName.rawname;
            if (arawname == XMLSymbols.PREFIX_XMLNS) {
                fAttributeQName.uri = fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
                attributes.setName(i, fAttributeQName);
            }
            else if (aprefix != XMLSymbols.EMPTY_STRING) {
                fAttributeQName.uri = fNamespaceContext.getURI(aprefix);
                if (fAttributeQName.uri == null) {
                    fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                               "AttributePrefixUnbound",
                                               new Object[]{element.rawname,arawname,aprefix},
                                               XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }
                attributes.setName(i, fAttributeQName);
            }
        }

        int attrCount = attributes.getLength();
        for (int i = 0; i < attrCount - 1; i++) {
            String auri = attributes.getURI(i);
            if (auri == null || auri == NamespaceContext.XMLNS_URI) {
                continue;
            }
            String alocalpart = attributes.getLocalName(i);
            for (int j = i + 1; j < attrCount; j++) {
                String blocalpart = attributes.getLocalName(j);
                String buri = attributes.getURI(j);
                if (alocalpart == blocalpart && auri == buri) {
                    fErrorReporter.reportError(XMLMessageFormatter.XMLNS_DOMAIN,
                                               "AttributeNSNotUnique",
                                               new Object[]{element.rawname,alocalpart, auri},
                                               XMLErrorReporter.SEVERITY_FATAL_ERROR);
                }
            }
        }

        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            if (isEmpty) {
                fDocumentHandler.emptyElement(element, attributes, augs);
            }
            else {
                fDocumentHandler.startElement(element, attributes, augs);
            }
        }


    } protected void handleEndElement(QName element, Augmentations augs, boolean isEmpty)
        throws XNIException {

        String eprefix = element.prefix != null ? element.prefix : XMLSymbols.EMPTY_STRING;
        element.uri = fNamespaceContext.getURI(eprefix);
        if (element.uri != null) {
            element.prefix = eprefix;
        }

        if (fDocumentHandler != null && !fOnlyPassPrefixMappingEvents) {
            if (!isEmpty) {
                fDocumentHandler.endElement(element, augs);
            }
        }

        fNamespaceContext.popContext();

    } protected boolean prefixBoundToNullURI(String uri, String localpart) {
        return (uri == XMLSymbols.EMPTY_STRING && localpart != XMLSymbols.PREFIX_XMLNS);
    } }