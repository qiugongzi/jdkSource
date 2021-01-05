

package com.sun.org.apache.xerces.internal.xinclude;


import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Stack;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dtd.DTDGrammar;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
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
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;

public class XPointerElementHandler implements XPointerSchema {


    protected static final String ERROR_REPORTER =
    Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    protected static final String GRAMMAR_POOL =
    Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;


    protected static final String ENTITY_RESOLVER =
    Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;

    protected static final String XPOINTER_SCHEMA =
    Constants.XERCES_PROPERTY_PREFIX + Constants.XPOINTER_SCHEMA_PROPERTY;


    private static final String[] RECOGNIZED_FEATURES = {
    };


    private static final Boolean[] FEATURE_DEFAULTS = {
    };



    private static final String[] RECOGNIZED_PROPERTIES =
    { ERROR_REPORTER, GRAMMAR_POOL, ENTITY_RESOLVER, XPOINTER_SCHEMA };


    private static final Object[] PROPERTY_DEFAULTS = { null, null, null,null };

    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;

    protected XIncludeHandler fParentXIncludeHandler;

    protected XMLLocator fDocLocation;
    protected XIncludeNamespaceSupport fNamespaceContext;
    protected XMLErrorReporter fErrorReporter;
    protected XMLGrammarPool fGrammarPool;
    protected XMLGrammarDescription fGrammarDesc;
    protected DTDGrammar fDTDGrammar;
    protected XMLEntityResolver fEntityResolver;
    protected ParserConfigurationSettings fSettings;
    protected StringBuffer fPointer;
    private int elemCount = 0;


    private int fDepth;

    private int fRootDepth;

    private static final int INITIAL_SIZE = 8;


    private boolean[] fSawInclude = new boolean[INITIAL_SIZE];


    private boolean[] fSawFallback = new boolean[INITIAL_SIZE];


    private int[] fState = new int[INITIAL_SIZE];

    QName foundElement = null;
    boolean skip = false;
    public XPointerElementHandler() {


        fDepth = 0;
        fRootDepth = 0;
        fSawFallback[fDepth] = false;
        fSawInclude[fDepth] = false;
        fSchemaName="element";


    }

    public void reset(){
        elemCount =0;
        fPointerToken = null;
        fCurrentTokenint=0;
        fCurrentTokenString=null;
        fCurrentTokenType=0 ;
        fElementCount =0;
        fCurrentToken =0;
        includeElement = false;
        foundElement = null;
        skip = false;
        fSubResourceIdentified=false;
    }

