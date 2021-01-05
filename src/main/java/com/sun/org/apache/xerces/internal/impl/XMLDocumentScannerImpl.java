



package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.StaxXMLInputSource;
import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
import java.io.EOFException;
import java.io.IOException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;



public class XMLDocumentScannerImpl
        extends XMLDocumentFragmentScannerImpl{

    protected static final int SCANNER_STATE_XML_DECL = 42;


    protected static final int SCANNER_STATE_PROLOG = 43;


    protected static final int SCANNER_STATE_TRAILING_MISC = 44;


    protected static final int SCANNER_STATE_DTD_INTERNAL_DECLS = 45;


    protected static final int SCANNER_STATE_DTD_EXTERNAL = 46;


    protected static final int SCANNER_STATE_DTD_EXTERNAL_DECLS = 47;


    protected static final int SCANNER_STATE_NO_SUCH_ELEMENT_EXCEPTION = 48;

    protected static final String DOCUMENT_SCANNER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.DOCUMENT_SCANNER_PROPERTY;


    protected static final String LOAD_EXTERNAL_DTD =
            Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE;


    protected static final String DISALLOW_DOCTYPE_DECL_FEATURE =
            Constants.XERCES_FEATURE_PREFIX + Constants.DISALLOW_DOCTYPE_DECL_FEATURE;

    protected static final String DTD_SCANNER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.DTD_SCANNER_PROPERTY;

    protected static final String VALIDATION_MANAGER =
            Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATION_MANAGER_PROPERTY;


    protected static final String NAMESPACE_CONTEXT =
        Constants.XERCES_PROPERTY_PREFIX + Constants.NAMESPACE_CONTEXT_PROPERTY;

    private static final String[] RECOGNIZED_FEATURES = {
        LOAD_EXTERNAL_DTD,
                DISALLOW_DOCTYPE_DECL_FEATURE,
    };


    private static final Boolean[] FEATURE_DEFAULTS = {
        Boolean.TRUE,
                Boolean.FALSE,
    };


    private static final String[] RECOGNIZED_PROPERTIES = {
        DTD_SCANNER,
                VALIDATION_MANAGER
    };


    private static final Object[] PROPERTY_DEFAULTS = {
            null,
                null
    };

    protected XMLDTDScanner fDTDScanner = null;


    protected ValidationManager fValidationManager;

    protected XMLStringBuffer fDTDDecl = null;
    protected boolean fReadingDTD = false;
    protected boolean fAddedListener = false;

    protected String fDoctypeName;


    protected String fDoctypePublicId;


    protected String fDoctypeSystemId;


    protected NamespaceContext fNamespaceContext = new NamespaceSupport();

    protected boolean fLoadExternalDTD = true;

    protected boolean fSeenDoctypeDecl;

    protected boolean fScanEndElement;

    protected Driver fXMLDeclDriver = new XMLDeclDriver();


    protected Driver fPrologDriver = new PrologDriver();


    protected Driver fDTDDriver = null ;


    protected Driver fTrailingMiscDriver = new TrailingMiscDriver();
    protected int fStartPos = 0;
    protected int fEndPos = 0;
    protected boolean fSeenInternalSubset= false;
    private String[] fStrings = new String[3];


    private XMLInputSource fExternalSubsetSource = null;


    private final XMLDTDDescription fDTDDescription = new XMLDTDDescription(null, null, null, null, null);

    private static final char [] DOCTYPE = {'D','O','C','T','Y','P','E'};
    private static final char [] COMMENTSTRING = {'-','-'};

    public XMLDocumentScannerImpl() {} public void setInputSource(XMLInputSource inputSource) throws IOException {
        fEntityManager.setEntityHandler(this);
        fEntityManager.startDocumentEntity(inputSource);
        setScannerState(XMLEvent.START_DOCUMENT);
    } public int getScannetState(){
        return fScannerState ;
    }




    public void reset(PropertyManager propertyManager) {
        super.reset(propertyManager);
        fDoctypeName = null;
        fDoctypePublicId = null;
        fDoctypeSystemId = null;
        fSeenDoctypeDecl = false;
        fNamespaceContext.reset();
        fSupportDTD = ((Boolean)propertyManager.getProperty(XMLInputFactory.SUPPORT_DTD)).booleanValue();

        fLoadExternalDTD = !((Boolean)propertyManager.getProperty(Constants.ZEPHYR_PROPERTY_PREFIX + Constants.IGNORE_EXTERNAL_DTD)).booleanValue();
        setScannerState(XMLEvent.START_DOCUMENT);
        setDriver(fXMLDeclDriver);
        fSeenInternalSubset = false;
        if(fDTDScanner != null){
            ((XMLDTDScannerImpl)fDTDScanner).reset(propertyManager);
        }
        fEndPos = 0;
        fStartPos = 0;
        if(fDTDDecl != null){
            fDTDDecl.clear();
        }

    }


    public void reset(XMLComponentManager componentManager)
    throws XMLConfigurationException {

        super.reset(componentManager);

        fDoctypeName = null;
        fDoctypePublicId = null;
        fDoctypeSystemId = null;
        fSeenDoctypeDecl = false;
        fExternalSubsetSource = null;

        fLoadExternalDTD = componentManager.getFeature(LOAD_EXTERNAL_DTD, true);
        fDisallowDoctype = componentManager.getFeature(DISALLOW_DOCTYPE_DECL_FEATURE, false);

        fNamespaces = componentManager.getFeature(NAMESPACES, true);

        fSeenInternalSubset = false;
        fDTDScanner = (XMLDTDScanner)componentManager.getProperty(DTD_SCANNER);

        fValidationManager = (ValidationManager)componentManager.getProperty(VALIDATION_MANAGER, null);

        try {
            fNamespaceContext = (NamespaceContext)componentManager.getProperty(NAMESPACE_CONTEXT);
        }
        catch (XMLConfigurationException e) { }
        if (fNamespaceContext == null) {
            fNamespaceContext = new NamespaceSupport();
        }
        fNamespaceContext.reset();

        fEndPos = 0;
        fStartPos = 0;
        if(fDTDDecl != null)
            fDTDDecl.clear();


        setScannerState(SCANNER_STATE_XML_DECL);
        setDriver(fXMLDeclDriver);

    } public String[] getRecognizedFeatures() {
        String[] featureIds = super.getRecognizedFeatures();
        int length = featureIds != null ? featureIds.length : 0;
        String[] combinedFeatureIds = new String[length + RECOGNIZED_FEATURES.length];
        if (featureIds != null) {
            System.arraycopy(featureIds, 0, combinedFeatureIds, 0, featureIds.length);
        }
        System.arraycopy(RECOGNIZED_FEATURES, 0, combinedFeatureIds, length, RECOGNIZED_FEATURES.length);
        return combinedFeatureIds;
    } public void setFeature(String featureId, boolean state)
    throws XMLConfigurationException {

        super.setFeature(featureId, state);

        if (featureId.startsWith(Constants.XERCES_FEATURE_PREFIX)) {
            final int suffixLength = featureId.length() - Constants.XERCES_FEATURE_PREFIX.length();

            if (suffixLength == Constants.LOAD_EXTERNAL_DTD_FEATURE.length() &&
                featureId.endsWith(Constants.LOAD_EXTERNAL_DTD_FEATURE)) {
                fLoadExternalDTD = state;
                return;
            }
            else if (suffixLength == Constants.DISALLOW_DOCTYPE_DECL_FEATURE.length() &&
                featureId.endsWith(Constants.DISALLOW_DOCTYPE_DECL_FEATURE)) {
                fDisallowDoctype = state;
                return;
            }
        }

    } public String[] getRecognizedProperties() {
        String[] propertyIds = super.getRecognizedProperties();
        int length = propertyIds != null ? propertyIds.length : 0;
        String[] combinedPropertyIds = new String[length + RECOGNIZED_PROPERTIES.length];
        if (propertyIds != null) {
            System.arraycopy(propertyIds, 0, combinedPropertyIds, 0, propertyIds.length);
        }
        System.arraycopy(RECOGNIZED_PROPERTIES, 0, combinedPropertyIds, length, RECOGNIZED_PROPERTIES.length);
        return combinedPropertyIds;
    } public void setProperty(String propertyId, Object value)
    throws XMLConfigurationException {

        super.setProperty(propertyId, value);

        if (propertyId.startsWith(Constants.XERCES_PROPERTY_PREFIX)) {
            final int suffixLength = propertyId.length() - Constants.XERCES_PROPERTY_PREFIX.length();

            if (suffixLength == Constants.DTD_SCANNER_PROPERTY.length() &&
                propertyId.endsWith(Constants.DTD_SCANNER_PROPERTY)) {
                fDTDScanner = (XMLDTDScanner)value;
            }
            if (suffixLength == Constants.NAMESPACE_CONTEXT_PROPERTY.length() &&
                propertyId.endsWith(Constants.NAMESPACE_CONTEXT_PROPERTY)) {
                if (value != null) {
                    fNamespaceContext = (NamespaceContext)value;
                }
            }

            return;
        }

    } public Boolean getFeatureDefault(String featureId) {

        for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
            if (RECOGNIZED_FEATURES[i].equals(featureId)) {
                return FEATURE_DEFAULTS[i];
            }
        }
        return super.getFeatureDefault(featureId);
    } public Object getPropertyDefault(String propertyId) {
        for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
            if (RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return PROPERTY_DEFAULTS[i];
            }
        }
        return super.getPropertyDefault(propertyId);
    } public void startEntity(String name,
            XMLResourceIdentifier identifier,
            String encoding, Augmentations augs) throws XNIException {

        super.startEntity(name, identifier, encoding,augs);

        fEntityScanner.registerListener(this);

        if (!name.equals("[xml]") && fEntityScanner.isExternal()) {
            if (augs == null || !((Boolean) augs.getItem(Constants.ENTITY_SKIPPED)).booleanValue()) {
                setScannerState(SCANNER_STATE_TEXT_DECL);
            }
        }

        if (fDocumentHandler != null && name.equals("[xml]")) {
            fDocumentHandler.startDocument(fEntityScanner, encoding, fNamespaceContext, null);
        }

    } public void endEntity(String name, Augmentations augs) throws IOException, XNIException {

        super.endEntity(name, augs);

        if(name.equals("[xml]")){
            if(fMarkupDepth == 0 && fDriver == fTrailingMiscDriver){
                setScannerState(SCANNER_STATE_TERMINATED) ;
            } else{
                throw new java.io.EOFException();
            }

            }
    } public XMLStringBuffer getDTDDecl(){
        Entity entity = fEntityScanner.getCurrentEntity();
        fDTDDecl.append(((Entity.ScannedEntity)entity).ch,fStartPos , fEndPos-fStartPos);
        if(fSeenInternalSubset)
            fDTDDecl.append("]>");
        return fDTDDecl;
    }

    public String getCharacterEncodingScheme(){
        return fDeclaredEncoding;
    }



    public int next() throws IOException, XNIException {
        return fDriver.next();
    }

    public NamespaceContext getNamespaceContext(){
        return fNamespaceContext ;
    }



    protected Driver createContentDriver() {
        return new ContentDriver();
    } protected boolean scanDoctypeDecl(boolean supportDTD) throws IOException, XNIException {

        if (!fEntityScanner.skipSpaces()) {
            reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ROOT_ELEMENT_TYPE_IN_DOCTYPEDECL",
                    null);
        }

        fDoctypeName = fEntityScanner.scanName(NameType.DOCTYPE);
        if (fDoctypeName == null) {
            reportFatalError("MSG_ROOT_ELEMENT_TYPE_REQUIRED", null);
        }

        if (fEntityScanner.skipSpaces()) {
            scanExternalID(fStrings, false);
            fDoctypeSystemId = fStrings[0];
            fDoctypePublicId = fStrings[1];
            fEntityScanner.skipSpaces();
        }

        fHasExternalDTD = fDoctypeSystemId != null;

        if (supportDTD && !fHasExternalDTD && fExternalSubsetResolver != null) {
            fDTDDescription.setValues(null, null, fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
            fDTDDescription.setRootName(fDoctypeName);
            fExternalSubsetSource = fExternalSubsetResolver.getExternalSubset(fDTDDescription);
            fHasExternalDTD = fExternalSubsetSource != null;
        }

        if (supportDTD && fDocumentHandler != null) {
            if (fExternalSubsetSource == null) {
                fDocumentHandler.doctypeDecl(fDoctypeName, fDoctypePublicId, fDoctypeSystemId, null);
            }
            else {
                fDocumentHandler.doctypeDecl(fDoctypeName, fExternalSubsetSource.getPublicId(), fExternalSubsetSource.getSystemId(), null);
            }
        }

        boolean internalSubset = true;
        if (!fEntityScanner.skipChar('[', null)) {
            internalSubset = false;
            fEntityScanner.skipSpaces();
            if (!fEntityScanner.skipChar('>', null)) {
                reportFatalError("DoctypedeclUnterminated", new Object[]{fDoctypeName});
            }
            fMarkupDepth--;
        }
        return internalSubset;

    } protected void setEndDTDScanState() {
        setScannerState(SCANNER_STATE_PROLOG);
        setDriver(fPrologDriver);
        fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
        fReadingDTD=false;
    }


    protected String getScannerStateName(int state) {

        switch (state) {
            case SCANNER_STATE_XML_DECL: return "SCANNER_STATE_XML_DECL";
            case SCANNER_STATE_PROLOG: return "SCANNER_STATE_PROLOG";
            case SCANNER_STATE_TRAILING_MISC: return "SCANNER_STATE_TRAILING_MISC";
            case SCANNER_STATE_DTD_INTERNAL_DECLS: return "SCANNER_STATE_DTD_INTERNAL_DECLS";
            case SCANNER_STATE_DTD_EXTERNAL: return "SCANNER_STATE_DTD_EXTERNAL";
            case SCANNER_STATE_DTD_EXTERNAL_DECLS: return "SCANNER_STATE_DTD_EXTERNAL_DECLS";
        }
        return super.getScannerStateName(state);

    } protected final class XMLDeclDriver
            implements Driver {

        public int next() throws IOException, XNIException {
            if(DEBUG_NEXT){
                System.out.println("NOW IN XMLDeclDriver");
            }

            setScannerState(SCANNER_STATE_PROLOG);
            setDriver(fPrologDriver);

            try {
                if (fEntityScanner.skipString(xmlDecl)) {
                    fMarkupDepth++;
                    if (XMLChar.isName(fEntityScanner.peekChar())) {
                        fStringBuffer.clear();
                        fStringBuffer.append("xml");
                        while (XMLChar.isName(fEntityScanner.peekChar())) {
                            fStringBuffer.append((char)fEntityScanner.scanChar(null));
                        }
                        String target = fSymbolTable.addSymbol(fStringBuffer.ch, fStringBuffer.offset, fStringBuffer.length);
                        fContentBuffer.clear() ;
                        scanPIData(target, fContentBuffer);
                        fEntityManager.fCurrentEntity.mayReadChunks = true;
                        return XMLEvent.PROCESSING_INSTRUCTION ;
                    }
                    else {
                        scanXMLDeclOrTextDecl(false);
                        fEntityManager.fCurrentEntity.mayReadChunks = true;
                        return XMLEvent.START_DOCUMENT;
                    }
                } else{
                    fEntityManager.fCurrentEntity.mayReadChunks = true;
                    return XMLEvent.START_DOCUMENT;
                }


                }

            catch (EOFException e) {
                reportFatalError("PrematureEOF", null);
                return -1;
                }

        }
    } protected final class PrologDriver
            implements Driver {



        public int next() throws IOException, XNIException {
            if(DEBUG_NEXT){
                System.out.println("NOW IN PrologDriver");
            }
            try {
                do {
                    switch (fScannerState) {
                        case SCANNER_STATE_PROLOG: {
                            fEntityScanner.skipSpaces();
                            if (fEntityScanner.skipChar('<', null)) {
                                setScannerState(SCANNER_STATE_START_OF_MARKUP);
                            } else if (fEntityScanner.skipChar('&', NameType.REFERENCE)) {
                                setScannerState(SCANNER_STATE_REFERENCE);
                            } else {
                                setScannerState(SCANNER_STATE_CONTENT);
                            }
                            break;
                        }

                        case SCANNER_STATE_START_OF_MARKUP: {
                            fMarkupDepth++;
                            if (isValidNameStartChar(fEntityScanner.peekChar()) ||
                                    isValidNameStartHighSurrogate(fEntityScanner.peekChar())) {
                                setScannerState(SCANNER_STATE_ROOT_ELEMENT);
                                setDriver(fContentDriver);
                                return fContentDriver.next();
                            } else if (fEntityScanner.skipChar('!', null)) {
                                if (fEntityScanner.skipChar('-', null)) {
                                    if (!fEntityScanner.skipChar('-', null)) {
                                        reportFatalError("InvalidCommentStart",
                                                null);
                                    }
                                    setScannerState(SCANNER_STATE_COMMENT);
                                } else if (fEntityScanner.skipString(DOCTYPE)) {
                                    setScannerState(SCANNER_STATE_DOCTYPE);
                                    Entity entity = fEntityScanner.getCurrentEntity();
                                    if(entity instanceof Entity.ScannedEntity){
                                        fStartPos=((Entity.ScannedEntity)entity).position;
                                    }
                                    fReadingDTD=true;
                                    if(fDTDDecl == null)
                                        fDTDDecl = new XMLStringBuffer();
                                    fDTDDecl.append("<!DOCTYPE");

                                } else {
                                    reportFatalError("MarkupNotRecognizedInProlog",
                                            null);
                                }
                            } else if (fEntityScanner.skipChar('?', null)) {
                                setScannerState(SCANNER_STATE_PI);
                            } else {
                                reportFatalError("MarkupNotRecognizedInProlog",
                                        null);
                            }
                            break;
                        }
                    }
                } while (fScannerState == SCANNER_STATE_PROLOG || fScannerState == SCANNER_STATE_START_OF_MARKUP );

                switch(fScannerState){

                    case SCANNER_STATE_COMMENT: {
                        scanComment();
                        setScannerState(SCANNER_STATE_PROLOG);
                        return XMLEvent.COMMENT;
                        }
                    case SCANNER_STATE_PI: {
                        fContentBuffer.clear() ;
                        scanPI(fContentBuffer);
                        setScannerState(SCANNER_STATE_PROLOG);
                        return XMLEvent.PROCESSING_INSTRUCTION;
                    }

                    case SCANNER_STATE_DOCTYPE: {
                        if (fDisallowDoctype) {
                            reportFatalError("DoctypeNotAllowed", null);
                        }

                        if (fSeenDoctypeDecl) {
                            reportFatalError("AlreadySeenDoctype", null);
                        }
                        fSeenDoctypeDecl = true;

                        if (scanDoctypeDecl(fSupportDTD)) {
                            setScannerState(SCANNER_STATE_DTD_INTERNAL_DECLS);
                            fSeenInternalSubset = true;
                            if(fDTDDriver == null){
                                fDTDDriver = new DTDDriver();
                            }
                            setDriver(fContentDriver);
                            return fDTDDriver.next();
                        }

                        if(fSeenDoctypeDecl){
                            Entity entity = fEntityScanner.getCurrentEntity();
                            if(entity instanceof Entity.ScannedEntity){
                                fEndPos = ((Entity.ScannedEntity)entity).position;
                            }
                            fReadingDTD = false;
                        }

                        if (fDoctypeSystemId != null) {
                            if (((fValidation || fLoadExternalDTD)
                                && (fValidationManager == null || !fValidationManager.isCachedDTD()))) {
                                if (fSupportDTD) {
                                    setScannerState(SCANNER_STATE_DTD_EXTERNAL);
                                } else {
                                    setScannerState(SCANNER_STATE_PROLOG);
                                }

                                setDriver(fContentDriver);
                                if(fDTDDriver == null) {
                                    fDTDDriver = new DTDDriver();
                                }

                                return fDTDDriver.next();
                            }
                        }
                        else if (fExternalSubsetSource != null) {
                            if (((fValidation || fLoadExternalDTD)
                                && (fValidationManager == null || !fValidationManager.isCachedDTD()))) {
                                fDTDScanner.setInputSource(fExternalSubsetSource);
                                fExternalSubsetSource = null;
                            if (fSupportDTD)
                                setScannerState(SCANNER_STATE_DTD_EXTERNAL_DECLS);
                            else
                                setScannerState(SCANNER_STATE_PROLOG);
                            setDriver(fContentDriver);
                            if(fDTDDriver == null)
                                fDTDDriver = new DTDDriver();
                            return fDTDDriver.next();
                            }
                        }

                        if (fDTDScanner != null) {
                            fDTDScanner.setInputSource(null);
                        }
                        setScannerState(SCANNER_STATE_PROLOG);
                        return XMLEvent.DTD;
                    }

                    case SCANNER_STATE_CONTENT: {
                        reportFatalError("ContentIllegalInProlog", null);
                        fEntityScanner.scanChar(null);
                    }
                    case SCANNER_STATE_REFERENCE: {
                        reportFatalError("ReferenceIllegalInProlog", null);
                    }


                }
            }
            catch (EOFException e) {
                reportFatalError("PrematureEOF", null);
                return -1 ;
                }
            return -1;

        }


    } protected final class DTDDriver
            implements Driver {

        public int next() throws IOException, XNIException{
            if(DEBUG_NEXT){
                System.out.println("Now in DTD Driver");
            }

            dispatch(true);

            if(DEBUG_NEXT){
                System.out.println("After calling dispatch(true) -- At this point whole DTD is read.");
            }

            if(fPropertyManager != null){
                dtdGrammarUtil =  new DTDGrammarUtil(((XMLDTDScannerImpl)fDTDScanner).getGrammar(),fSymbolTable, fNamespaceContext);
            }

            return XMLEvent.DTD ;
        }


        public boolean dispatch(boolean complete)
        throws IOException, XNIException {
            fEntityManager.setEntityHandler(null);
            try {
                boolean again;
                XMLResourceIdentifierImpl resourceIdentifier = new XMLResourceIdentifierImpl();
                if( fDTDScanner == null){

                    if (fEntityManager.getEntityScanner() instanceof XML11EntityScanner){
                        fDTDScanner = new XML11DTDScannerImpl();
                    } else

                    fDTDScanner = new XMLDTDScannerImpl();

                    ((XMLDTDScannerImpl)fDTDScanner).reset(fPropertyManager);
                }

                fDTDScanner.setLimitAnalyzer(fLimitAnalyzer);
                do {
                    again = false;
                    switch (fScannerState) {
                        case SCANNER_STATE_DTD_INTERNAL_DECLS: {
                            boolean moreToScan = false;
                            if (!fDTDScanner.skipDTD(fSupportDTD)) {
                            boolean completeDTD = true;

                                moreToScan = fDTDScanner.scanDTDInternalSubset(completeDTD, fStandalone, fHasExternalDTD && fLoadExternalDTD);
                            }
                            Entity entity = fEntityScanner.getCurrentEntity();
                            if(entity instanceof Entity.ScannedEntity){
                                fEndPos=((Entity.ScannedEntity)entity).position;
                            }
                            fReadingDTD=false;
                            if (!moreToScan) {
                                if (!fEntityScanner.skipChar(']', null)) {
                                    reportFatalError("DoctypedeclNotClosed", new Object[]{fDoctypeName});
                                }
                                fEntityScanner.skipSpaces();
                                if (!fEntityScanner.skipChar('>', null)) {
                                    reportFatalError("DoctypedeclUnterminated", new Object[]{fDoctypeName});
                                }
                                fMarkupDepth--;

                                if (!fSupportDTD) {
                                    fEntityStore = fEntityManager.getEntityStore();
                                    fEntityStore.reset();
                                } else {
                                    if (fDoctypeSystemId != null && (fValidation || fLoadExternalDTD)) {
                                        setScannerState(SCANNER_STATE_DTD_EXTERNAL);
                                        break;
                                    }
                                }

                                setEndDTDScanState();
                                return true;

                            }
                            break;
                        }
                        case SCANNER_STATE_DTD_EXTERNAL: {


                            resourceIdentifier.setValues(fDoctypePublicId, fDoctypeSystemId, null, null);
                            XMLInputSource xmlInputSource = null ;
                            StaxXMLInputSource staxInputSource =  fEntityManager.resolveEntityAsPerStax(resourceIdentifier);

                            if (!staxInputSource.hasResolver()) {
                                String accessError = checkAccess(fDoctypeSystemId, fAccessExternalDTD);
                                if (accessError != null) {
                                    reportFatalError("AccessExternalDTD", new Object[]{ SecuritySupport.sanitizePath(fDoctypeSystemId), accessError });
                                }
                            }
                            xmlInputSource = staxInputSource.getXMLInputSource();
                            fDTDScanner.setInputSource(xmlInputSource);
                            if (fEntityScanner.fCurrentEntity != null) {
                                setScannerState(SCANNER_STATE_DTD_EXTERNAL_DECLS);
                            } else {
                                setScannerState(SCANNER_STATE_PROLOG);
                            }
                            again = true;
                            break;
                        }
                        case SCANNER_STATE_DTD_EXTERNAL_DECLS: {
                            boolean completeDTD = true;
                            boolean moreToScan = fDTDScanner.scanDTDExternalSubset(completeDTD);
                            if (!moreToScan) {
                                setEndDTDScanState();
                                return true;
                            }
                            break;
                        }
                        case SCANNER_STATE_PROLOG : {
                            setEndDTDScanState();
                            return true;
                        }
                        default: {
                            throw new XNIException("DTDDriver#dispatch: scanner state="+fScannerState+" ("+getScannerStateName(fScannerState)+')');
                        }
                    }
                } while (complete || again);
            }

            catch (EOFException e) {
                e.printStackTrace();
                reportFatalError("PrematureEOF", null);
                return false;
                }

            finally {
                fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
            }

            return true;

        }

        } protected class ContentDriver
            extends FragmentContentDriver {

        protected boolean scanForDoctypeHook()
        throws IOException, XNIException {

            if (fEntityScanner.skipString(DOCTYPE)) {
                setScannerState(SCANNER_STATE_DOCTYPE);
                return true;
            }
            return false;

        } protected boolean elementDepthIsZeroHook()
        throws IOException, XNIException {

            setScannerState(SCANNER_STATE_TRAILING_MISC);
            setDriver(fTrailingMiscDriver);
            return true;

        } protected boolean scanRootElementHook()
        throws IOException, XNIException {

            if (scanStartElement()) {
                setScannerState(SCANNER_STATE_TRAILING_MISC);
                setDriver(fTrailingMiscDriver);
                return true;
            }
            return false;

        } protected void endOfFileHook(EOFException e)
        throws IOException, XNIException {

            reportFatalError("PrematureEOF", null);
            } protected void resolveExternalSubsetAndRead()
        throws IOException, XNIException {

            fDTDDescription.setValues(null, null, fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
            fDTDDescription.setRootName(fElementQName.rawname);
            XMLInputSource src = fExternalSubsetResolver.getExternalSubset(fDTDDescription);

            if (src != null) {
                fDoctypeName = fElementQName.rawname;
                fDoctypePublicId = src.getPublicId();
                fDoctypeSystemId = src.getSystemId();
                if (fDocumentHandler != null) {
                    fDocumentHandler.doctypeDecl(fDoctypeName, fDoctypePublicId, fDoctypeSystemId, null);
                }
                try {
                    fDTDScanner.setInputSource(src);
                    while (fDTDScanner.scanDTDExternalSubset(true));
                } finally {
                    fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
                }
            }
        } } protected final class TrailingMiscDriver
            implements Driver {

        public int next() throws IOException, XNIException{
            if(fEmptyElement){
                fEmptyElement = false;
                return XMLEvent.END_ELEMENT;
            }

            try {
                if(fScannerState == SCANNER_STATE_TERMINATED){
                    return XMLEvent.END_DOCUMENT ;}
                do {
                    switch (fScannerState) {
                        case SCANNER_STATE_TRAILING_MISC: {

                            fEntityScanner.skipSpaces();
                            if(fScannerState == SCANNER_STATE_TERMINATED ){
                                return XMLEvent.END_DOCUMENT ;
                            }
                            if (fEntityScanner.skipChar('<', null)) {
                                setScannerState(SCANNER_STATE_START_OF_MARKUP);
                            } else {
                                setScannerState(SCANNER_STATE_CONTENT);
                            }
                            break;
                        }
                        case SCANNER_STATE_START_OF_MARKUP: {
                            fMarkupDepth++;
                            if (fEntityScanner.skipChar('?', null)) {
                                setScannerState(SCANNER_STATE_PI);
                            } else if (fEntityScanner.skipChar('!', null)) {
                                setScannerState(SCANNER_STATE_COMMENT);
                            } else if (fEntityScanner.skipChar('/', null)) {
                                reportFatalError("MarkupNotRecognizedInMisc",
                                        null);
                            } else if (isValidNameStartChar(fEntityScanner.peekChar()) ||
                                    isValidNameStartHighSurrogate(fEntityScanner.peekChar())) {
                                reportFatalError("MarkupNotRecognizedInMisc",
                                        null);
                                scanStartElement();
                                setScannerState(SCANNER_STATE_CONTENT);
                            } else {
                                reportFatalError("MarkupNotRecognizedInMisc",
                                        null);
                            }
                            break;
                        }
                    }
                }while(fScannerState == SCANNER_STATE_START_OF_MARKUP || fScannerState == SCANNER_STATE_TRAILING_MISC);
                if(DEBUG_NEXT){
                    System.out.println("State set by deciding while loop [TrailingMiscellaneous] is = " + getScannerStateName(fScannerState));
                }
                switch (fScannerState){
                    case SCANNER_STATE_PI: {
                        fContentBuffer.clear();
                        scanPI(fContentBuffer);
                        setScannerState(SCANNER_STATE_TRAILING_MISC);
                        return XMLEvent.PROCESSING_INSTRUCTION ;
                    }
                    case SCANNER_STATE_COMMENT: {
                        if (!fEntityScanner.skipString(COMMENTSTRING)) {
                            reportFatalError("InvalidCommentStart", null);
                        }
                        scanComment();
                        setScannerState(SCANNER_STATE_TRAILING_MISC);
                        return XMLEvent.COMMENT;
                    }
                    case SCANNER_STATE_CONTENT: {
                        int ch = fEntityScanner.peekChar();
                        if (ch == -1) {
                            setScannerState(SCANNER_STATE_TERMINATED);
                            return XMLEvent.END_DOCUMENT ;
                        } else{
                            reportFatalError("ContentIllegalInTrailingMisc",
                                    null);
                            fEntityScanner.scanChar(null);
                            setScannerState(SCANNER_STATE_TRAILING_MISC);
                            return XMLEvent.CHARACTERS;
                        }

                    }
                    case SCANNER_STATE_REFERENCE: {
                        reportFatalError("ReferenceIllegalInTrailingMisc",
                                null);
                        setScannerState(SCANNER_STATE_TRAILING_MISC);
                        return XMLEvent.ENTITY_REFERENCE ;
                    }
                    case SCANNER_STATE_TERMINATED: {
                        setScannerState(SCANNER_STATE_NO_SUCH_ELEMENT_EXCEPTION);
                        return XMLEvent.END_DOCUMENT ;
                    }
                    case SCANNER_STATE_NO_SUCH_ELEMENT_EXCEPTION:{
                        throw new java.util.NoSuchElementException("No more events to be parsed");
                    }
                    default: throw new XNIException("Scanner State " + fScannerState + " not Recognized ");
                }} catch (EOFException e) {
                if (fMarkupDepth != 0) {
                    reportFatalError("PrematureEOF", null);
                    return -1;
                    }
                setScannerState(SCANNER_STATE_TERMINATED);
            }

            return XMLEvent.END_DOCUMENT;

        }} public void refresh(int refreshPosition){
        super.refresh(refreshPosition);
        if(fReadingDTD){
            Entity entity = fEntityScanner.getCurrentEntity();
            if(entity instanceof Entity.ScannedEntity){
                fEndPos=((Entity.ScannedEntity)entity).position;
            }
            fDTDDecl.append(((Entity.ScannedEntity)entity).ch,fStartPos , fEndPos-fStartPos);
            fStartPos = refreshPosition;
        }
    }

}