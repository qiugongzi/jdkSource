

package com.sun.org.apache.xerces.internal.impl;

import javax.xml.XMLConstants;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;




public class XMLStreamFilterImpl implements javax.xml.stream.XMLStreamReader {

    private StreamFilter fStreamFilter = null;
    private XMLStreamReader fStreamReader = null;
    private int fCurrentEvent;
    private boolean fEventAccepted = false;


    private boolean fStreamAdvancedByHasNext = false;


    public XMLStreamFilterImpl(XMLStreamReader reader,StreamFilter filter){
        fStreamReader = reader;
        this.fStreamFilter = filter;

        try {
            if (fStreamFilter.accept(fStreamReader)) {
                fEventAccepted = true;
            } else {
                findNextEvent();
            }
        }catch(XMLStreamException xs){
            System.err.println("Error while creating a stream Filter"+xs);
        }
    }


    protected void setStreamFilter(StreamFilter sf){
        this.fStreamFilter = sf;
    }


    public int next() throws XMLStreamException {
        if (fStreamAdvancedByHasNext && fEventAccepted) {
            fStreamAdvancedByHasNext = false;
            return fCurrentEvent;
        }
        int event = findNextEvent();
        if (event != -1) {
            return event;
        }

        throw new IllegalStateException("The stream reader has reached the end of the document, or there are no more "+
                                    " items to return");
    }

    public int nextTag() throws XMLStreamException {
        if (fStreamAdvancedByHasNext && fEventAccepted &&
                (fCurrentEvent == XMLEvent.START_ELEMENT || fCurrentEvent == XMLEvent.START_ELEMENT)) {
            fStreamAdvancedByHasNext = false;
            return fCurrentEvent;
        }

        int event = findNextTag();
        if (event != -1) {
            return event;
        }
        throw new IllegalStateException("The stream reader has reached the end of the document, or there are no more "+
                                    " items to return");
    }


    public boolean hasNext() throws XMLStreamException {
        if (fStreamReader.hasNext()) {
            if (!fEventAccepted) {
                if ((fCurrentEvent = findNextEvent()) == -1) {
                    return false;
                } else {
                    fStreamAdvancedByHasNext = true;
                }
            }
            return true;
        }
        return false;
    }

    private int findNextEvent() throws XMLStreamException {
        fStreamAdvancedByHasNext = false;
        while(fStreamReader.hasNext()){
            fCurrentEvent = fStreamReader.next();
            if(fStreamFilter.accept(fStreamReader)){
                fEventAccepted = true;
                return fCurrentEvent;
            }
        }
        if (fCurrentEvent == XMLEvent.END_DOCUMENT)
            return fCurrentEvent;
        else
            return -1;
    }
    private int findNextTag() throws XMLStreamException {
        fStreamAdvancedByHasNext = false;
        while(fStreamReader.hasNext()){
            fCurrentEvent = fStreamReader.nextTag();
            if(fStreamFilter.accept(fStreamReader)){
                fEventAccepted = true;
                return fCurrentEvent;
            }
        }
        if (fCurrentEvent == XMLEvent.END_DOCUMENT)
            return fCurrentEvent;
        else
            return -1;
    }

    public void close() throws XMLStreamException {
        fStreamReader.close();
    }


    public int getAttributeCount() {
        return fStreamReader.getAttributeCount();
    }


    public QName getAttributeName(int index) {
        return fStreamReader.getAttributeName(index);
    }


    public String getAttributeNamespace(int index) {
        return fStreamReader.getAttributeNamespace(index);
    }


    public String getAttributePrefix(int index) {
        return fStreamReader.getAttributePrefix(index);
    }


    public String getAttributeType(int index) {
        return fStreamReader.getAttributeType(index);
    }


    public String getAttributeValue(int index) {
        return fStreamReader.getAttributeValue(index);
    }


    public String getAttributeValue(String namespaceURI, String localName) {
        return fStreamReader.getAttributeValue(namespaceURI,localName);
    }


    public String getCharacterEncodingScheme() {
        return fStreamReader.getCharacterEncodingScheme();
    }


    public String getElementText() throws XMLStreamException {
        return fStreamReader.getElementText();
    }


    public String getEncoding() {
        return fStreamReader.getEncoding();
    }


    public int getEventType() {
        return fStreamReader.getEventType();
    }


    public String getLocalName() {
        return fStreamReader.getLocalName();
    }


    public javax.xml.stream.Location getLocation() {
        return fStreamReader.getLocation();
    }


    public javax.xml.namespace.QName getName() {
        return fStreamReader.getName();
    }


    public javax.xml.namespace.NamespaceContext getNamespaceContext() {
        return fStreamReader.getNamespaceContext();
    }


    public int getNamespaceCount() {
        return fStreamReader.getNamespaceCount();
    }


    public String getNamespacePrefix(int index) {
        return fStreamReader.getNamespacePrefix(index);
    }


    public String getNamespaceURI() {
        return fStreamReader.getNamespaceURI();
    }


    public String getNamespaceURI(int index) {
        return fStreamReader.getNamespaceURI(index);
    }


    public String getNamespaceURI(String prefix) {
        return fStreamReader.getNamespaceURI(prefix);
    }


    public String getPIData() {
        return fStreamReader.getPIData();
    }


    public String getPITarget() {
        return fStreamReader.getPITarget();
    }


    public String getPrefix() {
        return fStreamReader.getPrefix();
    }


    public Object getProperty(java.lang.String name) throws java.lang.IllegalArgumentException {
        return fStreamReader.getProperty(name);
    }


    public String getText() {
        return fStreamReader.getText();
    }


    public char[] getTextCharacters() {
        return fStreamReader.getTextCharacters();
    }


    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        return fStreamReader.getTextCharacters(sourceStart, target,targetStart,length);
    }


    public int getTextLength() {
        return fStreamReader.getTextLength();
    }


    public int getTextStart() {
        return fStreamReader.getTextStart();
    }


    public String getVersion() {
        return fStreamReader.getVersion();
    }


    public boolean hasName() {
        return fStreamReader.hasName();
    }


    public boolean hasText() {
        return fStreamReader.hasText();
    }


    public boolean isAttributeSpecified(int index) {
        return fStreamReader.isAttributeSpecified(index);
    }


    public boolean isCharacters() {
        return fStreamReader.isCharacters();
    }


    public boolean isEndElement() {
        return fStreamReader.isEndElement();
    }


    public boolean isStandalone() {
        return fStreamReader.isStandalone();
    }


    public boolean isStartElement() {
        return fStreamReader.isStartElement();
    }


    public boolean isWhiteSpace() {
        return fStreamReader.isWhiteSpace();
    }



    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        fStreamReader.require(type,namespaceURI,localName);
    }


    public boolean standaloneSet() {
        return fStreamReader.standaloneSet();
    }


    public String getAttributeLocalName(int index){
        return fStreamReader.getAttributeLocalName(index);
    }
}
