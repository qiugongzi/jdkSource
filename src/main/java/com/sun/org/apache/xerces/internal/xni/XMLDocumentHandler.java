


package com.sun.org.apache.xerces.internal.xni;

import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;


public interface XMLDocumentHandler {

    public void startDocument(XMLLocator locator, String encoding,
                              NamespaceContext namespaceContext,
                              Augmentations augs)
        throws XNIException;


    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs)
        throws XNIException;


    public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs)
        throws XNIException;


    public void comment(XMLString text, Augmentations augs) throws XNIException;


    public void processingInstruction(String target, XMLString data, Augmentations augs)
        throws XNIException;


    public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException;


    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException;


    public void startGeneralEntity(String name,
                                   XMLResourceIdentifier identifier,
                                   String encoding,
                                   Augmentations augs) throws XNIException;


    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException;


    public void endGeneralEntity(String name, Augmentations augs) throws XNIException;


    public void characters(XMLString text, Augmentations augs) throws XNIException;


    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException;


    public void endElement(QName element, Augmentations augs) throws XNIException;


    public void startCDATA(Augmentations augs) throws XNIException;


    public void endCDATA(Augmentations augs) throws XNIException;


    public void endDocument(Augmentations augs) throws XNIException;



    public void setDocumentSource(XMLDocumentSource source);



    public XMLDocumentSource getDocumentSource();

}