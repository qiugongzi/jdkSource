

package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.NamespaceContextWrapper;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.StaxErrorReporter;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
import com.sun.xml.internal.stream.dtd.nonvalidating.XMLNotationDecl;
import com.sun.xml.internal.stream.events.EntityDeclarationImpl;
import com.sun.xml.internal.stream.events.NotationDeclarationImpl;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class XMLStreamReaderImpl implements javax.xml.stream.XMLStreamReader {


    protected static final String ENTITY_MANAGER =
    Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;


    protected static final String ERROR_REPORTER =
    Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    protected static final String SYMBOL_TABLE =
    Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;

    protected static final String READER_IN_DEFINED_STATE =
    Constants.READER_IN_DEFINED_STATE;

    private SymbolTable fSymbolTable = new SymbolTable();


    protected XMLDocumentScannerImpl fScanner = new XMLNSDocumentScannerImpl();

    protected NamespaceContextWrapper fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)fScanner.getNamespaceContext()) ;
    protected XMLEntityManager fEntityManager = new XMLEntityManager();
    protected StaxErrorReporter fErrorReporter = new StaxErrorReporter();



    protected XMLEntityScanner fEntityScanner = null;


    protected XMLInputSource fInputSource = null;

    protected PropertyManager fPropertyManager = null ;


    private int fEventType ;

    static final boolean DEBUG = false ;

    private boolean fReuse = true;
    private boolean fReaderInDefinedState = true ;
    private boolean fBindNamespaces = true;
    private String fDTDDecl = null;
    private String versionStr = null;


    public XMLStreamReaderImpl(InputStream inputStream, PropertyManager props) throws  XMLStreamException {
        init(props);
        XMLInputSource inputSource = new XMLInputSource(null,null,null,inputStream,null);
        setInputSource(inputSource);
    }

    public XMLDocumentScannerImpl getScanner(){
        System.out.println("returning scanner");
        return fScanner;
    }

    public XMLStreamReaderImpl(String systemid, PropertyManager props) throws  XMLStreamException {
        init(props);
        XMLInputSource inputSource = new XMLInputSource(null,systemid,null);
        setInputSource(inputSource);
    }



    public XMLStreamReaderImpl(InputStream inputStream, String encoding, PropertyManager props ) throws  XMLStreamException {
        init(props);
        XMLInputSource inputSource = new XMLInputSource(null,null,null, new BufferedInputStream(inputStream),encoding );
        setInputSource(inputSource);
    }


    public XMLStreamReaderImpl(Reader reader, PropertyManager props) throws  XMLStreamException {
        init(props);
        XMLInputSource inputSource = new XMLInputSource(null,null,null,new BufferedReader(reader),null);
        setInputSource(inputSource);
    }


    public XMLStreamReaderImpl(XMLInputSource inputSource, PropertyManager props) throws  XMLStreamException {
        init(props);
        setInputSource(inputSource);
    }


    public void setInputSource(XMLInputSource inputSource ) throws XMLStreamException {
        fReuse = false;

        try{

            fScanner.setInputSource(inputSource) ;
            if(fReaderInDefinedState){
                fEventType = fScanner.next();
                if (versionStr == null)
                    versionStr = getVersion();

                if (fEventType == XMLStreamConstants.START_DOCUMENT && versionStr != null && versionStr.equals("1.1")){
                    switchToXML11Scanner();
                }

            }
        }catch(java.io.IOException ex){
            throw new XMLStreamException(ex);
        } catch(XNIException ex){ throw new XMLStreamException(ex.getMessage(), getLocation(), ex.getException());
        }
    }void init(PropertyManager propertyManager) throws XMLStreamException {
        fPropertyManager = propertyManager;
        propertyManager.setProperty(SYMBOL_TABLE,  fSymbolTable ) ;
        propertyManager.setProperty(ERROR_REPORTER,  fErrorReporter ) ;
        propertyManager.setProperty(ENTITY_MANAGER, fEntityManager);
        reset();
    }


    public boolean canReuse(){
        if(DEBUG){
            System.out.println("fReuse = " + fReuse);
            System.out.println("fEventType = " + getEventTypeString(fEventType) );
        }
        return fReuse;
    }


    public void reset(){
        fReuse = true;
        fEventType = 0 ;
        fEntityManager.reset(fPropertyManager);
        fScanner.reset(fPropertyManager);
        fDTDDecl = null;
        fEntityScanner = (XMLEntityScanner)fEntityManager.getEntityScanner()  ;
        fReaderInDefinedState = ((Boolean)fPropertyManager.getProperty(READER_IN_DEFINED_STATE)).booleanValue();
        fBindNamespaces = ((Boolean)fPropertyManager.getProperty(XMLInputFactory.IS_NAMESPACE_AWARE)).booleanValue();
        versionStr = null;
    }



    public void close() throws XMLStreamException {
        fReuse = true ;
    }



    public String getCharacterEncodingScheme() {
        return fScanner.getCharacterEncodingScheme();

    }



    public int getColumnNumber() {
        return fEntityScanner.getColumnNumber();
    }public String getEncoding() {
        return fEntityScanner.getEncoding();
    }public int getEventType() {
        return fEventType ;
    }public int getLineNumber() {
        return fEntityScanner.getLineNumber() ;
    }public String getLocalName() {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT){
            return fScanner.getElementQName().localpart ;
        }
        else if(fEventType == XMLEvent.ENTITY_REFERENCE){
            return fScanner.getEntityName();
        }
        throw new IllegalStateException("Method getLocalName() cannot be called for " +
            getEventTypeString(fEventType) + " event.");
    }public String getNamespaceURI() {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT){
            return fScanner.getElementQName().uri ;
        }
        return null ;
    }public String getPIData() {
        if( fEventType == XMLEvent.PROCESSING_INSTRUCTION){
            return fScanner.getPIData().toString();
        }
        else throw new java.lang.IllegalStateException("Current state of the parser is " + getEventTypeString(fEventType) +
        " But Expected state is " + XMLEvent.PROCESSING_INSTRUCTION  ) ;
    }public String getPITarget() {
        if( fEventType == XMLEvent.PROCESSING_INSTRUCTION){
            return fScanner.getPITarget();
        }
        else throw new java.lang.IllegalStateException("Current state of the parser is " + getEventTypeString(fEventType) +
        " But Expected state is " + XMLEvent.PROCESSING_INSTRUCTION  ) ;

    }public String getPrefix() {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT){
            String prefix = fScanner.getElementQName().prefix;
            return prefix == null ? XMLConstants.DEFAULT_NS_PREFIX : prefix;
        }
        return null ;
    }public char[] getTextCharacters() {
        if( fEventType == XMLEvent.CHARACTERS || fEventType == XMLEvent.COMMENT
                 || fEventType == XMLEvent.CDATA || fEventType == XMLEvent.SPACE){
             return fScanner.getCharacterData().ch;
         } else{
             throw new IllegalStateException("Current state = " + getEventTypeString(fEventType)
             + " is not among the states " + getEventTypeString(XMLEvent.CHARACTERS) + " , "
                     + getEventTypeString(XMLEvent.COMMENT) + " , " + getEventTypeString(XMLEvent.CDATA)
                     + " , " + getEventTypeString(XMLEvent.SPACE) +" valid for getTextCharacters() " ) ;
         }
    }


    public int getTextLength() {
        if( fEventType == XMLEvent.CHARACTERS || fEventType == XMLEvent.COMMENT
                 || fEventType == XMLEvent.CDATA || fEventType == XMLEvent.SPACE){
             return fScanner.getCharacterData().length;
         } else{
             throw new IllegalStateException("Current state = " + getEventTypeString(fEventType)
             + " is not among the states " + getEventTypeString(XMLEvent.CHARACTERS) + " , "
                     + getEventTypeString(XMLEvent.COMMENT) + " , " + getEventTypeString(XMLEvent.CDATA)
                     + " , " + getEventTypeString(XMLEvent.SPACE) +" valid for getTextLength() " ) ;
         }

   }


    public int getTextStart() {
        if( fEventType == XMLEvent.CHARACTERS || fEventType == XMLEvent.COMMENT || fEventType == XMLEvent.CDATA || fEventType == XMLEvent.SPACE){
             return  fScanner.getCharacterData().offset;
         } else{
             throw new IllegalStateException("Current state = " + getEventTypeString(fEventType)
             + " is not among the states " + getEventTypeString(XMLEvent.CHARACTERS) + " , "
                     + getEventTypeString(XMLEvent.COMMENT) + " , " + getEventTypeString(XMLEvent.CDATA)
                     + " , " + getEventTypeString(XMLEvent.SPACE) +" valid for getTextStart() " ) ;
         }
    }


    public String getValue() {
        if(fEventType == XMLEvent.PROCESSING_INSTRUCTION){
            return fScanner.getPIData().toString();
        } else if(fEventType == XMLEvent.COMMENT){
            return fScanner.getComment();
        } else if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT){
            return fScanner.getElementQName().localpart ;
        } else if(fEventType == XMLEvent.CHARACTERS){
            return fScanner.getCharacterData().toString();
        }
        return null;
    }public String getVersion() {
        String version = fEntityScanner.getXMLVersion();

        return "1.0".equals(version) && !fEntityScanner.xmlVersionSetExplicitly ? null : version;
    }


    public boolean hasAttributes() {
        return fScanner.getAttributeIterator().getLength() > 0 ? true : false ;
    }


    public boolean hasName() {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT) {
            return true;
        }  else {
            return false;
        }
    }public boolean hasNext() throws XMLStreamException {
        if (fEventType == -1) return false;
        return fEventType != XMLEvent.END_DOCUMENT;
    }


    public boolean hasValue() {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT
        || fEventType == XMLEvent.ENTITY_REFERENCE || fEventType == XMLEvent.PROCESSING_INSTRUCTION
        || fEventType == XMLEvent.COMMENT || fEventType == XMLEvent.CHARACTERS) {
            return true;
        } else {
            return false;
        }

    }


    public boolean isEndElement() {
        return fEventType == XMLEvent.END_ELEMENT;
    }


    public boolean isStandalone() {
        return fScanner.isStandAlone();
    }


    public boolean isStartElement() {
        return fEventType == XMLEvent.START_ELEMENT;
    }


    public boolean isWhiteSpace() {
        if(isCharacters() || (fEventType == XMLStreamConstants.CDATA)){
            char [] ch = this.getTextCharacters();
            final int start = this.getTextStart();
            final int end = start + this.getTextLength();
            for (int i = start; i < end; i++){
                if(!XMLChar.isSpace(ch[i])){
                    return false;
                }
            }
            return true;
        }
        return false;
    }




    public int next() throws XMLStreamException {
        if( !hasNext() ) {
            if (fEventType != -1) {
                throw new java.util.NoSuchElementException( "END_DOCUMENT reached: no more elements on the stream." );
            } else {
                throw new XMLStreamException( "Error processing input source. The input stream is not complete." );
            }
        }
        try {
            fEventType = fScanner.next();

            if (versionStr == null) {
                versionStr = getVersion();
            }

            if (fEventType == XMLStreamConstants.START_DOCUMENT
                    && versionStr != null
                    && versionStr.equals("1.1")) {
                switchToXML11Scanner();
            }

            if (fEventType == XMLStreamConstants.CHARACTERS ||
                    fEventType == XMLStreamConstants.ENTITY_REFERENCE ||
                    fEventType == XMLStreamConstants.PROCESSING_INSTRUCTION ||
                    fEventType == XMLStreamConstants.COMMENT ||
                    fEventType == XMLStreamConstants.CDATA) {
                    fEntityScanner.checkNodeCount(fEntityScanner.fCurrentEntity);
            }

            return fEventType;
        } catch (IOException ex) {
            if (fScanner.fScannerState == fScanner.SCANNER_STATE_DTD_EXTERNAL) {
                Boolean isValidating = (Boolean) fPropertyManager.getProperty(
                        XMLInputFactory.IS_VALIDATING);
                if (isValidating != null
                        && !isValidating.booleanValue()) {
                    fEventType = XMLEvent.DTD;
                    fScanner.setScannerState(fScanner.SCANNER_STATE_PROLOG);
                    fScanner.setDriver(fScanner.fPrologDriver);
                    if (fDTDDecl == null
                            || fDTDDecl.length() == 0) {
                        fDTDDecl = "<!-- "
                                + "Exception scanning External DTD Subset.  "
                                + "True contents of DTD cannot be determined.  "
                                + "Processing will continue as XMLInputFactory.IS_VALIDATING == false."
                                + " -->";
                    }
                    return XMLEvent.DTD;
                }
            }

            throw new XMLStreamException(ex.getMessage(), getLocation(), ex);
        } catch (XNIException ex) {
            throw new XMLStreamException(
                    ex.getMessage(),
                    getLocation(),
                    ex.getException());
        }
    } private void switchToXML11Scanner() throws IOException{

        int oldEntityDepth = fScanner.fEntityDepth;
        com.sun.org.apache.xerces.internal.xni.NamespaceContext oldNamespaceContext = fScanner.fNamespaceContext;

        fScanner = new XML11NSDocumentScannerImpl();

        fScanner.reset(fPropertyManager);
        fScanner.setPropertyManager(fPropertyManager);
        fEntityScanner = (XMLEntityScanner)fEntityManager.getEntityScanner()  ;
        fEntityManager.fCurrentEntity.mayReadChunks = true;
        fScanner.setScannerState(XMLEvent.START_DOCUMENT);

        fScanner.fEntityDepth = oldEntityDepth;
        fScanner.fNamespaceContext = oldNamespaceContext;
        fEventType = fScanner.next();
    }



    final static String getEventTypeString(int eventType) {
        switch (eventType){
            case XMLEvent.START_ELEMENT:
                return "START_ELEMENT";
            case XMLEvent.END_ELEMENT:
                return "END_ELEMENT";
            case XMLEvent.PROCESSING_INSTRUCTION:
                return "PROCESSING_INSTRUCTION";
            case XMLEvent.CHARACTERS:
                return "CHARACTERS";
            case XMLEvent.COMMENT:
                return "COMMENT";
            case XMLEvent.START_DOCUMENT:
                return "START_DOCUMENT";
            case XMLEvent.END_DOCUMENT:
                return "END_DOCUMENT";
            case XMLEvent.ENTITY_REFERENCE:
                return "ENTITY_REFERENCE";
            case XMLEvent.ATTRIBUTE:
                return "ATTRIBUTE";
            case XMLEvent.DTD:
                return "DTD";
            case XMLEvent.CDATA:
                return "CDATA";
            case XMLEvent.SPACE:
                return "SPACE";
        }
        return "UNKNOWN_EVENT_TYPE, " + String.valueOf(eventType);
    }


    public int getAttributeCount() {
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getLength() ;
        } else{
            throw new java.lang.IllegalStateException( "Current state is not among the states "
                     + getEventTypeString(XMLEvent.START_ELEMENT) + " , "
                     + getEventTypeString(XMLEvent.ATTRIBUTE)
                     + "valid for getAttributeCount()") ;
        }
    }public QName getAttributeName(int index) {
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return convertXNIQNametoJavaxQName(fScanner.getAttributeIterator().getQualifiedName(index)) ;
        } else{
            throw new java.lang.IllegalStateException("Current state is not among the states "
                     + getEventTypeString(XMLEvent.START_ELEMENT) + " , "
                     + getEventTypeString(XMLEvent.ATTRIBUTE)
                     + "valid for getAttributeName()") ;
        }
    }public String getAttributeLocalName(int index) {
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getLocalName(index) ;
        } else{
            throw new java.lang.IllegalStateException() ;
        }
    }public String getAttributeNamespace(int index) {
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getURI(index);
        } else{
            throw new java.lang.IllegalStateException("Current state is not among the states "
                     + getEventTypeString(XMLEvent.START_ELEMENT) + " , "
                     + getEventTypeString(XMLEvent.ATTRIBUTE)
                     + "valid for getAttributeNamespace()") ;
        }

    }public String getAttributePrefix(int index) {
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getPrefix(index);
        } else{
            throw new java.lang.IllegalStateException("Current state is not among the states "
                     + getEventTypeString(XMLEvent.START_ELEMENT) + " , "
                     + getEventTypeString(XMLEvent.ATTRIBUTE)
                     + "valid for getAttributePrefix()") ;
        }
    }public javax.xml.namespace.QName getAttributeQName(int index) {
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            String localName = fScanner.getAttributeIterator().getLocalName(index) ;
            String uri = fScanner.getAttributeIterator().getURI(index) ;
            return new javax.xml.namespace.QName(uri, localName) ;
        } else{
            throw new java.lang.IllegalStateException("Current state is not among the states "
                     + getEventTypeString(XMLEvent.START_ELEMENT) + " , "
                     + getEventTypeString(XMLEvent.ATTRIBUTE)
                     + "valid for getAttributeQName()") ;
        }
    }public String getAttributeType(int index) {
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getType(index) ;
        } else{
            throw new java.lang.IllegalStateException("Current state is not among the states "
                     + getEventTypeString(XMLEvent.START_ELEMENT) + " , "
                     + getEventTypeString(XMLEvent.ATTRIBUTE)
                     + "valid for getAttributeType()") ;
        }

    }public String getAttributeValue(int index) {
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            return fScanner.getAttributeIterator().getValue(index) ;
        } else{
            throw new java.lang.IllegalStateException("Current state is not among the states "
                     + getEventTypeString(XMLEvent.START_ELEMENT) + " , "
                     + getEventTypeString(XMLEvent.ATTRIBUTE)
                     + "valid for getAttributeValue()") ;
        }

    }public String getAttributeValue(String namespaceURI, String localName) {
        if( fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.ATTRIBUTE) {
            XMLAttributesImpl attributes = fScanner.getAttributeIterator();
            if (namespaceURI == null) { return attributes.getValue(attributes.getIndexByLocalName(localName)) ;
            } else {
                return fScanner.getAttributeIterator().getValue(
                        namespaceURI.length() == 0 ? null : namespaceURI, localName) ;
            }

        } else{
            throw new java.lang.IllegalStateException("Current state is not among the states "
                     + getEventTypeString(XMLEvent.START_ELEMENT) + " , "
                     + getEventTypeString(XMLEvent.ATTRIBUTE)
                     + "valid for getAttributeValue()") ;
        }

    }


    public String getElementText() throws XMLStreamException {

        if(getEventType() != XMLStreamConstants.START_ELEMENT) {
            throw new XMLStreamException(
            "parser must be on START_ELEMENT to read next text", getLocation());
        }
        int eventType = next();
        StringBuffer content = new StringBuffer();
        while(eventType != XMLStreamConstants.END_ELEMENT ) {
            if(eventType == XMLStreamConstants.CHARACTERS
            || eventType == XMLStreamConstants.CDATA
            || eventType == XMLStreamConstants.SPACE
            || eventType == XMLStreamConstants.ENTITY_REFERENCE) {
                content.append(getText());
            } else if(eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
            || eventType == XMLStreamConstants.COMMENT) {
                } else if(eventType == XMLStreamConstants.END_DOCUMENT) {
                throw new XMLStreamException("unexpected end of document when reading element text content");
            } else if(eventType == XMLStreamConstants.START_ELEMENT) {
                throw new XMLStreamException(
                "elementGetText() function expects text only elment but START_ELEMENT was encountered.", getLocation());
            } else {
                throw new XMLStreamException(
                "Unexpected event type "+ eventType, getLocation());
            }
            eventType = next();
        }
        return content.toString();
    }


    public Location getLocation() {
        return new Location() {
            String _systemId = fEntityScanner.getExpandedSystemId();
            String _publicId = fEntityScanner.getPublicId();
            int _offset = fEntityScanner.getCharacterOffset();
            int _columnNumber = fEntityScanner.getColumnNumber();
            int _lineNumber = fEntityScanner.getLineNumber();
            public String getLocationURI(){
                return _systemId;
            }

            public int getCharacterOffset(){
                return _offset;
            }

            public int getColumnNumber() {
                return _columnNumber;
            }

            public int getLineNumber(){
                return _lineNumber;
            }

            public String getPublicId(){
                return _publicId;
            }

            public String getSystemId(){
                return _systemId;
            }

            public String toString(){
                StringBuffer sbuffer = new StringBuffer() ;
                sbuffer.append("Line number = " + getLineNumber());
                sbuffer.append("\n") ;
                sbuffer.append("Column number = " + getColumnNumber());
                sbuffer.append("\n") ;
                sbuffer.append("System Id = " + getSystemId());
                sbuffer.append("\n") ;
                sbuffer.append("Public Id = " + getPublicId());
                sbuffer.append("\n") ;
                sbuffer.append("Location Uri= " + getLocationURI());
                sbuffer.append("\n") ;
                sbuffer.append("CharacterOffset = " + getCharacterOffset());
                sbuffer.append("\n") ;
                return sbuffer.toString();
            }
        } ;

    }


    public javax.xml.namespace.QName getName() {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT)
            return convertXNIQNametoJavaxQName(fScanner.getElementQName());
        else
            throw new java.lang.IllegalStateException("Illegal to call getName() "+
            "when event type is "+ getEventTypeString(fEventType) + "."
                     + " Valid states are " + getEventTypeString(XMLEvent.START_ELEMENT) + ", "
                     + getEventTypeString(XMLEvent.END_ELEMENT));
    }


    public NamespaceContext getNamespaceContext() {
        return fNamespaceContextWrapper ;
    }


    public int getNamespaceCount() {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT || fEventType == XMLEvent.NAMESPACE){
            return fScanner.getNamespaceContext().getDeclaredPrefixCount() ;
        } else{
            throw new IllegalStateException("Current event state is " + getEventTypeString(fEventType)
             + " is not among the states " + getEventTypeString(XMLEvent.START_ELEMENT)
             + ", " + getEventTypeString(XMLEvent.END_ELEMENT) + ", "
                     + getEventTypeString(XMLEvent.NAMESPACE)
             + " valid for getNamespaceCount()." );
        }
    }


    public String getNamespacePrefix(int index) {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT || fEventType == XMLEvent.NAMESPACE){
            String prefix = fScanner.getNamespaceContext().getDeclaredPrefixAt(index) ;
            return prefix.equals("") ? null : prefix ;
        }
        else{
            throw new IllegalStateException("Current state " + getEventTypeString(fEventType)
             + " is not among the states " + getEventTypeString(XMLEvent.START_ELEMENT)
             + ", " + getEventTypeString(XMLEvent.END_ELEMENT) + ", "
                     + getEventTypeString(XMLEvent.NAMESPACE)
             + " valid for getNamespacePrefix()." );
        }
    }


    public String getNamespaceURI(int index) {
        if(fEventType == XMLEvent.START_ELEMENT || fEventType == XMLEvent.END_ELEMENT || fEventType == XMLEvent.NAMESPACE){
            return fScanner.getNamespaceContext().getURI(fScanner.getNamespaceContext().getDeclaredPrefixAt(index));
        }
        else{
            throw new IllegalStateException("Current state " + getEventTypeString(fEventType)
             + " is not among the states " + getEventTypeString(XMLEvent.START_ELEMENT)
             + ", " + getEventTypeString(XMLEvent.END_ELEMENT) + ", "
                     + getEventTypeString(XMLEvent.NAMESPACE)
             + " valid for getNamespaceURI()." );
        }

    }


    public Object getProperty(java.lang.String name) throws java.lang.IllegalArgumentException {
        if(name == null) throw new java.lang.IllegalArgumentException() ;
        if (fPropertyManager != null ){
            if(name.equals(fPropertyManager.STAX_NOTATIONS)){
                return getNotationDecls();
            }else if(name.equals(fPropertyManager.STAX_ENTITIES)){
                return getEntityDecls();
            }else
                return fPropertyManager.getProperty(name);
        }
        return null;
    }


    public String getText() {
        if( fEventType == XMLEvent.CHARACTERS || fEventType == XMLEvent.COMMENT
                || fEventType == XMLEvent.CDATA || fEventType == XMLEvent.SPACE){
            return fScanner.getCharacterData().toString() ;
        } else if(fEventType == XMLEvent.ENTITY_REFERENCE){
            String name = fScanner.getEntityName();
            if(name != null){
                if(fScanner.foundBuiltInRefs)
                    return fScanner.getCharacterData().toString();

                XMLEntityStorage entityStore = fEntityManager.getEntityStore();
                Entity en = entityStore.getEntity(name);
                if(en == null)
                    return null;
                if(en.isExternal())
                    return ((Entity.ExternalEntity)en).entityLocation.getExpandedSystemId();
                else
                    return ((Entity.InternalEntity)en).text;
            }else
                return null;
        }
        else if(fEventType == XMLEvent.DTD){
                if(fDTDDecl != null){
                    return fDTDDecl;
                }
                XMLStringBuffer tmpBuffer = fScanner.getDTDDecl();
                fDTDDecl = tmpBuffer.toString();
                return fDTDDecl;
        } else{
                throw new IllegalStateException("Current state " + getEventTypeString(fEventType)
                     + " is not among the states" + getEventTypeString(XMLEvent.CHARACTERS) + ", "
                     + getEventTypeString(XMLEvent.COMMENT) + ", "
                     + getEventTypeString(XMLEvent.CDATA) + ", "
                     + getEventTypeString(XMLEvent.SPACE) + ", "
                     + getEventTypeString(XMLEvent.ENTITY_REFERENCE) + ", "
                     + getEventTypeString(XMLEvent.DTD) + " valid for getText() " ) ;
        }
    }public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        if( type != fEventType)
             throw new XMLStreamException("Event type " + getEventTypeString(type) + " specified did " +
                     "not match with current parser event " + getEventTypeString(fEventType));
          if( namespaceURI != null && !namespaceURI.equals(getNamespaceURI()) )
             throw new XMLStreamException("Namespace URI " + namespaceURI +" specified did not match " +
                     "with current namespace URI");
          if(localName != null && !localName.equals(getLocalName()))
             throw new XMLStreamException("LocalName " + localName +" specified did not match with " +
                     "current local name");
        return;
    }


    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {

        if(target == null){
            throw new NullPointerException("target char array can't be null") ;
        }

        if(targetStart < 0 || length < 0 || sourceStart < 0 || targetStart >= target.length ||
            (targetStart + length ) > target.length) {
            throw new IndexOutOfBoundsException();
        }

        int copiedLength = 0;
        int available = getTextLength() - sourceStart;
        if(available < 0){
            throw new IndexOutOfBoundsException("sourceStart is greater than" +
                "number of characters associated with this event");
        }
        if(available < length){
            copiedLength = available;
        } else{
            copiedLength = length;
        }

        System.arraycopy(getTextCharacters(), getTextStart() + sourceStart , target, targetStart, copiedLength);
        return copiedLength;
    }


    public boolean hasText() {
        if(DEBUG) pr("XMLReaderImpl#EVENT TYPE = " + fEventType ) ;
        if( fEventType == XMLEvent.CHARACTERS || fEventType == XMLEvent.COMMENT || fEventType == XMLEvent.CDATA) {
            return fScanner.getCharacterData().length > 0;
        } else if(fEventType == XMLEvent.ENTITY_REFERENCE) {
            String name = fScanner.getEntityName();
            if(name != null){
                if(fScanner.foundBuiltInRefs)
                    return true;

                XMLEntityStorage entityStore = fEntityManager.getEntityStore();
                Entity en = entityStore.getEntity(name);
                if(en == null)
                    return false;
                if(en.isExternal()){
                    return ((Entity.ExternalEntity)en).entityLocation.getExpandedSystemId() != null;
                } else{
                    return ((Entity.InternalEntity)en).text != null ;
                }
            }else
                return false;
        } else {
            if(fEventType == XMLEvent.DTD)
                return fScanner.fSeenDoctypeDecl;
        }
        return false;
    }


    public boolean isAttributeSpecified(int index) {
        if( (fEventType == XMLEvent.START_ELEMENT) || (fEventType == XMLEvent.ATTRIBUTE)){
            return fScanner.getAttributeIterator().isSpecified(index) ;
        } else{
            throw new IllegalStateException("Current state is not among the states "
                     + getEventTypeString(XMLEvent.START_ELEMENT) + " , "
                     + getEventTypeString(XMLEvent.ATTRIBUTE)
                     + "valid for isAttributeSpecified()")  ;
        }
    }


    public boolean isCharacters() {
        return fEventType == XMLEvent.CHARACTERS ;
    }


    public int nextTag() throws XMLStreamException {

        int eventType = next();
        while((eventType == XMLStreamConstants.CHARACTERS && isWhiteSpace()) || (eventType == XMLStreamConstants.CDATA && isWhiteSpace())
        || eventType == XMLStreamConstants.SPACE
        || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
        || eventType == XMLStreamConstants.COMMENT
        ) {
            eventType = next();
        }

        if (eventType != XMLStreamConstants.START_ELEMENT && eventType != XMLStreamConstants.END_ELEMENT) {
            throw new XMLStreamException(
                    "found: " + getEventTypeString(eventType)
                    + ", expected " + getEventTypeString(XMLStreamConstants.START_ELEMENT)
                    + " or " + getEventTypeString(XMLStreamConstants.END_ELEMENT),
                    getLocation());
        }

        return eventType;
    }


    public boolean standaloneSet() {
        return fScanner.standaloneSet() ;
    }


    public javax.xml.namespace.QName convertXNIQNametoJavaxQName(com.sun.org.apache.xerces.internal.xni.QName qname){
        if (qname == null) return null;
        if(qname.prefix == null){
            return new javax.xml.namespace.QName(qname.uri, qname.localpart) ;
        } else{
            return new javax.xml.namespace.QName(qname.uri, qname.localpart, qname.prefix) ;
        }
    }


    public String getNamespaceURI(String prefix) {
        if(prefix == null) throw new java.lang.IllegalArgumentException("prefix cannot be null.") ;

        return fScanner.getNamespaceContext().getURI(fSymbolTable.addSymbol(prefix)) ;
    }

    protected void setPropertyManager(PropertyManager propertyManager){
        fPropertyManager = propertyManager ;
        fScanner.setProperty("stax-properties",propertyManager);
        fScanner.setPropertyManager(propertyManager) ;
    }


    protected PropertyManager getPropertyManager(){
        return fPropertyManager ;
    }

    static void pr(String str) {
        System.out.println(str) ;
    }

    protected List getEntityDecls(){
        if(fEventType == XMLStreamConstants.DTD){
            XMLEntityStorage entityStore = fEntityManager.getEntityStore();
            ArrayList list = null;
            if(entityStore.hasEntities()){
                EntityDeclarationImpl decl = null;
                list = new ArrayList(entityStore.getEntitySize());
                Enumeration enu = entityStore.getEntityKeys();
                while(enu.hasMoreElements()){
                    String key = (String)enu.nextElement();
                    Entity en = (Entity)entityStore.getEntity(key);
                    decl = new EntityDeclarationImpl();
                    decl.setEntityName(key);
                    if(en.isExternal()){
                        decl.setXMLResourceIdentifier(((Entity.ExternalEntity)en).entityLocation);
                        decl.setNotationName(((Entity.ExternalEntity)en).notation);
                    }
                    else
                        decl.setEntityReplacementText(((Entity.InternalEntity)en).text);
                    list.add(decl);
                }
            }
            return list;
        }
        return null;
    }

    protected List getNotationDecls(){
        if(fEventType == XMLStreamConstants.DTD){
            if(fScanner.fDTDScanner == null) return null;
            DTDGrammar grammar = ((XMLDTDScannerImpl)(fScanner.fDTDScanner)).getGrammar();
            if(grammar == null) return null;
            List notations = grammar.getNotationDecls();

            Iterator it = notations.iterator();
            ArrayList list = new ArrayList();
            while(it.hasNext()){
                XMLNotationDecl ni = (XMLNotationDecl)it.next();
                if(ni!= null){
                    list.add(new NotationDeclarationImpl(ni));
                }
            }
            return list;
        }
        return null;
    }



}