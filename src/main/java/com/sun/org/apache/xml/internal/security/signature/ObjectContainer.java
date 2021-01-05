

package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



public class ObjectContainer extends SignatureElementProxy {


    public ObjectContainer(Document doc) {
        super(doc);
    }


    public ObjectContainer(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }


    public void setId(String Id) {
        if (Id != null) {
            this.constructionElement.setAttributeNS(null, Constants._ATT_ID, Id);
            this.constructionElement.setIdAttributeNS(null, Constants._ATT_ID, true);
        }
    }


    public String getId() {
        return this.constructionElement.getAttributeNS(null, Constants._ATT_ID);
    }


    public void setMimeType(String MimeType) {
        if (MimeType != null) {
            this.constructionElement.setAttributeNS(null, Constants._ATT_MIMETYPE, MimeType);
        }
    }


    public String getMimeType() {
        return this.constructionElement.getAttributeNS(null, Constants._ATT_MIMETYPE);
    }


    public void setEncoding(String Encoding) {
        if (Encoding != null) {
            this.constructionElement.setAttributeNS(null, Constants._ATT_ENCODING, Encoding);
        }
    }


    public String getEncoding() {
        return this.constructionElement.getAttributeNS(null, Constants._ATT_ENCODING);
    }


    public Node appendChild(Node node) {
        return this.constructionElement.appendChild(node);
    }


    public String getBaseLocalName() {
        return Constants._TAG_OBJECT;
    }
}
