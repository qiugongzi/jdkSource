



package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.XMLEntityHandler;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;

import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
import java.io.EOFException;
import java.io.IOException;


public class XMLDTDScannerImpl
extends XMLScanner
implements XMLDTDScanner, XMLComponent, XMLEntityHandler {

    protected static final int SCANNER_STATE_END_OF_INPUT = 0;


    protected static final int SCANNER_STATE_TEXT_DECL = 1;


    protected static final int SCANNER_STATE_MARKUP_DECL = 2;

    private static final String[] RECOGNIZED_FEATURES = {
        VALIDATION,
        NOTIFY_CHAR_REFS,
    };


    private static final Boolean[] FEATURE_DEFAULTS = {
        null,
        Boolean.FALSE,
    };


    private static final String[] RECOGNIZED_PROPERTIES = {
        SYMBOL_TABLE,
        ERROR_REPORTER,
        ENTITY_MANAGER,
    };


    private static final Object[] PROPERTY_DEFAULTS = {
        null,
        null,
        null,
    };

    private static final boolean DEBUG_SCANNER_STATE = false;

    public XMLDTDHandler fDTDHandler = null;


    protected XMLDTDContentModelHandler fDTDContentModelHandler;

    protected int fScannerState;


    protected boolean fStandalone;


    protected boolean fSeenExternalDTD;


    protected boolean fSeenExternalPE;

    private boolean fStartDTDCalled;


    private XMLAttributesImpl fAttributes = new XMLAttributesImpl();


    private int[] fContentStack = new int[5];


    private int fContentDepth;


    private int[] fPEStack = new int[5];



    private boolean[] fPEReport = new boolean[5];


    private int fPEDepth;


    private int fMarkUpDepth;


    private int fExtEntityDepth;


    private int fIncludeSectDepth;

    private String[] fStrings = new String[3];


    private XMLString fString = new XMLString();


    private XMLStringBuffer fStringBuffer = new XMLStringBuffer();


    private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();


    private XMLString fLiteral = new XMLString();


    private XMLString fLiteral2 = new XMLString();


    private String[] fEnumeration = new String[5];


    private int fEnumerationCount;


    private XMLStringBuffer fIgnoreConditionalBuffer = new XMLStringBuffer(128);


    DTDGrammar nvGrammarInfo = null;

    boolean nonValidatingMode = false;
    public XMLDTDScannerImpl() {
    } public XMLDTDScannerImpl(SymbolTable symbolTable,
            XMLErrorReporter errorReporter, XMLEntityManager entityManager) {
        fSymbolTable = symbolTable;
        fErrorReporter = errorReporter;
        fEntityManager = entityManager;
        entityManager.setProperty(SYMBOL_TABLE, fSymbolTable);
    }

    public void setInputSource(XMLInputSource inputSource) throws IOException {
        if (inputSource == null) {
            if (fDTDHandler != null) {
                fDTDHandler.startDTD(null, null);
                fDTDHandler.endDTD(null);
            }
            if (nonValidatingMode){
                nvGrammarInfo.startDTD(null,null);
                nvGrammarInfo.endDTD(null);
            }
            return;
        }
        fEntityManager.setEntityHandler(this);
        fEntityManager.startDTDEntity(inputSource);
    } public void setLimitAnalyzer(XMLLimitAnalyzer limitAnalyzer) {
        fLimitAnalyzer = limitAnalyzer;
    }


    public boolean scanDTDExternalSubset(boolean complete)
    throws IOException, XNIException {

        fEntityManager.setEntityHandler(this);
        if (fScannerState == SCANNER_STATE_TEXT_DECL) {
            fSeenExternalDTD = true;
            boolean textDecl = scanTextDecl();
            if (fScannerState == SCANNER_STATE_END_OF_INPUT) {
                return false;
            }
            else {
                setScannerState(SCANNER_STATE_MARKUP_DECL);
                if (textDecl && !complete) {
                    return true;
                }
            }
        }
        do {
            if (!scanDecls(complete)) {
                return false;
            }
        } while (complete);

        return true;

    } public boolean scanDTDInternalSubset(boolean complete, boolean standalone,
    boolean hasExternalSubset)
    throws IOException, XNIException {
        fEntityScanner = (XMLEntityScanner)fEntityManager.getEntityScanner();
        fEntityManager.setEntityHandler(this);
        fStandalone = standalone;
        if (fScannerState == SCANNER_STATE_TEXT_DECL) {
            if (fDTDHandler != null) {
                fDTDHandler.startDTD(fEntityScanner, null);
                fStartDTDCalled = true;
            }

            if (nonValidatingMode){
                fStartDTDCalled = true;
                nvGrammarInfo.startDTD(fEntityScanner,null);
            }
            setScannerState(SCANNER_STATE_MARKUP_DECL);
        }
        do {
            if (!scanDecls(complete)) {
                if (fDTDHandler != null && hasExternalSubset == false) {
                    fDTDHandler.endDTD(null);
                }
                if (nonValidatingMode && hasExternalSubset == false ){
                    nvGrammarInfo.endDTD(null);
                }
                setScannerState(SCANNER_STATE_TEXT_DECL);
                fLimitAnalyzer.reset(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT);
                fLimitAnalyzer.reset(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT);
                return false;
            }
        } while (complete);

        return true;

    } @Override
    public boolean skipDTD(boolean supportDTD) throws IOException {
        if (supportDTD)
            return false;

        fStringBuffer.clear();
        while (fEntityScanner.scanData("]", fStringBuffer)) {
            int c = fEntityScanner.peekChar();
            if (c != -1) {
                if (XMLChar.isHighSurrogate(c)) {
                    scanSurrogates(fStringBuffer);
                }
                if (isInvalidLiteral(c)) {
                    reportFatalError("InvalidCharInDTD",
                        new Object[] { Integer.toHexString(c) });
                    fEntityScanner.scanChar(null);
                }
            }
        }
        fEntityScanner.fCurrentEntity.position--;
        return true;
    }

    public void reset(XMLComponentManager componentManager)
    throws XMLConfigurationException {

        super.reset(componentManager);
        init();

    } public void reset() {
        super.reset();
        init();

    }

    public void reset(PropertyManager props) {
        setPropertyManager(props);
        super.reset(props);
        init() ;
        nonValidatingMode = true;
        nvGrammarInfo = new DTDGrammar(fSymbolTable);
    }

    public String[] getRecognizedFeatures() {
        return (String[])(RECOGNIZED_FEATURES.clone());
    } public String[] getRecognizedProperties() {
        return (String[])(RECOGNIZED_PROPERTIES.clone());
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
    } public void setDTDHandler(XMLDTDHandler dtdHandler) {
        fDTDHandler = dtdHandler;
    } public XMLDTDHandler getDTDHandler() {
        return fDTDHandler;
    } public void setDTDContentModelHandler(XMLDTDContentModelHandler
    dtdContentModelHandler) {
        fDTDContentModelHandler = dtdContentModelHandler;
    } public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return fDTDContentModelHandler ;
    } public void startEntity(String name,
                            XMLResourceIdentifier identifier,
                            String encoding, Augmentations augs) throws XNIException {

        super.startEntity(name, identifier, encoding, augs);

        boolean dtdEntity = name.equals("[dtd]");
        if (dtdEntity) {
            if (fDTDHandler != null && !fStartDTDCalled ) {
                fDTDHandler.startDTD(fEntityScanner, null);
            }
            if (fDTDHandler != null) {
                fDTDHandler.startExternalSubset(identifier,null);
            }
            fEntityManager.startExternalSubset();
            fEntityStore.startExternalSubset();
            fExtEntityDepth++;
        }
        else if (name.charAt(0) == '%') {
            pushPEStack(fMarkUpDepth, fReportEntity);
            if (fEntityScanner.isExternal()) {
                fExtEntityDepth++;
            }
        }

        if (fDTDHandler != null && !dtdEntity && fReportEntity) {
            fDTDHandler.startParameterEntity(name, identifier, encoding, null);
        }

    } public void endEntity(String name, Augmentations augs)
    throws XNIException, IOException {

        super.endEntity(name, augs);

        if (fScannerState == SCANNER_STATE_END_OF_INPUT)
            return;

        boolean reportEntity = fReportEntity;
        if (name.startsWith("%")) {
            reportEntity = peekReportEntity();
            int startMarkUpDepth = popPEStack();
            if(startMarkUpDepth == 0 &&
            startMarkUpDepth < fMarkUpDepth) {
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                "ILL_FORMED_PARAMETER_ENTITY_WHEN_USED_IN_DECL",
                new Object[]{ fEntityManager.fCurrentEntity.name},
                XMLErrorReporter.SEVERITY_FATAL_ERROR);
            }
            if (startMarkUpDepth != fMarkUpDepth) {
                reportEntity = false;
                if (fValidation) {
                    fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                    "ImproperDeclarationNesting",
                    new Object[]{ name },
                    XMLErrorReporter.SEVERITY_ERROR);
                }
            }
            if (fEntityScanner.isExternal()) {
                fExtEntityDepth--;
            }
        }

        boolean dtdEntity = name.equals("[dtd]");
        if (fDTDHandler != null && !dtdEntity && reportEntity) {
            fDTDHandler.endParameterEntity(name, null);
        }

        if (dtdEntity) {
            if (fIncludeSectDepth != 0) {
                reportFatalError("IncludeSectUnterminated", null);
            }
            fScannerState = SCANNER_STATE_END_OF_INPUT;
            fEntityManager.endExternalSubset();
            fEntityStore.endExternalSubset();

            if (fDTDHandler != null) {
                fDTDHandler.endExternalSubset(null);
                fDTDHandler.endDTD(null);
            }
            fExtEntityDepth--;
        }

        if (augs != null && Boolean.TRUE.equals(augs.getItem(Constants.LAST_ENTITY))
            && ( fMarkUpDepth != 0 || fExtEntityDepth !=0 || fIncludeSectDepth != 0)){
            throw new EOFException();
        }

    } protected final void setScannerState(int state) {

        fScannerState = state;
        if (DEBUG_SCANNER_STATE) {
            System.out.print("### setScannerState: ");
            System.out.print(getScannerStateName(state));
            }

    } private static String getScannerStateName(int state) {

        if (DEBUG_SCANNER_STATE) {
            switch (state) {
                case SCANNER_STATE_END_OF_INPUT: return "SCANNER_STATE_END_OF_INPUT";
                case SCANNER_STATE_TEXT_DECL: return "SCANNER_STATE_TEXT_DECL";
                case SCANNER_STATE_MARKUP_DECL: return "SCANNER_STATE_MARKUP_DECL";
            }
        }

        return "??? ("+state+')';

    } protected final boolean scanningInternalSubset() {
        return fExtEntityDepth == 0;
    }


    protected void startPE(String name, boolean literal)
    throws IOException, XNIException {
        int depth = fPEDepth;
        String pName = "%"+name;
        if (fValidation && !fEntityStore.isDeclaredEntity(pName)) {
            fErrorReporter.reportError( XMLMessageFormatter.XML_DOMAIN,"EntityNotDeclared",
            new Object[]{name}, XMLErrorReporter.SEVERITY_ERROR);
        }
        fEntityManager.startEntity(false, fSymbolTable.addSymbol(pName),
        literal);
        if (depth != fPEDepth && fEntityScanner.isExternal()) {
            scanTextDecl();
        }
    }


    protected final boolean scanTextDecl()
    throws IOException, XNIException {

        boolean textDecl = false;
        if (fEntityScanner.skipString("<?xml")) {
            fMarkUpDepth++;
            if (isValidNameChar(fEntityScanner.peekChar())) {
                fStringBuffer.clear();
                fStringBuffer.append("xml");
                while (isValidNameChar(fEntityScanner.peekChar())) {
                    fStringBuffer.append((char)fEntityScanner.scanChar(null));
                }
                String target =
                fSymbolTable.addSymbol(fStringBuffer.ch,
                fStringBuffer.offset,
                fStringBuffer.length);
                scanPIData(target, fString);
            }

            else {
                String version = null;
                String encoding = null;

                scanXMLDeclOrTextDecl(true, fStrings);
                textDecl = true;
                fMarkUpDepth--;

                version = fStrings[0];
                encoding = fStrings[1];

                fEntityScanner.setEncoding(encoding);

                if (fDTDHandler != null) {
                    fDTDHandler.textDecl(version, encoding, null);
                }
            }
        }
        fEntityManager.fCurrentEntity.mayReadChunks = true;

        return textDecl;

    } protected final void scanPIData(String target, XMLString data)
    throws IOException, XNIException {
        fMarkUpDepth--;

        if (fDTDHandler != null) {
            fDTDHandler.processingInstruction(target, data, null);
        }

    } protected final void scanComment() throws IOException, XNIException {

        fReportEntity = false;
        scanComment(fStringBuffer);
        fMarkUpDepth--;

        if (fDTDHandler != null) {
            fDTDHandler.comment(fStringBuffer, null);
        }
        fReportEntity = true;

    } protected final void scanElementDecl() throws IOException, XNIException {

        fReportEntity = false;
        if (!skipSeparator(true, !scanningInternalSubset())) {
            reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ELEMENTDECL",
            null);
        }

        String name = fEntityScanner.scanName(NameType.ELEMENTSTART);
        if (name == null) {
            reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_ELEMENTDECL",
            null);
        }

        if (!skipSeparator(true, !scanningInternalSubset())) {
            reportFatalError("MSG_SPACE_REQUIRED_BEFORE_CONTENTSPEC_IN_ELEMENTDECL",
            new Object[]{name});
        }

        if (fDTDContentModelHandler != null) {
            fDTDContentModelHandler.startContentModel(name, null);
        }
        String contentModel = null;
        fReportEntity = true;
        if (fEntityScanner.skipString("EMPTY")) {
            contentModel = "EMPTY";
            if (fDTDContentModelHandler != null) {
                fDTDContentModelHandler.empty(null);
            }
        }
        else if (fEntityScanner.skipString("ANY")) {
            contentModel = "ANY";
            if (fDTDContentModelHandler != null) {
                fDTDContentModelHandler.any(null);
            }
        }
        else {
            if (!fEntityScanner.skipChar('(', null)) {
                reportFatalError("MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN",
                new Object[]{name});
            }
            if (fDTDContentModelHandler != null) {
                fDTDContentModelHandler.startGroup(null);
            }
            fStringBuffer.clear();
            fStringBuffer.append('(');
            fMarkUpDepth++;
            skipSeparator(false, !scanningInternalSubset());

            if (fEntityScanner.skipString("#PCDATA")) {
                scanMixed(name);
            }
            else {              scanChildren(name);
            }
            contentModel = fStringBuffer.toString();
        }

        if (fDTDContentModelHandler != null) {
            fDTDContentModelHandler.endContentModel(null);
        }

        fReportEntity = false;
        skipSeparator(false, !scanningInternalSubset());
        if (!fEntityScanner.skipChar('>', null)) {
            reportFatalError("ElementDeclUnterminated", new Object[]{name});
        }
        fReportEntity = true;
        fMarkUpDepth--;

        if (fDTDHandler != null) {
            fDTDHandler.elementDecl(name, contentModel, null);
        }
        if (nonValidatingMode) nvGrammarInfo.elementDecl(name, contentModel, null);
    } private final void scanMixed(String elName)
    throws IOException, XNIException {

        String childName = null;

        fStringBuffer.append("#PCDATA");
        if (fDTDContentModelHandler != null) {
            fDTDContentModelHandler.pcdata(null);
        }
        skipSeparator(false, !scanningInternalSubset());
        while (fEntityScanner.skipChar('|', null)) {
            fStringBuffer.append('|');
            if (fDTDContentModelHandler != null) {
                fDTDContentModelHandler.separator(XMLDTDContentModelHandler.SEPARATOR_CHOICE,
                null);
            }
            skipSeparator(false, !scanningInternalSubset());

            childName = fEntityScanner.scanName(NameType.ENTITY);
            if (childName == null) {
                reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_MIXED_CONTENT",
                new Object[]{elName});
            }
            fStringBuffer.append(childName);
            if (fDTDContentModelHandler != null) {
                fDTDContentModelHandler.element(childName, null);
            }
            skipSeparator(false, !scanningInternalSubset());
        }
        if (fEntityScanner.skipString(")*")) {
            fStringBuffer.append(")*");
            if (fDTDContentModelHandler != null) {
                fDTDContentModelHandler.endGroup(null);
                fDTDContentModelHandler.occurrence(XMLDTDContentModelHandler.OCCURS_ZERO_OR_MORE,
                null);
            }
        }
        else if (childName != null) {
            reportFatalError("MixedContentUnterminated",
            new Object[]{elName});
        }
        else if (fEntityScanner.skipChar(')', null)){
            fStringBuffer.append(')');
            if (fDTDContentModelHandler != null) {
                fDTDContentModelHandler.endGroup(null);
            }
        }
        else {
            reportFatalError("MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN",
            new Object[]{elName});
        }
        fMarkUpDepth--;
        }


    private final void scanChildren(String elName)
    throws IOException, XNIException {

        fContentDepth = 0;
        pushContentStack(0);
        int currentOp = 0;
        int c;
        while (true) {
            if (fEntityScanner.skipChar('(', null)) {
                fMarkUpDepth++;
                fStringBuffer.append('(');
                if (fDTDContentModelHandler != null) {
                    fDTDContentModelHandler.startGroup(null);
                }
                pushContentStack(currentOp);
                currentOp = 0;
                skipSeparator(false, !scanningInternalSubset());
                continue;
            }
            skipSeparator(false, !scanningInternalSubset());
            String childName = fEntityScanner.scanName(NameType.ELEMENTSTART);
            if (childName == null) {
                reportFatalError("MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN",
                new Object[]{elName});
                return;
            }
            if (fDTDContentModelHandler != null) {
                fDTDContentModelHandler.element(childName, null);
            }
            fStringBuffer.append(childName);
            c = fEntityScanner.peekChar();
            if (c == '?' || c == '*' || c == '+') {
                if (fDTDContentModelHandler != null) {
                    short oc;
                    if (c == '?') {
                        oc = XMLDTDContentModelHandler.OCCURS_ZERO_OR_ONE;
                    }
                    else if (c == '*') {
                        oc = XMLDTDContentModelHandler.OCCURS_ZERO_OR_MORE;
                    }
                    else {
                        oc = XMLDTDContentModelHandler.OCCURS_ONE_OR_MORE;
                    }
                    fDTDContentModelHandler.occurrence(oc, null);
                }
                fEntityScanner.scanChar(null);
                fStringBuffer.append((char)c);
            }
            while (true) {
                skipSeparator(false, !scanningInternalSubset());
                c = fEntityScanner.peekChar();
                if (c == ',' && currentOp != '|') {
                    currentOp = c;
                    if (fDTDContentModelHandler != null) {
                        fDTDContentModelHandler.separator(XMLDTDContentModelHandler.SEPARATOR_SEQUENCE,
                        null);
                    }
                    fEntityScanner.scanChar(null);
                    fStringBuffer.append(',');
                    break;
                }
                else if (c == '|' && currentOp != ',') {
                    currentOp = c;
                    if (fDTDContentModelHandler != null) {
                        fDTDContentModelHandler.separator(XMLDTDContentModelHandler.SEPARATOR_CHOICE,
                        null);
                    }
                    fEntityScanner.scanChar(null);
                    fStringBuffer.append('|');
                    break;
                }
                else if (c != ')') {
                    reportFatalError("MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN",
                    new Object[]{elName});
                }
                if (fDTDContentModelHandler != null) {
                    fDTDContentModelHandler.endGroup(null);
                }
                currentOp = popContentStack();
                short oc;
                if (fEntityScanner.skipString(")?")) {
                    fStringBuffer.append(")?");
                    if (fDTDContentModelHandler != null) {
                        oc = XMLDTDContentModelHandler.OCCURS_ZERO_OR_ONE;
                        fDTDContentModelHandler.occurrence(oc, null);
                    }
                }
                else if (fEntityScanner.skipString(")+")) {
                    fStringBuffer.append(")+");
                    if (fDTDContentModelHandler != null) {
                        oc = XMLDTDContentModelHandler.OCCURS_ONE_OR_MORE;
                        fDTDContentModelHandler.occurrence(oc, null);
                    }
                }
                else if (fEntityScanner.skipString(")*")) {
                    fStringBuffer.append(")*");
                    if (fDTDContentModelHandler != null) {
                        oc = XMLDTDContentModelHandler.OCCURS_ZERO_OR_MORE;
                        fDTDContentModelHandler.occurrence(oc, null);
                    }
                }
                else {
                    fEntityScanner.scanChar(null);
                    fStringBuffer.append(')');
                }
                fMarkUpDepth--;
                if (fContentDepth == 0) {
                    return;
                }
            }
            skipSeparator(false, !scanningInternalSubset());
        }
    }


    protected final void scanAttlistDecl() throws IOException, XNIException {

        fReportEntity = false;
        if (!skipSeparator(true, !scanningInternalSubset())) {
            reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ATTLISTDECL",
            null);
        }

        String elName = fEntityScanner.scanName(NameType.ELEMENTSTART);
        if (elName == null) {
            reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_ATTLISTDECL",
            null);
        }

        if (fDTDHandler != null) {
            fDTDHandler.startAttlist(elName, null);
        }

        if (!skipSeparator(true, !scanningInternalSubset())) {
            if (fEntityScanner.skipChar('>', null)) {
                if (fDTDHandler != null) {
                    fDTDHandler.endAttlist(null);
                }
                fMarkUpDepth--;
                return;
            }
            else {
                reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ATTRIBUTE_NAME_IN_ATTDEF",
                new Object[]{elName});
            }
        }

        while (!fEntityScanner.skipChar('>', null)) {
            String name = fEntityScanner.scanName(NameType.ATTRIBUTENAME);
            if (name == null) {
                reportFatalError("AttNameRequiredInAttDef",
                new Object[]{elName});
            }
            if (!skipSeparator(true, !scanningInternalSubset())) {
                reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ATTTYPE_IN_ATTDEF",
                new Object[]{elName, name});
            }
            String type = scanAttType(elName, name);

            if (!skipSeparator(true, !scanningInternalSubset())) {
                reportFatalError("MSG_SPACE_REQUIRED_BEFORE_DEFAULTDECL_IN_ATTDEF",
                new Object[]{elName, name});
            }

            String defaultType = scanAttDefaultDecl(elName, name,
            type,
            fLiteral, fLiteral2);
            String[] enumr = null;
            if( fDTDHandler != null || nonValidatingMode){
                if (fEnumerationCount != 0) {
                    enumr = new String[fEnumerationCount];
                    System.arraycopy(fEnumeration, 0, enumr,
                    0, fEnumerationCount);
                }
            }
            if (defaultType!=null && (defaultType.equals("#REQUIRED") ||
            defaultType.equals("#IMPLIED"))) {
                if (fDTDHandler != null){
                    fDTDHandler.attributeDecl(elName, name, type, enumr,
                    defaultType, null, null, null);
                }
                if(nonValidatingMode){
                    nvGrammarInfo.attributeDecl(elName, name, type, enumr,
                    defaultType, null, null, null);

                }
            }
            else {
                if (fDTDHandler != null){
                    fDTDHandler.attributeDecl(elName, name, type, enumr,
                    defaultType, fLiteral, fLiteral2, null);
                }
                if(nonValidatingMode){
                    nvGrammarInfo.attributeDecl(elName, name, type, enumr,
                    defaultType, fLiteral, fLiteral2, null);
                }
            }
            skipSeparator(false, !scanningInternalSubset());
        }

        if (fDTDHandler != null) {
            fDTDHandler.endAttlist(null);
        }
        fMarkUpDepth--;
        fReportEntity = true;

    } private final String scanAttType(String elName, String atName)
    throws IOException, XNIException {

        String type = null;
        fEnumerationCount = 0;

        if (fEntityScanner.skipString("CDATA")) {
            type = "CDATA";
        }
        else if (fEntityScanner.skipString("IDREFS")) {
            type = "IDREFS";
        }
        else if (fEntityScanner.skipString("IDREF")) {
            type = "IDREF";
        }
        else if (fEntityScanner.skipString("ID")) {
            type = "ID";
        }
        else if (fEntityScanner.skipString("ENTITY")) {
            type = "ENTITY";
        }
        else if (fEntityScanner.skipString("ENTITIES")) {
            type = "ENTITIES";
        }
        else if (fEntityScanner.skipString("NMTOKENS")) {
            type = "NMTOKENS";
        }
        else if (fEntityScanner.skipString("NMTOKEN")) {
            type = "NMTOKEN";
        }
        else if (fEntityScanner.skipString("NOTATION")) {
            type = "NOTATION";
            if (!skipSeparator(true, !scanningInternalSubset())) {
                reportFatalError("MSG_SPACE_REQUIRED_AFTER_NOTATION_IN_NOTATIONTYPE",
                new Object[]{elName, atName});
            }
            int c = fEntityScanner.scanChar(null);
            if (c != '(') {
                reportFatalError("MSG_OPEN_PAREN_REQUIRED_IN_NOTATIONTYPE",
                new Object[]{elName, atName});
            }
            fMarkUpDepth++;
            do {
                skipSeparator(false, !scanningInternalSubset());
                String aName = fEntityScanner.scanName(NameType.ATTRIBUTENAME);
                if (aName == null) {
                    reportFatalError("MSG_NAME_REQUIRED_IN_NOTATIONTYPE",
                    new Object[]{elName, atName});
                }
                ensureEnumerationSize(fEnumerationCount + 1);
                fEnumeration[fEnumerationCount++] = aName;
                skipSeparator(false, !scanningInternalSubset());
                c = fEntityScanner.scanChar(null);
            } while (c == '|');
            if (c != ')') {
                reportFatalError("NotationTypeUnterminated",
                new Object[]{elName, atName});
            }
            fMarkUpDepth--;
        }
        else {              type = "ENUMERATION";
            int c = fEntityScanner.scanChar(null);
            if (c != '(') {
                reportFatalError("AttTypeRequiredInAttDef",
                new Object[]{elName, atName});
            }
            fMarkUpDepth++;
            do {
                skipSeparator(false, !scanningInternalSubset());
                String token = fEntityScanner.scanNmtoken();
                if (token == null) {
                    reportFatalError("MSG_NMTOKEN_REQUIRED_IN_ENUMERATION",
                    new Object[]{elName, atName});
                }
                ensureEnumerationSize(fEnumerationCount + 1);
                fEnumeration[fEnumerationCount++] = token;
                skipSeparator(false, !scanningInternalSubset());
                c = fEntityScanner.scanChar(null);
            } while (c == '|');
            if (c != ')') {
                reportFatalError("EnumerationUnterminated",
                new Object[]{elName, atName});
            }
            fMarkUpDepth--;
        }
        return type;

    } protected final String scanAttDefaultDecl(String elName, String atName,
    String type,
    XMLString defaultVal,
    XMLString nonNormalizedDefaultVal)
    throws IOException, XNIException {

        String defaultType = null;
        fString.clear();
        defaultVal.clear();
        if (fEntityScanner.skipString("#REQUIRED")) {
            defaultType = "#REQUIRED";
        }
        else if (fEntityScanner.skipString("#IMPLIED")) {
            defaultType = "#IMPLIED";
        }
        else {
            if (fEntityScanner.skipString("#FIXED")) {
                defaultType = "#FIXED";
                if (!skipSeparator(true, !scanningInternalSubset())) {
                    reportFatalError("MSG_SPACE_REQUIRED_AFTER_FIXED_IN_DEFAULTDECL",
                    new Object[]{elName, atName});
                }
            }
            boolean isVC = !fStandalone  &&  (fSeenExternalDTD || fSeenExternalPE) ;
            scanAttributeValue(defaultVal, nonNormalizedDefaultVal, atName,
            fAttributes, 0, isVC, elName, false);
        }
        return defaultType;

    } private final void scanEntityDecl() throws IOException, XNIException {

        boolean isPEDecl = false;
        boolean sawPERef = false;
        fReportEntity = false;
        if (fEntityScanner.skipSpaces()) {
            if (!fEntityScanner.skipChar('%', NameType.REFERENCE)) {
                isPEDecl = false; }
            else if (skipSeparator(true, !scanningInternalSubset())) {
                isPEDecl = true;
            }
            else if (scanningInternalSubset()) {
                reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL",
                null);
                isPEDecl = true;
            }
            else if (fEntityScanner.peekChar() == '%') {
                skipSeparator(false, !scanningInternalSubset());
                isPEDecl = true;
            }
            else {
                sawPERef = true;
            }
        }
        else if (scanningInternalSubset() || !fEntityScanner.skipChar('%', NameType.REFERENCE)) {
            reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL",
            null);
            isPEDecl = false;
        }
        else if (fEntityScanner.skipSpaces()) {
            reportFatalError("MSG_SPACE_REQUIRED_BEFORE_PERCENT_IN_PEDECL",
            null);
            isPEDecl = false;
        }
        else {
            sawPERef = true;
        }
        if (sawPERef) {
            while (true) {
                String peName = fEntityScanner.scanName(NameType.REFERENCE);
                if (peName == null) {
                    reportFatalError("NameRequiredInPEReference", null);
                }
                else if (!fEntityScanner.skipChar(';', NameType.REFERENCE)) {
                    reportFatalError("SemicolonRequiredInPEReference",
                    new Object[]{peName});
                }
                else {
                    startPE(peName, false);
                }
                fEntityScanner.skipSpaces();
                if (!fEntityScanner.skipChar('%', NameType.REFERENCE))
                    break;
                if (!isPEDecl) {
                    if (skipSeparator(true, !scanningInternalSubset())) {
                        isPEDecl = true;
                        break;
                    }
                    isPEDecl = fEntityScanner.skipChar('%', NameType.REFERENCE);
                }
            }
        }

        String name = fEntityScanner.scanName(NameType.ENTITY);
        if (name == null) {
            reportFatalError("MSG_ENTITY_NAME_REQUIRED_IN_ENTITYDECL", null);
        }

        if (!skipSeparator(true, !scanningInternalSubset())) {
            reportFatalError("MSG_SPACE_REQUIRED_AFTER_ENTITY_NAME_IN_ENTITYDECL",
            new Object[]{name});
        }

        scanExternalID(fStrings, false);
        String systemId = fStrings[0];
        String publicId = fStrings[1];

        if (isPEDecl && systemId != null) {
            fSeenExternalPE = true;
        }

        String notation = null;
        boolean sawSpace = skipSeparator(true, !scanningInternalSubset());
        if (!isPEDecl && fEntityScanner.skipString("NDATA")) {
            if (!sawSpace) {
                reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NDATA_IN_UNPARSED_ENTITYDECL",
                new Object[]{name});
            }

            if (!skipSeparator(true, !scanningInternalSubset())) {
                reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_UNPARSED_ENTITYDECL",
                new Object[]{name});
            }
            notation = fEntityScanner.scanName(NameType.NOTATION);
            if (notation == null) {
                reportFatalError("MSG_NOTATION_NAME_REQUIRED_FOR_UNPARSED_ENTITYDECL",
                new Object[]{name});
            }
        }

        if (systemId == null) {
            scanEntityValue(name, isPEDecl, fLiteral, fLiteral2);
            fStringBuffer.clear();
            fStringBuffer2.clear();
            fStringBuffer.append(fLiteral.ch, fLiteral.offset, fLiteral.length);
            fStringBuffer2.append(fLiteral2.ch, fLiteral2.offset, fLiteral2.length);
        }

        skipSeparator(false, !scanningInternalSubset());

        if (!fEntityScanner.skipChar('>', null)) {
            reportFatalError("EntityDeclUnterminated", new Object[]{name});
        }
        fMarkUpDepth--;

        if (isPEDecl) {
            name = "%" + name;
        }
        if (systemId != null) {
            String baseSystemId = fEntityScanner.getBaseSystemId();
            if (notation != null) {
                fEntityStore.addUnparsedEntity(name, publicId, systemId, baseSystemId, notation);
            }
            else {
                fEntityStore.addExternalEntity(name, publicId, systemId,
                baseSystemId);
            }
            if (fDTDHandler != null) {
                fResourceIdentifier.setValues(publicId, systemId, baseSystemId, XMLEntityManager.expandSystemId(systemId, baseSystemId ));

                if (notation != null) {
                    fDTDHandler.unparsedEntityDecl(name, fResourceIdentifier,
                    notation, null);
                }
                else {
                    fDTDHandler.externalEntityDecl(name, fResourceIdentifier, null);
                }
            }
        }
        else {
            fEntityStore.addInternalEntity(name, fStringBuffer.toString());
            if (fDTDHandler != null) {
                fDTDHandler.internalEntityDecl(name, fStringBuffer, fStringBuffer2, null);
            }
        }
        fReportEntity = true;

    } protected final void scanEntityValue(String entityName, boolean isPEDecl, XMLString value,
    XMLString nonNormalizedValue)
    throws IOException, XNIException {
        int quote = fEntityScanner.scanChar(null);
        if (quote != '\'' && quote != '"') {
            reportFatalError("OpenQuoteMissingInDecl", null);
        }
        int entityDepth = fEntityDepth;

        XMLString literal = fString;
        XMLString literal2 = fString;
        int countChar = 0;
        if (fLimitAnalyzer == null ) {
            fLimitAnalyzer = fEntityManager.fLimitAnalyzer;
         }
        fLimitAnalyzer.startEntity(entityName);

        if (fEntityScanner.scanLiteral(quote, fString, false) != quote) {
            fStringBuffer.clear();
            fStringBuffer2.clear();
            int offset;
            do {
                countChar = 0;
                offset = fStringBuffer.length;
                fStringBuffer.append(fString);
                fStringBuffer2.append(fString);
                if (fEntityScanner.skipChar('&', NameType.REFERENCE)) {
                    if (fEntityScanner.skipChar('#', NameType.REFERENCE)) {
                        fStringBuffer2.append("&#");
                        scanCharReferenceValue(fStringBuffer, fStringBuffer2);
                    }
                    else {
                        fStringBuffer.append('&');
                        fStringBuffer2.append('&');
                        String eName = fEntityScanner.scanName(NameType.REFERENCE);
                        if (eName == null) {
                            reportFatalError("NameRequiredInReference",
                            null);
                        }
                        else {
                            fStringBuffer.append(eName);
                            fStringBuffer2.append(eName);
                        }
                        if (!fEntityScanner.skipChar(';', NameType.REFERENCE)) {
                            reportFatalError("SemicolonRequiredInReference",
                            new Object[]{eName});
                        }
                        else {
                            fStringBuffer.append(';');
                            fStringBuffer2.append(';');
                        }
                    }
                }
                else if (fEntityScanner.skipChar('%', NameType.REFERENCE)) {
                    while (true) {
                        fStringBuffer2.append('%');
                        String peName = fEntityScanner.scanName(NameType.REFERENCE);
                        if (peName == null) {
                            reportFatalError("NameRequiredInPEReference",
                            null);
                        }
                        else if (!fEntityScanner.skipChar(';', NameType.REFERENCE)) {
                            reportFatalError("SemicolonRequiredInPEReference",
                            new Object[]{peName});
                        }
                        else {
                            if (scanningInternalSubset()) {
                                reportFatalError("PEReferenceWithinMarkup",
                                new Object[]{peName});
                            }
                            fStringBuffer2.append(peName);
                            fStringBuffer2.append(';');
                        }
                        startPE(peName, true);
                        fEntityScanner.skipSpaces();
                        if (!fEntityScanner.skipChar('%', NameType.REFERENCE))
                            break;
                    }
                }
                else {
                    int c = fEntityScanner.peekChar();
                    if (XMLChar.isHighSurrogate(c)) {
                        countChar++;
                        scanSurrogates(fStringBuffer2);
                    }
                    else if (isInvalidLiteral(c)) {
                        reportFatalError("InvalidCharInLiteral",
                        new Object[]{Integer.toHexString(c)});
                        fEntityScanner.scanChar(null);
                    }
                    else if (c != quote || entityDepth != fEntityDepth) {
                        fStringBuffer.append((char)c);
                        fStringBuffer2.append((char)c);
                        fEntityScanner.scanChar(null);
                    }
                }
                checkEntityLimit(isPEDecl, entityName, fStringBuffer.length - offset + countChar);
            } while (fEntityScanner.scanLiteral(quote, fString, false) != quote);
            checkEntityLimit(isPEDecl, entityName, fString.length);
            fStringBuffer.append(fString);
            fStringBuffer2.append(fString);
            literal = fStringBuffer;
            literal2 = fStringBuffer2;
        } else {
            checkEntityLimit(isPEDecl, entityName, literal);
        }
        value.setValues(literal);
        nonNormalizedValue.setValues(literal2);
        if (fLimitAnalyzer != null) {
            if (isPEDecl) {
                fLimitAnalyzer.endEntity(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, entityName);
            } else {
                fLimitAnalyzer.endEntity(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, entityName);
            }
        }

        if (!fEntityScanner.skipChar(quote, null)) {
            reportFatalError("CloseQuoteMissingInDecl", null);
        }
    } private final void scanNotationDecl() throws IOException, XNIException {

        fReportEntity = false;
        if (!skipSeparator(true, !scanningInternalSubset())) {
            reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_NOTATIONDECL",
            null);
        }

        String name = fEntityScanner.scanName(NameType.NOTATION);
        if (name == null) {
            reportFatalError("MSG_NOTATION_NAME_REQUIRED_IN_NOTATIONDECL",
            null);
        }

        if (!skipSeparator(true, !scanningInternalSubset())) {
            reportFatalError("MSG_SPACE_REQUIRED_AFTER_NOTATION_NAME_IN_NOTATIONDECL",
            new Object[]{name});
        }

        scanExternalID(fStrings, true);
        String systemId = fStrings[0];
        String publicId = fStrings[1];
        String baseSystemId = fEntityScanner.getBaseSystemId();

        if (systemId == null && publicId == null) {
            reportFatalError("ExternalIDorPublicIDRequired",
            new Object[]{name});
        }

        skipSeparator(false, !scanningInternalSubset());

        if (!fEntityScanner.skipChar('>', null)) {
            reportFatalError("NotationDeclUnterminated", new Object[]{name});
        }
        fMarkUpDepth--;

        fResourceIdentifier.setValues(publicId, systemId, baseSystemId, XMLEntityManager.expandSystemId(systemId, baseSystemId ));
        if (nonValidatingMode) nvGrammarInfo.notationDecl(name, fResourceIdentifier, null);
        if (fDTDHandler != null) {
            fDTDHandler.notationDecl(name, fResourceIdentifier, null);
        }
        fReportEntity = true;

    } private final void scanConditionalSect(int currPEDepth)
    throws IOException, XNIException {

        fReportEntity = false;
        skipSeparator(false, !scanningInternalSubset());

        if (fEntityScanner.skipString("INCLUDE")) {
            skipSeparator(false, !scanningInternalSubset());
            if(currPEDepth != fPEDepth && fValidation) {
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                "INVALID_PE_IN_CONDITIONAL",
                new Object[]{ fEntityManager.fCurrentEntity.name},
                XMLErrorReporter.SEVERITY_ERROR);
            }
            if (!fEntityScanner.skipChar('[', null)) {
                reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
            }

            if (fDTDHandler != null) {
                fDTDHandler.startConditional(XMLDTDHandler.CONDITIONAL_INCLUDE,
                null);
            }
            fIncludeSectDepth++;
            fReportEntity = true;
        }
        else if (fEntityScanner.skipString("IGNORE")) {
            skipSeparator(false, !scanningInternalSubset());
            if(currPEDepth != fPEDepth && fValidation) {
                fErrorReporter.reportError(XMLMessageFormatter.XML_DOMAIN,
                "INVALID_PE_IN_CONDITIONAL",
                new Object[]{ fEntityManager.fCurrentEntity.name},
                XMLErrorReporter.SEVERITY_ERROR);
            }
            if (fDTDHandler != null) {
                fDTDHandler.startConditional(XMLDTDHandler.CONDITIONAL_IGNORE,
                null);
            }
            if (!fEntityScanner.skipChar('[', null)) {
                reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
            }
            fReportEntity = true;
            int initialDepth = ++fIncludeSectDepth;
            if (fDTDHandler != null) {
                fIgnoreConditionalBuffer.clear();
            }
            while (true) {
                if (fEntityScanner.skipChar('<', null)) {
                    if (fDTDHandler != null) {
                        fIgnoreConditionalBuffer.append('<');
                    }
                    if (fEntityScanner.skipChar('!', null)) {
                        if(fEntityScanner.skipChar('[', null)) {
                            if (fDTDHandler != null) {
                                fIgnoreConditionalBuffer.append("![");
                            }
                            fIncludeSectDepth++;
                        } else {
                            if (fDTDHandler != null) {
                                fIgnoreConditionalBuffer.append("!");
                            }
                        }
                    }
                }
                else if (fEntityScanner.skipChar(']', null)) {
                    if (fDTDHandler != null) {
                        fIgnoreConditionalBuffer.append(']');
                    }
                    if (fEntityScanner.skipChar(']', null)) {
                        if (fDTDHandler != null) {
                            fIgnoreConditionalBuffer.append(']');
                        }
                        while (fEntityScanner.skipChar(']', null)) {

                            if (fDTDHandler != null) {
                                fIgnoreConditionalBuffer.append(']');
                            }
                        }
                        if (fEntityScanner.skipChar('>', null)) {
                            if (fIncludeSectDepth-- == initialDepth) {
                                fMarkUpDepth--;
                                if (fDTDHandler != null) {
                                    fLiteral.setValues(fIgnoreConditionalBuffer.ch, 0,
                                    fIgnoreConditionalBuffer.length - 2);
                                    fDTDHandler.ignoredCharacters(fLiteral, null);
                                    fDTDHandler.endConditional(null);
                                }
                                return;
                            } else if(fDTDHandler != null) {
                                fIgnoreConditionalBuffer.append('>');
                            }
                        }
                    }
                }
                else {
                    int c = fEntityScanner.scanChar(null);
                    if (fScannerState == SCANNER_STATE_END_OF_INPUT) {
                        reportFatalError("IgnoreSectUnterminated", null);
                        return;
                    }
                    if (fDTDHandler != null) {
                        fIgnoreConditionalBuffer.append((char)c);
                    }
                }
            }
        }
        else {
            reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
        }

    } protected final boolean scanDecls(boolean complete)
    throws IOException, XNIException {

        skipSeparator(false, true);
        boolean again = true;
        while (again && fScannerState == SCANNER_STATE_MARKUP_DECL) {
            again = complete;
            if (fEntityScanner.skipChar('<', null)) {
                fMarkUpDepth++;
                if (fEntityScanner.skipChar('?', null)) {
                    fStringBuffer.clear();
                    scanPI(fStringBuffer);
                    fMarkUpDepth--; }
                else if (fEntityScanner.skipChar('!', null)) {
                    if (fEntityScanner.skipChar('-', null)) {
                        if (!fEntityScanner.skipChar('-', null)) {
                            reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD",
                            null);
                        } else {
                            scanComment();
                        }
                    }
                    else if (fEntityScanner.skipString("ELEMENT")) {
                        scanElementDecl();
                    }
                    else if (fEntityScanner.skipString("ATTLIST")) {
                        scanAttlistDecl();
                    }
                    else if (fEntityScanner.skipString("ENTITY")) {
                        scanEntityDecl();
                    }
                    else if (fEntityScanner.skipString("NOTATION")) {
                        scanNotationDecl();
                    }
                    else if (fEntityScanner.skipChar('[', null) &&
                    !scanningInternalSubset()) {
                        scanConditionalSect(fPEDepth);
                    }
                    else {
                        fMarkUpDepth--;
                        reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD",
                        null);
                    }
                }
                else {
                    fMarkUpDepth--;
                    reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
                }
            }
            else if (fIncludeSectDepth > 0 && fEntityScanner.skipChar(']', null)) {
                if (!fEntityScanner.skipChar(']', null)
                || !fEntityScanner.skipChar('>', null)) {
                    reportFatalError("IncludeSectUnterminated", null);
                }
                if (fDTDHandler != null) {
                    fDTDHandler.endConditional(null);
                }
                fIncludeSectDepth--;
                fMarkUpDepth--;
            }
            else if (scanningInternalSubset() &&
            fEntityScanner.peekChar() == ']') {
                return false;
            }
            else if (fEntityScanner.skipSpaces()) {
                }
            else {
                reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
            }
            skipSeparator(false, true);
        }
        return fScannerState != SCANNER_STATE_END_OF_INPUT;
    }


    private boolean skipSeparator(boolean spaceRequired, boolean lookForPERefs)
    throws IOException, XNIException {
        int depth = fPEDepth;
        boolean sawSpace = fEntityScanner.skipSpaces();
        if (!lookForPERefs || !fEntityScanner.skipChar('%', NameType.REFERENCE)) {
            return !spaceRequired || sawSpace || (depth != fPEDepth);
        }
        while (true) {
            String name = fEntityScanner.scanName(NameType.ENTITY);
            if (name == null) {
                reportFatalError("NameRequiredInPEReference", null);
            }
            else if (!fEntityScanner.skipChar(';', NameType.REFERENCE)) {
                reportFatalError("SemicolonRequiredInPEReference",
                new Object[]{name});
            }
            startPE(name, false);
            fEntityScanner.skipSpaces();
            if (!fEntityScanner.skipChar('%', NameType.REFERENCE))
                return true;
        }
    }



    private final void pushContentStack(int c) {
        if (fContentStack.length == fContentDepth) {
            int[] newStack = new int[fContentDepth * 2];
            System.arraycopy(fContentStack, 0, newStack, 0, fContentDepth);
            fContentStack = newStack;
        }
        fContentStack[fContentDepth++] = c;
    }

    private final int popContentStack() {
        return fContentStack[--fContentDepth];
    }



    private final void pushPEStack(int depth, boolean report) {
        if (fPEStack.length == fPEDepth) {
            int[] newIntStack = new int[fPEDepth * 2];
            System.arraycopy(fPEStack, 0, newIntStack, 0, fPEDepth);
            fPEStack = newIntStack;
            boolean[] newBooleanStack = new boolean[fPEDepth * 2];
            System.arraycopy(fPEReport, 0, newBooleanStack, 0, fPEDepth);
            fPEReport = newBooleanStack;

        }
        fPEReport[fPEDepth] = report;
        fPEStack[fPEDepth++] = depth;
    }


    private final int popPEStack() {
        return fPEStack[--fPEDepth];
    }


    private final boolean peekReportEntity() {
        return fPEReport[fPEDepth-1];
    }



    private final void ensureEnumerationSize(int size) {
        if (fEnumeration.length == size) {
            String[] newEnum = new String[size * 2];
            System.arraycopy(fEnumeration, 0, newEnum, 0, size);
            fEnumeration = newEnum;
        }
    }

    private void init() {
        fStartDTDCalled = false;
        fExtEntityDepth = 0;
        fIncludeSectDepth = 0;
        fMarkUpDepth = 0;
        fPEDepth = 0;

        fStandalone = false;
        fSeenExternalDTD = false;
        fSeenExternalPE = false;

        setScannerState(SCANNER_STATE_TEXT_DECL);
        fLimitAnalyzer = fEntityManager.fLimitAnalyzer;
        fSecurityManager = fEntityManager.fSecurityManager;
    }

    public DTDGrammar getGrammar(){
        return nvGrammarInfo;
    }

}