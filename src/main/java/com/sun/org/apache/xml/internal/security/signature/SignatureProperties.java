

package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class SignatureProperties extends SignatureElementProxy {


    public SignatureProperties(Document doc) {
        super(doc);

        XMLUtils.addReturnToElement(this.constructionElement);
    }


    public SignatureProperties(Element element, String BaseURI) throws XMLSecurityException {
        super(element, BaseURI);

        Attr attr = element.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            element.setIdAttributeNode(attr, true);
        }

        int length = getLength();
        for (int i = 0; i < length; i++) {
            Element propertyElem =
                XMLUtils.selectDsNode(this.constructionElement, Constants._TAG_SIGNATUREPROPERTY, i);
            Attr propertyAttr = propertyElem.getAttributeNodeNS(null, "Id");
            if (propertyAttr != null) {
                propertyElem.setIdAttributeNode(propertyAttr, true);
            }
        }
    }


    public int getLength() {
        Element[] propertyElems =
            XMLUtils.selectDsNodes(this.constructionElement, Constants._TAG_SIGNATUREPROPERTY);

        return propertyElems.length;
    }


    public SignatureProperty item(int i) throws XMLSignatureException {
        try {
            Element propertyElem =
                XMLUtils.selectDsNode(this.constructionElement, Constants._TAG_SIGNATUREPROPERTY, i);

            if (propertyElem == null) {
                return null;
            }
            return new SignatureProperty(propertyElem, this.baseURI);
        } catch (XMLSecurityException ex) {
            throw new XMLSignatureException("empty", ex);
        }
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


    public void addSignatureProperty(SignatureProperty sp) {
        this.constructionElement.appendChild(sp.getElement());
        XMLUtils.addReturnToElement(this.constructionElement);
    }


    public String getBaseLocalName() {
        return Constants._TAG_SIGNATUREPROPERTIES;
    }
}
