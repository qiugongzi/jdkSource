


package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;


public class PSVIDOMImplementationImpl extends CoreDOMImplementationImpl {

    static PSVIDOMImplementationImpl singleton = new PSVIDOMImplementationImpl();

    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    public boolean hasFeature(String feature, String version) {
        return super.hasFeature(feature, version) ||
               feature.equalsIgnoreCase("psvi");
    } public Document           createDocument(String namespaceURI,
                                             String qualifiedName,
                                             DocumentType doctype)
                                             throws DOMException
    {
        if (doctype != null && doctype.getOwnerDocument() != null) {
            throw new DOMException(DOMException.WRONG_DOCUMENT_ERR,
                                   DOMMessageFormatter.formatMessage(
                                   DOMMessageFormatter.XML_DOMAIN,
                                                       "WRONG_DOCUMENT_ERR", null));
        }
        DocumentImpl doc = new PSVIDocumentImpl(doctype);
        Element e = doc.createElementNS( namespaceURI, qualifiedName);
        doc.appendChild(e);
        return doc;
    }


}