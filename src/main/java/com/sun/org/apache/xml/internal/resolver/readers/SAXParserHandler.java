
package com.sun.org.apache.xml.internal.resolver.readers;

import java.io.IOException;

import org.xml.sax.*;
import org.xml.sax.helpers.*;


public class SAXParserHandler extends DefaultHandler {
  private EntityResolver er = null;
  private ContentHandler ch = null;

  public SAXParserHandler() {
    super();
  }

  public void setEntityResolver(EntityResolver er) {
    this.er = er;
  }

  public void setContentHandler(ContentHandler ch) {
    this.ch = ch;
  }

  public InputSource resolveEntity(String publicId, String systemId)
    throws SAXException {

    if (er != null) {
      try {
        return er.resolveEntity(publicId, systemId);
      } catch (IOException e) {
          System.out.println("resolveEntity threw IOException!");
          return null;
      }
    } else {
      return null;
    }
  }

  public void characters(char[] ch, int start, int length)
    throws SAXException {
    if (this.ch != null) {
      this.ch.characters(ch, start, length);
    }
  }

  public void endDocument()
    throws SAXException {
    if (ch != null) {
      ch.endDocument();
    }
  }

  public void endElement(String namespaceURI, String localName, String qName)
    throws SAXException {
    if (ch != null) {
      ch.endElement(namespaceURI, localName, qName);
    }
  }

  public void endPrefixMapping(String prefix)
    throws SAXException {
    if (ch != null) {
      ch.endPrefixMapping(prefix);
    }
  }

  public void ignorableWhitespace(char[] ch, int start, int length)
    throws SAXException {
    if (this.ch != null) {
      this.ch.ignorableWhitespace(ch, start, length);
    }
  }

  public void processingInstruction(String target, String data)
    throws SAXException {
    if (ch != null) {
      ch.processingInstruction(target, data);
    }
  }

  public void setDocumentLocator(Locator locator) {
    if (ch != null) {
      ch.setDocumentLocator(locator);
    }
  }

  public void skippedEntity(String name)
    throws SAXException {
    if (ch != null) {
      ch.skippedEntity(name);
    }
  }

  public void startDocument()
    throws SAXException {
    if (ch != null) {
      ch.startDocument();
    }
  }

  public void startElement(String namespaceURI, String localName,
                           String qName, Attributes atts)
    throws SAXException {
    if (ch != null) {
      ch.startElement(namespaceURI, localName, qName, atts);
    }
  }

  public void startPrefixMapping(String prefix, String uri)
    throws SAXException {
    if (ch != null) {
      ch.startPrefixMapping(prefix, uri);
    }
  }
}
