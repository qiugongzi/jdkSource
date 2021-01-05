

package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.dom.SAXImpl;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
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
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.AttributesImpl;




public class StAXStream2SAX implements XMLReader, Locator {

    private final XMLStreamReader staxStreamReader;

    private ContentHandler _sax = null;
    private LexicalHandler _lex = null;
    private SAXImpl _saxImpl = null;

    public StAXStream2SAX(XMLStreamReader staxSrc) {
            staxStreamReader = staxSrc;
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



    public void parse(String sysId) throws IOException, SAXException {
        throw new IOException("This method is not yet implemented.");
    }


   public void bridge() throws XMLStreamException {

        try {
            int depth=0;

            int event = staxStreamReader.getEventType();
            if (event == XMLStreamConstants.START_DOCUMENT) {
                event = staxStreamReader.next();
            }

            if (event != XMLStreamConstants.START_ELEMENT) {
                event = staxStreamReader.nextTag();
                if (event != XMLStreamConstants.START_ELEMENT) {
                    throw new IllegalStateException("The current event is " +
                            "not START_ELEMENT\n but" + event);
                }
            }

            handleStartDocument();

            do {
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT :
                        depth++;
                        handleStartElement();
                        break;
                    case XMLStreamConstants.END_ELEMENT :
                        handleEndElement();
                        depth--;
                        break;
                    case XMLStreamConstants.CHARACTERS :
                        handleCharacters();
                        break;
                    case XMLStreamConstants.ENTITY_REFERENCE :
                        handleEntityReference();
                        break;
                    case XMLStreamConstants.PROCESSING_INSTRUCTION :
                        handlePI();
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

                event=staxStreamReader.next();
            } while (depth!=0);

            handleEndDocument();
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleEndDocument() throws SAXException {
        _sax.endDocument();
    }

    private void handleStartDocument() throws SAXException {
        _sax.setDocumentLocator(new Locator2() {
            public int getColumnNumber() {
                return staxStreamReader.getLocation().getColumnNumber();
            }
            public int getLineNumber() {
                return staxStreamReader.getLocation().getLineNumber();
            }
            public String getPublicId() {
                return staxStreamReader.getLocation().getPublicId();
            }
            public String getSystemId() {
                return staxStreamReader.getLocation().getSystemId();
            }
            public String getXMLVersion() {
                return staxStreamReader.getVersion();
            }
            public String getEncoding() {
                return staxStreamReader.getEncoding();
            }
         });
        _sax.startDocument();
    }

    private void handlePI() throws XMLStreamException {
        try {
            _sax.processingInstruction(
                staxStreamReader.getPITarget(),
                staxStreamReader.getPIData());
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleCharacters() throws XMLStreamException {

        int textLength = staxStreamReader.getTextLength();
        char[] chars = new char[textLength];

        staxStreamReader.getTextCharacters(0, chars, 0, textLength);

        try {
            _sax.characters(chars, 0, chars.length);
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }


}

    private void handleEndElement() throws XMLStreamException {
        QName qName = staxStreamReader.getName();

        try {
            String qname = "";
            if (qName.getPrefix() != null && qName.getPrefix().trim().length() != 0){
                qname = qName.getPrefix() + ":";
            }
            qname += qName.getLocalPart();

            _sax.endElement(
                qName.getNamespaceURI(),
                qName.getLocalPart(),
                qname);

            int nsCount = staxStreamReader.getNamespaceCount();
            for (int i = nsCount - 1; i >= 0; i--) {
                String prefix = staxStreamReader.getNamespacePrefix(i);
                if (prefix == null) { prefix = "";
                }
                _sax.endPrefixMapping(prefix);
            }
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleStartElement() throws XMLStreamException {

        try {
            int nsCount = staxStreamReader.getNamespaceCount();
            for (int i = 0; i < nsCount; i++) {
                String prefix = staxStreamReader.getNamespacePrefix(i);
                if (prefix == null) { prefix = "";
                }
                _sax.startPrefixMapping(
                    prefix,
                    staxStreamReader.getNamespaceURI(i));
            }

            QName qName = staxStreamReader.getName();
            String prefix = qName.getPrefix();
            String rawname;
            if(prefix==null || prefix.length()==0)
                rawname = qName.getLocalPart();
            else
                rawname = prefix + ':' + qName.getLocalPart();
            Attributes attrs = getAttributes();
            _sax.startElement(
                qName.getNamespaceURI(),
                qName.getLocalPart(),
                rawname,
                attrs);
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }


    private Attributes getAttributes() {
        AttributesImpl attrs = new AttributesImpl();

        int eventType = staxStreamReader.getEventType();
        if (eventType != XMLStreamConstants.ATTRIBUTE
            && eventType != XMLStreamConstants.START_ELEMENT) {
            throw new InternalError(
                "getAttributes() attempting to process: " + eventType);
        }

        for (int i = 0; i < staxStreamReader.getAttributeCount(); i++) {
            String uri = staxStreamReader.getAttributeNamespace(i);
            if(uri==null)   uri="";
            String localName = staxStreamReader.getAttributeLocalName(i);
            String prefix = staxStreamReader.getAttributePrefix(i);
            String qName;
            if(prefix==null || prefix.length()==0)
                qName = localName;
            else
                qName = prefix + ':' + localName;
            String type = staxStreamReader.getAttributeType(i);
            String value = staxStreamReader.getAttributeValue(i);

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
