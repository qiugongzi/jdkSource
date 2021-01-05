

package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class SignatureProperty extends SignatureElementProxy {


    public SignatureProperty(Document doc, String target) {
        this(doc, target, null);
    }


    public SignatureProperty(Document doc, String target, String id) {
        super(doc);

        this.setTarget(target);
        this.setId(id);
    }


    public SignatureProperty(Element element, String BaseURI) throws XMLSecurityException {
        super(element, BaseURI);
    }


    public void setId(String id) {
        if (id != null) {
            this.constructionElement.setAttributeNS(null, Constants._ATT_ID, id);
            this.constructionElement.setIdAttributeNS(null, Constants._ATT_ID, true);
        }
    }


    public String getId() {
        return this.constructionElement.getAttributeNS(null, Constants._ATT_ID);
    }


    public void setTarget(String target) {
        if (target != null) {
            this.constructionElement.setAttributeNS(null, Constants._ATT_TARGET, target);
        }
    }


    public String getTarget() {
        return this.constructionElement.getAttributeNS(null, Constants._ATT_TARGET);
    }


    public Node appendChild(Node node) {
        return this.constructionElement.appendChild(node);
    }


    public String getBaseLocalName() {
        return Constants._TAG_SIGNATUREPROPERTY;
    }
}
