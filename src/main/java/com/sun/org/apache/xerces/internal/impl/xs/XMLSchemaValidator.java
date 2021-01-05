


package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeException;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Field;
import com.sun.org.apache.xerces.internal.impl.xs.identity.FieldActivator;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.identity.KeyRef;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import com.sun.org.apache.xerces.internal.impl.xs.identity.UniqueOrKey;
import com.sun.org.apache.xerces.internal.impl.xs.identity.ValueStore;
import com.sun.org.apache.xerces.internal.impl.xs.identity.XPathMatcher;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMNodeFactory;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
import com.sun.org.apache.xerces.internal.parsers.XMLParser;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.IntStack;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
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
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSConstants;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import jdk.xml.internal.JdkXmlUtils;


public class XMLSchemaValidator
    implements XMLComponent, XMLDocumentFilter, FieldActivator, RevalidationHandler {

    private static final boolean DEBUG = false;

    protected static final String VALIDATION =
        Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE;


    protected static final String SCHEMA_VALIDATION =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_VALIDATION_FEATURE;


    protected static final String SCHEMA_FULL_CHECKING =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_FULL_CHECKING;


    protected static final String DYNAMIC_VALIDATION =
        Constants.XERCES_FEATURE_PREFIX + Constants.DYNAMIC_VALIDATION_FEATURE;


    protected static final String NORMALIZE_DATA =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_NORMALIZED_VALUE;


    protected static final String SCHEMA_ELEMENT_DEFAULT =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_ELEMENT_DEFAULT;


    protected static final String SCHEMA_AUGMENT_PSVI =
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_AUGMENT_PSVI;


    protected static final String ALLOW_JAVA_ENCODINGS =
        Constants.XERCES_FEATURE_PREFIX + Constants.ALLOW_JAVA_ENCODINGS_FEATURE;


    protected static final String STANDARD_URI_CONFORMANT_FEATURE =
        Constants.XERCES_FEATURE_PREFIX + Constants.STANDARD_URI_CONFORMANT_FEATURE;


    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS =
        Constants.XERCES_FEATURE_PREFIX + Constants.GENERATE_SYNTHETIC_ANNOTATIONS_FEATURE;


    protected static final String VALIDATE_ANNOTATIONS =
        Constants.XERCES_FEATURE_PREFIX + Constants.VALIDATE_ANNOTATIONS_FEATURE;


    protected static final String HONOUR_ALL_SCHEMALOCATIONS =
        Constants.XERCES_FEATURE_PREFIX + Constants.HONOUR_ALL_SCHEMALOCATIONS_FEATURE;


    protected static final String USE_GRAMMAR_POOL_ONLY =
        Constants.XERCES_FEATURE_PREFIX + Constants.USE_GRAMMAR_POOL_ONLY_FEATURE;


    protected static final String CONTINUE_AFTER_FATAL_ERROR =
        Constants.XERCES_FEATURE_PREFIX + Constants.CONTINUE_AFTER_FATAL_ERROR_FEATURE;

    protected static final String PARSER_SETTINGS =
            Constants.XERCES_FEATURE_PREFIX + Constants.PARSER_SETTINGS;


    protected static final String NAMESPACE_GROWTH =
        Constants.XERCES_FEATURE_PREFIX + Constants.NAMESPACE_GROWTH_FEATURE;


    protected static final String TOLERATE_DUPLICATES =
        Constants.XERCES_FEATURE_PREFIX + Constants.TOLERATE_DUPLICATES_FEATURE;

    protected static final String REPORT_WHITESPACE =
            Constants.SUN_SCHEMA_FEATURE_PREFIX + Constants.SUN_REPORT_IGNORED_ELEMENT_CONTENT_WHITESPACE;

    public static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    public static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    public static final String ENTITY_RESOLVER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;


    public static final String XMLGRAMMAR_POOL =
        Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;

    protected static final String VALIDATION_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATION_MANAGER_PROPERTY;

    protected static final String ENTITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;


    protected static final String SCHEMA_LOCATION =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_LOCATION;


    protected static final String SCHEMA_NONS_LOCATION =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_NONS_LOCATION;


    protected static final String JAXP_SCHEMA_SOURCE =
        Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_SOURCE;


    protected static final String JAXP_SCHEMA_LANGUAGE =
        Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_LANGUAGE;


    protected static final String SCHEMA_DV_FACTORY =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_DV_FACTORY_PROPERTY;


    private static final String XML_SECURITY_PROPERTY_MANAGER =
            Constants.XML_SECURITY_PROPERTY_MANAGER;

    protected static final String OVERRIDE_PARSER = JdkXmlUtils.OVERRIDE_PARSER;

    private static final String[] RECOGNIZED_FEATURES =
        {
            VALIDATION,
            SCHEMA_VALIDATION,
            DYNAMIC_VALIDATION,
            SCHEMA_FULL_CHECKING,
            ALLOW_JAVA_ENCODINGS,
            CONTINUE_AFTER_FATAL_ERROR,
            STANDARD_URI_CONFORMANT_FEATURE,
            GENERATE_SYNTHETIC_ANNOTATIONS,
            VALIDATE_ANNOTATIONS,
            HONOUR_ALL_SCHEMALOCATIONS,
            USE_GRAMMAR_POOL_ONLY,
            NAMESPACE_GROWTH,
            TOLERATE_DUPLICATES,
            OVERRIDE_PARSER
    };


    private static final Boolean[] FEATURE_DEFAULTS = { null,
        null, null, null, null, null, null,
        null,
        null,
        null,
        null,
        null,
        null,
        JdkXmlUtils.OVERRIDE_PARSER_DEFAULT
    };


    private static final String[] RECOGNIZED_PROPERTIES =
        {
            SYMBOL_TABLE,
            ERROR_REPORTER,
            ENTITY_RESOLVER,
            VALIDATION_MANAGER,
            SCHEMA_LOCATION,
            SCHEMA_NONS_LOCATION,
            JAXP_SCHEMA_SOURCE,
            JAXP_SCHEMA_LANGUAGE,
            SCHEMA_DV_FACTORY,
            XML_SECURITY_PROPERTY_MANAGER
            };


    private static final Object[] PROPERTY_DEFAULTS =
        { null, null, null, null, null, null, null, null, null, null, null, null, null};

    protected static final int ID_CONSTRAINT_NUM = 1;

    protected ElementPSVImpl fCurrentPSVI = new ElementPSVImpl();

    protected final AugmentationsImpl fAugmentations = new AugmentationsImpl();


    protected final HashMap fMayMatchFieldMap = new HashMap();

    protected XMLString fDefaultValue;

    protected boolean fDynamicValidation = false;
    protected boolean fSchemaDynamicValidation = false;
    protected boolean fDoValidation = false;
    protected boolean fFullChecking = false;
    protected boolean fNormalizeData = true;
    protected boolean fSchemaElementDefault = true;
    protected boolean fAugPSVI = true;
    protected boolean fIdConstraint = false;
    protected boolean fUseGrammarPoolOnly = false;

    protected boolean fNamespaceGrowth = false;


    private String fSchemaType = null;

    protected boolean fEntityRef = false;
    protected boolean fInCDATA = false;

    protected boolean fSawOnlyWhitespaceInElementContent = false;

    protected SymbolTable fSymbolTable;


    private XMLLocator fLocator;


    protected final class XSIErrorReporter {

        XMLErrorReporter fErrorReporter;

        Vector fErrors = new Vector();
        int[] fContext = new int[INITIAL_STACK_SIZE];
        int fContextCount;

        public void reset(XMLErrorReporter errorReporter) {
            fErrorReporter = errorReporter;
            fErrors.removeAllElements();
            fContextCount = 0;
        }

        public void pushContext() {
            if (!fAugPSVI) {
                return;
            }
            if (fContextCount == fContext.length) {
                int newSize = fContextCount + INC_STACK_SIZE;
                int[] newArray = new int[newSize];
                System.arraycopy(fContext, 0, newArray, 0, fContextCount);
                fContext = newArray;
            }

            fContext[fContextCount++] = fErrors.size();
        }

        public String[] popContext() {
            if (!fAugPSVI) {
                return null;
            }
            int contextPos = fContext[--fContextCount];
            int size = fErrors.size() - contextPos;
            if (size == 0)
                return null;
            String[] errors = new String[size];
            for (int i = 0; i < size; i++) {
                errors[i] = (String) fErrors.elementAt(contextPos + i);
            }
            fErrors.setSize(contextPos);
            return errors;
        }

        public String[] mergeContext() {
            if (!fAugPSVI) {
                return null;
            }
            int contextPos = fContext[--fContextCount];
            int size = fErrors.size() - contextPos;
            if (size == 0)
                return null;
            String[] errors = new String[size];
            for (int i = 0; i < size; i++) {
                errors[i] = (String) fErrors.elementAt(contextPos + i);
            }
            return errors;
        }

        public void reportError(String domain, String key, Object[] arguments, short severity)
            throws XNIException {
            fErrorReporter.reportError(domain, key, arguments, severity);
            if (fAugPSVI) {
                fErrors.addElement(key);
            }
        } public void reportError(
            XMLLocator location,
            String domain,
            String key,
            Object[] arguments,
            short severity)
            throws XNIException {
            fErrorReporter.reportError(location, domain, key, arguments, severity);
            if (fAugPSVI) {
                fErrors.addElement(key);
            }
        } }


    protected final XSIErrorReporter fXSIErrorReporter = new XSIErrorReporter();


    protected XMLEntityResolver fEntityResolver;

    protected ValidationManager fValidationManager = null;
    protected ValidationState fValidationState = new ValidationState();
    protected XMLGrammarPool fGrammarPool;

    protected String fExternalSchemas = null;
    protected String fExternalNoNamespaceSchema = null;

    protected Object fJaxpSchemaSource = null;


    protected final XSDDescription fXSDDescription = new XSDDescription();
    protected final Map<String, XMLSchemaLoader.LocationArray> fLocationPairs = new HashMap<>();


    protected XMLDocumentHandler fDocumentHandler;

    protected XMLDocumentSource fDocumentSource;

    boolean reportWhitespace = false;

    public String[] getRecognizedFeatures() {
        return (String[]) (RECOGNIZED_FEATURES.clone());
    } public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
    } public String[] getRecognizedProperties() {
        return (String[]) (RECOGNIZED_PROPERTIES.clone());
    } public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
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

        if (documentHandler instanceof XMLParser) {
            try {
                reportWhitespace =
                    ((XMLParser) documentHandler).getFeature(REPORT_WHITESPACE);
            }
            catch (Exception e) {
                reportWhitespace = false;
            }
        }
    } public XMLDocumentHandler getDocumentHandler() {
        return fDocumentHandler;
    } public void setDocumentSource(XMLDocumentSource source) {
        fDocumentSource = source;
    } public XMLDocumentSource getDocumentSource() {
        return fDocumentSource;
    } public void startDocument(
        XMLLocator locator,
        String encoding,
        NamespaceContext namespaceContext,
        Augmentations augs)
        throws XNIException {

        fValidationState.setNamespaceSupport(namespaceContext);
        fState4XsiType.setNamespaceSupport(namespaceContext);
        fState4ApplyDefault.setNamespaceSupport(namespaceContext);
        fLocator = locator;

        handleStartDocument(locator, encoding);
        if (fDocumentHandler != null) {
            fDocumentHandler.startDocument(locator, encoding, namespaceContext, augs);
        }

    } public void xmlDecl(String version, String encoding, String standalone, Augmentations augs)
        throws XNIException {

        if (fDocumentHandler != null) {
            fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
        }

    } public void doctypeDecl(
        String rootElement,
        String publicId,
        String systemId,
        Augmentations augs)
        throws XNIException {

        if (fDocumentHandler != null) {
            fDocumentHandler.doctypeDecl(rootElement, publicId, systemId, augs);
        }

    } public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException {

        Augmentations modifiedAugs = handleStartElement(element, attributes, augs);
        if (fDocumentHandler != null) {
            fDocumentHandler.startElement(element, attributes, modifiedAugs);
        }

    } public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException {

        Augmentations modifiedAugs = handleStartElement(element, attributes, augs);

        fDefaultValue = null;
        if (fElementDepth != -2)
            modifiedAugs = handleEndElement(element, modifiedAugs);

        if (fDocumentHandler != null) {
            if (!fSchemaElementDefault || fDefaultValue == null) {
                fDocumentHandler.emptyElement(element, attributes, modifiedAugs);
            } else {
                fDocumentHandler.startElement(element, attributes, modifiedAugs);
                fDocumentHandler.characters(fDefaultValue, null);
                fDocumentHandler.endElement(element, modifiedAugs);
            }
        }
    } public void characters(XMLString text, Augmentations augs) throws XNIException {
        text = handleCharacters(text);

        if (fSawOnlyWhitespaceInElementContent) {
            fSawOnlyWhitespaceInElementContent = false;
            if (!reportWhitespace) {
                ignorableWhitespace(text, augs);
                return;
            }
        }

        if (fDocumentHandler != null) {
            if (fNormalizeData && fUnionType) {
                if (augs != null)
                    fDocumentHandler.characters(fEmptyXMLStr, augs);
            } else {
                fDocumentHandler.characters(text, augs);
            }
        }

    } public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        handleIgnorableWhitespace(text);
        if (fDocumentHandler != null) {
            fDocumentHandler.ignorableWhitespace(text, augs);
        }

    } public void endElement(QName element, Augmentations augs) throws XNIException {

        fDefaultValue = null;
        Augmentations modifiedAugs = handleEndElement(element, augs);
        if (fDocumentHandler != null) {
            if (!fSchemaElementDefault || fDefaultValue == null) {
                fDocumentHandler.endElement(element, modifiedAugs);
            } else {
                fDocumentHandler.characters(fDefaultValue, null);
                fDocumentHandler.endElement(element, modifiedAugs);
            }
        }
    } public void startCDATA(Augmentations augs) throws XNIException {

        fInCDATA = true;
        if (fDocumentHandler != null) {
            fDocumentHandler.startCDATA(augs);
        }

    } public void endCDATA(Augmentations augs) throws XNIException {

        fInCDATA = false;
        if (fDocumentHandler != null) {
            fDocumentHandler.endCDATA(augs);
        }

    } public void endDocument(Augmentations augs) throws XNIException {

        handleEndDocument();

        if (fDocumentHandler != null) {
            fDocumentHandler.endDocument(augs);
        }
        fLocator = null;

    } public boolean characterData(String data, Augmentations augs) {

        fSawText = fSawText || data.length() > 0;

        if (fNormalizeData && fWhiteSpace != -1 && fWhiteSpace != XSSimpleType.WS_PRESERVE) {
            normalizeWhitespace(data, fWhiteSpace == XSSimpleType.WS_COLLAPSE);
            fBuffer.append(fNormalizedStr.ch, fNormalizedStr.offset, fNormalizedStr.length);
        } else {
            if (fAppendBuffer)
                fBuffer.append(data);
        }

        boolean allWhiteSpace = true;
        if (fCurrentType != null
            && fCurrentType.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
            XSComplexTypeDecl ctype = (XSComplexTypeDecl) fCurrentType;
            if (ctype.fContentType == XSComplexTypeDecl.CONTENTTYPE_ELEMENT) {
                for (int i = 0; i < data.length(); i++) {
                    if (!XMLChar.isSpace(data.charAt(i))) {
                        allWhiteSpace = false;
                        fSawCharacters = true;
                        break;
                    }
                }
            }
        }

        return allWhiteSpace;
    }

    public void elementDefault(String data) {
        }

    public void startGeneralEntity(
        String name,
        XMLResourceIdentifier identifier,
        String encoding,
        Augmentations augs)
        throws XNIException {

        fEntityRef = true;
        if (fDocumentHandler != null) {
            fDocumentHandler.startGeneralEntity(name, identifier, encoding, augs);
        }

    } public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {

        if (fDocumentHandler != null) {
            fDocumentHandler.textDecl(version, encoding, augs);
        }

    } public void comment(XMLString text, Augmentations augs) throws XNIException {

        if (fDocumentHandler != null) {
            fDocumentHandler.comment(text, augs);
        }

    } public void processingInstruction(String target, XMLString data, Augmentations augs)
        throws XNIException {

        if (fDocumentHandler != null) {
            fDocumentHandler.processingInstruction(target, data, augs);
        }

    } public void endGeneralEntity(String name, Augmentations augs) throws XNIException {

        fEntityRef = false;
        if (fDocumentHandler != null) {
            fDocumentHandler.endGeneralEntity(name, augs);
        }

    } static final int INITIAL_STACK_SIZE = 8;
    static final int INC_STACK_SIZE = 8;

    private static final boolean DEBUG_NORMALIZATION = false;
    private final XMLString fEmptyXMLStr = new XMLString(null, 0, -1);
    private static final int BUFFER_SIZE = 20;
    private final XMLString fNormalizedStr = new XMLString();
    private boolean fFirstChunk = true;
    private boolean fTrailing = false; private short fWhiteSpace = -1; private boolean fUnionType = false;


    private final XSGrammarBucket fGrammarBucket = new XSGrammarBucket();
    private final SubstitutionGroupHandler fSubGroupHandler = new SubstitutionGroupHandler(fGrammarBucket);


    private final XSSimpleType fQNameDV =
        (XSSimpleType) SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(SchemaSymbols.ATTVAL_QNAME);

    private final CMNodeFactory nodeFactory = new CMNodeFactory();

    private final CMBuilder fCMBuilder = new CMBuilder(nodeFactory);

    private final XMLSchemaLoader fSchemaLoader =
        new XMLSchemaLoader(
                fXSIErrorReporter.fErrorReporter,
                fGrammarBucket,
                fSubGroupHandler,
                fCMBuilder);

    private String fValidationRoot;


    private int fSkipValidationDepth;


    private int fNFullValidationDepth;


    private int fNNoneValidationDepth;


    private int fElementDepth;


    private boolean fSubElement;


    private boolean[] fSubElementStack = new boolean[INITIAL_STACK_SIZE];


    private XSElementDecl fCurrentElemDecl;


    private XSElementDecl[] fElemDeclStack = new XSElementDecl[INITIAL_STACK_SIZE];


    private boolean fNil;


    private boolean[] fNilStack = new boolean[INITIAL_STACK_SIZE];


    private XSNotationDecl fNotation;


    private XSNotationDecl[] fNotationStack = new XSNotationDecl[INITIAL_STACK_SIZE];


    private XSTypeDefinition fCurrentType;


    private XSTypeDefinition[] fTypeStack = new XSTypeDefinition[INITIAL_STACK_SIZE];


    private XSCMValidator fCurrentCM;


    private XSCMValidator[] fCMStack = new XSCMValidator[INITIAL_STACK_SIZE];


    private int[] fCurrCMState;


    private int[][] fCMStateStack = new int[INITIAL_STACK_SIZE][];


    private boolean fStrictAssess = true;


    private boolean[] fStrictAssessStack = new boolean[INITIAL_STACK_SIZE];


    private final StringBuffer fBuffer = new StringBuffer();


    private boolean fAppendBuffer = true;


    private boolean fSawText = false;


    private boolean[] fSawTextStack = new boolean[INITIAL_STACK_SIZE];


    private boolean fSawCharacters = false;


    private boolean[] fStringContent = new boolean[INITIAL_STACK_SIZE];


    private final QName fTempQName = new QName();


    private ValidatedInfo fValidatedInfo = new ValidatedInfo();

    private ValidationState fState4XsiType = new ValidationState();

    private ValidationState fState4ApplyDefault = new ValidationState();

    protected XPathMatcherStack fMatcherStack = new XPathMatcherStack();


    protected ValueStoreCache fValueStoreCache = new ValueStoreCache();

    public XMLSchemaValidator() {
        fState4XsiType.setExtraChecking(false);
        fState4ApplyDefault.setFacetChecking(false);

    } public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {


        fIdConstraint = false;
        fLocationPairs.clear();

        fValidationState.resetIDTables();

        nodeFactory.reset(componentManager);

        fSchemaLoader.reset(componentManager);

        fCurrentElemDecl = null;
        fCurrentCM = null;
        fCurrCMState = null;
        fSkipValidationDepth = -1;
        fNFullValidationDepth = -1;
        fNNoneValidationDepth = -1;
        fElementDepth = -1;
        fSubElement = false;
        fSchemaDynamicValidation = false;

        fEntityRef = false;
        fInCDATA = false;

        fMatcherStack.clear();

        if (!fMayMatchFieldMap.isEmpty()) {
            fMayMatchFieldMap.clear();
        }

        fXSIErrorReporter.reset((XMLErrorReporter) componentManager.getProperty(ERROR_REPORTER));

        boolean parser_settings = componentManager.getFeature(PARSER_SETTINGS, true);

        if (!parser_settings){
            fValidationManager.addValidationState(fValidationState);
            XMLSchemaLoader.processExternalHints(
                fExternalSchemas,
                fExternalNoNamespaceSchema,
                fLocationPairs,
                fXSIErrorReporter.fErrorReporter);
            return;
        }


        SymbolTable symbolTable = (SymbolTable) componentManager.getProperty(SYMBOL_TABLE);
        if (symbolTable != fSymbolTable) {
            fSymbolTable = symbolTable;
        }

        fNamespaceGrowth = componentManager.getFeature(NAMESPACE_GROWTH, false);
        fDynamicValidation = componentManager.getFeature(DYNAMIC_VALIDATION, false);

        if (fDynamicValidation) {
            fDoValidation = true;
        } else {
            fDoValidation = componentManager.getFeature(VALIDATION, false);
        }

        if (fDoValidation) {
            fDoValidation |= componentManager.getFeature(XMLSchemaValidator.SCHEMA_VALIDATION, false);
        }

        fFullChecking = componentManager.getFeature(SCHEMA_FULL_CHECKING, false);
        fNormalizeData = componentManager.getFeature(NORMALIZE_DATA, false);
        fSchemaElementDefault = componentManager.getFeature(SCHEMA_ELEMENT_DEFAULT, false);

        fAugPSVI = componentManager.getFeature(SCHEMA_AUGMENT_PSVI, true);

        fSchemaType =
                (String) componentManager.getProperty(
                    Constants.JAXP_PROPERTY_PREFIX + Constants.SCHEMA_LANGUAGE, null);

        fUseGrammarPoolOnly = componentManager.getFeature(USE_GRAMMAR_POOL_ONLY, false);

        fEntityResolver = (XMLEntityResolver) componentManager.getProperty(ENTITY_MANAGER);

        fValidationManager = (ValidationManager) componentManager.getProperty(VALIDATION_MANAGER);
        fValidationManager.addValidationState(fValidationState);
        fValidationState.setSymbolTable(fSymbolTable);


        try {
            fExternalSchemas = (String) componentManager.getProperty(SCHEMA_LOCATION);
            fExternalNoNamespaceSchema =
                (String) componentManager.getProperty(SCHEMA_NONS_LOCATION);
        } catch (XMLConfigurationException e) {
            fExternalSchemas = null;
            fExternalNoNamespaceSchema = null;
        }

        XMLSchemaLoader.processExternalHints(
            fExternalSchemas,
            fExternalNoNamespaceSchema,
            fLocationPairs,
            fXSIErrorReporter.fErrorReporter);

        fJaxpSchemaSource = componentManager.getProperty(JAXP_SCHEMA_SOURCE, null);

        fGrammarPool = (XMLGrammarPool) componentManager.getProperty(XMLGRAMMAR_POOL, null);

        fState4XsiType.setSymbolTable(symbolTable);
        fState4ApplyDefault.setSymbolTable(symbolTable);

    } public void startValueScopeFor(IdentityConstraint identityConstraint, int initialDepth) {

        ValueStoreBase valueStore =
            fValueStoreCache.getValueStoreFor(identityConstraint, initialDepth);
        valueStore.startValueScope();

    } public XPathMatcher activateField(Field field, int initialDepth) {
        ValueStore valueStore =
            fValueStoreCache.getValueStoreFor(field.getIdentityConstraint(), initialDepth);
        setMayMatch(field, Boolean.TRUE);
        XPathMatcher matcher = field.createMatcher(this, valueStore);
        fMatcherStack.addMatcher(matcher);
        matcher.startDocumentFragment();
        return matcher;
    } public void endValueScopeFor(IdentityConstraint identityConstraint, int initialDepth) {

        ValueStoreBase valueStore =
            fValueStoreCache.getValueStoreFor(identityConstraint, initialDepth);
        valueStore.endValueScope();

    } public void setMayMatch(Field field, Boolean state) {
        fMayMatchFieldMap.put(field, state);
    } public Boolean mayMatch(Field field) {
        return (Boolean) fMayMatchFieldMap.get(field);
    } private void activateSelectorFor(IdentityConstraint ic) {
        Selector selector = ic.getSelector();
        FieldActivator activator = this;
        if (selector == null)
            return;
        XPathMatcher matcher = selector.createMatcher(activator, fElementDepth);
        fMatcherStack.addMatcher(matcher);
        matcher.startDocumentFragment();
    }

    void ensureStackCapacity() {

        if (fElementDepth == fElemDeclStack.length) {
            int newSize = fElementDepth + INC_STACK_SIZE;
            boolean[] newArrayB = new boolean[newSize];
            System.arraycopy(fSubElementStack, 0, newArrayB, 0, fElementDepth);
            fSubElementStack = newArrayB;

            XSElementDecl[] newArrayE = new XSElementDecl[newSize];
            System.arraycopy(fElemDeclStack, 0, newArrayE, 0, fElementDepth);
            fElemDeclStack = newArrayE;

            newArrayB = new boolean[newSize];
            System.arraycopy(fNilStack, 0, newArrayB, 0, fElementDepth);
            fNilStack = newArrayB;

            XSNotationDecl[] newArrayN = new XSNotationDecl[newSize];
            System.arraycopy(fNotationStack, 0, newArrayN, 0, fElementDepth);
            fNotationStack = newArrayN;

            XSTypeDefinition[] newArrayT = new XSTypeDefinition[newSize];
            System.arraycopy(fTypeStack, 0, newArrayT, 0, fElementDepth);
            fTypeStack = newArrayT;

            XSCMValidator[] newArrayC = new XSCMValidator[newSize];
            System.arraycopy(fCMStack, 0, newArrayC, 0, fElementDepth);
            fCMStack = newArrayC;

            newArrayB = new boolean[newSize];
            System.arraycopy(fSawTextStack, 0, newArrayB, 0, fElementDepth);
            fSawTextStack = newArrayB;

            newArrayB = new boolean[newSize];
            System.arraycopy(fStringContent, 0, newArrayB, 0, fElementDepth);
            fStringContent = newArrayB;

            newArrayB = new boolean[newSize];
            System.arraycopy(fStrictAssessStack, 0, newArrayB, 0, fElementDepth);
            fStrictAssessStack = newArrayB;

            int[][] newArrayIA = new int[newSize][];
            System.arraycopy(fCMStateStack, 0, newArrayIA, 0, fElementDepth);
            fCMStateStack = newArrayIA;
        }

    } void handleStartDocument(XMLLocator locator, String encoding) {
        fValueStoreCache.startDocument();
        if (fAugPSVI) {
            fCurrentPSVI.fGrammars = null;
            fCurrentPSVI.fSchemaInformation = null;
        }
    } void handleEndDocument() {
        fValueStoreCache.endDocument();
    } XMLString handleCharacters(XMLString text) {

        if (fSkipValidationDepth >= 0)
            return text;

        fSawText = fSawText || text.length > 0;

        if (fNormalizeData && fWhiteSpace != -1 && fWhiteSpace != XSSimpleType.WS_PRESERVE) {
            normalizeWhitespace(text, fWhiteSpace == XSSimpleType.WS_COLLAPSE);
            text = fNormalizedStr;
        }
        if (fAppendBuffer)
            fBuffer.append(text.ch, text.offset, text.length);

        fSawOnlyWhitespaceInElementContent = false;
        if (fCurrentType != null
            && fCurrentType.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
            XSComplexTypeDecl ctype = (XSComplexTypeDecl) fCurrentType;
            if (ctype.fContentType == XSComplexTypeDecl.CONTENTTYPE_ELEMENT) {
                for (int i = text.offset; i < text.offset + text.length; i++) {
                    if (!XMLChar.isSpace(text.ch[i])) {
                        fSawCharacters = true;
                        break;
                    }
                    fSawOnlyWhitespaceInElementContent = !fSawCharacters;
                }
            }
        }

        return text;
    } private void normalizeWhitespace(XMLString value, boolean collapse) {
        boolean skipSpace = collapse;
        boolean sawNonWS = false;
        boolean leading = false;
        boolean trailing = false;
        char c;
        int size = value.offset + value.length;

        if (fNormalizedStr.ch == null || fNormalizedStr.ch.length < value.length + 1) {
            fNormalizedStr.ch = new char[value.length + 1];
        }
        fNormalizedStr.offset = 1;
        fNormalizedStr.length = 1;

        for (int i = value.offset; i < size; i++) {
            c = value.ch[i];
            if (XMLChar.isSpace(c)) {
                if (!skipSpace) {
                    fNormalizedStr.ch[fNormalizedStr.length++] = ' ';
                    skipSpace = collapse;
                }
                if (!sawNonWS) {
                    leading = true;
                }
            } else {
                fNormalizedStr.ch[fNormalizedStr.length++] = c;
                skipSpace = false;
                sawNonWS = true;
            }
        }
        if (skipSpace) {
            if (fNormalizedStr.length > 1) {
                fNormalizedStr.length--;
                trailing = true;
            } else if (leading && !fFirstChunk) {
                trailing = true;
            }
        }

        if (fNormalizedStr.length > 1) {
            if (!fFirstChunk && (fWhiteSpace == XSSimpleType.WS_COLLAPSE)) {
                if (fTrailing) {
                    fNormalizedStr.offset = 0;
                    fNormalizedStr.ch[0] = ' ';
                } else if (leading) {
                    fNormalizedStr.offset = 0;
                    fNormalizedStr.ch[0] = ' ';
                }
            }
        }

        fNormalizedStr.length -= fNormalizedStr.offset;

        fTrailing = trailing;

        if (trailing || sawNonWS)
            fFirstChunk = false;
    }

    private void normalizeWhitespace(String value, boolean collapse) {
        boolean skipSpace = collapse;
        char c;
        int size = value.length();

        if (fNormalizedStr.ch == null || fNormalizedStr.ch.length < size) {
            fNormalizedStr.ch = new char[size];
        }
        fNormalizedStr.offset = 0;
        fNormalizedStr.length = 0;

        for (int i = 0; i < size; i++) {
            c = value.charAt(i);
            if (XMLChar.isSpace(c)) {
                if (!skipSpace) {
                    fNormalizedStr.ch[fNormalizedStr.length++] = ' ';
                    skipSpace = collapse;
                }
            } else {
                fNormalizedStr.ch[fNormalizedStr.length++] = c;
                skipSpace = false;
            }
        }
        if (skipSpace) {
            if (fNormalizedStr.length != 0)
                fNormalizedStr.length--;
        }
    }

    void handleIgnorableWhitespace(XMLString text) {

        if (fSkipValidationDepth >= 0)
            return;

        } Augmentations handleStartElement(QName element, XMLAttributes attributes, Augmentations augs) {

        if (DEBUG) {
            System.out.println("==>handleStartElement: " + element);
        }

        if (fElementDepth == -1 && fValidationManager.isGrammarFound()) {
            if (fSchemaType == null) {
                fSchemaDynamicValidation = true;
            } else {
                }

        }

        String sLocation =
            attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_SCHEMALOCATION);
        String nsLocation =
            attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
        storeLocations(sLocation, nsLocation);

        if (fSkipValidationDepth >= 0) {
            fElementDepth++;
            if (fAugPSVI)
                augs = getEmptyAugs(augs);
            return augs;
        }

        SchemaGrammar sGrammar =
            findSchemaGrammar(
                XSDDescription.CONTEXT_ELEMENT,
                element.uri,
                null,
                element,
                attributes);

        Object decl = null;
        if (fCurrentCM != null) {
            decl = fCurrentCM.oneTransition(element, fCurrCMState, fSubGroupHandler);
            if (fCurrCMState[0] == XSCMValidator.FIRST_ERROR) {
                XSComplexTypeDecl ctype = (XSComplexTypeDecl) fCurrentType;
                Vector next;
                if (ctype.fParticle != null
                    && (next = fCurrentCM.whatCanGoHere(fCurrCMState)).size() > 0) {
                    String expected = expectedStr(next);
                    reportSchemaError(
                        "cvc-complex-type.2.4.a",
                        new Object[] { element.rawname, expected });
                } else {
                    reportSchemaError("cvc-complex-type.2.4.d", new Object[] { element.rawname });
                }
            }
        }

        if (fElementDepth != -1) {
            ensureStackCapacity();
            fSubElementStack[fElementDepth] = true;
            fSubElement = false;
            fElemDeclStack[fElementDepth] = fCurrentElemDecl;
            fNilStack[fElementDepth] = fNil;
            fNotationStack[fElementDepth] = fNotation;
            fTypeStack[fElementDepth] = fCurrentType;
            fStrictAssessStack[fElementDepth] = fStrictAssess;
            fCMStack[fElementDepth] = fCurrentCM;
            fCMStateStack[fElementDepth] = fCurrCMState;
            fSawTextStack[fElementDepth] = fSawText;
            fStringContent[fElementDepth] = fSawCharacters;
        }

        fElementDepth++;
        fCurrentElemDecl = null;
        XSWildcardDecl wildcard = null;
        fCurrentType = null;
        fStrictAssess = true;
        fNil = false;
        fNotation = null;

        fBuffer.setLength(0);
        fSawText = false;
        fSawCharacters = false;

        if (decl != null) {
            if (decl instanceof XSElementDecl) {
                fCurrentElemDecl = (XSElementDecl) decl;
            } else {
                wildcard = (XSWildcardDecl) decl;
            }
        }

        if (wildcard != null && wildcard.fProcessContents == XSWildcardDecl.PC_SKIP) {
            fSkipValidationDepth = fElementDepth;
            if (fAugPSVI)
                augs = getEmptyAugs(augs);
            return augs;
        }

        if (fCurrentElemDecl == null) {
            if (sGrammar != null) {
                fCurrentElemDecl = sGrammar.getGlobalElementDecl(element.localpart);
            }
        }

        if (fCurrentElemDecl != null) {
            fCurrentType = fCurrentElemDecl.fType;
        }

        String xsiType = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_TYPE);

        if (fCurrentType == null && xsiType == null) {
            if (fElementDepth == 0) {
                if (fDynamicValidation || fSchemaDynamicValidation) {
                    if (fDocumentSource != null) {
                        fDocumentSource.setDocumentHandler(fDocumentHandler);
                        if (fDocumentHandler != null)
                            fDocumentHandler.setDocumentSource(fDocumentSource);
                        fElementDepth = -2;
                        return augs;
                    }

                    fSkipValidationDepth = fElementDepth;
                    if (fAugPSVI)
                        augs = getEmptyAugs(augs);
                    return augs;
                }
                fXSIErrorReporter.fErrorReporter.reportError(
                    XSMessageFormatter.SCHEMA_DOMAIN,
                    "cvc-elt.1",
                    new Object[] { element.rawname },
                    XMLErrorReporter.SEVERITY_ERROR);
            }
            else if (wildcard != null && wildcard.fProcessContents == XSWildcardDecl.PC_STRICT) {
                reportSchemaError("cvc-complex-type.2.4.c", new Object[] { element.rawname });
            }
            fCurrentType = SchemaGrammar.fAnyType;
            fStrictAssess = false;
            fNFullValidationDepth = fElementDepth;
            fAppendBuffer = false;

            fXSIErrorReporter.pushContext();
        } else {
            fXSIErrorReporter.pushContext();

            if (xsiType != null) {
                XSTypeDefinition oldType = fCurrentType;
                fCurrentType = getAndCheckXsiType(element, xsiType, attributes);
                if (fCurrentType == null) {
                    if (oldType == null)
                        fCurrentType = SchemaGrammar.fAnyType;
                    else
                        fCurrentType = oldType;
                }
            }

            fNNoneValidationDepth = fElementDepth;
            if (fCurrentElemDecl != null
                && fCurrentElemDecl.getConstraintType() == XSConstants.VC_FIXED) {
                fAppendBuffer = true;
            }
            else if (fCurrentType.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
                fAppendBuffer = true;
            } else {
                XSComplexTypeDecl ctype = (XSComplexTypeDecl) fCurrentType;
                fAppendBuffer = (ctype.fContentType == XSComplexTypeDecl.CONTENTTYPE_SIMPLE);
            }
        }

        if (fCurrentElemDecl != null && fCurrentElemDecl.getAbstract())
            reportSchemaError("cvc-elt.2", new Object[] { element.rawname });

        if (fElementDepth == 0) {
            fValidationRoot = element.rawname;
        }

        if (fNormalizeData) {
            fFirstChunk = true;
            fTrailing = false;
            fUnionType = false;
            fWhiteSpace = -1;
        }

        if (fCurrentType.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
            XSComplexTypeDecl ctype = (XSComplexTypeDecl) fCurrentType;
            if (ctype.getAbstract()) {
                reportSchemaError("cvc-type.2", new Object[] { element.rawname });
            }
            if (fNormalizeData) {
                if (ctype.fContentType == XSComplexTypeDecl.CONTENTTYPE_SIMPLE) {
                    if (ctype.fXSSimpleType.getVariety() == XSSimpleType.VARIETY_UNION) {
                        fUnionType = true;
                    } else {
                        try {
                            fWhiteSpace = ctype.fXSSimpleType.getWhitespace();
                        } catch (DatatypeException e) {
                            }
                    }
                }
            }
        }
        else if (fNormalizeData) {
            XSSimpleType dv = (XSSimpleType) fCurrentType;
            if (dv.getVariety() == XSSimpleType.VARIETY_UNION) {
                fUnionType = true;
            } else {
                try {
                    fWhiteSpace = dv.getWhitespace();
                } catch (DatatypeException e) {
                    }
            }
        }

        fCurrentCM = null;
        if (fCurrentType.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
            fCurrentCM = ((XSComplexTypeDecl) fCurrentType).getContentModel(fCMBuilder);
        }

        fCurrCMState = null;
        if (fCurrentCM != null)
            fCurrCMState = fCurrentCM.startContentModel();

        String xsiNil = attributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NIL);
        if (xsiNil != null && fCurrentElemDecl != null)
            fNil = getXsiNil(element, xsiNil);

        XSAttributeGroupDecl attrGrp = null;
        if (fCurrentType.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
            XSComplexTypeDecl ctype = (XSComplexTypeDecl) fCurrentType;
            attrGrp = ctype.getAttrGrp();
        }
        fValueStoreCache.startElement();
        fMatcherStack.pushContext();
        if (fCurrentElemDecl != null && fCurrentElemDecl.fIDCPos > 0) {
            fIdConstraint = true;
            fValueStoreCache.initValueStoresFor(fCurrentElemDecl, this);
        }
        processAttributes(element, attributes, attrGrp);

        if (attrGrp != null) {
            addDefaultAttributes(element, attributes, attrGrp);
        }

        int count = fMatcherStack.getMatcherCount();
        for (int i = 0; i < count; i++) {
            XPathMatcher matcher = fMatcherStack.getMatcherAt(i);
            matcher.startElement( element, attributes);
        }

        if (fAugPSVI) {
            augs = getEmptyAugs(augs);

            fCurrentPSVI.fValidationContext = fValidationRoot;
            fCurrentPSVI.fDeclaration = fCurrentElemDecl;
            fCurrentPSVI.fTypeDecl = fCurrentType;
            fCurrentPSVI.fNotation = fNotation;
        }

        return augs;

    } Augmentations handleEndElement(QName element, Augmentations augs) {

        if (DEBUG) {
            System.out.println("==>handleEndElement:" + element);
        }
        if (fSkipValidationDepth >= 0) {
            if (fSkipValidationDepth == fElementDepth && fSkipValidationDepth > 0) {
                fNFullValidationDepth = fSkipValidationDepth - 1;
                fSkipValidationDepth = -1;
                fElementDepth--;
                fSubElement = fSubElementStack[fElementDepth];
                fCurrentElemDecl = fElemDeclStack[fElementDepth];
                fNil = fNilStack[fElementDepth];
                fNotation = fNotationStack[fElementDepth];
                fCurrentType = fTypeStack[fElementDepth];
                fCurrentCM = fCMStack[fElementDepth];
                fStrictAssess = fStrictAssessStack[fElementDepth];
                fCurrCMState = fCMStateStack[fElementDepth];
                fSawText = fSawTextStack[fElementDepth];
                fSawCharacters = fStringContent[fElementDepth];
            }
            else {
                fElementDepth--;
            }

            if (fElementDepth == -1 && fFullChecking) {
                XSConstraints.fullSchemaChecking(
                    fGrammarBucket,
                    fSubGroupHandler,
                    fCMBuilder,
                    fXSIErrorReporter.fErrorReporter);
            }

            if (fAugPSVI)
                augs = getEmptyAugs(augs);
            return augs;
        }

        processElementContent(element);

        int oldCount = fMatcherStack.getMatcherCount();
        for (int i = oldCount - 1; i >= 0; i--) {
            XPathMatcher matcher = fMatcherStack.getMatcherAt(i);
            if (fCurrentElemDecl == null)
                matcher.endElement(element, null, false, fValidatedInfo.actualValue, fValidatedInfo.actualValueType, fValidatedInfo.itemValueTypes);

            else
                matcher.endElement(
                    element,
                    fCurrentType,
                    fCurrentElemDecl.getNillable(),
                    fDefaultValue == null
                        ? fValidatedInfo.actualValue
                        : fCurrentElemDecl.fDefault.actualValue,
                    fDefaultValue == null
                        ? fValidatedInfo.actualValueType
                        : fCurrentElemDecl.fDefault.actualValueType,
                    fDefaultValue == null
                        ? fValidatedInfo.itemValueTypes
                        : fCurrentElemDecl.fDefault.itemValueTypes);
        }

        if (fMatcherStack.size() > 0) {
            fMatcherStack.popContext();
        }

        int newCount = fMatcherStack.getMatcherCount();
        for (int i = oldCount - 1; i >= newCount; i--) {
            XPathMatcher matcher = fMatcherStack.getMatcherAt(i);
            if (matcher instanceof Selector.Matcher) {
                Selector.Matcher selMatcher = (Selector.Matcher) matcher;
                IdentityConstraint id;
                if ((id = selMatcher.getIdentityConstraint()) != null
                    && id.getCategory() != IdentityConstraint.IC_KEYREF) {
                    fValueStoreCache.transplant(id, selMatcher.getInitialDepth());
                }
            }
        }

        for (int i = oldCount - 1; i >= newCount; i--) {
            XPathMatcher matcher = fMatcherStack.getMatcherAt(i);
            if (matcher instanceof Selector.Matcher) {
                Selector.Matcher selMatcher = (Selector.Matcher) matcher;
                IdentityConstraint id;
                if ((id = selMatcher.getIdentityConstraint()) != null
                    && id.getCategory() == IdentityConstraint.IC_KEYREF) {
                    ValueStoreBase values =
                        fValueStoreCache.getValueStoreFor(id, selMatcher.getInitialDepth());
                    if (values != null) values.endDocumentFragment();
                }
            }
        }
        fValueStoreCache.endElement();

        SchemaGrammar[] grammars = null;
        if (fElementDepth == 0) {
            String invIdRef = fValidationState.checkIDRefID();
            fValidationState.resetIDTables();
            if (invIdRef != null) {
                reportSchemaError("cvc-id.1", new Object[] { invIdRef });
            }
            if (fFullChecking) {
                XSConstraints.fullSchemaChecking(
                    fGrammarBucket,
                    fSubGroupHandler,
                    fCMBuilder,
                    fXSIErrorReporter.fErrorReporter);
            }

            grammars = fGrammarBucket.getGrammars();
            if (fGrammarPool != null) {
                for (int k=0; k < grammars.length; k++) {
                    grammars[k].setImmutable(true);
                }
                fGrammarPool.cacheGrammars(XMLGrammarDescription.XML_SCHEMA, grammars);
            }
            augs = endElementPSVI(true, grammars, augs);
        } else {
            augs = endElementPSVI(false, grammars, augs);

            fElementDepth--;

            fSubElement = fSubElementStack[fElementDepth];
            fCurrentElemDecl = fElemDeclStack[fElementDepth];
            fNil = fNilStack[fElementDepth];
            fNotation = fNotationStack[fElementDepth];
            fCurrentType = fTypeStack[fElementDepth];
            fCurrentCM = fCMStack[fElementDepth];
            fStrictAssess = fStrictAssessStack[fElementDepth];
            fCurrCMState = fCMStateStack[fElementDepth];
            fSawText = fSawTextStack[fElementDepth];
            fSawCharacters = fStringContent[fElementDepth];

            fWhiteSpace = -1;
            fAppendBuffer = false;
            fUnionType = false;
        }

        return augs;
    } final Augmentations endElementPSVI(
        boolean root,
        SchemaGrammar[] grammars,
        Augmentations augs) {

        if (fAugPSVI) {
            augs = getEmptyAugs(augs);

            fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
            fCurrentPSVI.fTypeDecl = this.fCurrentType;
            fCurrentPSVI.fNotation = this.fNotation;
            fCurrentPSVI.fValidationContext = this.fValidationRoot;
            if (fElementDepth > fNFullValidationDepth) {
                fCurrentPSVI.fValidationAttempted = ElementPSVI.VALIDATION_FULL;
            }
            else if (fElementDepth > fNNoneValidationDepth) {
                fCurrentPSVI.fValidationAttempted = ElementPSVI.VALIDATION_NONE;
            }
            else {
                fCurrentPSVI.fValidationAttempted = ElementPSVI.VALIDATION_PARTIAL;
                fNFullValidationDepth = fNNoneValidationDepth = fElementDepth - 1;
            }

            if (fDefaultValue != null)
                fCurrentPSVI.fSpecified = true;
            fCurrentPSVI.fNil = fNil;
            fCurrentPSVI.fMemberType = fValidatedInfo.memberType;
            fCurrentPSVI.fNormalizedValue = fValidatedInfo.normalizedValue;
            fCurrentPSVI.fActualValue = fValidatedInfo.actualValue;
            fCurrentPSVI.fActualValueType = fValidatedInfo.actualValueType;
            fCurrentPSVI.fItemValueTypes = fValidatedInfo.itemValueTypes;

            if (fStrictAssess) {
                String[] errors = fXSIErrorReporter.mergeContext();

                fCurrentPSVI.fErrorCodes = errors;
                fCurrentPSVI.fValidity =
                    (errors == null) ? ElementPSVI.VALIDITY_VALID : ElementPSVI.VALIDITY_INVALID;
            } else {
                fCurrentPSVI.fValidity = ElementPSVI.VALIDITY_NOTKNOWN;
                fXSIErrorReporter.popContext();
            }

            if (root) {
                fCurrentPSVI.fGrammars = grammars;
                fCurrentPSVI.fSchemaInformation = null;
            }
        }

        return augs;

    }

    Augmentations getEmptyAugs(Augmentations augs) {
        if (augs == null) {
            augs = fAugmentations;
            augs.removeAllItems();
        }
        augs.putItem(Constants.ELEMENT_PSVI, fCurrentPSVI);
        fCurrentPSVI.reset();

        return augs;
    }

    void storeLocations(String sLocation, String nsLocation) {
        if (sLocation != null) {
            if (!XMLSchemaLoader.tokenizeSchemaLocationStr(sLocation, fLocationPairs)) {
                fXSIErrorReporter.reportError(
                    XSMessageFormatter.SCHEMA_DOMAIN,
                    "SchemaLocation",
                    new Object[] { sLocation },
                    XMLErrorReporter.SEVERITY_WARNING);
            }
        }
        if (nsLocation != null) {
            XMLSchemaLoader.LocationArray la = fLocationPairs.get(XMLSymbols.EMPTY_STRING);
            if (la == null) {
                la = new XMLSchemaLoader.LocationArray();
                fLocationPairs.put(XMLSymbols.EMPTY_STRING, la);
            }
            la.addLocation(nsLocation);
        }

    } SchemaGrammar findSchemaGrammar(
        short contextType,
        String namespace,
        QName enclosingElement,
        QName triggeringComponet,
        XMLAttributes attributes) {
        SchemaGrammar grammar = null;
        grammar = fGrammarBucket.getGrammar(namespace);

        if (grammar == null) {
            fXSDDescription.setNamespace(namespace);
            if (fGrammarPool != null) {
                grammar = (SchemaGrammar) fGrammarPool.retrieveGrammar(fXSDDescription);
                if (grammar != null) {
                    if (!fGrammarBucket.putGrammar(grammar, true, fNamespaceGrowth)) {
                        fXSIErrorReporter.fErrorReporter.reportError(
                            XSMessageFormatter.SCHEMA_DOMAIN,
                            "GrammarConflict",
                            null,
                            XMLErrorReporter.SEVERITY_WARNING);
                        grammar = null;
                    }
                }
            }
        }
        if ((grammar == null && !fUseGrammarPoolOnly) || fNamespaceGrowth) {
            fXSDDescription.reset();
            fXSDDescription.fContextType = contextType;
            fXSDDescription.setNamespace(namespace);
            fXSDDescription.fEnclosedElementName = enclosingElement;
            fXSDDescription.fTriggeringComponent = triggeringComponet;
            fXSDDescription.fAttributes = attributes;
            if (fLocator != null) {
                fXSDDescription.setBaseSystemId(fLocator.getExpandedSystemId());
            }

            Map<String, XMLSchemaLoader.LocationArray> locationPairs = fLocationPairs;
            XMLSchemaLoader.LocationArray locationArray =
                locationPairs.get(namespace == null ? XMLSymbols.EMPTY_STRING : namespace);
            if (locationArray != null) {
                String[] temp = locationArray.getLocationArray();
                if (temp.length != 0) {
                    setLocationHints(fXSDDescription, temp, grammar);
                }
            }

            if (grammar == null || fXSDDescription.fLocationHints != null) {
                boolean toParseSchema = true;
                if (grammar != null) {
                     locationPairs = Collections.emptyMap();
                }

                try {
                    XMLInputSource xis =
                        XMLSchemaLoader.resolveDocument(
                            fXSDDescription,
                            locationPairs,
                            fEntityResolver);
                    if (grammar != null && fNamespaceGrowth) {
                        try {
                            if (grammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(xis.getSystemId(), xis.getBaseSystemId(), false))) {
                                toParseSchema = false;
                            }
                        }
                        catch (MalformedURIException e) {
                        }
                    }
                    if (toParseSchema) {
                        grammar = fSchemaLoader.loadSchema(fXSDDescription, xis, fLocationPairs);
                    }
                } catch (IOException ex) {
                    final String [] locationHints = fXSDDescription.getLocationHints();
                    fXSIErrorReporter.fErrorReporter.reportError(
                        XSMessageFormatter.SCHEMA_DOMAIN,
                        "schema_reference.4",
                        new Object[] { locationHints != null ? locationHints[0] : XMLSymbols.EMPTY_STRING },
                        XMLErrorReporter.SEVERITY_WARNING);
                }
            }
        }

        return grammar;

    } private void setLocationHints(XSDDescription desc, String[] locations, SchemaGrammar grammar) {
        int length = locations.length;
        if (grammar == null) {
            fXSDDescription.fLocationHints = new String[length];
            System.arraycopy(locations, 0, fXSDDescription.fLocationHints, 0, length);
        }
        else {
            setLocationHints(desc, locations, grammar.getDocumentLocations());
        }
    }

    private void setLocationHints(XSDDescription desc, String[] locations, StringList docLocations) {
        int length = locations.length;
        String[] hints = new String[length];
        int counter = 0;

        for (int i=0; i<length; i++) {
            try {
                String id = XMLEntityManager.expandSystemId(locations[i], desc.getBaseSystemId(), false);
                if (!docLocations.contains(id)) {
                    hints[counter++] = locations[i];
                }
            }
            catch (MalformedURIException e) {
            }
        }

        if (counter > 0) {
            if (counter == length) {
                fXSDDescription.fLocationHints = hints;
            }
            else {
                fXSDDescription.fLocationHints = new String[counter];
                System.arraycopy(hints, 0, fXSDDescription.fLocationHints, 0, counter);
            }
        }
    }


    XSTypeDefinition getAndCheckXsiType(QName element, String xsiType, XMLAttributes attributes) {
        QName typeName = null;
        try {
            typeName = (QName) fQNameDV.validate(xsiType, fValidationState, null);
        } catch (InvalidDatatypeValueException e) {
            reportSchemaError(e.getKey(), e.getArgs());
            reportSchemaError(
                "cvc-elt.4.1",
                new Object[] {
                    element.rawname,
                    SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_TYPE,
                    xsiType });
            return null;
        }

        XSTypeDefinition type = null;
        if (typeName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
            type = SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(typeName.localpart);
        }
        if (type == null) {
            SchemaGrammar grammar =
                findSchemaGrammar(
                    XSDDescription.CONTEXT_XSITYPE,
                    typeName.uri,
                    element,
                    typeName,
                    attributes);

            if (grammar != null)
                type = grammar.getGlobalTypeDecl(typeName.localpart);
        }
        if (type == null) {
            reportSchemaError("cvc-elt.4.2", new Object[] { element.rawname, xsiType });
            return null;
        }

        if (fCurrentType != null) {
            short block = fCurrentElemDecl.fBlock;
            if (fCurrentType.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE)
                block |= ((XSComplexTypeDecl) fCurrentType).fBlock;
            if (!XSConstraints.checkTypeDerivationOk(type, fCurrentType, block))
                reportSchemaError(
                    "cvc-elt.4.3",
                    new Object[] { element.rawname, xsiType, fCurrentType.getName()});
        }

        return type;
    } boolean getXsiNil(QName element, String xsiNil) {
        if (fCurrentElemDecl != null && !fCurrentElemDecl.getNillable()) {
            reportSchemaError(
                "cvc-elt.3.1",
                new Object[] {
                    element.rawname,
                    SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
        }
        else {
            String value = XMLChar.trim(xsiNil);
            if (value.equals(SchemaSymbols.ATTVAL_TRUE)
                || value.equals(SchemaSymbols.ATTVAL_TRUE_1)) {
                if (fCurrentElemDecl != null
                    && fCurrentElemDecl.getConstraintType() == XSConstants.VC_FIXED) {
                    reportSchemaError(
                        "cvc-elt.3.2.2",
                        new Object[] {
                            element.rawname,
                            SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
                }
                return true;
            }
        }
        return false;
    }

    void processAttributes(QName element, XMLAttributes attributes, XSAttributeGroupDecl attrGrp) {

        if (DEBUG) {
            System.out.println("==>processAttributes: " + attributes.getLength());
        }

        String wildcardIDName = null;

        int attCount = attributes.getLength();

        Augmentations augs = null;
        AttributePSVImpl attrPSVI = null;

        boolean isSimple =
            fCurrentType == null || fCurrentType.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE;

        XSObjectList attrUses = null;
        int useCount = 0;
        XSWildcardDecl attrWildcard = null;
        if (!isSimple) {
            attrUses = attrGrp.getAttributeUses();
            useCount = attrUses.getLength();
            attrWildcard = attrGrp.fAttributeWC;
        }

        for (int index = 0; index < attCount; index++) {

            attributes.getName(index, fTempQName);

            if (DEBUG) {
                System.out.println("==>process attribute: " + fTempQName);
            }

            if (fAugPSVI || fIdConstraint) {
                augs = attributes.getAugmentations(index);
                attrPSVI = (AttributePSVImpl) augs.getItem(Constants.ATTRIBUTE_PSVI);
                if (attrPSVI != null) {
                    attrPSVI.reset();
                } else {
                    attrPSVI = new AttributePSVImpl();
                    augs.putItem(Constants.ATTRIBUTE_PSVI, attrPSVI);
                }
                attrPSVI.fValidationContext = fValidationRoot;
            }

            if (fTempQName.uri == SchemaSymbols.URI_XSI) {
                XSAttributeDecl attrDecl = null;
                if (fTempQName.localpart == SchemaSymbols.XSI_SCHEMALOCATION)
                    attrDecl =
                        SchemaGrammar.SG_XSI.getGlobalAttributeDecl(
                            SchemaSymbols.XSI_SCHEMALOCATION);
                else if (fTempQName.localpart == SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION)
                    attrDecl =
                        SchemaGrammar.SG_XSI.getGlobalAttributeDecl(
                            SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
                else if (fTempQName.localpart == SchemaSymbols.XSI_NIL)
                    attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NIL);
                else if (fTempQName.localpart == SchemaSymbols.XSI_TYPE)
                    attrDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_TYPE);
                if (attrDecl != null) {
                    processOneAttribute(element, attributes, index, attrDecl, null, attrPSVI);
                    continue;
                }
            }

            if (fTempQName.rawname == XMLSymbols.PREFIX_XMLNS
                || fTempQName.rawname.startsWith("xmlns:")) {
                continue;
            }

            if (isSimple) {
                reportSchemaError(
                    "cvc-type.3.1.1",
                    new Object[] { element.rawname, fTempQName.rawname });
                continue;
            }

            XSAttributeUseImpl currUse = null, oneUse;
            for (int i = 0; i < useCount; i++) {
                oneUse = (XSAttributeUseImpl) attrUses.item(i);
                if (oneUse.fAttrDecl.fName == fTempQName.localpart
                    && oneUse.fAttrDecl.fTargetNamespace == fTempQName.uri) {
                    currUse = oneUse;
                    break;
                }
            }

            if (currUse == null) {
                if (attrWildcard == null || !attrWildcard.allowNamespace(fTempQName.uri)) {
                    reportSchemaError(
                        "cvc-complex-type.3.2.2",
                        new Object[] { element.rawname, fTempQName.rawname });
                    continue;
                }
            }

            XSAttributeDecl currDecl = null;
            if (currUse != null) {
                currDecl = currUse.fAttrDecl;
            } else {
                if (attrWildcard.fProcessContents == XSWildcardDecl.PC_SKIP)
                    continue;

                SchemaGrammar grammar =
                    findSchemaGrammar(
                        XSDDescription.CONTEXT_ATTRIBUTE,
                        fTempQName.uri,
                        element,
                        fTempQName,
                        attributes);

                if (grammar != null) {
                    currDecl = grammar.getGlobalAttributeDecl(fTempQName.localpart);
                }

                if (currDecl == null) {
                    if (attrWildcard.fProcessContents == XSWildcardDecl.PC_STRICT) {
                        reportSchemaError(
                            "cvc-complex-type.3.2.2",
                            new Object[] { element.rawname, fTempQName.rawname });
                    }

                    continue;
                } else {
                    if (currDecl.fType.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE
                        && ((XSSimpleType) currDecl.fType).isIDType()) {
                        if (wildcardIDName != null) {
                            reportSchemaError(
                                "cvc-complex-type.5.1",
                                new Object[] { element.rawname, currDecl.fName, wildcardIDName });
                        } else
                            wildcardIDName = currDecl.fName;
                    }
                }
            }

            processOneAttribute(element, attributes, index, currDecl, currUse, attrPSVI);
        } if (!isSimple && attrGrp.fIDAttrName != null && wildcardIDName != null) {
            reportSchemaError(
                "cvc-complex-type.5.2",
                new Object[] { element.rawname, wildcardIDName, attrGrp.fIDAttrName });
        }

    } void processOneAttribute(
        QName element,
        XMLAttributes attributes,
        int index,
        XSAttributeDecl currDecl,
        XSAttributeUseImpl currUse,
        AttributePSVImpl attrPSVI) {

        String attrValue = attributes.getValue(index);
        fXSIErrorReporter.pushContext();

        XSSimpleType attDV = currDecl.fType;

        Object actualValue = null;
        try {
            actualValue = attDV.validate(attrValue, fValidationState, fValidatedInfo);
            if (fNormalizeData)
                attributes.setValue(index, fValidatedInfo.normalizedValue);
            if (attributes instanceof XMLAttributesImpl) {
                XMLAttributesImpl attrs = (XMLAttributesImpl) attributes;
                boolean schemaId =
                    fValidatedInfo.memberType != null
                        ? fValidatedInfo.memberType.isIDType()
                        : attDV.isIDType();
                attrs.setSchemaId(index, schemaId);
            }

            if (attDV.getVariety() == XSSimpleType.VARIETY_ATOMIC
                && attDV.getPrimitiveKind() == XSSimpleType.PRIMITIVE_NOTATION) {
                QName qName = (QName) actualValue;
                SchemaGrammar grammar = fGrammarBucket.getGrammar(qName.uri);

                if (grammar != null) {
                    fNotation = grammar.getGlobalNotationDecl(qName.localpart);
                }
            }
        } catch (InvalidDatatypeValueException idve) {
            reportSchemaError(idve.getKey(), idve.getArgs());
            reportSchemaError(
                "cvc-attribute.3",
                new Object[] { element.rawname, fTempQName.rawname, attrValue, attDV.getName()});
        }

        if (actualValue != null && currDecl.getConstraintType() == XSConstants.VC_FIXED) {
            if (!isComparable(fValidatedInfo, currDecl.fDefault) || !actualValue.equals(currDecl.fDefault.actualValue)) {
                reportSchemaError(
                    "cvc-attribute.4",
                    new Object[] {
                        element.rawname,
                        fTempQName.rawname,
                        attrValue,
                        currDecl.fDefault.stringValue()});
            }
        }

        if (actualValue != null
            && currUse != null
            && currUse.fConstraintType == XSConstants.VC_FIXED) {
            if (!isComparable(fValidatedInfo, currUse.fDefault) || !actualValue.equals(currUse.fDefault.actualValue)) {
                reportSchemaError(
                    "cvc-complex-type.3.1",
                    new Object[] {
                        element.rawname,
                        fTempQName.rawname,
                        attrValue,
                        currUse.fDefault.stringValue()});
            }
        }
        if (fIdConstraint) {
            attrPSVI.fActualValue = actualValue;
        }

        if (fAugPSVI) {
            attrPSVI.fDeclaration = currDecl;
            attrPSVI.fTypeDecl = attDV;

            attrPSVI.fMemberType = fValidatedInfo.memberType;
            attrPSVI.fNormalizedValue = fValidatedInfo.normalizedValue;
            attrPSVI.fActualValue = fValidatedInfo.actualValue;
            attrPSVI.fActualValueType = fValidatedInfo.actualValueType;
            attrPSVI.fItemValueTypes = fValidatedInfo.itemValueTypes;



            attrPSVI.fValidationAttempted = AttributePSVI.VALIDATION_FULL;

            String[] errors = fXSIErrorReporter.mergeContext();
            attrPSVI.fErrorCodes = errors;
            attrPSVI.fValidity =
                (errors == null) ? AttributePSVI.VALIDITY_VALID : AttributePSVI.VALIDITY_INVALID;
        }
    }

    void addDefaultAttributes(
        QName element,
        XMLAttributes attributes,
        XSAttributeGroupDecl attrGrp) {
        if (DEBUG) {
            System.out.println("==>addDefaultAttributes: " + element);
        }
        XSObjectList attrUses = attrGrp.getAttributeUses();
        int useCount = attrUses.getLength();
        XSAttributeUseImpl currUse;
        XSAttributeDecl currDecl;
        short constType;
        ValidatedInfo defaultValue;
        boolean isSpecified;
        QName attName;
        for (int i = 0; i < useCount; i++) {

            currUse = (XSAttributeUseImpl) attrUses.item(i);
            currDecl = currUse.fAttrDecl;
            constType = currUse.fConstraintType;
            defaultValue = currUse.fDefault;
            if (constType == XSConstants.VC_NONE) {
                constType = currDecl.getConstraintType();
                defaultValue = currDecl.fDefault;
            }
            isSpecified = attributes.getValue(currDecl.fTargetNamespace, currDecl.fName) != null;

            if (currUse.fUse == SchemaSymbols.USE_REQUIRED) {
                if (!isSpecified)
                    reportSchemaError(
                        "cvc-complex-type.4",
                        new Object[] { element.rawname, currDecl.fName });
            }
            if (!isSpecified && constType != XSConstants.VC_NONE) {
                attName =
                    new QName(null, currDecl.fName, currDecl.fName, currDecl.fTargetNamespace);
                String normalized = (defaultValue != null) ? defaultValue.stringValue() : "";
                int attrIndex = attributes.addAttribute(attName, "CDATA", normalized);
                if (attributes instanceof XMLAttributesImpl) {
                    XMLAttributesImpl attrs = (XMLAttributesImpl) attributes;
                    boolean schemaId =
                        defaultValue != null
                            && defaultValue.memberType != null
                                ? defaultValue.memberType.isIDType()
                                : currDecl.fType.isIDType();
                    attrs.setSchemaId(attrIndex, schemaId);
                }

                if (fAugPSVI) {

                    Augmentations augs = attributes.getAugmentations(attrIndex);
                    AttributePSVImpl attrPSVI = new AttributePSVImpl();
                    augs.putItem(Constants.ATTRIBUTE_PSVI, attrPSVI);

                    attrPSVI.fDeclaration = currDecl;
                    attrPSVI.fTypeDecl = currDecl.fType;
                    attrPSVI.fMemberType = defaultValue.memberType;
                    attrPSVI.fNormalizedValue = normalized;
                    attrPSVI.fActualValue = defaultValue.actualValue;
                    attrPSVI.fActualValueType = defaultValue.actualValueType;
                    attrPSVI.fItemValueTypes = defaultValue.itemValueTypes;
                    attrPSVI.fValidationContext = fValidationRoot;
                    attrPSVI.fValidity = AttributePSVI.VALIDITY_VALID;
                    attrPSVI.fValidationAttempted = AttributePSVI.VALIDATION_FULL;
                    attrPSVI.fSpecified = true;
                }
            }

        } } void processElementContent(QName element) {
        if (fCurrentElemDecl != null
            && fCurrentElemDecl.fDefault != null
            && !fSawText
            && !fSubElement
            && !fNil) {

            String strv = fCurrentElemDecl.fDefault.stringValue();
            int bufLen = strv.length();
            if (fNormalizedStr.ch == null || fNormalizedStr.ch.length < bufLen) {
                fNormalizedStr.ch = new char[bufLen];
            }
            strv.getChars(0, bufLen, fNormalizedStr.ch, 0);
            fNormalizedStr.offset = 0;
            fNormalizedStr.length = bufLen;
            fDefaultValue = fNormalizedStr;
        }
        fValidatedInfo.normalizedValue = null;

        if (fNil) {
            if (fSubElement || fSawText) {
                reportSchemaError(
                    "cvc-elt.3.2.1",
                    new Object[] {
                        element.rawname,
                        SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL });
            }
        }

        this.fValidatedInfo.reset();

        if (fCurrentElemDecl != null
            && fCurrentElemDecl.getConstraintType() != XSConstants.VC_NONE
            && !fSubElement
            && !fSawText
            && !fNil) {
            if (fCurrentType != fCurrentElemDecl.fType) {
                if (XSConstraints
                    .ElementDefaultValidImmediate(
                        fCurrentType,
                        fCurrentElemDecl.fDefault.stringValue(),
                        fState4XsiType,
                        null)
                    == null)
                    reportSchemaError(
                        "cvc-elt.5.1.1",
                        new Object[] {
                            element.rawname,
                            fCurrentType.getName(),
                            fCurrentElemDecl.fDefault.stringValue()});
            }
            elementLocallyValidType(element, fCurrentElemDecl.fDefault.stringValue());
        } else {
            Object actualValue = elementLocallyValidType(element, fBuffer);
            if (fCurrentElemDecl != null
                && fCurrentElemDecl.getConstraintType() == XSConstants.VC_FIXED
                && !fNil) {
                String content = fBuffer.toString();
                if (fSubElement)
                    reportSchemaError("cvc-elt.5.2.2.1", new Object[] { element.rawname });
                if (fCurrentType.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
                    XSComplexTypeDecl ctype = (XSComplexTypeDecl) fCurrentType;
                    if (ctype.fContentType == XSComplexTypeDecl.CONTENTTYPE_MIXED) {
                        if (!fCurrentElemDecl.fDefault.normalizedValue.equals(content))
                            reportSchemaError(
                                "cvc-elt.5.2.2.2.1",
                                new Object[] {
                                    element.rawname,
                                    content,
                                    fCurrentElemDecl.fDefault.normalizedValue });
                    }
                    else if (ctype.fContentType == XSComplexTypeDecl.CONTENTTYPE_SIMPLE) {
                        if (actualValue != null && (!isComparable(fValidatedInfo, fCurrentElemDecl.fDefault)
                                || !actualValue.equals(fCurrentElemDecl.fDefault.actualValue))) {
                            reportSchemaError(
                                "cvc-elt.5.2.2.2.2",
                                new Object[] {
                                    element.rawname,
                                    content,
                                    fCurrentElemDecl.fDefault.stringValue()});
                        }
                    }
                } else if (fCurrentType.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
                    if (actualValue != null && (!isComparable(fValidatedInfo, fCurrentElemDecl.fDefault)
                            || !actualValue.equals(fCurrentElemDecl.fDefault.actualValue))) {
                        reportSchemaError(
                            "cvc-elt.5.2.2.2.2",
                            new Object[] {
                                element.rawname,
                                content,
                                fCurrentElemDecl.fDefault.stringValue()});
                    }
                }
            }
        }

        if (fDefaultValue == null && fNormalizeData && fDocumentHandler != null && fUnionType) {
            String content = fValidatedInfo.normalizedValue;
            if (content == null)
                content = fBuffer.toString();

            int bufLen = content.length();
            if (fNormalizedStr.ch == null || fNormalizedStr.ch.length < bufLen) {
                fNormalizedStr.ch = new char[bufLen];
            }
            content.getChars(0, bufLen, fNormalizedStr.ch, 0);
            fNormalizedStr.offset = 0;
            fNormalizedStr.length = bufLen;
            fDocumentHandler.characters(fNormalizedStr, null);
        }
    } Object elementLocallyValidType(QName element, Object textContent) {
        if (fCurrentType == null)
            return null;

        Object retValue = null;
        if (fCurrentType.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
            if (fSubElement)
                reportSchemaError("cvc-type.3.1.2", new Object[] { element.rawname });
            if (!fNil) {
                XSSimpleType dv = (XSSimpleType) fCurrentType;
                try {
                    if (!fNormalizeData || fUnionType) {
                        fValidationState.setNormalizationRequired(true);
                    }
                    retValue = dv.validate(textContent, fValidationState, fValidatedInfo);
                } catch (InvalidDatatypeValueException e) {
                    reportSchemaError(e.getKey(), e.getArgs());
                    reportSchemaError(
                        "cvc-type.3.1.3",
                        new Object[] { element.rawname, textContent });
                }
            }
        } else {
            retValue = elementLocallyValidComplexType(element, textContent);
        }

        return retValue;
    } Object elementLocallyValidComplexType(QName element, Object textContent) {
        Object actualValue = null;
        XSComplexTypeDecl ctype = (XSComplexTypeDecl) fCurrentType;

        if (!fNil) {
            if (ctype.fContentType == XSComplexTypeDecl.CONTENTTYPE_EMPTY
                && (fSubElement || fSawText)) {
                reportSchemaError("cvc-complex-type.2.1", new Object[] { element.rawname });
            }
            else if (ctype.fContentType == XSComplexTypeDecl.CONTENTTYPE_SIMPLE) {
                if (fSubElement)
                    reportSchemaError("cvc-complex-type.2.2", new Object[] { element.rawname });
                XSSimpleType dv = ctype.fXSSimpleType;
                try {
                    if (!fNormalizeData || fUnionType) {
                        fValidationState.setNormalizationRequired(true);
                    }
                    actualValue = dv.validate(textContent, fValidationState, fValidatedInfo);
                } catch (InvalidDatatypeValueException e) {
                    reportSchemaError(e.getKey(), e.getArgs());
                    reportSchemaError("cvc-complex-type.2.2", new Object[] { element.rawname });
                }
                }
            else if (ctype.fContentType == XSComplexTypeDecl.CONTENTTYPE_ELEMENT) {
                if (fSawCharacters) {
                    reportSchemaError("cvc-complex-type.2.3", new Object[] { element.rawname });
                }
            }
            if (ctype.fContentType == XSComplexTypeDecl.CONTENTTYPE_ELEMENT
                || ctype.fContentType == XSComplexTypeDecl.CONTENTTYPE_MIXED) {
                if (DEBUG) {
                    System.out.println(fCurrCMState);
                }
                if (fCurrCMState[0] >= 0 && !fCurrentCM.endContentModel(fCurrCMState)) {
                    String expected = expectedStr(fCurrentCM.whatCanGoHere(fCurrCMState));
                    reportSchemaError(
                        "cvc-complex-type.2.4.b",
                        new Object[] { element.rawname, expected });
                } else {
                    ArrayList errors = fCurrentCM.checkMinMaxBounds();
                    if (errors != null) {
                        for (int i = 0; i < errors.size(); i += 2) {
                            reportSchemaError(
                                (String) errors.get(i),
                                new Object[] { element.rawname, errors.get(i + 1) });
                        }
                    }
                }
             }
        }
        return actualValue;
    } void reportSchemaError(String key, Object[] arguments) {
        if (fDoValidation)
            fXSIErrorReporter.reportError(
                XSMessageFormatter.SCHEMA_DOMAIN,
                key,
                arguments,
                XMLErrorReporter.SEVERITY_ERROR);
    }


    private boolean isComparable(ValidatedInfo info1, ValidatedInfo info2) {
        final short primitiveType1 = convertToPrimitiveKind(info1.actualValueType);
        final short primitiveType2 = convertToPrimitiveKind(info2.actualValueType);
        if (primitiveType1 != primitiveType2) {
            return (primitiveType1 == XSConstants.ANYSIMPLETYPE_DT && primitiveType2 == XSConstants.STRING_DT ||
                    primitiveType1 == XSConstants.STRING_DT && primitiveType2 == XSConstants.ANYSIMPLETYPE_DT);
        }
        else if (primitiveType1 == XSConstants.LIST_DT || primitiveType1 == XSConstants.LISTOFUNION_DT) {
            final ShortList typeList1 = info1.itemValueTypes;
            final ShortList typeList2 = info2.itemValueTypes;
            final int typeList1Length = typeList1 != null ? typeList1.getLength() : 0;
            final int typeList2Length = typeList2 != null ? typeList2.getLength() : 0;
            if (typeList1Length != typeList2Length) {
                return false;
            }
            for (int i = 0; i < typeList1Length; ++i) {
                final short primitiveItem1 = convertToPrimitiveKind(typeList1.item(i));
                final short primitiveItem2 = convertToPrimitiveKind(typeList2.item(i));
                if (primitiveItem1 != primitiveItem2) {
                    if (primitiveItem1 == XSConstants.ANYSIMPLETYPE_DT && primitiveItem2 == XSConstants.STRING_DT ||
                        primitiveItem1 == XSConstants.STRING_DT && primitiveItem2 == XSConstants.ANYSIMPLETYPE_DT) {
                        continue;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private short convertToPrimitiveKind(short valueType) {

        if (valueType <= XSConstants.NOTATION_DT) {
            return valueType;
        }

        if (valueType <= XSConstants.ENTITY_DT) {
            return XSConstants.STRING_DT;
        }

        if (valueType <= XSConstants.POSITIVEINTEGER_DT) {
            return XSConstants.DECIMAL_DT;
        }

        return valueType;
    }

    private String expectedStr(Vector expected) {
        StringBuffer ret = new StringBuffer("{");
        int size = expected.size();
        for (int i = 0; i < size; i++) {
            if (i > 0)
                ret.append(", ");
            ret.append(expected.elementAt(i).toString());
        }
        ret.append('}');
        return ret.toString();
    }



    protected static class XPathMatcherStack {

        protected XPathMatcher[] fMatchers = new XPathMatcher[4];


        protected int fMatchersCount;


        protected IntStack fContextStack = new IntStack();

        public XPathMatcherStack() {
        } public void clear() {
            for (int i = 0; i < fMatchersCount; i++) {
                fMatchers[i] = null;
            }
            fMatchersCount = 0;
            fContextStack.clear();
        } public int size() {
            return fContextStack.size();
        } public int getMatcherCount() {
            return fMatchersCount;
        } public void addMatcher(XPathMatcher matcher) {
            ensureMatcherCapacity();
            fMatchers[fMatchersCount++] = matcher;
        } public XPathMatcher getMatcherAt(int index) {
            return fMatchers[index];
        } public void pushContext() {
            fContextStack.push(fMatchersCount);
        } public void popContext() {
            fMatchersCount = fContextStack.pop();
        } private void ensureMatcherCapacity() {
            if (fMatchersCount == fMatchers.length) {
                XPathMatcher[] array = new XPathMatcher[fMatchers.length * 2];
                System.arraycopy(fMatchers, 0, array, 0, fMatchers.length);
                fMatchers = array;
            }
        } } protected abstract class ValueStoreBase implements ValueStore {

        protected IdentityConstraint fIdentityConstraint;
        protected int fFieldCount = 0;
        protected Field[] fFields = null;

        protected Object[] fLocalValues = null;
        protected short[] fLocalValueTypes = null;
        protected ShortList[] fLocalItemValueTypes = null;


        protected int fValuesCount;


        public final Vector fValues = new Vector();
        public ShortVector fValueTypes = null;
        public Vector fItemValueTypes = null;

        private boolean fUseValueTypeVector = false;
        private int fValueTypesLength = 0;
        private short fValueType = 0;

        private boolean fUseItemValueTypeVector = false;
        private int fItemValueTypesLength = 0;
        private ShortList fItemValueType = null;


        final StringBuffer fTempBuffer = new StringBuffer();

        protected ValueStoreBase(IdentityConstraint identityConstraint) {
            fIdentityConstraint = identityConstraint;
            fFieldCount = fIdentityConstraint.getFieldCount();
            fFields = new Field[fFieldCount];
            fLocalValues = new Object[fFieldCount];
            fLocalValueTypes = new short[fFieldCount];
            fLocalItemValueTypes = new ShortList[fFieldCount];
            for (int i = 0; i < fFieldCount; i++) {
                fFields[i] = fIdentityConstraint.getFieldAt(i);
            }
        } public void clear() {
            fValuesCount = 0;
            fUseValueTypeVector = false;
            fValueTypesLength = 0;
            fValueType = 0;
            fUseItemValueTypeVector = false;
            fItemValueTypesLength = 0;
            fItemValueType = null;
            fValues.setSize(0);
            if (fValueTypes != null) {
                fValueTypes.clear();
            }
            if (fItemValueTypes != null) {
                fItemValueTypes.setSize(0);
            }
        } public void append(ValueStoreBase newVal) {
            for (int i = 0; i < newVal.fValues.size(); i++) {
                fValues.addElement(newVal.fValues.elementAt(i));
            }
        } public void startValueScope() {
            fValuesCount = 0;
            for (int i = 0; i < fFieldCount; i++) {
                fLocalValues[i] = null;
                fLocalValueTypes[i] = 0;
                fLocalItemValueTypes[i] = null;
            }
        } public void endValueScope() {

            if (fValuesCount == 0) {
                if (fIdentityConstraint.getCategory() == IdentityConstraint.IC_KEY) {
                    String code = "AbsentKeyValue";
                    String eName = fIdentityConstraint.getElementName();
                    String cName = fIdentityConstraint.getIdentityConstraintName();
                    reportSchemaError(code, new Object[] { eName, cName });
                }
                return;
            }

            if (fValuesCount != fFieldCount) {
                if (fIdentityConstraint.getCategory() == IdentityConstraint.IC_KEY) {
                    String code = "KeyNotEnoughValues";
                    UniqueOrKey key = (UniqueOrKey) fIdentityConstraint;
                    String eName = fIdentityConstraint.getElementName();
                    String cName = key.getIdentityConstraintName();
                    reportSchemaError(code, new Object[] { eName, cName });
                }
                return;
            }

        } public void endDocumentFragment() {
        } public void endDocument() {
        } public void reportError(String key, Object[] args) {
            reportSchemaError(key, args);
        } public void addValue(Field field, Object actualValue, short valueType, ShortList itemValueType) {
            int i;
            for (i = fFieldCount - 1; i > -1; i--) {
                if (fFields[i] == field) {
                    break;
                }
            }
            if (i == -1) {
                String code = "UnknownField";
                String eName = fIdentityConstraint.getElementName();
                String cName = fIdentityConstraint.getIdentityConstraintName();
                reportSchemaError(code, new Object[] { field.toString(), eName, cName });
                return;
            }
            if (Boolean.TRUE != mayMatch(field)) {
                String code = "FieldMultipleMatch";
                String cName = fIdentityConstraint.getIdentityConstraintName();
                reportSchemaError(code, new Object[] { field.toString(), cName });
            } else {
                fValuesCount++;
            }
            fLocalValues[i] = actualValue;
            fLocalValueTypes[i] = valueType;
            fLocalItemValueTypes[i] = itemValueType;
            if (fValuesCount == fFieldCount) {
                checkDuplicateValues();
                for (i = 0; i < fFieldCount; i++) {
                    fValues.addElement(fLocalValues[i]);
                    addValueType(fLocalValueTypes[i]);
                    addItemValueType(fLocalItemValueTypes[i]);
                }
            }
        } public boolean contains() {
            int next = 0;
            final int size = fValues.size();
            LOOP : for (int i = 0; i < size; i = next) {
                next = i + fFieldCount;
                for (int j = 0; j < fFieldCount; j++) {
                    Object value1 = fLocalValues[j];
                    Object value2 = fValues.elementAt(i);
                    short valueType1 = fLocalValueTypes[j];
                    short valueType2 = getValueTypeAt(i);
                    if (value1 == null || value2 == null || valueType1 != valueType2 || !(value1.equals(value2))) {
                        continue LOOP;
                    }
                    else if(valueType1 == XSConstants.LIST_DT || valueType1 == XSConstants.LISTOFUNION_DT) {
                        ShortList list1 = fLocalItemValueTypes[j];
                        ShortList list2 = getItemValueTypeAt(i);
                        if(list1 == null || list2 == null || !list1.equals(list2))
                            continue LOOP;
                    }
                    i++;
                }
                return true;
            }
            return false;
        } public int contains(ValueStoreBase vsb) {

            final Vector values = vsb.fValues;
            final int size1 = values.size();
            if (fFieldCount <= 1) {
                for (int i = 0; i < size1; ++i) {
                    short val = vsb.getValueTypeAt(i);
                    if (!valueTypeContains(val) || !fValues.contains(values.elementAt(i))) {
                        return i;
                    }
                    else if(val == XSConstants.LIST_DT || val == XSConstants.LISTOFUNION_DT) {
                        ShortList list1 = vsb.getItemValueTypeAt(i);
                        if (!itemValueTypeContains(list1)) {
                            return i;
                        }
                    }
                }
            }

            else {
                final int size2 = fValues.size();

                OUTER: for (int i = 0; i < size1; i += fFieldCount) {

                    INNER: for (int j = 0; j < size2; j += fFieldCount) {
                        for (int k = 0; k < fFieldCount; ++k) {
                            final Object value1 = values.elementAt(i+k);
                            final Object value2 = fValues.elementAt(j+k);
                            final short valueType1 = vsb.getValueTypeAt(i+k);
                            final short valueType2 = getValueTypeAt(j+k);
                            if (value1 != value2 && (valueType1 != valueType2 || value1 == null || !value1.equals(value2))) {
                                continue INNER;
                            }
                            else if(valueType1 == XSConstants.LIST_DT || valueType1 == XSConstants.LISTOFUNION_DT) {
                                ShortList list1 = vsb.getItemValueTypeAt(i+k);
                                ShortList list2 = getItemValueTypeAt(j+k);
                                if (list1 == null || list2 == null || !list1.equals(list2)) {
                                    continue INNER;
                                }
                            }
                        }
                        continue OUTER;
                    }
                    return i;
                }
            }
            return -1;

        } protected void checkDuplicateValues() {
            } protected String toString(Object[] values) {

            int size = values.length;
            if (size == 0) {
                return "";
            }

            fTempBuffer.setLength(0);

            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    fTempBuffer.append(',');
                }
                fTempBuffer.append(values[i]);
            }
            return fTempBuffer.toString();

        } protected String toString(Vector values, int start, int length) {

            if (length == 0) {
                return "";
            }

            if (length == 1) {
                return String.valueOf(values.elementAt(start));
            }

            StringBuffer str = new StringBuffer();
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    str.append(',');
                }
                str.append(values.elementAt(start + i));
            }
            return str.toString();

        } public String toString() {
            String s = super.toString();
            int index1 = s.lastIndexOf('$');
            if (index1 != -1) {
                s = s.substring(index1 + 1);
            }
            int index2 = s.lastIndexOf('.');
            if (index2 != -1) {
                s = s.substring(index2 + 1);
            }
            return s + '[' + fIdentityConstraint + ']';
        } private void addValueType(short type) {
            if (fUseValueTypeVector) {
                fValueTypes.add(type);
            }
            else if (fValueTypesLength++ == 0) {
                fValueType = type;
            }
            else if (fValueType != type) {
                fUseValueTypeVector = true;
                if (fValueTypes == null) {
                    fValueTypes = new ShortVector(fValueTypesLength * 2);
                }
                for (int i = 1; i < fValueTypesLength; ++i) {
                    fValueTypes.add(fValueType);
                }
                fValueTypes.add(type);
            }
        }

        private short getValueTypeAt(int index) {
            if (fUseValueTypeVector) {
                return fValueTypes.valueAt(index);
            }
            return fValueType;
        }

        private boolean valueTypeContains(short value) {
            if (fUseValueTypeVector) {
                return fValueTypes.contains(value);
            }
            return fValueType == value;
        }

        private void addItemValueType(ShortList itemValueType) {
            if (fUseItemValueTypeVector) {
                fItemValueTypes.add(itemValueType);
            }
            else if (fItemValueTypesLength++ == 0) {
                fItemValueType = itemValueType;
            }
            else if (!(fItemValueType == itemValueType ||
                    (fItemValueType != null && fItemValueType.equals(itemValueType)))) {
                fUseItemValueTypeVector = true;
                if (fItemValueTypes == null) {
                    fItemValueTypes = new Vector(fItemValueTypesLength * 2);
                }
                for (int i = 1; i < fItemValueTypesLength; ++i) {
                    fItemValueTypes.add(fItemValueType);
                }
                fItemValueTypes.add(itemValueType);
            }
        }

        private ShortList getItemValueTypeAt(int index) {
            if (fUseItemValueTypeVector) {
                return (ShortList) fItemValueTypes.elementAt(index);
            }
            return fItemValueType;
        }

        private boolean itemValueTypeContains(ShortList value) {
            if (fUseItemValueTypeVector) {
                return fItemValueTypes.contains(value);
            }
            return fItemValueType == value ||
                (fItemValueType != null && fItemValueType.equals(value));
        }

    } protected class UniqueValueStore extends ValueStoreBase {

        public UniqueValueStore(UniqueOrKey unique) {
            super(unique);
        } protected void checkDuplicateValues() {
            if (contains()) {
                String code = "DuplicateUnique";
                String value = toString(fLocalValues);
                String eName = fIdentityConstraint.getElementName();
                String cName = fIdentityConstraint.getIdentityConstraintName();
                reportSchemaError(code, new Object[] { value, eName, cName });
            }
        } } protected class KeyValueStore extends ValueStoreBase {

        public KeyValueStore(UniqueOrKey key) {
            super(key);
        } protected void checkDuplicateValues() {
            if (contains()) {
                String code = "DuplicateKey";
                String value = toString(fLocalValues);
                String eName = fIdentityConstraint.getElementName();
                String cName = fIdentityConstraint.getIdentityConstraintName();
                reportSchemaError(code, new Object[] { value, eName, cName });
            }
        } } protected class KeyRefValueStore extends ValueStoreBase {

        protected ValueStoreBase fKeyValueStore;

        public KeyRefValueStore(KeyRef keyRef, KeyValueStore keyValueStore) {
            super(keyRef);
            fKeyValueStore = keyValueStore;
        } public void endDocumentFragment() {

            super.endDocumentFragment();

            fKeyValueStore =
                (ValueStoreBase) fValueStoreCache.fGlobalIDConstraintMap.get(
                    ((KeyRef) fIdentityConstraint).getKey());

            if (fKeyValueStore == null) {
                String code = "KeyRefOutOfScope";
                String value = fIdentityConstraint.toString();
                reportSchemaError(code, new Object[] { value });
                return;
            }
            int errorIndex = fKeyValueStore.contains(this);
            if (errorIndex != -1) {
                String code = "KeyNotFound";
                String values = toString(fValues, errorIndex, fFieldCount);
                String element = fIdentityConstraint.getElementName();
                String name = fIdentityConstraint.getName();
                reportSchemaError(code, new Object[] { name, values, element });
            }

        } public void endDocument() {
            super.endDocument();

        } } protected class ValueStoreCache {

        final LocalIDKey fLocalId = new LocalIDKey();
        protected final Vector fValueStores = new Vector();


        protected final Map<LocalIDKey, ValueStoreBase>
                fIdentityConstraint2ValueStoreMap = new HashMap<>();

        protected final Stack<Map<IdentityConstraint, ValueStoreBase>>
                fGlobalMapStack = new Stack<>();
        protected final Map<IdentityConstraint, ValueStoreBase>
                fGlobalIDConstraintMap = new HashMap<>();

        public ValueStoreCache() {
        } public void startDocument() {
            fValueStores.removeAllElements();
            fIdentityConstraint2ValueStoreMap.clear();
            fGlobalIDConstraintMap.clear();
            fGlobalMapStack.removeAllElements();
        } public void startElement() {
            if (fGlobalIDConstraintMap.size() > 0)
                fGlobalMapStack.push((Map<IdentityConstraint, ValueStoreBase>)
                        ((HashMap)fGlobalIDConstraintMap).clone());
            else
                fGlobalMapStack.push(null);
            fGlobalIDConstraintMap.clear();
        } public void endElement() {
            if (fGlobalMapStack.isEmpty()) {
                return; }
            Map<IdentityConstraint, ValueStoreBase> oldMap = fGlobalMapStack.pop();
            if (oldMap == null) {
                return;
            }

            for (Map.Entry<IdentityConstraint, ValueStoreBase> entry : oldMap.entrySet()) {
                IdentityConstraint id = entry.getKey();
                ValueStoreBase oldVal = entry.getValue();
                if (oldVal != null) {
                    ValueStoreBase currVal = fGlobalIDConstraintMap.get(id);
                    if (currVal == null) {
                        fGlobalIDConstraintMap.put(id, oldVal);
                    }
                    else if (currVal != oldVal) {
                        currVal.append(oldVal);
                    }
                }
            }
        } public void initValueStoresFor(XSElementDecl eDecl, FieldActivator activator) {
            IdentityConstraint[] icArray = eDecl.fIDConstraints;
            int icCount = eDecl.fIDCPos;
            for (int i = 0; i < icCount; i++) {
                switch (icArray[i].getCategory()) {
                    case (IdentityConstraint.IC_UNIQUE) :
                        UniqueOrKey unique = (UniqueOrKey) icArray[i];
                        LocalIDKey toHash = new LocalIDKey(unique, fElementDepth);
                        UniqueValueStore uniqueValueStore =
                            (UniqueValueStore) fIdentityConstraint2ValueStoreMap.get(toHash);
                        if (uniqueValueStore == null) {
                            uniqueValueStore = new UniqueValueStore(unique);
                            fIdentityConstraint2ValueStoreMap.put(toHash, uniqueValueStore);
                        } else {
                            uniqueValueStore.clear();
                        }
                        fValueStores.addElement(uniqueValueStore);
                        activateSelectorFor(icArray[i]);
                        break;
                    case (IdentityConstraint.IC_KEY) :
                        UniqueOrKey key = (UniqueOrKey) icArray[i];
                        toHash = new LocalIDKey(key, fElementDepth);
                        KeyValueStore keyValueStore =
                            (KeyValueStore) fIdentityConstraint2ValueStoreMap.get(toHash);
                        if (keyValueStore == null) {
                            keyValueStore = new KeyValueStore(key);
                            fIdentityConstraint2ValueStoreMap.put(toHash, keyValueStore);
                        } else {
                            keyValueStore.clear();
                        }
                        fValueStores.addElement(keyValueStore);
                        activateSelectorFor(icArray[i]);
                        break;
                    case (IdentityConstraint.IC_KEYREF) :
                        KeyRef keyRef = (KeyRef) icArray[i];
                        toHash = new LocalIDKey(keyRef, fElementDepth);
                        KeyRefValueStore keyRefValueStore =
                            (KeyRefValueStore) fIdentityConstraint2ValueStoreMap.get(toHash);
                        if (keyRefValueStore == null) {
                            keyRefValueStore = new KeyRefValueStore(keyRef, null);
                            fIdentityConstraint2ValueStoreMap.put(toHash, keyRefValueStore);
                        } else {
                            keyRefValueStore.clear();
                        }
                        fValueStores.addElement(keyRefValueStore);
                        activateSelectorFor(icArray[i]);
                        break;
                }
            }
        } public ValueStoreBase getValueStoreFor(IdentityConstraint id, int initialDepth) {
            fLocalId.fDepth = initialDepth;
            fLocalId.fId = id;
            return fIdentityConstraint2ValueStoreMap.get(fLocalId);
        } public ValueStoreBase getGlobalValueStoreFor(IdentityConstraint id) {
            return fGlobalIDConstraintMap.get(id);
        } public void transplant(IdentityConstraint id, int initialDepth) {
            fLocalId.fDepth = initialDepth;
            fLocalId.fId = id;
            ValueStoreBase newVals = fIdentityConstraint2ValueStoreMap.get(fLocalId);
            if (id.getCategory() == IdentityConstraint.IC_KEYREF)
                return;
            ValueStoreBase currVals = fGlobalIDConstraintMap.get(id);
            if (currVals != null) {
                currVals.append(newVals);
                fGlobalIDConstraintMap.put(id, currVals);
            } else
                fGlobalIDConstraintMap.put(id, newVals);

        } public void endDocument() {

            int count = fValueStores.size();
            for (int i = 0; i < count; i++) {
                ValueStoreBase valueStore = (ValueStoreBase) fValueStores.elementAt(i);
                valueStore.endDocument();
            }

        } public String toString() {
            String s = super.toString();
            int index1 = s.lastIndexOf('$');
            if (index1 != -1) {
                return s.substring(index1 + 1);
            }
            int index2 = s.lastIndexOf('.');
            if (index2 != -1) {
                return s.substring(index2 + 1);
            }
            return s;
        } } protected class LocalIDKey {

        public IdentityConstraint fId;
        public int fDepth;

        public LocalIDKey() {
        }

        public LocalIDKey(IdentityConstraint id, int depth) {
            fId = id;
            fDepth = depth;
        } public int hashCode() {
            return fId.hashCode() + fDepth;
        }

        public boolean equals(Object localIDKey) {
            if (localIDKey instanceof LocalIDKey) {
                LocalIDKey lIDKey = (LocalIDKey) localIDKey;
                return (lIDKey.fId == fId && lIDKey.fDepth == fDepth);
            }
            return false;
        }
    } protected static final class ShortVector {

        private int fLength;


        private short[] fData;

        public ShortVector() {}

        public ShortVector(int initialCapacity) {
            fData = new short[initialCapacity];
        }

        public int length() {
            return fLength;
        }


        public void add(short value) {
            ensureCapacity(fLength + 1);
            fData[fLength++] = value;
        }


        public short valueAt(int position) {
            return fData[position];
        }


        public void clear() {
            fLength = 0;
        }


        public boolean contains(short value) {
            for (int i = 0; i < fLength; ++i) {
                if (fData[i] == value) {
                    return true;
                }
            }
            return false;
        }

        private void ensureCapacity(int size) {
            if (fData == null) {
                fData = new short[8];
            }
            else if (fData.length <= size) {
                short[] newdata = new short[fData.length * 2];
                System.arraycopy(fData, 0, newdata, 0, fData.length);
                fData = newdata;
            }
        }
    }

}