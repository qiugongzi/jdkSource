


package com.sun.org.apache.xerces.internal.xni;


public interface XMLDocumentFragmentHandler {

    public void startDocumentFragment(XMLLocator locator,
                                      NamespaceContext namespaceContext,
                                      Augmentations augmentations)
        throws XNIException;


    public void startGeneralEntity(String name,
                                   XMLResourceIdentifier identifier,
                                   String encoding,
                                   Augmentations augmentations) throws XNIException;


    public void textDecl(String version, String encoding,
                         Augmentations augmentations) throws XNIException;


    public void endGeneralEntity(String name, Augmentations augmentations)
        throws XNIException;


    public void comment(XMLString text, Augmentations augmentations)
        throws XNIException;


    public void processingInstruction(String target, XMLString data,
                                      Augmentations augmentations)
        throws XNIException;


    public void startElement(QName element, XMLAttributes attributes,
                             Augmentations augmentations) throws XNIException;


    public void emptyElement(QName element, XMLAttributes attributes,
                             Augmentations augmentations) throws XNIException;


    public void characters(XMLString text, Augmentations augmentations)
        throws XNIException;


    public void ignorableWhitespace(XMLString text,
                                    Augmentations augmentations)
        throws XNIException;


    public void endElement(QName element, Augmentations augmentations)
        throws XNIException;


    public void startCDATA(Augmentations augmentations) throws XNIException;


    public void endCDATA(Augmentations augmentations) throws XNIException;


    public void endDocumentFragment(Augmentations augmentations)
        throws XNIException;

}