    public void reset(XMLComponentManager componentManager)
    throws XNIException {
        fNamespaceContext = null;
        elemCount =0;
        fDepth = 0;
        fRootDepth = 0;
        fPointerToken = null;
        fCurrentTokenint=0;
        fCurrentTokenString=null;
        fCurrentTokenType=0 ;
        foundElement = null;
        includeElement = false;
        skip = false;
        fSubResourceIdentified=false;




        try {
            setErrorReporter(
            (XMLErrorReporter)componentManager.getProperty(ERROR_REPORTER));
        }
        catch (XMLConfigurationException e) {
            fErrorReporter = null;
        }
        try {
            fGrammarPool =
            (XMLGrammarPool)componentManager.getProperty(GRAMMAR_POOL);
        }
        catch (XMLConfigurationException e) {
            fGrammarPool = null;
        }
        try {
            fEntityResolver =
            (XMLEntityResolver)componentManager.getProperty(
            ENTITY_RESOLVER);
        }
        catch (XMLConfigurationException e) {
            fEntityResolver = null;
        }

        fSettings = new ParserConfigurationSettings();

        Enumeration xercesFeatures = Constants.getXercesFeatures();
        while (xercesFeatures.hasMoreElements()) {
            String featureId = (String)xercesFeatures.nextElement();
            fSettings.addRecognizedFeatures(new String[] { featureId });
            try {
                fSettings.setFeature(
                featureId,
                componentManager.getFeature(featureId));
            }
            catch (XMLConfigurationException e) {
                }
        }


    } public String[] getRecognizedFeatures() {
        return RECOGNIZED_FEATURES;
    } public void setFeature(String featureId, boolean state)
    throws XMLConfigurationException {
        if (fSettings != null) {
            fSettings.setFeature(featureId, state);
        }

    } public String[] getRecognizedProperties() {
        return RECOGNIZED_PROPERTIES;
    } public void setProperty(String propertyId, Object value)
    throws XMLConfigurationException {
        if (propertyId.equals(ERROR_REPORTER)) {
            setErrorReporter((XMLErrorReporter)value);
        }
        if (propertyId.equals(GRAMMAR_POOL)) {
            fGrammarPool = (XMLGrammarPool)value;
        }
        if (propertyId.equals(ENTITY_RESOLVER)) {
            fEntityResolver = (XMLEntityResolver)value;
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
    } private void setErrorReporter(XMLErrorReporter reporter) {
        fErrorReporter = reporter;
        if (fErrorReporter != null) {
            fErrorReporter.putMessageFormatter(
            XIncludeMessageFormatter.XINCLUDE_DOMAIN,
            new XIncludeMessageFormatter());
        }
    }
    public void setDocumentHandler(XMLDocumentHandler handler) {
        fDocumentHandler = handler;
    }

    public XMLDocumentHandler getDocumentHandler() {
        return fDocumentHandler;
    }

    String fSchemaName;
    String fSchemaPointer;
    boolean fSubResourceIdentified;

    public void setXPointerSchemaName(String schemaName){
        fSchemaName = schemaName;
    }


    public String getXpointerSchemaName(){
        return fSchemaName;
    }


    public void setParent(Object parent){
        fParentXIncludeHandler = (XIncludeHandler)parent;
    }



    public Object getParent(){
        return fParentXIncludeHandler;
    }


    public void setXPointerSchemaPointer(String content){
        fSchemaPointer = content;
    }


    public String getXPointerSchemaPointer(){
        return fSchemaPointer;
    }

    public boolean isSubResourceIndentified(){
        return fSubResourceIdentified;
    }

    Stack fPointerToken = new Stack();
    int  fCurrentTokenint=0;
    String fCurrentTokenString=null;
    int fCurrentTokenType=0 ;public void getTokens(){
        fSchemaPointer = fSchemaPointer.substring(fSchemaPointer.indexOf("(")+1, fSchemaPointer.length());
        StringTokenizer st = new StringTokenizer(fSchemaPointer, "/");
        String tempToken;
        Integer integerToken =null;
        Stack tempPointerToken = new Stack();
        if(fPointerToken == null){
            fPointerToken = new Stack();
        }
        while(st.hasMoreTokens()){
            tempToken=st.nextToken();
            try {
                integerToken = Integer.valueOf(tempToken);
                tempPointerToken.push(integerToken);
            }catch(NumberFormatException e){
                tempPointerToken.push(tempToken);
            }
        }
        while(!tempPointerToken.empty()){
            fPointerToken.push(tempPointerToken.pop());
        }
    }public boolean hasMoreToken(){
        if(fPointerToken.isEmpty())
            return false;
        else
            return true;
    }

    public boolean getNextToken(){
        Object currentToken;
        if (!fPointerToken.isEmpty()){
            currentToken = fPointerToken.pop();
            if(currentToken instanceof Integer){
                fCurrentTokenint = ((Integer)currentToken).intValue();
                fCurrentTokenType = 1;
            }
            else{
                fCurrentTokenString = ((String)currentToken).toString();
                fCurrentTokenType = 2;
            }
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isIdAttribute(XMLAttributes attributes,Augmentations augs, int index) {
        Object o = augs.getItem(Constants.ID_ATTRIBUTE);
        if( o instanceof Boolean )
            return ((Boolean)o).booleanValue();
        return "ID".equals(attributes.getType(index));
    }

    public boolean checkStringToken(QName element, XMLAttributes attributes){
        QName cacheQName = null;
        String id =null;
        String rawname =null;
        QName attrName = new QName();
        String attrType = null;
        String attrValue = null;
        int attrCount = attributes.getLength();
        for (int i = 0; i < attrCount; i++) {
            Augmentations aaugs = attributes.getAugmentations(i);
            attributes.getName(i,attrName);
            attrType = attributes.getType(i);
            attrValue = attributes.getValue(i);
            if(attrType != null && attrValue!= null && isIdAttribute(attributes,aaugs,i) && attrValue.equals(fCurrentTokenString)){
                if(hasMoreToken()){
                    fCurrentTokenType = 0;
                    fCurrentTokenString = null;
                    return true;
                }
                else{
                    foundElement = element;
                    includeElement = true;
                    fCurrentTokenType = 0;
                    fCurrentTokenString = null;
                    fSubResourceIdentified = true;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkIntegerToken(QName element){
        if(!skip){
            fElementCount++;
            if(fCurrentTokenint == fElementCount){
                if(hasMoreToken()){
                    fElementCount=0;
                    fCurrentTokenType = 0;
                    return true;
                }
                else{
                    foundElement = element;
                    includeElement = true;
                    fCurrentTokenType = 0;
                    fElementCount=0;
                    fSubResourceIdentified =true;
                    return true;
                }
            }else{
                addQName(element);
                skip = true;
                return false;
            }
        }
        return false;
    }

    public void addQName(QName element){
        QName cacheQName = new QName(element);
        ftempCurrentElement.push(cacheQName);
    }

    public void startDocument(XMLLocator locator, String encoding,
    NamespaceContext namespaceContext, Augmentations augs)
    throws XNIException {

        getTokens();
    }

    public void doctypeDecl(String rootElement, String publicId, String systemId,
    Augmentations augs)throws XNIException {
    }

    public void xmlDecl(String version, String encoding, String standalone,
    Augmentations augs) throws XNIException {
    }


    public void comment(XMLString text, Augmentations augs)
    throws XNIException {
        if (fDocumentHandler != null && includeElement) {
            fDocumentHandler.comment(text, augs);
        }
    }

    public void processingInstruction(String target, XMLString data,
    Augmentations augs) throws XNIException {
        if (fDocumentHandler != null && includeElement) {
            fDocumentHandler.processingInstruction(target, data, augs);

        }
    }

    Stack  ftempCurrentElement = new Stack();
    int fElementCount =0;
    int fCurrentToken ;
    boolean includeElement;


    public void startElement(QName element, XMLAttributes attributes,
    Augmentations augs)throws XNIException {

        boolean requiredToken=false;
        if(fCurrentTokenType == 0)
            getNextToken();
        if(fCurrentTokenType ==1)
            requiredToken = checkIntegerToken(element);
        else if (fCurrentTokenType ==2)
            requiredToken = checkStringToken(element, attributes);
        if(requiredToken && hasMoreToken())
            getNextToken();
        if(fDocumentHandler != null && includeElement){
            elemCount++;
            fDocumentHandler.startElement(element, attributes, augs);
        }

    }


    public void endElement(QName element, Augmentations augs)
    throws XNIException {
        if(includeElement && foundElement != null ){
            if(elemCount >0 )elemCount --;
            fDocumentHandler.endElement(element, augs);
            if(elemCount == 0)includeElement = false;

        }else if(!ftempCurrentElement.empty()){
            QName name = (QName)ftempCurrentElement.peek();
            if(name.equals(element)){
                ftempCurrentElement.pop();
                skip = false;
            }
        }
    }

    public void emptyElement(QName element, XMLAttributes attributes,
    Augmentations augs)throws XNIException {
        if(fDocumentHandler != null && includeElement){
            fDocumentHandler.emptyElement(element, attributes, augs);
        }
    }

    public void startGeneralEntity(String name, XMLResourceIdentifier resId,
    String encoding,
    Augmentations augs)
    throws XNIException {
        if (fDocumentHandler != null && includeElement) {
            fDocumentHandler.startGeneralEntity(name, resId, encoding, augs);
        }
    }

    public void textDecl(String version, String encoding, Augmentations augs)
    throws XNIException {
        if (fDocumentHandler != null && includeElement) {
            fDocumentHandler.textDecl(version, encoding, augs);
        }
    }

    public void endGeneralEntity(String name, Augmentations augs)
    throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endGeneralEntity(name, augs);
        }
    }

    public void characters(XMLString text, Augmentations augs)
    throws XNIException {
        if (fDocumentHandler != null  && includeElement) {
            fDocumentHandler.characters(text, augs);
        }
    }

    public void ignorableWhitespace(XMLString text, Augmentations augs)
    throws XNIException {
        if (fDocumentHandler != null && includeElement) {
            fDocumentHandler.ignorableWhitespace(text, augs);
        }
    }

    public void startCDATA(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null && includeElement) {
            fDocumentHandler.startCDATA(augs);
        }
    }

    public void endCDATA(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null && includeElement) {
            fDocumentHandler.endCDATA(augs);
        }
    }

    public void endDocument(Augmentations augs) throws XNIException {
    }

    public void setDocumentSource(XMLDocumentSource source) {
        fDocumentSource = source;
    }

    public XMLDocumentSource getDocumentSource() {
        return fDocumentSource;
    }


    protected void reportFatalError(String key) {
        this.reportFatalError(key, null);
    }

    protected void reportFatalError(String key, Object[] args) {
        if (fErrorReporter != null) {
            fErrorReporter.reportError(
            fDocLocation,
            XIncludeMessageFormatter.XINCLUDE_DOMAIN,
            key,
            args,
            XMLErrorReporter.SEVERITY_FATAL_ERROR);
        }
        }



    protected boolean isRootDocument() {
        return this.fParentXIncludeHandler == null;
    }


}