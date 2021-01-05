

package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.io.IOException;
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.ext.Locator2;
import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.StartDocument;



public class StAXEvent2SAX implements XMLReader, Locator {

    private final XMLEventReader staxEventReader;

    private ContentHandler _sax = null;
    private LexicalHandler _lex = null;
    private SAXImpl _saxImpl = null;
    private String version = null;
    private String encoding = null;


    public StAXEvent2SAX(XMLEventReader staxCore) {
        staxEventReader = staxCore;
    }

    public ContentHandler getContentHandler() {
        return _sax;
    }

    public void setContentHandler(ContentHandler handler) throws
        NullPointerException
    {
        _sax = handler;
        if (handler instanceof LexicalHandler) {
            _lex = (LexicalHandler) handler;
        }

        if (handler instanceof SAXImpl) {
            _saxImpl = (SAXImpl)handler;
        }
    }


    public void parse(InputSource unused) throws IOException, SAXException {
       try {
            bridge();
        } catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }


    public void parse() throws IOException, SAXException, XMLStreamException {
        bridge();
    }





    private void bridge() throws XMLStreamException {

        try {
            int depth=0;
            boolean startedAtDocument = false;

            XMLEvent event = staxEventReader.peek();

            if (!event.isStartDocument() && !event.isStartElement()) {
                throw new IllegalStateException();
            }

            if (event.getEventType() == XMLStreamConstants.START_DOCUMENT){
                startedAtDocument = true;
                version = ((StartDocument)event).getVersion();
                if (((StartDocument)event).encodingSet())
                    encoding = ((StartDocument)event).getCharacterEncodingScheme();
                event=staxEventReader.nextEvent(); event=staxEventReader.nextEvent(); }

            handleStartDocument(event);

            while (event.getEventType() != XMLStreamConstants.START_ELEMENT) {
                switch (event.getEventType()) {
                    case XMLStreamConstants.CHARACTERS :
                        handleCharacters(event.asCharacters());
                        break;
                    case XMLStreamConstants.PROCESSING_INSTRUCTION :
                        handlePI((ProcessingInstruction)event);
                        break;
                    case XMLStreamConstants.COMMENT :
                        handleComment();
                        break;
                    case XMLStreamConstants.DTD :
                        handleDTD();
                        break;
                    case XMLStreamConstants.SPACE :
                        handleSpace();
                        break;
                    default :
                        throw new InternalError("processing prolog event: " + event);
                }
                event=staxEventReader.nextEvent();
            }

            do {
                switch (event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT :
                        depth++;
                        handleStartElement(event.asStartElement());
                        break;
                    case XMLStreamConstants.END_ELEMENT :
                        handleEndElement(event.asEndElement());
                        depth--;
                        break;
                    case XMLStreamConstants.CHARACTERS :
                        handleCharacters(event.asCharacters());
                        break;
                    case XMLStreamConstants.ENTITY_REFERENCE :
                        handleEntityReference();
                        break;
                    case XMLStreamConstants.PROCESSING_INSTRUCTION :
                        handlePI((ProcessingInstruction)event);
                        break;
                    case XMLStreamConstants.COMMENT :
                        handleComment();
                        break;
                    case XMLStreamConstants.DTD :
                        handleDTD();
                        break;
                    case XMLStreamConstants.ATTRIBUTE :
                        handleAttribute();
                        break;
                    case XMLStreamConstants.NAMESPACE :
                        handleNamespace();
                        break;
                    case XMLStreamConstants.CDATA :
                        handleCDATA();
                        break;
                    case XMLStreamConstants.ENTITY_DECLARATION :
                        handleEntityDecl();
                        break;
                    case XMLStreamConstants.NOTATION_DECLARATION :
                        handleNotationDecl();
                        break;
                    case XMLStreamConstants.SPACE :
                        handleSpace();
                        break;
                    default :
                        throw new InternalError("processing event: " + event);
                }

                event=staxEventReader.nextEvent();
            } while (depth!=0);

            if (startedAtDocument) {
                while (event.getEventType() != XMLStreamConstants.END_DOCUMENT) {
                    switch (event.getEventType()) {
                        case XMLStreamConstants.CHARACTERS :
                            handleCharacters(event.asCharacters());
                            break;
                        case XMLStreamConstants.PROCESSING_INSTRUCTION :
                            handlePI((ProcessingInstruction)event);
                            break;
                        case XMLStreamConstants.COMMENT :
                            handleComment();
                            break;
                        case XMLStreamConstants.SPACE :
                            handleSpace();
                            break;
                        default :
                            throw new InternalError("processing misc event after document element: " + event);
                    }
                    event=staxEventReader.nextEvent();
                }
            }

            handleEndDocument();
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }


    private void handleEndDocument() throws SAXException {
        _sax.endDocument();
    }

    private void handleStartDocument(final XMLEvent event) throws SAXException {
        _sax.setDocumentLocator(new Locator2() {
            public int getColumnNumber() {
                return event.getLocation().getColumnNumber();
            }
            public int getLineNumber() {
                return event.getLocation().getLineNumber();
            }
            public String getPublicId() {
                return event.getLocation().getPublicId();
            }
            public String getSystemId() {
                return event.getLocation().getSystemId();
            }
            public String getXMLVersion(){
                return version;
            }
            public String getEncoding(){
                return encoding;
            }

        });
        _sax.startDocument();
    }

    private void handlePI(ProcessingInstruction event)
        throws XMLStreamException {
        try {
            _sax.processingInstruction(
                event.getTarget(),
                event.getData());
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleCharacters(Characters event) throws XMLStreamException {
        try {
            _sax.characters(
                event.getData().toCharArray(),
                0,
                event.getData().length());
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleEndElement(EndElement event) throws XMLStreamException {
        QName qName = event.getName();

        String qname = "";
        if (qName.getPrefix() != null && qName.getPrefix().trim().length() != 0){
            qname = qName.getPrefix() + ":";
        }
        qname += qName.getLocalPart();

        try {
            _sax.endElement(
                qName.getNamespaceURI(),
                qName.getLocalPart(),
                qname);

            for( Iterator i = event.getNamespaces(); i.hasNext();) {
                String prefix = (String)i.next();
                if( prefix == null ) { prefix = "";
                }
                _sax.endPrefixMapping(prefix);
            }
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleStartElement(StartElement event)
        throws XMLStreamException {
        try {
            for (Iterator i = event.getNamespaces(); i.hasNext();) {
                String prefix = ((Namespace)i.next()).getPrefix();
                if (prefix == null) { prefix = "";
                }
                _sax.startPrefixMapping(
                    prefix,
                    event.getNamespaceURI(prefix));
            }

            QName qName = event.getName();
            String prefix = qName.getPrefix();
            String rawname;
            if (prefix == null || prefix.length() == 0) {
                rawname = qName.getLocalPart();
            } else {
                rawname = prefix + ':' + qName.getLocalPart();
            }

            Attributes saxAttrs = getAttributes(event);
            _sax.startElement(
                qName.getNamespaceURI(),
                qName.getLocalPart(),
                rawname,
                saxAttrs);
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }


    private Attributes getAttributes(StartElement event) {
        AttributesImpl attrs = new AttributesImpl();

        if ( !event.isStartElement() ) {
            throw new InternalError(
                "getAttributes() attempting to process: " + event);
        }

        for (Iterator i = event.getAttributes(); i.hasNext();) {
            Attribute staxAttr = (javax.xml.stream.events.Attribute)i.next();

            String uri = staxAttr.getName().getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            String localName = staxAttr.getName().getLocalPart();
            String prefix = staxAttr.getName().getPrefix();
            String qName;
            if (prefix == null || prefix.length() == 0) {
                qName = localName;
            } else {
                qName = prefix + ':' + localName;
            }
            String type = staxAttr.getDTDType();
            String value = staxAttr.getValue();

            attrs.addAttribute(uri, localName, qName, type, value);
        }

        return attrs;
    }

    private void handleNamespace() {
        }

    private void handleAttribute() {
        }

    private void handleDTD() {
        }

    private void handleComment() {
        }

    private void handleEntityReference() {
        }

    private void handleSpace() {
        }

    private void handleNotationDecl() {
        }

    private void handleEntityDecl() {
        }

    private void handleCDATA() {
        }



    public DTDHandler getDTDHandler() {
        return null;
    }


    public ErrorHandler getErrorHandler() {
        return null;
    }


    public boolean getFeature(String name) throws SAXNotRecognizedException,
        SAXNotSupportedException
    {
        return false;
    }


    public void setFeature(String name, boolean value) throws
        SAXNotRecognizedException, SAXNotSupportedException
    {
    }


    public void parse(String sysId) throws IOException, SAXException {
        throw new IOException("This method is not yet implemented.");
    }


    public void setDTDHandler(DTDHandler handler) throws NullPointerException {
    }


    public void setEntityResolver(EntityResolver resolver) throws
        NullPointerException
    {
    }


    public EntityResolver getEntityResolver() {
        return null;
    }


    public void setErrorHandler(ErrorHandler handler) throws
        NullPointerException
    {
    }


    public void setProperty(String name, Object value) throws
        SAXNotRecognizedException, SAXNotSupportedException {
    }


    public Object getProperty(String name) throws SAXNotRecognizedException,
        SAXNotSupportedException
    {
        return null;
    }


    public int getColumnNumber() {
        return 0;
    }


    public int getLineNumber() {
        return 0;
    }


    public String getPublicId() {
        return null;
    }


    public String getSystemId() {
        return null;
    }
}
