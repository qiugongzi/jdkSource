

package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class KeyInfoReference extends Signature11ElementProxy implements KeyInfoContent {


    public KeyInfoReference(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }


    public KeyInfoReference(Document doc, String URI) {
        super(doc);

        this.constructionElement.setAttributeNS(null, Constants._ATT_URI, URI);
    }


    public Attr getURIAttr() {
        return this.constructionElement.getAttributeNodeNS(null, Constants._ATT_URI);
    }


    public String getURI() {
        return this.getURIAttr().getNodeValue();
    }


    public void setId(String id) {
        if (id != null) {
            this.constructionElement.setAttributeNS(null, Constants._ATT_ID, id);
            this.constructionElement.setIdAttributeNS(null, Constants._ATT_ID, true);
        } else {
            this.constructionElement.removeAttributeNS(null, Constants._ATT_ID);
        }
    }


    public String getId() {
        return this.constructionElement.getAttributeNS(null, Constants._ATT_ID);
    }


    public String getBaseLocalName() {
        return Constants._TAG_KEYINFOREFERENCE;
    }
}
