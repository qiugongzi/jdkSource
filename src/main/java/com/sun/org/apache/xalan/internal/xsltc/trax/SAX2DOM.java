




package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Constants;
import jdk.xml.internal.JdkXmlUtils;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;


public class SAX2DOM implements ContentHandler, LexicalHandler, Constants {

    private Node _root = null;
    private Document _document = null;
    private Node _nextSibling = null;
    private Stack _nodeStk = new Stack();
    private Vector _namespaceDecls = null;
    private Node _lastSibling = null;
    private Locator locator = null;
    private boolean needToSetDocumentInfo = true;

    private StringBuilder _textBuffer = new StringBuilder();
    private Node _nextSiblingCache = null;

    private DocumentBuilderFactory _factory;
    private boolean _internal = true;

    public SAX2DOM(boolean overrideDefaultParser) throws ParserConfigurationException {
        _document = createDocument(overrideDefaultParser);
        _root = _document;
    }

    public SAX2DOM(Node root, Node nextSibling, boolean overrideDefaultParser)
            throws ParserConfigurationException {
        _root = root;
        if (root instanceof Document) {
          _document = (Document)root;
        }
        else if (root != null) {
          _document = root.getOwnerDocument();
        }
        else {
          _document = createDocument(overrideDefaultParser);
          _root = _document;
        }

        _nextSibling = nextSibling;
    }

    public SAX2DOM(Node root, boolean overrideDefaultParser)
            throws ParserConfigurationException {
        this(root, null, overrideDefaultParser);
    }

    public Node getDOM() {
        return _root;
    }

    public void characters(char[] ch, int start, int length) {
        if (length == 0) {
            return;
        }

        final Node last = (Node)_nodeStk.peek();

        if (last != _document) {
            _nextSiblingCache = _nextSibling;
            _textBuffer.append(ch, start, length);
        }
    }
    private void appendTextNode() {
        if (_textBuffer.length() > 0) {
            final Node last = (Node)_nodeStk.peek();
            if (last == _root && _nextSiblingCache != null) {
                _lastSibling = last.insertBefore(_document.createTextNode(_textBuffer.toString()), _nextSiblingCache);
            }
            else {
                _lastSibling = last.appendChild(_document.createTextNode(_textBuffer.toString()));
            }
            _textBuffer.setLength(0);
        }
    }
    public void startDocument() {
        _nodeStk.push(_root);
    }

    public void endDocument() {
        _nodeStk.pop();
    }

    private void setDocumentInfo() {
        if (locator == null) return;
        try{
            _document.setXmlVersion(((Locator2)locator).getXMLVersion());
        }catch(ClassCastException e){}

    }

    public void startElement(String namespace, String localName, String qName,
        Attributes attrs)
    {
        appendTextNode();
        if (needToSetDocumentInfo) {
            setDocumentInfo();
            needToSetDocumentInfo = false;
        }

        final Element tmp = (Element)_document.createElementNS(namespace, qName);

        if (_namespaceDecls != null) {
            final int nDecls = _namespaceDecls.size();
            for (int i = 0; i < nDecls; i++) {
                final String prefix = (String) _namespaceDecls.elementAt(i++);

                if (prefix == null || prefix.equals(EMPTYSTRING)) {
                    tmp.setAttributeNS(XMLNS_URI, XMLNS_PREFIX,
                        (String) _namespaceDecls.elementAt(i));
                }
                else {
                    tmp.setAttributeNS(XMLNS_URI, XMLNS_STRING + prefix,
                        (String) _namespaceDecls.elementAt(i));
                }
            }
            _namespaceDecls.clear();
        }

        final int nattrs = attrs.getLength();
        for (int i = 0; i < nattrs; i++) {
            String attQName = attrs.getQName(i);
            String attURI = attrs.getURI(i);
            if (attrs.getLocalName(i).equals("")) {
                tmp.setAttribute(attQName, attrs.getValue(i));
                if (attrs.getType(i).equals("ID")) {
                    tmp.setIdAttribute(attQName, true);
                }
            } else {
                tmp.setAttributeNS(attURI, attQName, attrs.getValue(i));
                if (attrs.getType(i).equals("ID")) {
                    tmp.setIdAttributeNS(attURI, attrs.getLocalName(i), true);
                }
            }
        }


        Node last = (Node)_nodeStk.peek();

        if (last == _root && _nextSibling != null)
            last.insertBefore(tmp, _nextSibling);
        else
            last.appendChild(tmp);

        _nodeStk.push(tmp);
        _lastSibling = null;
    }

    public void endElement(String namespace, String localName, String qName) {
        appendTextNode();
        _nodeStk.pop();
        _lastSibling = null;
    }

    public void startPrefixMapping(String prefix, String uri) {
        if (_namespaceDecls == null) {
            _namespaceDecls = new Vector(2);
        }
        _namespaceDecls.addElement(prefix);
        _namespaceDecls.addElement(uri);
    }

    public void endPrefixMapping(String prefix) {
        }


    public void ignorableWhitespace(char[] ch, int start, int length) {
    }


    public void processingInstruction(String target, String data) {
        appendTextNode();
        final Node last = (Node)_nodeStk.peek();
        ProcessingInstruction pi = _document.createProcessingInstruction(
                target, data);
        if (pi != null){
          if (last == _root && _nextSibling != null)
              last.insertBefore(pi, _nextSibling);
          else
              last.appendChild(pi);

          _lastSibling = pi;
        }
    }


    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }


    public void skippedEntity(String name) {
    }



    public void comment(char[] ch, int start, int length) {
        appendTextNode();
        final Node last = (Node)_nodeStk.peek();
        Comment comment = _document.createComment(new String(ch,start,length));
        if (comment != null){
          if (last == _root && _nextSibling != null)
              last.insertBefore(comment, _nextSibling);
          else
              last.appendChild(comment);

          _lastSibling = comment;
        }
    }

    public void startCDATA() { }
    public void endCDATA() { }
    public void startEntity(java.lang.String name) { }
    public void endDTD() { }
    public void endEntity(String name) { }
    public void startDTD(String name, String publicId, String systemId)
        throws SAXException {}

    private Document createDocument(boolean overrideDefaultParser)
            throws ParserConfigurationException {
        if (_factory == null) {
            _factory = JdkXmlUtils.getDOMFactory(overrideDefaultParser);
            _internal = true;
            if (!(_factory instanceof com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl)) {
                _internal = false;
            }
        }
        Document doc;
        if (_internal) {
            doc = _factory.newDocumentBuilder().newDocument();
        } else {
            synchronized(SAX2DOM.class) {
                doc = _factory.newDocumentBuilder().newDocument();
            }
        }
        return doc;
    }

}
