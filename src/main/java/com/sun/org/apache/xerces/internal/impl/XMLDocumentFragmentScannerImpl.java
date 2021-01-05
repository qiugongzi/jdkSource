



package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.XMLAttributesIteratorImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.Limit;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.XMLBufferListener;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
import java.io.EOFException;
import java.io.IOException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.XMLEvent;



public class XMLDocumentFragmentScannerImpl
        extends XMLScanner
        implements XMLDocumentScanner, XMLComponent, XMLEntityHandler, XMLBufferListener {

    protected int fElementAttributeLimit, fXMLNameLimit;


    protected ExternalSubsetResolver fExternalSubsetResolver;

    protected static final int SCANNER_STATE_START_OF_MARKUP = 21;


    protected static final int SCANNER_STATE_CONTENT = 22;


    protected static final int SCANNER_STATE_PI = 23;


    protected static final int SCANNER_STATE_DOCTYPE = 24;


    protected static final int SCANNER_STATE_XML_DECL = 25;


    protected static final int SCANNER_STATE_ROOT_ELEMENT = 26;


    protected static final int SCANNER_STATE_COMMENT = 27;


    protected static final int SCANNER_STATE_REFERENCE = 28;

    protected static final int SCANNER_STATE_ATTRIBUTE = 29;

    protected static final int SCANNER_STATE_ATTRIBUTE_VALUE = 30;


    protected static final int SCANNER_STATE_END_OF_INPUT = 33;


    protected static final int SCANNER_STATE_TERMINATED = 34;


    protected static final int SCANNER_STATE_CDATA = 35;


    protected static final int SCANNER_STATE_TEXT_DECL = 36;


    protected static final int SCANNER_STATE_CHARACTER_DATA = 37;

    protected static final int SCANNER_STATE_START_ELEMENT_TAG = 38;

    protected static final int SCANNER_STATE_END_ELEMENT_TAG = 39;

    protected static final int SCANNER_STATE_CHAR_REFERENCE = 40;
    protected static final int SCANNER_STATE_BUILT_IN_REFS = 41;

    protected static final String NOTIFY_BUILTIN_REFS =
            Constants.XERCES_FEATURE_PREFIX + Constants.NOTIFY_BUILTIN_REFS_FEATURE;


    protected static final String ENTITY_RESOLVER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;


    protected static final String STANDARD_URI_CONFORMANT =
            Constants.XERCES_FEATURE_PREFIX +Constants.STANDARD_URI_CONFORMANT_FEATURE;


    private static final String XML_SECURITY_PROPERTY_MANAGER =
            Constants.XML_SECURITY_PROPERTY_MANAGER;


    final static String EXTERNAL_ACCESS_DEFAULT = Constants.EXTERNAL_ACCESS_DEFAULT;

    private static final String[] RECOGNIZED_FEATURES = {
                NAMESPACES,
                VALIDATION,
                NOTIFY_BUILTIN_REFS,
                NOTIFY_CHAR_REFS,
                Constants.STAX_REPORT_CDATA_EVENT
    };


    private static final Boolean[] FEATURE_DEFAULTS = {
                Boolean.TRUE,
                null,
                Boolean.FALSE,
                Boolean.FALSE,
                Boolean.TRUE
    };


    private static final String[] RECOGNIZED_PROPERTIES = {
        SYMBOL_TABLE,
                ERROR_REPORTER,
                ENTITY_MANAGER,
                XML_SECURITY_PROPERTY_MANAGER
    };


    private static final Object[] PROPERTY_DEFAULTS = {
                null,
                null,
                null,
                null
    };

    private static final char [] cdata = {'[','C','D','A','T','A','['};
    static final char [] xmlDecl = {'<','?','x','m','l'};
    private static final boolean DEBUG_SCANNER_STATE = false;


    private static final boolean DEBUG_DISPATCHER = false;


    protected static final boolean DEBUG_START_END_ELEMENT = false;



    protected static final boolean DEBUG_NEXT = false ;


    protected static final boolean DEBUG = false;
    protected static final boolean DEBUG_COALESCE = false;
    protected XMLDocumentHandler fDocumentHandler;
    protected int fScannerLastState ;


    protected XMLEntityStorage fEntityStore;


    protected int[] fEntityStack = new int[4];


    protected int fMarkupDepth;

    protected boolean fEmptyElement ;

    protected boolean fReadingAttributes = false;


    protected int fScannerState;


    protected boolean fInScanContent = false;
    protected boolean fLastSectionWasCData = false;
    protected boolean fLastSectionWasEntityReference = false;
    protected boolean fLastSectionWasCharacterData = false;


    protected boolean fHasExternalDTD;


    protected boolean fStandaloneSet;
    protected boolean fStandalone;
    protected String fVersion;

    protected QName fCurrentElement;


    protected ElementStack fElementStack = new ElementStack();
    protected ElementStack2 fElementStack2 = new ElementStack2();

    protected String fPITarget ;

    protected XMLString fPIData  = new XMLString();

    protected boolean fNotifyBuiltInRefs = false;

    protected boolean fSupportDTD = true;
    protected boolean fReplaceEntityReferences = true;
    protected boolean fSupportExternalEntities = false;
    protected boolean fReportCdataEvent = false ;
    protected boolean fIsCoalesce = false ;
    protected String fDeclaredEncoding =  null;

    protected boolean fDisallowDoctype = false;


    protected String fAccessExternalDTD = EXTERNAL_ACCESS_DEFAULT;


    protected boolean fStrictURI;

    protected Driver fDriver;


    protected Driver fContentDriver = createContentDriver();

    protected QName fElementQName = new QName();


    protected QName fAttributeQName = new QName();


    protected XMLAttributesIteratorImpl fAttributes = new XMLAttributesIteratorImpl();



    protected XMLString fTempString = new XMLString();


    protected XMLString fTempString2 = new XMLString();


    private String[] fStrings = new String[3];


    protected XMLStringBuffer fStringBuffer = new XMLStringBuffer();


    protected XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();



    protected XMLStringBuffer fContentBuffer = new XMLStringBuffer();


    private final char[] fSingleChar = new char[1];
    private String fCurrentEntityName = null;

    protected boolean fScanToEnd = false;

    protected DTDGrammarUtil dtdGrammarUtil= null;

    protected boolean fAddDefaultAttr = false;

    protected boolean foundBuiltInRefs = false;


    static final short MAX_DEPTH_LIMIT = 5 ;
    static final short ELEMENT_ARRAY_LENGTH = 200 ;
    static final short MAX_POINTER_AT_A_DEPTH = 4 ;
    static final boolean DEBUG_SKIP_ALGORITHM = false;
    String [] fElementArray = new String[ELEMENT_ARRAY_LENGTH] ;
    short fLastPointerLocation = 0 ;
    short fElementPointer = 0 ;
    short [] [] fPointerInfo = new short[MAX_DEPTH_LIMIT] [MAX_POINTER_AT_A_DEPTH] ;
    protected String fElementRawname ;
    protected boolean fShouldSkip = false;
    protected boolean fAdd = false ;
    protected boolean fSkip = false;


    private Augmentations fTempAugmentations = null;
    public XMLDocumentFragmentScannerImpl() {
    } public void setInputSource(XMLInputSource inputSource) throws IOException {
        fEntityManager.setEntityHandler(this);
        fEntityManager.startEntity(false, "$fragment$", inputSource, false, true);
        } public boolean scanDocument(boolean complete)
    throws IOException, XNIException {

        fEntityManager.setEntityHandler(this);
        int event = next();
        do {
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT :
                    break;
                case XMLStreamConstants.START_ELEMENT :
                    break;
                case XMLStreamConstants.CHARACTERS :
                    fEntityScanner.checkNodeCount(fEntityScanner.fCurrentEntity);
                    fDocumentHandler.characters(getCharacterData(),null);
                    break;
                case XMLStreamConstants.SPACE:
                    break;
                case XMLStreamConstants.ENTITY_REFERENCE :
                    fEntityScanner.checkNodeCount(fEntityScanner.fCurrentEntity);
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION :
                    fEntityScanner.checkNodeCount(fEntityScanner.fCurrentEntity);
                    fDocumentHandler.processingInstruction(getPITarget(),getPIData(),null);
                    break;
                case XMLStreamConstants.COMMENT :
                    fEntityScanner.checkNodeCount(fEntityScanner.fCurrentEntity);
                    fDocumentHandler.comment(getCharacterData(),null);
                    break;
                case XMLStreamConstants.DTD :
                    break;
                case XMLStreamConstants.CDATA:
                    fEntityScanner.checkNodeCount(fEntityScanner.fCurrentEntity);
                    fDocumentHandler.startCDATA(null);
                    fDocumentHandler.characters(getCharacterData(),null);
                    fDocumentHandler.endCDATA(null);
                    break;
                case XMLStreamConstants.NOTATION_DECLARATION :
                    break;
                case XMLStreamConstants.ENTITY_DECLARATION :
                    break;
                case XMLStreamConstants.NAMESPACE :
                    break;
                case XMLStreamConstants.ATTRIBUTE :
                    break;
                case XMLStreamConstants.END_ELEMENT :
                    break;
                default :
                    throw new InternalError("processing event: " + event);

            }
            event = next();
            } while (event!=XMLStreamConstants.END_DOCUMENT && complete);

        if(event == XMLStreamConstants.END_DOCUMENT) {
            fDocumentHandler.endDocument(null);
            return false;
        }

        return true;

    } public com.sun.org.apache.xerces.internal.xni.QName getElementQName(){
        if(fScannerLastState == XMLEvent.END_ELEMENT){
            fElementQName.setValues(fElementStack.getLastPoppedElement());
        }
        return fElementQName ;
    }



    public int next() throws IOException, XNIException {
        return fDriver.next();
    }

    public void reset(XMLComponentManager componentManager)
    throws XMLConfigurationException {

        super.reset(componentManager);

        fReportCdataEvent = componentManager.getFeature(Constants.STAX_REPORT_CDATA_EVENT, true);
        fSecurityManager = (XMLSecurityManager)componentManager.getProperty(Constants.SECURITY_MANAGER, null);
        fNotifyBuiltInRefs = componentManager.getFeature(NOTIFY_BUILTIN_REFS, false);

        Object resolver = componentManager.getProperty(ENTITY_RESOLVER, null);
        fExternalSubsetResolver = (resolver instanceof ExternalSubsetResolver) ?
                (ExternalSubsetResolver) resolver : null;

        fReadingAttributes = false;
        fSupportExternalEntities = true;
        fReplaceEntityReferences = true;
        fIsCoalesce = false;

        setScannerState(SCANNER_STATE_CONTENT);
        setDriver(fContentDriver);

        XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)
                componentManager.getProperty(XML_SECURITY_PROPERTY_MANAGER, null);
        fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);

        fStrictURI = componentManager.getFeature(STANDARD_URI_CONFORMANT, false);

        resetCommon();
        } public void reset(PropertyManager propertyManager){

        super.reset(propertyManager);

        fNamespaces = ((Boolean)propertyManager.getProperty(XMLInputFactory.IS_NAMESPACE_AWARE)).booleanValue();
        fNotifyBuiltInRefs = false ;

        Boolean bo = (Boolean)propertyManager.getProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES);
        fReplaceEntityReferences = bo.booleanValue();
        bo = (Boolean)propertyManager.getProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES);
        fSupportExternalEntities = bo.booleanValue();
        Boolean cdata = (Boolean)propertyManager.getProperty(Constants.ZEPHYR_PROPERTY_PREFIX + Constants.STAX_REPORT_CDATA_EVENT) ;
        if(cdata != null)
            fReportCdataEvent = cdata.booleanValue() ;
        Boolean coalesce = (Boolean)propertyManager.getProperty(XMLInputFactory.IS_COALESCING) ;
        if(coalesce != null)
            fIsCoalesce = coalesce.booleanValue();
        fReportCdataEvent = fIsCoalesce ? false : (fReportCdataEvent && true) ;
        fReplaceEntityReferences = fIsCoalesce ? true : fReplaceEntityReferences;
        XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)
                propertyManager.getProperty(XML_SECURITY_PROPERTY_MANAGER);
        fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);

        fSecurityManager = (XMLSecurityManager)propertyManager.getProperty(Constants.SECURITY_MANAGER);
        resetCommon();
    } void resetCommon() {
        fMarkupDepth = 0;
        fCurrentElement = null;
        fElementStack.clear();
        fHasExternalDTD = false;
        fStandaloneSet = false;
        fStandalone = false;
        fInScanContent = false;
        fShouldSkip = false;
        fAdd = false;
        fSkip = false;

        fEntityStore = fEntityManager.getEntityStore();
        dtdGrammarUtil = null;

        if (fSecurityManager != null) {
            fElementAttributeLimit = fSecurityManager.getLimit(XMLSecurityManager.Limit.ELEMENT_ATTRIBUTE_LIMIT);
            fXMLNameLimit = fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT);
        } else {
            fElementAttributeLimit = 0;
            fXMLNameLimit = XMLSecurityManager.Limit.MAX_NAME_LIMIT.defaultValue();
        }
        fLimitAnalyzer = fEntityManager.fLimitAnalyzer;
    }


    public String[] getRecognizedFeatures() {
        return (String[])(RECOGNIZED_FEATURES.clone());
    } public void setFeature(String featureId, boolean state)
    throws XMLConfigurationException {

        super.setFeature(featureId, state);

        if (featureId.startsWith(Constants.XERCES_FEATURE_PREFIX)) {
            String feature = featureId.substring(Constants.XERCES_FEATURE_PREFIX.length());
            if (feature.equals(Constants.NOTIFY_BUILTIN_REFS_FEATURE)) {
                fNotifyBuiltInRefs = state;
            }
        }

    } public String[] getRecognizedProperties() {
        return (String[])(RECOGNIZED_PROPERTIES.clone());
    } public void setProperty(String propertyId, Object value)
    throws XMLConfigurationException {

        super.setProperty(propertyId, value);

        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
            final int suffixLength = propertyId.length() - Constants.XERCES_PROPERTY_PREFIX.length();
            if (suffixLength == Constants.ENTITY_MANAGER_PROPERTY.length() &&
                    propertyId.endsWith(Constants.ENTITY_MANAGER_PROPERTY)) {
                fEntityManager = (XMLEntityManager)value;
                return;
            }
            if (suffixLength == Constants.ENTITY_RESOLVER_PROPERTY.length() &&
                    propertyId.endsWith(Constants.ENTITY_RESOLVER_PROPERTY)) {
                fExternalSubsetResolver = (value instanceof ExternalSubsetResolver) ?
                    (ExternalSubsetResolver) value : null;
                return;
            }
        }


                if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
            String property = propertyId.substring(Constants.XERCES_PROPERTY_PREFIX.length());
            if (property.equals(Constants.ENTITY_MANAGER_PROPERTY)) {
                fEntityManager = (XMLEntityManager)value;
            }
            return;
        }

        if (propertyId.equals(XML_SECURITY_PROPERTY_MANAGER))
        {
            XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)value;
            fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
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
        } public XMLDocumentHandler getDocumentHandler(){
        return fDocumentHandler;
    }

    public void startEntity(String name,
            XMLResourceIdentifier identifier,
            String encoding, Augmentations augs) throws XNIException {

        if (fEntityDepth == fEntityStack.length) {
            int[] entityarray = new int[fEntityStack.length * 2];
            System.arraycopy(fEntityStack, 0, entityarray, 0, fEntityStack.length);
            fEntityStack = entityarray;
        }
        fEntityStack[fEntityDepth] = fMarkupDepth;

        super.startEntity(name, identifier, encoding, augs);

        if(fStandalone && fEntityStore.isEntityDeclInExternalSubset(name)) {
            reportFatalError("MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE",
                    new Object[]{name});
        }


        if (fDocumentHandler != null && !fScanningAttribute) {
            if (!name.equals("[xml]")) {
                fDocumentHandler.startGeneralEntity(name, identifier, encoding, augs);
            }
        }

    } public void endEntity(String name, Augmentations augs) throws IOException, XNIException {


        super.endEntity(name, augs);

        if (fMarkupDepth != fEntityStack[fEntityDepth]) {
            reportFatalError("MarkupEntityMismatch", null);
        }


    protected Driver createContentDriver() {
        return new FragmentContentDriver();
    } protected void scanXMLDeclOrTextDecl(boolean scanningTextDecl)
    throws IOException, XNIException {

        super.scanXMLDeclOrTextDecl(scanningTextDecl, fStrings);
        fMarkupDepth--;

        String version = fStrings[0];
        String encoding = fStrings[1];
        String standalone = fStrings[2];
        fDeclaredEncoding = encoding;
        fStandaloneSet = standalone != null;
        fStandalone = fStandaloneSet && standalone.equals("yes");
        fEntityManager.setStandalone(fStandalone);


        if (fDocumentHandler != null) {
            if (scanningTextDecl) {
                fDocumentHandler.textDecl(version, encoding, null);
            } else {
                fDocumentHandler.xmlDecl(version, encoding, standalone, null);
            }
        }

        if(version != null){
            fEntityScanner.setVersion(version);
            fEntityScanner.setXMLVersion(version);
        }
        if (encoding != null && !fEntityScanner.getCurrentEntity().isEncodingExternallySpecified()) {
             fEntityScanner.setEncoding(encoding);
        }

    } public String getPITarget(){
        return fPITarget ;
    }

    public XMLStringBuffer getPIData(){
        return fContentBuffer ;
    }

    public XMLString getCharacterData(){
        if(fUsebuffer){
            return fContentBuffer ;
        }else{
            return fTempString;
        }

    }



    protected void scanPIData(String target, XMLStringBuffer data)
    throws IOException, XNIException {

        super.scanPIData(target, data);

        fPITarget = target ;

        fMarkupDepth--;

    } protected void scanComment() throws IOException, XNIException {
        fContentBuffer.clear();
        scanComment(fContentBuffer);
        fUsebuffer = true;
        fMarkupDepth--;

    } public String getComment(){
        return fContentBuffer.toString();
    }

    void addElement(String rawname){
        if(fElementPointer < ELEMENT_ARRAY_LENGTH){
            fElementArray[fElementPointer] = rawname ;
            if(DEBUG_SKIP_ALGORITHM){
                StringBuffer sb = new StringBuffer() ;
                sb.append(" Storing element information ") ;
                sb.append(" fElementPointer = " + fElementPointer) ;
                sb.append(" fElementRawname = " + fElementQName.rawname) ;
                sb.append(" fElementStack.fDepth = " + fElementStack.fDepth);
                System.out.println(sb.toString()) ;
            }

            if(fElementStack.fDepth < MAX_DEPTH_LIMIT){
                short column = storePointerForADepth(fElementPointer);
                if(column > 0){
                    short pointer = getElementPointer((short)fElementStack.fDepth, (short)(column - 1) );
                    if(rawname == fElementArray[pointer]){
                        fShouldSkip = true ;
                        fLastPointerLocation = pointer ;
                        resetPointer((short)fElementStack.fDepth , column) ;
                        fElementArray[fElementPointer] = null ;
                        return ;
                    }else{
                        fShouldSkip = false ;
                    }
                }
            }
            fElementPointer++ ;
        }
    }


    void resetPointer(short depth, short column){
        fPointerInfo[depth] [column] = (short)0;
    }

    short storePointerForADepth(short elementPointer){
        short depth = (short) fElementStack.fDepth ;

        for(short i = 0 ; i < MAX_POINTER_AT_A_DEPTH ; i++){

            if(canStore(depth, i)){
                fPointerInfo[depth][i] = elementPointer ;
                if(DEBUG_SKIP_ALGORITHM){
                    StringBuffer sb = new StringBuffer() ;
                    sb.append(" Pointer information ") ;
                    sb.append(" fElementPointer = " + fElementPointer) ;
                    sb.append(" fElementStack.fDepth = " + fElementStack.fDepth);
                    sb.append(" column = " + i ) ;
                    System.out.println(sb.toString()) ;
                }
                return i;
            }
            }
        return -1 ;
    }

    boolean canStore(short depth, short column){
        return fPointerInfo[depth][column] == 0 ? true : false ;
    }


    short getElementPointer(short depth, short column){
        return fPointerInfo[depth][column] ;
    }

    boolean skipFromTheBuffer(String rawname) throws IOException{
        if(fEntityScanner.skipString(rawname)){
            char c = (char)fEntityScanner.peekChar() ;
            if( c == ' ' || c == '/' || c == '>'){
                fElementRawname = rawname ;
                return true ;
            } else{
                return false;
            }
        } else
            return false ;
    }

    boolean skipQElement(String rawname) throws IOException{

        final int c = fEntityScanner.getChar(rawname.length());
        if(XMLChar.isName(c)){
            return false;
        }else{
            return fEntityScanner.skipString(rawname);
        }
    }

    protected boolean skipElement() throws IOException {

        if(!fShouldSkip) return false ;

        if(fLastPointerLocation != 0){
            String rawname = fElementArray[fLastPointerLocation + 1] ;
            if(rawname != null && skipFromTheBuffer(rawname)){
                fLastPointerLocation++ ;
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("Element " + fElementRawname + " was SKIPPED at pointer location = " + fLastPointerLocation);
                }
                return true ;
            } else{
                fLastPointerLocation = 0 ;

            }
        }
        return fShouldSkip && skipElement((short)0);

    }

    boolean skipElement(short column) throws IOException {
        short depth = (short)fElementStack.fDepth ;

        if(depth > MAX_DEPTH_LIMIT){
            return fShouldSkip = false ;
        }
        for(short i = column ; i < MAX_POINTER_AT_A_DEPTH ; i++){
            short pointer = getElementPointer(depth , i ) ;

            if(pointer == 0){
                return fShouldSkip = false ;
            }

            if(fElementArray[pointer] != null && skipFromTheBuffer(fElementArray[pointer])){
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println();
                    System.out.println("Element " + fElementRawname + " was SKIPPED at depth = " + fElementStack.fDepth + " column = " + column );
                    System.out.println();
                }
                fLastPointerLocation = pointer ;
                return fShouldSkip = true ;
            }
        }
        return fShouldSkip = false ;
    }


    protected boolean scanStartElement()
    throws IOException, XNIException {

        if (DEBUG_START_END_ELEMENT) System.out.println( this.getClass().toString() + ">>> scanStartElement()");
        if(fSkip && !fAdd){
            QName name = fElementStack.getNext();

            if(DEBUG_SKIP_ALGORITHM){
                System.out.println("Trying to skip String = " + name.rawname);
            }

            fSkip = fEntityScanner.skipString(name.rawname);

            if(fSkip){
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("Element SUCESSFULLY skipped = " + name.rawname);
                }
                fElementStack.push();
                fElementQName = name;
            }else{
                fElementStack.reposition();
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("Element was NOT skipped, REPOSITIONING stack" );
                }
            }
        }

        if(!fSkip || fAdd){
            fElementQName = fElementStack.nextElement();
            if (fNamespaces) {
                fEntityScanner.scanQName(fElementQName, NameType.ELEMENTSTART);
            } else {
                String name = fEntityScanner.scanName(NameType.ELEMENTSTART);
                fElementQName.setValues(null, name, name, null);
            }

            if(DEBUG)System.out.println("Element scanned in start element is " + fElementQName.toString());
            if(DEBUG_SKIP_ALGORITHM){
                if(fAdd){
                    System.out.println("Elements are being ADDED -- elemet added is = " + fElementQName.rawname + " at count = " + fElementStack.fCount);
                }
            }

        }

        if(fAdd){
            fElementStack.matchElement(fElementQName);
        }


        fCurrentElement = fElementQName;

        String rawname = fElementQName.rawname;

        fEmptyElement = false;

        fAttributes.removeAllAttributes();

        checkDepth(rawname);
        if(!seekCloseOfStartTag()){
            fReadingAttributes = true;
            fAttributeCacheUsedCount =0;
            fStringBufferIndex =0;
            fAddDefaultAttr = true;
            do {
                scanAttribute(fAttributes);
                if (fSecurityManager != null && !fSecurityManager.isNoLimit(fElementAttributeLimit) &&
                        fAttributes.getLength() > fElementAttributeLimit){
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                                                 "ElementAttributeLimit",
                                                 new Object[]{rawname, fElementAttributeLimit },
                                                 XMLErrorReporter.SEVERITY_FATAL_ERROR );
                }

            } while (!seekCloseOfStartTag());
            fReadingAttributes=false;
        }

        if (fEmptyElement) {
            fMarkupDepth--;

            if (fMarkupDepth < fEntityStack[fEntityDepth - 1]) {
                reportFatalError("ElementEntityMismatch",
                        new Object[]{fCurrentElement.rawname});
            }
            if (fDocumentHandler != null) {
                fDocumentHandler.emptyElement(fElementQName, fAttributes, null);
            }

            fElementStack.popElement();

        } else {

            if(dtdGrammarUtil != null)
                dtdGrammarUtil.startElement(fElementQName, fAttributes);
            if(fDocumentHandler != null){
                fDocumentHandler.startElement(fElementQName, fAttributes, null);
            }
        }


        if (DEBUG_START_END_ELEMENT) System.out.println(this.getClass().toString() + "<<< scanStartElement(): "+fEmptyElement);
        return fEmptyElement;

    } protected boolean seekCloseOfStartTag() throws IOException, XNIException {
        boolean sawSpace = fEntityScanner.skipSpaces();

        final int c = fEntityScanner.peekChar();
        if (c == '>') {
            fEntityScanner.scanChar(null);
            return true;
        } else if (c == '/') {
            fEntityScanner.scanChar(null);
            if (!fEntityScanner.skipChar('>', NameType.ELEMENTEND)) {
                reportFatalError("ElementUnterminated",
                        new Object[]{fElementQName.rawname});
            }
            fEmptyElement = true;
            return true;
        } else if (!isValidNameStartChar(c) || !sawSpace) {
            if (!isValidNameStartHighSurrogate(c) || !sawSpace) {
                reportFatalError("ElementUnterminated",
                        new Object[]{fElementQName.rawname});
            }
        }

        return false;
    }

    public boolean hasAttributes(){
        return fAttributes.getLength() > 0 ? true : false ;
    }










    public XMLAttributesIteratorImpl getAttributeIterator(){
        if(dtdGrammarUtil != null && fAddDefaultAttr){
            dtdGrammarUtil.addDTDDefaultAttrs(fElementQName,fAttributes);
            fAddDefaultAttr = false;
        }
        return fAttributes;
    }


    public boolean standaloneSet(){
        return fStandaloneSet;
    }

    public boolean isStandAlone(){
        return fStandalone ;
    }


    protected void scanAttribute(XMLAttributes attributes)
    throws IOException, XNIException {
        if (DEBUG_START_END_ELEMENT) System.out.println(this.getClass().toString() +">>> scanAttribute()");

        if (fNamespaces) {
            fEntityScanner.scanQName(fAttributeQName, NameType.ATTRIBUTENAME);
        } else {
            String name = fEntityScanner.scanName(NameType.ATTRIBUTENAME);
            fAttributeQName.setValues(null, name, name, null);
        }

        fEntityScanner.skipSpaces();
        if (!fEntityScanner.skipChar('=', NameType.ATTRIBUTE)) {
            reportFatalError("EqRequiredInAttribute",
                new Object[] {fCurrentElement.rawname, fAttributeQName.rawname});
        }
        fEntityScanner.skipSpaces();

        int attIndex = 0 ;
        boolean isVC =  fHasExternalDTD && !fStandalone;
        XMLString tmpStr = getString();

        scanAttributeValue(tmpStr, fTempString2, fAttributeQName.rawname, attributes,
                attIndex, isVC, fCurrentElement.rawname, false);

        int oldLen = attributes.getLength();
        attIndex = attributes.addAttribute(fAttributeQName, XMLSymbols.fCDATASymbol, null);

        if (oldLen == attributes.getLength()) {
            reportFatalError("AttributeNotUnique",
                    new Object[]{fCurrentElement.rawname,
                            fAttributeQName.rawname});
        }

        attributes.setValue(attIndex, null, tmpStr);

        attributes.setSpecified(attIndex, true);

        if (DEBUG_START_END_ELEMENT) System.out.println(this.getClass().toString() +"<<< scanAttribute()");

    } protected int scanContent(XMLStringBuffer content) throws IOException, XNIException {
        fTempString.length = 0;
        int c = fEntityScanner.scanContent(fTempString);
        content.append(fTempString);
        fTempString.length = 0;
        if (c == '\r') {
            fEntityScanner.scanChar(null);
            content.append((char)c);
            c = -1;
        } else if (c == ']') {
            content.append((char)fEntityScanner.scanChar(null));
            fInScanContent = true;
            if (fEntityScanner.skipChar(']', null)) {
                content.append(']');
                while (fEntityScanner.skipChar(']', null)) {
                    content.append(']');
                }
                if (fEntityScanner.skipChar('>', null)) {
                    reportFatalError("CDEndInContent", null);
                }
            }
            fInScanContent = false;
            c = -1;
        }
        if (fDocumentHandler != null && content.length > 0) {
            }
        return c;

    } protected boolean scanCDATASection(XMLStringBuffer contentBuffer, boolean complete)
    throws IOException, XNIException {

        if (fDocumentHandler != null) {
            }

        while (true) {
            if (!fEntityScanner.scanData("]]>", contentBuffer)) {
                break ;

            } else {
                int c = fEntityScanner.peekChar();
                if (c != -1 && isInvalidLiteral(c)) {
                    if (XMLChar.isHighSurrogate(c)) {
                        scanSurrogates(contentBuffer);
                    } else {
                        reportFatalError("InvalidCharInCDSect",
                                new Object[]{Integer.toString(c,16)});
                                fEntityScanner.scanChar(null);
                    }
                }
                if (fDocumentHandler != null) {
                    }
            }
        }
        fMarkupDepth--;

        if (fDocumentHandler != null && contentBuffer.length > 0) {
            }

        if (fDocumentHandler != null) {
            }

        return true;

    } protected int scanEndElement() throws IOException, XNIException {
        if (DEBUG_START_END_ELEMENT) System.out.println(this.getClass().toString() +">>> scanEndElement()");

        QName endElementName = fElementStack.popElement();

        String rawname = endElementName.rawname;
        if(DEBUG)System.out.println("endElementName = " + endElementName.toString());
        if (!fEntityScanner.skipString(endElementName.rawname)) {
             reportFatalError("ETagRequired", new Object[]{rawname});
        }

        fEntityScanner.skipSpaces();
        if (!fEntityScanner.skipChar('>', NameType.ELEMENTEND)) {
            reportFatalError("ETagUnterminated",
                    new Object[]{rawname});
        }
        fMarkupDepth--;

        fMarkupDepth--;

        if (fMarkupDepth < fEntityStack[fEntityDepth - 1]) {
            reportFatalError("ElementEntityMismatch",
                    new Object[]{rawname});
        }

        if (fDocumentHandler != null ) {
            fDocumentHandler.endElement(endElementName, null);
        }
        if(dtdGrammarUtil != null)
            dtdGrammarUtil.endElement(endElementName);

        return fMarkupDepth;

    } protected void scanCharReference()
    throws IOException, XNIException {

        fStringBuffer2.clear();
        int ch = scanCharReferenceValue(fStringBuffer2, null);
        fMarkupDepth--;
        if (ch != -1) {
            if (fDocumentHandler != null) {
                if (fNotifyCharRefs) {
                    fDocumentHandler.startGeneralEntity(fCharRefLiteral, null, null, null);
                }
                Augmentations augs = null;
                if (fValidation && ch <= 0x20) {
                    if (fTempAugmentations != null) {
                        fTempAugmentations.removeAllItems();
                    }
                    else {
                        fTempAugmentations = new AugmentationsImpl();
                    }
                    augs = fTempAugmentations;
                    augs.putItem(Constants.CHAR_REF_PROBABLE_WS, Boolean.TRUE);
                }
                if (fNotifyCharRefs) {
                    fDocumentHandler.endGeneralEntity(fCharRefLiteral, null);
                }
            }
        }

    } protected void scanEntityReference(XMLStringBuffer content) throws IOException, XNIException {
        String name = fEntityScanner.scanName(NameType.REFERENCE);
        if (name == null) {
            reportFatalError("NameRequiredInReference", null);
            return;
        }
        if (!fEntityScanner.skipChar(';', NameType.REFERENCE)) {
            reportFatalError("SemicolonRequiredInReference", new Object []{name});
        }
        if (fEntityStore.isUnparsedEntity(name)) {
            reportFatalError("ReferenceToUnparsedEntity", new Object[]{name});
        }
        fMarkupDepth--;
        fCurrentEntityName = name;

        if (name == fAmpSymbol) {
            handleCharacter('&', fAmpSymbol, content);
            fScannerState = SCANNER_STATE_BUILT_IN_REFS;
            return ;
        } else if (name == fLtSymbol) {
            handleCharacter('<', fLtSymbol, content);
            fScannerState = SCANNER_STATE_BUILT_IN_REFS;
            return ;
        } else if (name == fGtSymbol) {
            handleCharacter('>', fGtSymbol, content);
            fScannerState = SCANNER_STATE_BUILT_IN_REFS;
            return ;
        } else if (name == fQuotSymbol) {
            handleCharacter('"', fQuotSymbol, content);
            fScannerState = SCANNER_STATE_BUILT_IN_REFS;
            return ;
        } else if (name == fAposSymbol) {
            handleCharacter('\'', fAposSymbol, content);
            fScannerState = SCANNER_STATE_BUILT_IN_REFS;
            return ;
        }

        boolean isEE = fEntityStore.isExternalEntity(name);
        if((isEE && !fSupportExternalEntities) || (!isEE && !fReplaceEntityReferences) || foundBuiltInRefs){
            fScannerState = SCANNER_STATE_REFERENCE;
            return ;
        }
        if (!fEntityStore.isDeclaredEntity(name)) {
            if (!fSupportDTD && fReplaceEntityReferences) {
                reportFatalError("EntityNotDeclared", new Object[]{name});
                return;
            }
            if ( fHasExternalDTD && !fStandalone) {
                if (fValidation)
                    fErrorReporter.reportError(fEntityScanner, XMLMessageFormatter.XML_DOMAIN,"EntityNotDeclared",
                            new Object[]{name}, XMLErrorReporter.SEVERITY_ERROR);
            } else
                reportFatalError("EntityNotDeclared", new Object[]{name});
        }
        fEntityManager.startEntity(true, name, false);
        } void checkDepth(String elementName) {
        fLimitAnalyzer.addValue(Limit.MAX_ELEMENT_DEPTH_LIMIT, elementName, fElementStack.fDepth);
        if (fSecurityManager.isOverLimit(Limit.MAX_ELEMENT_DEPTH_LIMIT,fLimitAnalyzer)) {
            fSecurityManager.debugPrint(fLimitAnalyzer);
            reportFatalError("MaxElementDepthLimit", new Object[]{elementName,
                fLimitAnalyzer.getTotalValue(Limit.MAX_ELEMENT_DEPTH_LIMIT),
                fSecurityManager.getLimit(Limit.MAX_ELEMENT_DEPTH_LIMIT),
                "maxElementDepth"});
        }
    }


    private void handleCharacter(char c, String entity, XMLStringBuffer content) throws XNIException {
        foundBuiltInRefs = true;
        checkEntityLimit(false, fEntityScanner.fCurrentEntity.name, 1);
        content.append(c);
        if (fDocumentHandler != null) {
            fSingleChar[0] = c;
            if (fNotifyBuiltInRefs) {
                fDocumentHandler.startGeneralEntity(entity, null, null, null);
            }
            fTempString.setValues(fSingleChar, 0, 1);
            if (fNotifyBuiltInRefs) {
                fDocumentHandler.endGeneralEntity(entity, null);
            }
        }
    } protected final void setScannerState(int state) {

        fScannerState = state;
        if (DEBUG_SCANNER_STATE) {
            System.out.print("### setScannerState: ");
            System.out.print(getScannerStateName(state));
            System.out.println();
        }

    } protected final void setDriver(Driver driver) {
        fDriver = driver;
        if (DEBUG_DISPATCHER) {
            System.out.print("%%% setDriver: ");
            System.out.print(getDriverName(driver));
            System.out.println();
        }
    }

    protected String getScannerStateName(int state) {

        switch (state) {
            case SCANNER_STATE_DOCTYPE: return "SCANNER_STATE_DOCTYPE";
            case SCANNER_STATE_ROOT_ELEMENT: return "SCANNER_STATE_ROOT_ELEMENT";
            case SCANNER_STATE_START_OF_MARKUP: return "SCANNER_STATE_START_OF_MARKUP";
            case SCANNER_STATE_COMMENT: return "SCANNER_STATE_COMMENT";
            case SCANNER_STATE_PI: return "SCANNER_STATE_PI";
            case SCANNER_STATE_CONTENT: return "SCANNER_STATE_CONTENT";
            case SCANNER_STATE_REFERENCE: return "SCANNER_STATE_REFERENCE";
            case SCANNER_STATE_END_OF_INPUT: return "SCANNER_STATE_END_OF_INPUT";
            case SCANNER_STATE_TERMINATED: return "SCANNER_STATE_TERMINATED";
            case SCANNER_STATE_CDATA: return "SCANNER_STATE_CDATA";
            case SCANNER_STATE_TEXT_DECL: return "SCANNER_STATE_TEXT_DECL";
            case SCANNER_STATE_ATTRIBUTE: return "SCANNER_STATE_ATTRIBUTE";
            case SCANNER_STATE_ATTRIBUTE_VALUE: return "SCANNER_STATE_ATTRIBUTE_VALUE";
            case SCANNER_STATE_START_ELEMENT_TAG: return "SCANNER_STATE_START_ELEMENT_TAG";
            case SCANNER_STATE_END_ELEMENT_TAG: return "SCANNER_STATE_END_ELEMENT_TAG";
            case SCANNER_STATE_CHARACTER_DATA: return "SCANNER_STATE_CHARACTER_DATA" ;
        }

        return "??? ("+state+')';

    } public String getEntityName(){
        return fCurrentEntityName;
    }


    public String getDriverName(Driver driver) {

        if (DEBUG_DISPATCHER) {
            if (driver != null) {
                String name = driver.getClass().getName();
                int index = name.lastIndexOf('.');
                if (index != -1) {
                    name = name.substring(index + 1);
                    index = name.lastIndexOf('$');
                    if (index != -1) {
                        name = name.substring(index + 1);
                    }
                }
                return name;
            }
        }
        return "null";

    } String checkAccess(String systemId, String allowedProtocols) throws IOException {
        String baseSystemId = fEntityScanner.getBaseSystemId();
        String expandedSystemId = XMLEntityManager.expandSystemId(systemId, baseSystemId, fStrictURI);
        return SecuritySupport.checkAccess(expandedSystemId, allowedProtocols, Constants.ACCESS_EXTERNAL_ALL);
    }

    protected static final class Element {

        public QName qname;

        public char[] fRawname;


        public Element next;

        public Element(QName qname, Element next) {
            this.qname.setValues(qname);
            this.fRawname = qname.rawname.toCharArray();
            this.next = next;
        }

    } protected class ElementStack2 {

        protected QName [] fQName = new QName[20];

        protected int fDepth;
        protected int fCount;
        protected int fPosition;
        protected int fMark;

        protected int fLastDepth ;

        public ElementStack2() {
            for (int i = 0; i < fQName.length; i++) {
                fQName[i] = new QName();
            }
            fMark = fPosition = 1;
        } public void resize(){

            int oldLength = fQName.length;
            QName [] tmp = new QName[oldLength * 2];
            System.arraycopy(fQName, 0, tmp, 0, oldLength);
            fQName = tmp;

            for (int i = oldLength; i < fQName.length; i++) {
                fQName[i] = new QName();
            }

        }


        public boolean matchElement(QName element) {
            if(DEBUG_SKIP_ALGORITHM){
                System.out.println("fLastDepth = " + fLastDepth);
                System.out.println("fDepth = " + fDepth);
            }
            boolean match = false;
            if(fLastDepth > fDepth && fDepth <= 2){
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("Checking if the elements match " + element.rawname + " , " + fQName[fDepth].rawname);
                }
                if(element.rawname == fQName[fDepth].rawname){
                    fAdd = false;
                    fMark = fDepth - 1;
                    fPosition = fMark + 1 ;
                    match = true;
                    --fCount;
                    if(DEBUG_SKIP_ALGORITHM){
                        System.out.println("fAdd FALSE -- NOW ELEMENT SHOULD NOT BE ADDED");
                        System.out.println("fMark = " + fMark);
                        System.out.println("fPosition = " + fPosition);
                        System.out.println("fDepth = " + fDepth);
                        System.out.println("fCount = " + fCount);
                    }
                }else{
                    fAdd = true;
                    if(DEBUG_SKIP_ALGORITHM)System.out.println("fAdd is " + fAdd);
                }
            }
            fLastDepth = fDepth++;
            return match;
        } public QName nextElement() {

            if (fCount == fQName.length) {
                fShouldSkip = false;
                fAdd = false;
                if(DEBUG_SKIP_ALGORITHM)System.out.println("SKIPPING STOPPED, fShouldSkip = " + fShouldSkip);
                return fQName[--fCount];
            }
            if(DEBUG_SKIP_ALGORITHM){
                System.out.println("fCount = " + fCount);
            }
            return fQName[fCount++];

        }


        public QName getNext(){
            if(fPosition == fCount){
                fPosition = fMark;
            }
            return fQName[fPosition++];
        }


        public int popElement(){
            return fDepth--;
        }



        public void clear() {
            fLastDepth = 0;
            fDepth = 0;
            fCount = 0 ;
            fPosition = fMark = 1;
        } } protected class ElementStack {

        protected QName[] fElements;
        protected int []  fInt = new int[20];


        protected int fDepth;
        protected int fCount;
        protected int fPosition;
        protected int fMark;

        protected int fLastDepth ;

        public ElementStack() {
            fElements = new QName[20];
            for (int i = 0; i < fElements.length; i++) {
                fElements[i] = new QName();
            }
        } public QName pushElement(QName element) {
            if (fDepth == fElements.length) {
                QName[] array = new QName[fElements.length * 2];
                System.arraycopy(fElements, 0, array, 0, fDepth);
                fElements = array;
                for (int i = fDepth; i < fElements.length; i++) {
                    fElements[i] = new QName();
                }
            }
            fElements[fDepth].setValues(element);
            return fElements[fDepth++];
        } public QName getNext(){
            if(fPosition == fCount){
                fPosition = fMark;
            }
            if(DEBUG_SKIP_ALGORITHM){
                System.out.println("Element at fPosition = " + fPosition + " is " + fElements[fPosition].rawname);
            }
            return fElements[fPosition];
        }


        public void push(){

            fInt[++fDepth] = fPosition++;
        }


        public boolean matchElement(QName element) {
            boolean match = false;
            if(fLastDepth > fDepth && fDepth <= 3){
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("----------ENTERED THE LOOP WHERE WE CHECK FOR MATCHING OF ELMENT-----");
                    System.out.println("Depth = " + fDepth + " Checking if INCOMING element " + element.rawname + " match STORED ELEMENT " + fElements[fDepth - 1].rawname);
                }
                if(element.rawname == fElements[fDepth - 1].rawname){
                    fAdd = false;
                    fMark = fDepth - 1;
                    fPosition = fMark;
                    match = true;
                    --fCount;
                    if(DEBUG_SKIP_ALGORITHM){
                        System.out.println("NOW ELEMENT SHOULD NOT BE ADDED, fAdd is set to false");
                        System.out.println("fMark = " + fMark);
                        System.out.println("fPosition = " + fPosition);
                        System.out.println("fDepth = " + fDepth);
                        System.out.println("fCount = " + fCount);
                        System.out.println("---------MATCH SUCEEDED-----------------");
                        System.out.println("");
                    }
                }else{
                    fAdd = true;
                    if(DEBUG_SKIP_ALGORITHM)System.out.println("fAdd is " + fAdd);
                }
            }
            if(match){
                fInt[fDepth] = fPosition++;
            } else{
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("At depth = " + fDepth + "array position is = " + (fCount - 1));
                }
                fInt[fDepth] = fCount - 1;
            }

            if (fCount == fElements.length) {
                fSkip = false;
                fAdd = false;
                reposition();
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("ALL THE ELMENTS IN ARRAY HAVE BEEN FILLED");
                    System.out.println("REPOSITIONING THE STACK");
                    System.out.println("-----------SKIPPING STOPPED----------");
                    System.out.println("");
                }
                return false;
            }
            if(DEBUG_SKIP_ALGORITHM){
                if(match){
                    System.out.println("Storing fPosition = " + fInt[fDepth] + " at fDepth = " + fDepth);
                }else{
                    System.out.println("Storing fCount = " + fInt[fDepth] + " at fDepth = " + fDepth);
                }
            }
            fLastDepth = fDepth;
            return match;
        } public QName nextElement() {
            if(fSkip){
                fDepth++;
                return fElements[fCount++];
            } else if (fDepth == fElements.length) {
                QName[] array = new QName[fElements.length * 2];
                System.arraycopy(fElements, 0, array, 0, fDepth);
                fElements = array;
                for (int i = fDepth; i < fElements.length; i++) {
                    fElements[i] = new QName();
                }
            }

            return fElements[fDepth++];

        } public QName popElement() {
            if(fSkip || fAdd ){
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("POPPING Element, at position " + fInt[fDepth] + " element at that count is = " + fElements[fInt[fDepth]].rawname);
                    System.out.println("");
                }
                return fElements[fInt[fDepth--]];
            } else{
                if(DEBUG_SKIP_ALGORITHM){
                    System.out.println("Retrieveing element at depth = " + fDepth + " is " + fElements[fDepth].rawname );
                }
                return fElements[--fDepth] ;
            }
            } public void reposition(){
            for( int i = 2 ; i <= fDepth ; i++){
                fElements[i-1] = fElements[fInt[i]];
            }
            if(DEBUG_SKIP_ALGORITHM){
                for( int i = 0 ; i < fDepth ; i++){
                    System.out.println("fElements[" + i + "]" + " = " + fElements[i].rawname);
                }
            }
        }


        public void clear() {
            fDepth = 0;
            fLastDepth = 0;
            fCount = 0 ;
            fPosition = fMark = 1;

        } public QName getLastPoppedElement(){
            return fElements[fDepth];
        }
    } protected interface Driver {




        public int next() throws IOException, XNIException;

    } protected class FragmentContentDriver
            implements Driver {

        private void startOfMarkup() throws IOException {
            fMarkupDepth++;
            final int ch = fEntityScanner.peekChar();

            if (isValidNameStartChar(ch) || isValidNameStartHighSurrogate(ch)) {
                setScannerState(SCANNER_STATE_START_ELEMENT_TAG);
            } else {
                switch(ch){
                    case '?' :{
                        setScannerState(SCANNER_STATE_PI);
                        fEntityScanner.skipChar(ch, null);
                        break;
                    }
                    case '!' :{
                        fEntityScanner.skipChar(ch, null);
                        if (fEntityScanner.skipChar('-', null)) {
                            if (!fEntityScanner.skipChar('-', NameType.COMMENT)) {
                                reportFatalError("InvalidCommentStart",
                                        null);
                            }
                            setScannerState(SCANNER_STATE_COMMENT);
                        } else if (fEntityScanner.skipString(cdata)) {
                            setScannerState(SCANNER_STATE_CDATA );
                        } else if (!scanForDoctypeHook()) {
                            reportFatalError("MarkupNotRecognizedInContent",
                                    null);
                        }
                        break;
                    }
                    case '/' :{
                        setScannerState(SCANNER_STATE_END_ELEMENT_TAG);
                        fEntityScanner.skipChar(ch, NameType.ELEMENTEND);
                        break;
                    }
                    default :{
                        reportFatalError("MarkupNotRecognizedInContent", null);
                    }
                }
            }

        }private void startOfContent() throws IOException {
            if (fEntityScanner.skipChar('<', null)) {
                setScannerState(SCANNER_STATE_START_OF_MARKUP);
            } else if (fEntityScanner.skipChar('&', NameType.REFERENCE)) {
                setScannerState(SCANNER_STATE_REFERENCE) ; } else {
                setScannerState(SCANNER_STATE_CHARACTER_DATA);
            }
        }public void decideSubState() throws IOException {
            while( fScannerState == SCANNER_STATE_CONTENT || fScannerState == SCANNER_STATE_START_OF_MARKUP){

                switch (fScannerState) {

                    case SCANNER_STATE_CONTENT: {
                        startOfContent() ;
                        break;
                    }

                    case SCANNER_STATE_START_OF_MARKUP: {
                        startOfMarkup() ;
                        break;
                    }
                }
            }
        }public int next() throws IOException, XNIException {
            while (true) {
            try {
                if(DEBUG_NEXT){
                    System.out.println("NOW IN FragmentContentDriver");
                    System.out.println("Entering the FragmentContentDriver with = " + getScannerStateName(fScannerState));
                }

                switch (fScannerState) {
                    case SCANNER_STATE_CONTENT: {
                        final int ch = fEntityScanner.peekChar();
                        if (ch == '<') {
                            fEntityScanner.scanChar(null);
                            setScannerState(SCANNER_STATE_START_OF_MARKUP);
                        } else if (ch == '&') {
                            fEntityScanner.scanChar(NameType.REFERENCE);
                            setScannerState(SCANNER_STATE_REFERENCE) ; break;
                        } else {
                            setScannerState(SCANNER_STATE_CHARACTER_DATA);
                            break;
                        }
                    }

                    case SCANNER_STATE_START_OF_MARKUP: {
                        startOfMarkup();
                        break;
                    }}if(fIsCoalesce){
                    fUsebuffer = true ;
                    if(fLastSectionWasCharacterData){

                        if((fScannerState != SCANNER_STATE_CDATA) && (fScannerState != SCANNER_STATE_REFERENCE)
                        && (fScannerState != SCANNER_STATE_CHARACTER_DATA)){
                            fLastSectionWasCharacterData = false;
                            return XMLEvent.CHARACTERS;
                        }
                    }else if((fLastSectionWasCData || fLastSectionWasEntityReference)){
                        if((fScannerState != SCANNER_STATE_CDATA) && (fScannerState != SCANNER_STATE_REFERENCE)
                        && (fScannerState != SCANNER_STATE_CHARACTER_DATA)){

                            fLastSectionWasCData = false;
                            fLastSectionWasEntityReference = false;
                            return XMLEvent.CHARACTERS;
                        }
                    }
                }


                if(DEBUG_NEXT){
                    System.out.println("Actual scanner state set by decideSubState is = " + getScannerStateName(fScannerState));
                }

                switch(fScannerState){

                    case XMLEvent.START_DOCUMENT :
                        return XMLEvent.START_DOCUMENT;

                    case SCANNER_STATE_START_ELEMENT_TAG :{

                        fEmptyElement = scanStartElement() ;
                        if(fEmptyElement){
                            setScannerState(SCANNER_STATE_END_ELEMENT_TAG);
                        }else{
                            setScannerState(SCANNER_STATE_CONTENT);
                        }
                        return XMLEvent.START_ELEMENT ;
                    }

                    case SCANNER_STATE_CHARACTER_DATA: {
                        if(DEBUG_COALESCE){
                            System.out.println("fLastSectionWasCData = " + fLastSectionWasCData);
                            System.out.println("fIsCoalesce = " + fIsCoalesce);
                        }
                        fUsebuffer = fLastSectionWasEntityReference || fLastSectionWasCData || fLastSectionWasCharacterData ;

                        if( fIsCoalesce && (fLastSectionWasEntityReference || fLastSectionWasCData || fLastSectionWasCharacterData) ){
                            fLastSectionWasEntityReference = false;
                            fLastSectionWasCData = false;
                            fLastSectionWasCharacterData = true ;
                            fUsebuffer = true;
                        }else{
                            fContentBuffer.clear();
                        }

                        fTempString.length = 0;
                        int c = fEntityScanner.scanContent(fTempString);
                        if(DEBUG){
                            System.out.println("fTempString = " + fTempString);
                        }
                        if(fEntityScanner.skipChar('<', null)){
                            if(fEntityScanner.skipChar('/', NameType.ELEMENTEND)){
                                fMarkupDepth++;
                                fLastSectionWasCharacterData = false;
                                setScannerState(SCANNER_STATE_END_ELEMENT_TAG);
                                }else if(XMLChar.isNameStart(fEntityScanner.peekChar())){
                                fMarkupDepth++;
                                fLastSectionWasCharacterData = false;
                                setScannerState(SCANNER_STATE_START_ELEMENT_TAG);
                            }else{
                                setScannerState(SCANNER_STATE_START_OF_MARKUP);
                                if(fIsCoalesce){
                                    fUsebuffer = true;
                                    fLastSectionWasCharacterData = true;
                                    fContentBuffer.append(fTempString);
                                    fTempString.length = 0;
                                    continue;
                                }
                            }
                            if(fUsebuffer){
                                fContentBuffer.append(fTempString);
                                fTempString.length = 0;
                            }
                            if(DEBUG){
                                System.out.println("NOT USING THE BUFFER, STRING = " + fTempString.toString());
                            }
                            if(dtdGrammarUtil!= null && dtdGrammarUtil.isIgnorableWhiteSpace(fContentBuffer)){
                                if(DEBUG)System.out.println("Return SPACE EVENT");
                                return XMLEvent.SPACE;
                            }else
                                return XMLEvent.CHARACTERS;

                        } else{
                            fUsebuffer = true ;
                            if(DEBUG){
                                System.out.println("fContentBuffer = " + fContentBuffer);
                                System.out.println("fTempString = " + fTempString);
                            }
                            fContentBuffer.append(fTempString);
                            fTempString.length = 0;
                        }
                        if (c == '\r') {
                            if(DEBUG){
                                System.out.println("'\r' character found");
                            }
                            fEntityScanner.scanChar(null);
                            fUsebuffer = true;
                            fContentBuffer.append((char)c);
                            c = -1 ;
                        } else if (c == ']') {
                            fUsebuffer = true;
                            fContentBuffer.append((char)fEntityScanner.scanChar(null));
                            fInScanContent = true;

                            if (fEntityScanner.skipChar(']', null)) {
                                fContentBuffer.append(']');
                                while (fEntityScanner.skipChar(']', null)) {
                                    fContentBuffer.append(']');
                                }
                                if (fEntityScanner.skipChar('>', null)) {
                                    reportFatalError("CDEndInContent", null);
                                }
                            }
                            c = -1 ;
                            fInScanContent = false;
                        }

                        do{
                            if (c == '<') {
                                fEntityScanner.scanChar(null);
                                setScannerState(SCANNER_STATE_START_OF_MARKUP);
                                break;
                            }else if (c == '&') {
                                fEntityScanner.scanChar(NameType.REFERENCE);
                                setScannerState(SCANNER_STATE_REFERENCE);
                                break;
                            }else if (c != -1 && isInvalidLiteral(c)) {
                                if (XMLChar.isHighSurrogate(c)) {
                                    scanSurrogates(fContentBuffer) ;
                                    setScannerState(SCANNER_STATE_CONTENT);
                                } else {
                                    reportFatalError("InvalidCharInContent",
                                            new Object[] {
                                        Integer.toString(c, 16)});
                                        fEntityScanner.scanChar(null);
                                }
                                break;
                            }
                            c = scanContent(fContentBuffer) ;
                            if(!fIsCoalesce){
                                setScannerState(SCANNER_STATE_CONTENT);
                                break;
                            }

                        }while(true);

                        if(DEBUG)System.out.println("USING THE BUFFER, STRING START=" + fContentBuffer.toString() +"=END");
                        if(fIsCoalesce){
                            fLastSectionWasCharacterData = true ;
                            continue;
                        }else{
                            if(dtdGrammarUtil!= null && dtdGrammarUtil.isIgnorableWhiteSpace(fContentBuffer)){
                                if(DEBUG)System.out.println("Return SPACE EVENT");
                                return XMLEvent.SPACE;
                            } else
                                return XMLEvent.CHARACTERS ;
                        }
                    }

                    case SCANNER_STATE_END_ELEMENT_TAG :{
                        if(fEmptyElement){
                            fEmptyElement = false;
                            setScannerState(SCANNER_STATE_CONTENT);
                            return (fMarkupDepth == 0 && elementDepthIsZeroHook() ) ? XMLEvent.END_ELEMENT : XMLEvent.END_ELEMENT ;

                        } else if(scanEndElement() == 0) {
                            if (elementDepthIsZeroHook()) {
                                return XMLEvent.END_ELEMENT ;
                            }

                        }
                        setScannerState(SCANNER_STATE_CONTENT);
                        return XMLEvent.END_ELEMENT ;
                    }

                    case SCANNER_STATE_COMMENT: { scanComment();
                        setScannerState(SCANNER_STATE_CONTENT);
                        return XMLEvent.COMMENT;
                        }
                    case SCANNER_STATE_PI:{ fContentBuffer.clear() ;
                        scanPI(fContentBuffer);
                        setScannerState(SCANNER_STATE_CONTENT);
                        return XMLEvent.PROCESSING_INSTRUCTION;
                        }
                    case SCANNER_STATE_CDATA :{ if(fIsCoalesce && ( fLastSectionWasEntityReference || fLastSectionWasCData || fLastSectionWasCharacterData)){
                            fLastSectionWasCData = true ;
                            fLastSectionWasEntityReference = false;
                            fLastSectionWasCharacterData = false;
                        }else{
                            fContentBuffer.clear();
                        }
                        fUsebuffer = true;
                        scanCDATASection(fContentBuffer , true);
                        setScannerState(SCANNER_STATE_CONTENT);
                        if(fIsCoalesce){
                            fLastSectionWasCData = true ;
                            continue;
                        }else if(fReportCdataEvent){
                            return XMLEvent.CDATA;
                        } else{
                            return XMLEvent.CHARACTERS;
                        }
                    }

                    case SCANNER_STATE_REFERENCE :{
                        fMarkupDepth++;
                        foundBuiltInRefs = false;

                        if(fIsCoalesce && ( fLastSectionWasEntityReference || fLastSectionWasCData || fLastSectionWasCharacterData)){
                            fLastSectionWasEntityReference = true ;
                            fLastSectionWasCData = false;
                            fLastSectionWasCharacterData = false;
                        }else{
                            fContentBuffer.clear();
                        }
                        fUsebuffer = true ;
                        if (fEntityScanner.skipChar('#', NameType.REFERENCE)) {
                            scanCharReferenceValue(fContentBuffer, null);
                            fMarkupDepth--;
                            if(!fIsCoalesce){
                                setScannerState(SCANNER_STATE_CONTENT);
                                return XMLEvent.CHARACTERS;
                            }
                        } else {
                            scanEntityReference(fContentBuffer);
                            if(fScannerState == SCANNER_STATE_BUILT_IN_REFS && !fIsCoalesce){
                                setScannerState(SCANNER_STATE_CONTENT);
                                return XMLEvent.CHARACTERS;
                            }

                            if(fScannerState == SCANNER_STATE_TEXT_DECL){
                                fLastSectionWasEntityReference = true ;
                                continue;
                            }

                            if(fScannerState == SCANNER_STATE_REFERENCE){
                                setScannerState(SCANNER_STATE_CONTENT);
                                if (fReplaceEntityReferences && fEntityStore.isDeclaredEntity(fCurrentEntityName)) {
                                    continue;
                                }
                                return XMLEvent.ENTITY_REFERENCE;
                            }
                        }
                        setScannerState(SCANNER_STATE_CONTENT);
                        fLastSectionWasEntityReference = true ;
                        continue;
                    }

                    case SCANNER_STATE_TEXT_DECL: {
                        if (fEntityScanner.skipString("<?xml")) {
                            fMarkupDepth++;
                            if (isValidNameChar(fEntityScanner.peekChar())) {
                                fStringBuffer.clear();
                                fStringBuffer.append("xml");

                                if (fNamespaces) {
                                    while (isValidNCName(fEntityScanner.peekChar())) {
                                        fStringBuffer.append((char)fEntityScanner.scanChar(null));
                                    }
                                } else {
                                    while (isValidNameChar(fEntityScanner.peekChar())) {
                                        fStringBuffer.append((char)fEntityScanner.scanChar(null));
                                    }
                                }
                                String target = fSymbolTable.addSymbol(fStringBuffer.ch, fStringBuffer.offset, fStringBuffer.length);
                                fContentBuffer.clear();
                                scanPIData(target, fContentBuffer);
                            }

                            else {
                                scanXMLDeclOrTextDecl(true);
                            }
                        }
                        fEntityManager.fCurrentEntity.mayReadChunks = true;
                        setScannerState(SCANNER_STATE_CONTENT);
                        continue;
                    }


                    case SCANNER_STATE_ROOT_ELEMENT: {
                        if (scanRootElementHook()) {
                            fEmptyElement = true;
                            return XMLEvent.START_ELEMENT;
                        }
                        setScannerState(SCANNER_STATE_CONTENT);
                        return XMLEvent.START_ELEMENT ;
                    }
                    case SCANNER_STATE_CHAR_REFERENCE : {
                        fContentBuffer.clear();
                        scanCharReferenceValue(fContentBuffer, null);
                        fMarkupDepth--;
                        setScannerState(SCANNER_STATE_CONTENT);
                        return XMLEvent.CHARACTERS;
                    }
                    default:
                        throw new XNIException("Scanner State " + fScannerState + " not Recognized ");

                }}
            catch (EOFException e) {
                endOfFileHook(e);
                return -1;
            }
            } }protected boolean scanForDoctypeHook()
        throws IOException, XNIException {
            return false;
        } protected boolean elementDepthIsZeroHook()
        throws IOException, XNIException {
            return false;
        } protected boolean scanRootElementHook()
        throws IOException, XNIException {
            return false;
        } protected void endOfFileHook(EOFException e)
        throws IOException, XNIException {

            if (fMarkupDepth != 0) {
                reportFatalError("PrematureEOF", null);
            }

        } } static void pr(String str) {
        System.out.println(str) ;
    }

    protected boolean fUsebuffer ;



    protected XMLString getString(){
        if(fAttributeCacheUsedCount < initialCacheCount || fAttributeCacheUsedCount < attributeValueCache.size()){
            return attributeValueCache.get(fAttributeCacheUsedCount++);
        } else{
            XMLString str = new XMLString();
            fAttributeCacheUsedCount++;
            attributeValueCache.add(str);
            return str;
        }
    }



    public void refresh(){
        refresh(0);
    }


    public void refresh(int refreshPosition){
        if(fReadingAttributes){
            fAttributes.refresh();
        }
        if(fScannerState == SCANNER_STATE_CHARACTER_DATA){
            fContentBuffer.append(fTempString);
            fTempString.length = 0;
            fUsebuffer = true;
        }
    }

}