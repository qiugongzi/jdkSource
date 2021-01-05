


package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dtd.models.ContentModelValidator;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
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
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;


public class XMLDTDValidator
        implements XMLComponent, XMLDocumentFilter, XMLDTDValidatorFilter, RevalidationHandler {

    private static final int TOP_LEVEL_SCOPE = -1;

    protected static final String NAMESPACES =
        Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE;


    protected static final String VALIDATION =
        Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;


    protected static final String DYNAMIC_VALIDATION =
        Constants.XERCES_FEATURE_PREFIX + Constants.DYNAMIC_VALIDATION_FEATURE;


    protected static final String BALANCE_SYNTAX_TREES =
        Constants.XERCES_FEATURE_PREFIX + Constants.BALANCE_SYNTAX_TREES;


    protected static final String WARN_ON_DUPLICATE_ATTDEF =
        Constants.XERCES_FEATURE_PREFIX + Constants.WARN_ON_DUPLICATE_ATTDEF_FEATURE;

        protected static final String PARSER_SETTINGS =
                Constants.XERCES_FEATURE_PREFIX + Constants.PARSER_SETTINGS;



    protected static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    protected static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    protected static final String GRAMMAR_POOL =
        Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;


    protected static final String DATATYPE_VALIDATOR_FACTORY =
        Constants.XERCES_PROPERTY_PREFIX + Constants.DATATYPE_VALIDATOR_FACTORY_PROPERTY;

    protected static final String VALIDATION_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATION_MANAGER_PROPERTY;

    private static final String[] RECOGNIZED_FEATURES = {
        NAMESPACES,
        VALIDATION,
        DYNAMIC_VALIDATION,
        BALANCE_SYNTAX_TREES
    };


    private static final Boolean[] FEATURE_DEFAULTS = {
        null,
        null,
        Boolean.FALSE,
        Boolean.FALSE,
    };


    private static final String[] RECOGNIZED_PROPERTIES = {
        SYMBOL_TABLE,
        ERROR_REPORTER,
        GRAMMAR_POOL,
        DATATYPE_VALIDATOR_FACTORY,
        VALIDATION_MANAGER
    };


    private static final Object[] PROPERTY_DEFAULTS = {
        null,
        null,
        null,
        null,
        null,
    };

    private static final boolean DEBUG_ATTRIBUTES = false;


    private static final boolean DEBUG_ELEMENT_CHILDREN = false;

    protected ValidationManager fValidationManager = null;

    protected final ValidationState fValidationState = new ValidationState();

    protected boolean fNamespaces;


    protected boolean fValidation;


    protected boolean fDTDValidation;


    protected boolean fDynamicValidation;


    protected boolean fBalanceSyntaxTrees;


    protected boolean fWarnDuplicateAttdef;

    protected SymbolTable fSymbolTable;


    protected XMLErrorReporter fErrorReporter;

    protected XMLGrammarPool fGrammarPool;


    protected DTDGrammarBucket fGrammarBucket;


    protected XMLLocator fDocLocation;


    protected NamespaceContext fNamespaceContext = null;


    protected DTDDVFactory fDatatypeValidatorFactory;

    protected XMLDocumentHandler fDocumentHandler;

    protected XMLDocumentSource fDocumentSource;
    protected DTDGrammar fDTDGrammar;

    protected boolean fSeenDoctypeDecl = false;


    private boolean fPerformValidation;


    private String fSchemaType;

    private final QName fCurrentElement = new QName();


    private int fCurrentElementIndex = -1;


    private int fCurrentContentSpecType = -1;


    private final QName fRootElement = new QName();

    private boolean fInCDATASection = false;
    private int[] fElementIndexStack = new int[8];


    private int[] fContentSpecTypeStack = new int[8];


    private QName[] fElementQNamePartsStack = new QName[8];

    private QName[] fElementChildren = new QName[32];


    private int fElementChildrenLength = 0;


    private int[] fElementChildrenOffsetStack = new int[32];


    private int fElementDepth = -1;

    private boolean fSeenRootElement = false;


    private boolean fInElementContent = false;

    private XMLElementDecl fTempElementDecl = new XMLElementDecl();


    private final XMLAttributeDecl fTempAttDecl = new XMLAttributeDecl();


    private final XMLEntityDecl fEntityDecl = new XMLEntityDecl();


    private final QName fTempQName = new QName();


    private final StringBuffer fBuffer = new StringBuffer();

    protected DatatypeValidator fValID;


    protected DatatypeValidator fValIDRef;


    protected DatatypeValidator fValIDRefs;


    protected DatatypeValidator fValENTITY;


    protected DatatypeValidator fValENTITIES;


    protected DatatypeValidator fValNMTOKEN;


    protected DatatypeValidator fValNMTOKENS;


    protected DatatypeValidator fValNOTATION;

    public XMLDTDValidator() {

        for (int i = 0; i < fElementQNamePartsStack.length; i++) {
            fElementQNamePartsStack[i] = new QName();
        }
        fGrammarBucket = new DTDGrammarBucket();

    } DTDGrammarBucket getGrammarBucket() {
        return fGrammarBucket;
    } public void reset(XMLComponentManager componentManager)
    throws XMLConfigurationException {

        fDTDGrammar = null;
        fSeenDoctypeDecl = false;
        fInCDATASection = false;
        fSeenRootElement = false;
        fInElementContent = false;
        fCurrentElementIndex = -1;
        fCurrentContentSpecType = -1;

        fRootElement.clear();

                fValidationState.resetIDTables();

                fGrammarBucket.clear();
                fElementDepth = -1;
                fElementChildrenLength = 0;

        boolean parser_settings = componentManager.getFeature(PARSER_SETTINGS, true);

        if (!parser_settings){
                fValidationManager.addValidationState(fValidationState);
                return;
        }

        fNamespaces = componentManager.getFeature(NAMESPACES, true);
        fValidation = componentManager.getFeature(VALIDATION, false);
        fDTDValidation = !(componentManager.getFeature(Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE, false));

        fDynamicValidation = componentManager.getFeature(DYNAMIC_VALIDATION, false);
        fBalanceSyntaxTrees = componentManager.getFeature(BALANCE_SYNTAX_TREES, false);
        fWarnDuplicateAttdef = componentManager.getFeature(WARN_ON_DUPLICATE_ATTDEF, false);

        fSchemaType = (String)componentManager.getProperty (Constants.JAXP_PROPERTY_PREFIX
            + Constants.SCHEMA_LANGUAGE, null);

        fValidationManager= (ValidationManager)componentManager.getProperty(VALIDATION_MANAGER);
        fValidationManager.addValidationState(fValidationState);
        fValidationState.setUsingNamespaces(fNamespaces);

        fErrorReporter = (XMLErrorReporter)componentManager.getProperty(Constants.XERCES_PROPERTY_PREFIX+Constants.ERROR_REPORTER_PROPERTY);
        fSymbolTable = (SymbolTable)componentManager.getProperty(Constants.XERCES_PROPERTY_PREFIX+Constants.SYMBOL_TABLE_PROPERTY);
        fGrammarPool= (XMLGrammarPool)componentManager.getProperty(GRAMMAR_POOL, null);

        fDatatypeValidatorFactory = (DTDDVFactory)componentManager.getProperty(Constants.XERCES_PROPERTY_PREFIX + Constants.DATATYPE_VALIDATOR_FACTORY_PROPERTY);
                init();

    } public String[] getRecognizedFeatures() {
        return (String[])(RECOGNIZED_FEATURES.clone());
    } public void setFeature(String featureId, boolean state)
    throws XMLConfigurationException {
    } public String[] getRecognizedProperties() {
        return (String[])(RECOGNIZED_PROPERTIES.clone());
    } public void setProperty(String propertyId, Object value)
    throws XMLConfigurationException {
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
    } public void startDocument(XMLLocator locator, String encoding,
                              NamespaceContext namespaceContext, Augmentations augs)
    throws XNIException {

        if (fGrammarPool != null) {
            Grammar [] grammars = fGrammarPool.retrieveInitialGrammarSet(XMLGrammarDescription.XML_DTD);
            final int length = (grammars != null) ? grammars.length : 0;
            for (int i = 0; i < length; ++i) {
                fGrammarBucket.putGrammar((DTDGrammar)grammars[i]);
            }
        }
        fDocLocation = locator;
        fNamespaceContext = namespaceContext;

        if (fDocumentHandler != null) {
            fDocumentHandler.startDocument(locator, encoding, namespaceContext, augs);
        }

    } public void xmlDecl(String version, String encoding, String standalone, Augmentations augs)
    throws XNIException {

        fGrammarBucket.setStandalone(standalone != null && standalone.equals("yes"));

        if (fDocumentHandler != null) {
            fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
        }

    } public void doctypeDecl(String rootElement, String publicId, String systemId,
                            Augmentations augs)
    throws XNIException {

        fSeenDoctypeDecl = true;
        fRootElement.setValues(null, rootElement, rootElement, null);
        String eid = null;
        try {
            eid = XMLEntityManager.expandSystemId(systemId, fDocLocation.getExpandedSystemId(), false);
        } catch (java.io.IOException e) {
        }
        XMLDTDDescription grammarDesc = new XMLDTDDescription(publicId, systemId, fDocLocation.getExpandedSystemId(), eid, rootElement);
        fDTDGrammar = fGrammarBucket.getGrammar(grammarDesc);
        if(fDTDGrammar == null) {
            if (fGrammarPool != null && (systemId != null || publicId != null)) {
                fDTDGrammar = (DTDGrammar)fGrammarPool.retrieveGrammar(grammarDesc);
            }
        }
        if(fDTDGrammar == null) {
            if (!fBalanceSyntaxTrees) {
                fDTDGrammar = new DTDGrammar(fSymbolTable, grammarDesc);
            }
            else {
                fDTDGrammar = new BalancedDTDGrammar(fSymbolTable, grammarDesc);
            }
        } else {
            fValidationManager.setCachedDTD(true);
        }
        fGrammarBucket.setActiveGrammar(fDTDGrammar);

        if (fDocumentHandler != null) {
            fDocumentHandler.doctypeDecl(rootElement, publicId, systemId, augs);
        }

    } public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
    throws XNIException {

        handleStartElement(element, attributes, augs);
        if (fDocumentHandler != null) {
            fDocumentHandler.startElement(element, attributes, augs);

        }

    } public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
    throws XNIException {

        boolean removed = handleStartElement(element, attributes, augs);

        if (fDocumentHandler !=null) {
            fDocumentHandler.emptyElement(element, attributes, augs);
        }
        if (!removed) {
            handleEndElement(element, augs, true);
        }


    } public void characters(XMLString text, Augmentations augs) throws XNIException {

        boolean callNextCharacters = true;

        boolean allWhiteSpace = true;
        for (int i=text.offset; i< text.offset+text.length; i++) {
            if (!isSpace(text.ch[i])) {
                allWhiteSpace = false;
                break;
            }
        }
        if (fInElementContent && allWhiteSpace && !fInCDATASection) {
            if (fDocumentHandler != null) {
                fDocumentHandler.ignorableWhitespace(text, augs);
                callNextCharacters = false;
            }
        }

        if (fPerformValidation) {
            if (fInElementContent) {
                if (fGrammarBucket.getStandalone() &&
                    fDTDGrammar.getElementDeclIsExternal(fCurrentElementIndex)) {
                    if (allWhiteSpace) {
                        fErrorReporter.reportError( XMLMessageFormatter.XML_DOMAIN,
                                                    "MSG_WHITE_SPACE_IN_ELEMENT_CONTENT_WHEN_STANDALONE",
                                                    null, XMLErrorReporter.SEVERITY_ERROR);
                    }
                }
                if (!allWhiteSpace) {
                    charDataInContent();
                }

                if (augs != null && augs.getItem(Constants.CHAR_REF_PROBABLE_WS) == Boolean.TRUE) {
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                               "MSG_CONTENT_INVALID_SPECIFIED",
                                               new Object[]{ fCurrentElement.rawname,
                                                   fDTDGrammar.getContentSpecAsString(fElementDepth),
                                                   "character reference"},
                                               XMLErrorReporter.SEVERITY_ERROR);
                }
            }

            if (fCurrentContentSpecType == XMLElementDecl.TYPE_EMPTY) {
                charDataInContent();
            }
        }

        if (callNextCharacters && fDocumentHandler != null) {
            fDocumentHandler.characters(text, augs);
        }

    } public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {

        if (fDocumentHandler != null) {
            fDocumentHandler.ignorableWhitespace(text, augs);
        }

    } public void endElement(QName element, Augmentations augs) throws XNIException {

        handleEndElement(element,  augs, false);

    } public void startCDATA(Augmentations augs) throws XNIException {

        if (fPerformValidation && fInElementContent) {
            charDataInContent();
        }
        fInCDATASection = true;
        if (fDocumentHandler != null) {
            fDocumentHandler.startCDATA(augs);
        }

    } public void endCDATA(Augmentations augs) throws XNIException {

        fInCDATASection = false;
        if (fDocumentHandler != null) {
            fDocumentHandler.endCDATA(augs);
        }

    } public void endDocument(Augmentations augs) throws XNIException {

        if (fDocumentHandler != null) {
            fDocumentHandler.endDocument(augs);
        }

    } public void comment(XMLString text, Augmentations augs) throws XNIException {
        if (fPerformValidation && fElementDepth >= 0 && fDTDGrammar != null) {
            fDTDGrammar.getElementDecl(fCurrentElementIndex, fTempElementDecl);
            if (fTempElementDecl.type == XMLElementDecl.TYPE_EMPTY) {
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                               "MSG_CONTENT_INVALID_SPECIFIED",
                                               new Object[]{ fCurrentElement.rawname,
                                                             "EMPTY",
                                                             "comment"},
                                               XMLErrorReporter.SEVERITY_ERROR);
            }
        }
        if (fDocumentHandler != null) {
            fDocumentHandler.comment(text, augs);
        }

    } public void processingInstruction(String target, XMLString data, Augmentations augs)
    throws XNIException {

        if (fPerformValidation && fElementDepth >= 0 && fDTDGrammar != null) {
            fDTDGrammar.getElementDecl(fCurrentElementIndex, fTempElementDecl);
            if (fTempElementDecl.type == XMLElementDecl.TYPE_EMPTY) {
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                               "MSG_CONTENT_INVALID_SPECIFIED",
                                               new Object[]{ fCurrentElement.rawname,
                                                             "EMPTY",
                                                             "processing instruction"},
                                               XMLErrorReporter.SEVERITY_ERROR);
            }
        }
        if (fDocumentHandler != null) {
            fDocumentHandler.processingInstruction(target, data, augs);
        }
    } public void startGeneralEntity(String name,
                                   XMLResourceIdentifier identifier,
                                   String encoding,
                                   Augmentations augs) throws XNIException {
        if (fPerformValidation && fElementDepth >= 0 && fDTDGrammar != null) {
            fDTDGrammar.getElementDecl(fCurrentElementIndex, fTempElementDecl);
            if (fTempElementDecl.type == XMLElementDecl.TYPE_EMPTY) {
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                           "MSG_CONTENT_INVALID_SPECIFIED",
                                           new Object[]{ fCurrentElement.rawname,
                                                         "EMPTY", "ENTITY"},
                                           XMLErrorReporter.SEVERITY_ERROR);
            }
            if (fGrammarBucket.getStandalone()) {
                XMLDTDLoader.checkStandaloneEntityRef(name, fDTDGrammar, fEntityDecl, fErrorReporter);
            }
        }
        if (fDocumentHandler != null) {
            fDocumentHandler.startGeneralEntity(name, identifier, encoding, augs);
        }
    }


    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endGeneralEntity(name, augs);
        }
    } public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {

        if (fDocumentHandler != null) {
            fDocumentHandler.textDecl(version, encoding, augs);
        }
    }


    public final boolean hasGrammar(){

        return (fDTDGrammar != null);
    }

    public final boolean validate(){
        return (fSchemaType != Constants.NS_XMLSCHEMA) &&
               (!fDynamicValidation && fValidation ||
                fDynamicValidation && fSeenDoctypeDecl) &&
               (fDTDValidation || fSeenDoctypeDecl);
    }

            protected void addDTDDefaultAttrsAndValidate(QName elementName, int elementIndex,
                                               XMLAttributes attributes)
    throws XNIException {

        if (elementIndex == -1 || fDTDGrammar == null) {
            return;
        }

        int attlistIndex = fDTDGrammar.getFirstAttributeDeclIndex(elementIndex);

        while (attlistIndex != -1) {

            fDTDGrammar.getAttributeDecl(attlistIndex, fTempAttDecl);

            if (DEBUG_ATTRIBUTES) {
                if (fTempAttDecl != null) {
                    XMLElementDecl elementDecl = new XMLElementDecl();
                    fDTDGrammar.getElementDecl(elementIndex, elementDecl);
                    System.out.println("element: "+(elementDecl.name.localpart));
                    System.out.println("attlistIndex " + attlistIndex + "\n"+
                                       "attName : '"+(fTempAttDecl.name.localpart) + "'\n"
                                       + "attType : "+fTempAttDecl.simpleType.type + "\n"
                                       + "attDefaultType : "+fTempAttDecl.simpleType.defaultType + "\n"
                                       + "attDefaultValue : '"+fTempAttDecl.simpleType.defaultValue + "'\n"
                                       + attributes.getLength() +"\n"
                                      );
                }
            }
            String attPrefix = fTempAttDecl.name.prefix;
            String attLocalpart = fTempAttDecl.name.localpart;
            String attRawName = fTempAttDecl.name.rawname;
            String attType = getAttributeTypeName(fTempAttDecl);
            int attDefaultType =fTempAttDecl.simpleType.defaultType;
            String attValue = null;

            if (fTempAttDecl.simpleType.defaultValue != null) {
                attValue = fTempAttDecl.simpleType.defaultValue;
            }

            boolean specified = false;
            boolean required = attDefaultType == XMLSimpleType.DEFAULT_TYPE_REQUIRED;
            boolean cdata = attType == XMLSymbols.fCDATASymbol;

            if (!cdata || required || attValue != null) {
                int attrCount = attributes.getLength();
                for (int i = 0; i < attrCount; i++) {
                    if (attributes.getQName(i) == attRawName) {
                        specified = true;
                        break;
                    }
                }
            }

            if (!specified) {
                if (required) {
                    if (fPerformValidation) {
                        Object[] args = {elementName.localpart, attRawName};
                        fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                   "MSG_REQUIRED_ATTRIBUTE_NOT_SPECIFIED", args,
                                                   XMLErrorReporter.SEVERITY_ERROR);
                    }
                }
                else if (attValue != null) {
                    if (fPerformValidation && fGrammarBucket.getStandalone()) {
                        if (fDTDGrammar.getAttributeDeclIsExternal(attlistIndex)) {

                            Object[] args = { elementName.localpart, attRawName};
                            fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                       "MSG_DEFAULTED_ATTRIBUTE_NOT_SPECIFIED", args,
                                                       XMLErrorReporter.SEVERITY_ERROR);
                        }
                    }

                    if (fNamespaces) {
                        int index = attRawName.indexOf(':');
                        if (index != -1) {
                            attPrefix = attRawName.substring(0, index);
                            attPrefix = fSymbolTable.addSymbol(attPrefix);
                            attLocalpart = attRawName.substring(index + 1);
                            attLocalpart = fSymbolTable.addSymbol(attLocalpart);
                        }
                    }

                    fTempQName.setValues(attPrefix, attLocalpart, attRawName, fTempAttDecl.name.uri);
                    int newAttr = attributes.addAttribute(fTempQName, attType, attValue);
                }
            }
            attlistIndex = fDTDGrammar.getNextAttributeDeclIndex(attlistIndex);
        }

        int attrCount = attributes.getLength();
        for (int i = 0; i < attrCount; i++) {
            String attrRawName = attributes.getQName(i);
            boolean declared = false;
            if (fPerformValidation) {
                if (fGrammarBucket.getStandalone()) {
                    String nonNormalizedValue = attributes.getNonNormalizedValue(i);
                    if (nonNormalizedValue != null) {
                        String entityName = getExternalEntityRefInAttrValue(nonNormalizedValue);
                        if (entityName != null) {
                            fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                       "MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE",
                                                       new Object[]{entityName},
                                                       XMLErrorReporter.SEVERITY_ERROR);
                        }
                    }
                }
            }
            int attDefIndex = -1;
            int position =
            fDTDGrammar.getFirstAttributeDeclIndex(elementIndex);
            while (position != -1) {
                fDTDGrammar.getAttributeDecl(position, fTempAttDecl);
                if (fTempAttDecl.name.rawname == attrRawName) {
                    attDefIndex = position;
                    declared = true;
                    break;
                }
                position = fDTDGrammar.getNextAttributeDeclIndex(position);
            }
            if (!declared) {
                if (fPerformValidation) {
                    Object[] args = { elementName.rawname, attrRawName};

                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                               "MSG_ATTRIBUTE_NOT_DECLARED",
                                               args,XMLErrorReporter.SEVERITY_ERROR);
                }
                continue;
            }
            String type = getAttributeTypeName(fTempAttDecl);
            attributes.setType(i, type);
            attributes.getAugmentations(i).putItem(Constants.ATTRIBUTE_DECLARED, Boolean.TRUE);

            boolean changedByNormalization = false;
            String oldValue = attributes.getValue(i);
            String attrValue = oldValue;
            if (attributes.isSpecified(i) && type != XMLSymbols.fCDATASymbol) {
                changedByNormalization = normalizeAttrValue(attributes, i);
                attrValue = attributes.getValue(i);
                if (fPerformValidation && fGrammarBucket.getStandalone()
                    && changedByNormalization
                    && fDTDGrammar.getAttributeDeclIsExternal(position)
                   ) {
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                               "MSG_ATTVALUE_CHANGED_DURING_NORMALIZATION_WHEN_STANDALONE",
                                               new Object[]{attrRawName, oldValue, attrValue},
                                               XMLErrorReporter.SEVERITY_ERROR);
                }
            }
            if (!fPerformValidation) {
                continue;
            }
            if (fTempAttDecl.simpleType.defaultType ==
                XMLSimpleType.DEFAULT_TYPE_FIXED) {
                String defaultValue = fTempAttDecl.simpleType.defaultValue;

                if (!attrValue.equals(defaultValue)) {
                    Object[] args = {elementName.localpart,
                        attrRawName,
                        attrValue,
                        defaultValue};
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                               "MSG_FIXED_ATTVALUE_INVALID",
                                               args, XMLErrorReporter.SEVERITY_ERROR);
                }
            }

            if (fTempAttDecl.simpleType.type == XMLSimpleType.TYPE_ENTITY ||
                fTempAttDecl.simpleType.type == XMLSimpleType.TYPE_ENUMERATION ||
                fTempAttDecl.simpleType.type == XMLSimpleType.TYPE_ID ||
                fTempAttDecl.simpleType.type == XMLSimpleType.TYPE_IDREF ||
                fTempAttDecl.simpleType.type == XMLSimpleType.TYPE_NMTOKEN ||
                fTempAttDecl.simpleType.type == XMLSimpleType.TYPE_NOTATION
               ) {
                validateDTDattribute(elementName, attrValue, fTempAttDecl);
            }
        } } protected String getExternalEntityRefInAttrValue(String nonNormalizedValue) {
        int valLength = nonNormalizedValue.length();
        int ampIndex = nonNormalizedValue.indexOf('&');
        while (ampIndex != -1) {
            if (ampIndex + 1 < valLength &&
                nonNormalizedValue.charAt(ampIndex+1) != '#') {
                int semicolonIndex = nonNormalizedValue.indexOf(';', ampIndex+1);
                String entityName = nonNormalizedValue.substring(ampIndex+1, semicolonIndex);
                entityName = fSymbolTable.addSymbol(entityName);
                int entIndex = fDTDGrammar.getEntityDeclIndex(entityName);
                if (entIndex > -1) {
                    fDTDGrammar.getEntityDecl(entIndex, fEntityDecl);
                    if (fEntityDecl.inExternal ||
                        (entityName = getExternalEntityRefInAttrValue(fEntityDecl.value)) != null) {
                        return entityName;
                    }
                }
            }
            ampIndex = nonNormalizedValue.indexOf('&', ampIndex+1);
        }
        return null;
    } protected void validateDTDattribute(QName element, String attValue,
                                      XMLAttributeDecl attributeDecl)
    throws XNIException {

        switch (attributeDecl.simpleType.type) {
        case XMLSimpleType.TYPE_ENTITY: {
                boolean isAlistAttribute = attributeDecl.simpleType.list;

                try {
                    if (isAlistAttribute) {
                        fValENTITIES.validate(attValue, fValidationState);
                    }
                    else {
                        fValENTITY.validate(attValue, fValidationState);
                    }
                }
                catch (InvalidDatatypeValueException ex) {
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                               ex.getKey(),
                                               ex.getArgs(),
                                               XMLErrorReporter.SEVERITY_ERROR );

                }
                break;
            }

        case XMLSimpleType.TYPE_NOTATION:
        case XMLSimpleType.TYPE_ENUMERATION: {
                boolean found = false;
                String [] enumVals = attributeDecl.simpleType.enumeration;
                if (enumVals == null) {
                    found = false;
                }
                else
                    for (int i = 0; i < enumVals.length; i++) {
                        if (attValue == enumVals[i] || attValue.equals(enumVals[i])) {
                            found = true;
                            break;
                        }
                    }

                if (!found) {
                    StringBuffer enumValueString = new StringBuffer();
                    if (enumVals != null)
                        for (int i = 0; i < enumVals.length; i++) {
                            enumValueString.append(enumVals[i]+" ");
                        }
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                               "MSG_ATTRIBUTE_VALUE_NOT_IN_LIST",
                                               new Object[]{attributeDecl.name.rawname, attValue, enumValueString},
                                               XMLErrorReporter.SEVERITY_ERROR);
                }
                break;
            }

        case XMLSimpleType.TYPE_ID: {
                try {
                    fValID.validate(attValue, fValidationState);
                }
                catch (InvalidDatatypeValueException ex) {
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                               ex.getKey(),
                                               ex.getArgs(),
                                               XMLErrorReporter.SEVERITY_ERROR );
                }
                break;
            }

        case XMLSimpleType.TYPE_IDREF: {
                boolean isAlistAttribute = attributeDecl.simpleType.list;try {
                    if (isAlistAttribute) {
                        fValIDRefs.validate(attValue, fValidationState);
                    }
                    else {
                        fValIDRef.validate(attValue, fValidationState);
                    }
                }
                catch (InvalidDatatypeValueException ex) {
                    if (isAlistAttribute) {
                        fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                   "IDREFSInvalid",
                                                   new Object[]{attValue},
                                                   XMLErrorReporter.SEVERITY_ERROR );
                    }
                    else {
                        fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                   ex.getKey(),
                                                   ex.getArgs(),
                                                   XMLErrorReporter.SEVERITY_ERROR );
                    }

                }
                break;
            }

        case XMLSimpleType.TYPE_NMTOKEN: {
                boolean isAlistAttribute = attributeDecl.simpleType.list;try {
                    if (isAlistAttribute) {
                        fValNMTOKENS.validate(attValue, fValidationState);
                    }
                    else {
                        fValNMTOKEN.validate(attValue, fValidationState);
                    }
                }
                catch (InvalidDatatypeValueException ex) {
                    if (isAlistAttribute) {
                        fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                   "NMTOKENSInvalid",
                                                   new Object[] { attValue},
                                                   XMLErrorReporter.SEVERITY_ERROR);
                    }
                    else {
                        fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                   "NMTOKENInvalid",
                                                   new Object[] { attValue},
                                                   XMLErrorReporter.SEVERITY_ERROR);
                    }
                }
                break;
            }

        } } protected boolean invalidStandaloneAttDef(QName element, QName attribute) {
        boolean state = true;

        return state;
    }


    private boolean normalizeAttrValue(XMLAttributes attributes, int index) {
        boolean leadingSpace = true;
        boolean spaceStart = false;
        boolean readingNonSpace = false;
        int count = 0;
        int eaten = 0;
        String attrValue = attributes.getValue(index);
        char[] attValue = new char[attrValue.length()];

        fBuffer.setLength(0);
        attrValue.getChars(0, attrValue.length(), attValue, 0);
        for (int i = 0; i < attValue.length; i++) {

            if (attValue[i] == ' ') {

                if (readingNonSpace) {
                    spaceStart = true;
                    readingNonSpace = false;
                }

                if (spaceStart && !leadingSpace) {
                    spaceStart = false;
                    fBuffer.append(attValue[i]);
                    count++;
                }
                else {
                    if (leadingSpace || !spaceStart) {
                        eaten ++;

                    }
                }

            }
            else {
                readingNonSpace = true;
                spaceStart = false;
                leadingSpace = false;
                fBuffer.append(attValue[i]);
                count++;
            }
        }

        if (count > 0 && fBuffer.charAt(count-1) == ' ') {
            fBuffer.setLength(count-1);

        }
        String newValue = fBuffer.toString();
        attributes.setValue(index, newValue);
        return ! attrValue.equals(newValue);
    }


    private final void rootElementSpecified(QName rootElement) throws XNIException {
        if (fPerformValidation) {
            String root1 = fRootElement.rawname;
            String root2 = rootElement.rawname;
            if (root1 == null || !root1.equals(root2)) {
                fErrorReporter.reportError( XMLMessageFormatter.XML_DOMAIN,
                                            "RootElementTypeMustMatchDoctypedecl",
                                            new Object[]{root1, root2},
                                            XMLErrorReporter.SEVERITY_ERROR);
            }
        }
    } private int checkContent(int elementIndex,
                             QName[] children,
                             int childOffset,
                             int childCount) throws XNIException {

        fDTDGrammar.getElementDecl(elementIndex, fTempElementDecl);

        final String elementType = fCurrentElement.rawname;

        final int contentType = fCurrentContentSpecType;


        if (contentType == XMLElementDecl.TYPE_EMPTY) {
            if (childCount != 0) {
                return 0;
            }
        }
        else if (contentType == XMLElementDecl.TYPE_ANY) {
            }
        else if (contentType == XMLElementDecl.TYPE_MIXED ||
                 contentType == XMLElementDecl.TYPE_CHILDREN) {
            ContentModelValidator cmElem = null;
            cmElem = fTempElementDecl.contentModelValidator;
            int result = cmElem.validate(children, childOffset, childCount);
            return result;
        }
        else if (contentType == -1) {
            }
        else if (contentType == XMLElementDecl.TYPE_SIMPLE) {

            }
        else {
            }

        return -1;

    } private int getContentSpecType(int elementIndex) {

        int contentSpecType = -1;
        if (elementIndex > -1) {
            if (fDTDGrammar.getElementDecl(elementIndex,fTempElementDecl)) {
                contentSpecType = fTempElementDecl.type;
            }
        }
        return contentSpecType;
    }


    private void charDataInContent() {

        if (DEBUG_ELEMENT_CHILDREN) {
            System.out.println("charDataInContent()");
        }
        if (fElementChildren.length <= fElementChildrenLength) {
            QName[] newarray = new QName[fElementChildren.length * 2];
            System.arraycopy(fElementChildren, 0, newarray, 0, fElementChildren.length);
            fElementChildren = newarray;
        }
        QName qname = fElementChildren[fElementChildrenLength];
        if (qname == null) {
            for (int i = fElementChildrenLength; i < fElementChildren.length; i++) {
                fElementChildren[i] = new QName();
            }
            qname = fElementChildren[fElementChildrenLength];
        }
        qname.clear();
        fElementChildrenLength++;

    } private String getAttributeTypeName(XMLAttributeDecl attrDecl) {

        switch (attrDecl.simpleType.type) {
        case XMLSimpleType.TYPE_ENTITY: {
                return attrDecl.simpleType.list ? XMLSymbols.fENTITIESSymbol : XMLSymbols.fENTITYSymbol;
            }
        case XMLSimpleType.TYPE_ENUMERATION: {
                StringBuffer buffer = new StringBuffer();
                buffer.append('(');
                for (int i=0; i<attrDecl.simpleType.enumeration.length ; i++) {
                    if (i > 0) {
                        buffer.append('|');
                    }
                    buffer.append(attrDecl.simpleType.enumeration[i]);
                }
                buffer.append(')');
                return fSymbolTable.addSymbol(buffer.toString());
            }
        case XMLSimpleType.TYPE_ID: {
                return XMLSymbols.fIDSymbol;
            }
        case XMLSimpleType.TYPE_IDREF: {
                return attrDecl.simpleType.list ? XMLSymbols.fIDREFSSymbol : XMLSymbols.fIDREFSymbol;
            }
        case XMLSimpleType.TYPE_NMTOKEN: {
                return attrDecl.simpleType.list ? XMLSymbols.fNMTOKENSSymbol : XMLSymbols.fNMTOKENSymbol;
            }
        case XMLSimpleType.TYPE_NOTATION: {
                return XMLSymbols.fNOTATIONSymbol;
            }
        }
        return XMLSymbols.fCDATASymbol;

    } protected void init() {

        if (fValidation || fDynamicValidation) {
            try {
                fValID       = fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDSymbol);
                fValIDRef    = fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDREFSymbol);
                fValIDRefs   = fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDREFSSymbol);
                fValENTITY   = fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fENTITYSymbol);
                fValENTITIES = fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fENTITIESSymbol);
                fValNMTOKEN  = fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNMTOKENSymbol);
                fValNMTOKENS = fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNMTOKENSSymbol);
                fValNOTATION = fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNOTATIONSymbol);

            }
            catch (Exception e) {
                e.printStackTrace(System.err);
            }

        }

    } private void ensureStackCapacity (int newElementDepth) {
        if (newElementDepth == fElementQNamePartsStack.length) {

            QName[] newStackOfQueue = new QName[newElementDepth * 2];
            System.arraycopy(this.fElementQNamePartsStack, 0, newStackOfQueue, 0, newElementDepth );
            fElementQNamePartsStack = newStackOfQueue;

            QName qname = fElementQNamePartsStack[newElementDepth];
            if (qname == null) {
                for (int i = newElementDepth; i < fElementQNamePartsStack.length; i++) {
                    fElementQNamePartsStack[i] = new QName();
                }
            }

            int[] newStack = new int[newElementDepth * 2];
            System.arraycopy(fElementIndexStack, 0, newStack, 0, newElementDepth);
            fElementIndexStack = newStack;

            newStack = new int[newElementDepth * 2];
            System.arraycopy(fContentSpecTypeStack, 0, newStack, 0, newElementDepth);
            fContentSpecTypeStack = newStack;

        }
    } protected boolean handleStartElement(QName element, XMLAttributes attributes, Augmentations augs)
                        throws XNIException {


        if (!fSeenRootElement) {
            fPerformValidation = validate();
            fSeenRootElement = true;
            fValidationManager.setEntityState(fDTDGrammar);
            fValidationManager.setGrammarFound(fSeenDoctypeDecl);
            rootElementSpecified(element);
        }
        if (fDTDGrammar == null) {

            if (!fPerformValidation) {
                fCurrentElementIndex = -1;
                fCurrentContentSpecType = -1;
                fInElementContent = false;
            }
            if (fPerformValidation) {
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                           "MSG_GRAMMAR_NOT_FOUND",
                                           new Object[]{ element.rawname},
                                           XMLErrorReporter.SEVERITY_ERROR);
            }
            if (fDocumentSource !=null ) {
                fDocumentSource.setDocumentHandler(fDocumentHandler);
                if (fDocumentHandler != null)
                    fDocumentHandler.setDocumentSource(fDocumentSource);
                return true;
            }
        }
        else {
            fCurrentElementIndex = fDTDGrammar.getElementDeclIndex(element);
            fCurrentContentSpecType = fDTDGrammar.getContentSpecType(fCurrentElementIndex);
            if (fCurrentContentSpecType == -1 && fPerformValidation) {
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                           "MSG_ELEMENT_NOT_DECLARED",
                                           new Object[]{ element.rawname},
                                           XMLErrorReporter.SEVERITY_ERROR);
            }

            addDTDDefaultAttrsAndValidate(element, fCurrentElementIndex, attributes);
        }

        fInElementContent = fCurrentContentSpecType == XMLElementDecl.TYPE_CHILDREN;

        fElementDepth++;
        if (fPerformValidation) {
            if (fElementChildrenOffsetStack.length <= fElementDepth) {
                int newarray[] = new int[fElementChildrenOffsetStack.length * 2];
                System.arraycopy(fElementChildrenOffsetStack, 0, newarray, 0, fElementChildrenOffsetStack.length);
                fElementChildrenOffsetStack = newarray;
            }
            fElementChildrenOffsetStack[fElementDepth] = fElementChildrenLength;

            if (fElementChildren.length <= fElementChildrenLength) {
                QName[] newarray = new QName[fElementChildrenLength * 2];
                System.arraycopy(fElementChildren, 0, newarray, 0, fElementChildren.length);
                fElementChildren = newarray;
            }
            QName qname = fElementChildren[fElementChildrenLength];
            if (qname == null) {
                for (int i = fElementChildrenLength; i < fElementChildren.length; i++) {
                    fElementChildren[i] = new QName();
                }
                qname = fElementChildren[fElementChildrenLength];
            }
            qname.setValues(element);
            fElementChildrenLength++;
        }

        fCurrentElement.setValues(element);
        ensureStackCapacity(fElementDepth);
        fElementQNamePartsStack[fElementDepth].setValues(fCurrentElement);
        fElementIndexStack[fElementDepth] = fCurrentElementIndex;
        fContentSpecTypeStack[fElementDepth] = fCurrentContentSpecType;
        startNamespaceScope(element, attributes, augs);
        return false;

    } protected void startNamespaceScope(QName element, XMLAttributes attributes, Augmentations augs){
    }


    protected void handleEndElement(QName element,  Augmentations augs, boolean isEmpty)
    throws XNIException {

        fElementDepth--;

        if (fPerformValidation) {
            int elementIndex = fCurrentElementIndex;
            if (elementIndex != -1 && fCurrentContentSpecType != -1) {
                QName children[] = fElementChildren;
                int childrenOffset = fElementChildrenOffsetStack[fElementDepth + 1] + 1;
                int childrenLength = fElementChildrenLength - childrenOffset;
                int result = checkContent(elementIndex,
                                          children, childrenOffset, childrenLength);

                if (result != -1) {
                    fDTDGrammar.getElementDecl(elementIndex, fTempElementDecl);
                    if (fTempElementDecl.type == XMLElementDecl.TYPE_EMPTY) {
                        fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                   "MSG_CONTENT_INVALID",
                                                   new Object[]{ element.rawname, "EMPTY"},
                                                   XMLErrorReporter.SEVERITY_ERROR);
                    }
                    else {
                        String messageKey = result != childrenLength ?
                                            "MSG_CONTENT_INVALID" : "MSG_CONTENT_INCOMPLETE";
                        fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                   messageKey,
                                                   new Object[]{ element.rawname,
                                                       fDTDGrammar.getContentSpecAsString(elementIndex)},
                                                   XMLErrorReporter.SEVERITY_ERROR);
                    }
                }
            }
            fElementChildrenLength = fElementChildrenOffsetStack[fElementDepth + 1] + 1;
        }

        endNamespaceScope(fCurrentElement, augs, isEmpty);

        if (fElementDepth < -1) {
            throw new RuntimeException("FWK008 Element stack underflow");
        }
        if (fElementDepth < 0) {
            fCurrentElement.clear();
            fCurrentElementIndex = -1;
            fCurrentContentSpecType = -1;
            fInElementContent = false;

            if (fPerformValidation) {
                String value = fValidationState.checkIDRefID();
                if (value != null) {
                    fErrorReporter.reportError( XMLMessageFormatter.XML_DOMAIN,
                                                "MSG_ELEMENT_WITH_ID_REQUIRED",
                                                new Object[]{value},
                                                XMLErrorReporter.SEVERITY_ERROR );
                }
            }
            return;
        }

        fCurrentElement.setValues(fElementQNamePartsStack[fElementDepth]);

        fCurrentElementIndex = fElementIndexStack[fElementDepth];
        fCurrentContentSpecType = fContentSpecTypeStack[fElementDepth];
        fInElementContent = (fCurrentContentSpecType == XMLElementDecl.TYPE_CHILDREN);

    } protected void endNamespaceScope(QName element,  Augmentations augs, boolean isEmpty){

        if (fDocumentHandler != null && !isEmpty) {
            fDocumentHandler.endElement(fCurrentElement, augs);
        }
    }

    protected boolean isSpace(int c) {
        return XMLChar.isSpace(c);
    } public boolean characterData(String data, Augmentations augs) {
        characters(new XMLString(data.toCharArray(), 0, data.length()), augs);
        return true;
    }

}