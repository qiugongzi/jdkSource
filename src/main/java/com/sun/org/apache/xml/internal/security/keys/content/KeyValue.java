

package com.sun.org.apache.xml.internal.security.keys.content;

import java.security.PublicKey;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class KeyValue extends SignatureElementProxy implements KeyInfoContent {


    public KeyValue(Document doc, DSAKeyValue dsaKeyValue) {
        super(doc);

        XMLUtils.addReturnToElement(this.constructionElement);
        this.constructionElement.appendChild(dsaKeyValue.getElement());
        XMLUtils.addReturnToElement(this.constructionElement);
    }


    public KeyValue(Document doc, RSAKeyValue rsaKeyValue) {
        super(doc);

        XMLUtils.addReturnToElement(this.constructionElement);
        this.constructionElement.appendChild(rsaKeyValue.getElement());
        XMLUtils.addReturnToElement(this.constructionElement);
    }


    public KeyValue(Document doc, Element unknownKeyValue) {
        super(doc);

        XMLUtils.addReturnToElement(this.constructionElement);
        this.constructionElement.appendChild(unknownKeyValue);
        XMLUtils.addReturnToElement(this.constructionElement);
    }


    public KeyValue(Document doc, PublicKey pk) {
        super(doc);

        XMLUtils.addReturnToElement(this.constructionElement);

        if (pk instanceof java.security.interfaces.DSAPublicKey) {
            DSAKeyValue dsa = new DSAKeyValue(this.doc, pk);

            this.constructionElement.appendChild(dsa.getElement());
            XMLUtils.addReturnToElement(this.constructionElement);
        } else if (pk instanceof java.security.interfaces.RSAPublicKey) {
            RSAKeyValue rsa = new RSAKeyValue(this.doc, pk);

            this.constructionElement.appendChild(rsa.getElement());
            XMLUtils.addReturnToElement(this.constructionElement);
        }
    }


    public KeyValue(Element element, String BaseURI) throws XMLSecurityException {
        super(element, BaseURI);
    }


    public PublicKey getPublicKey() throws XMLSecurityException {
        Element rsa =
            XMLUtils.selectDsNode(
                this.constructionElement.getFirstChild(), Constants._TAG_RSAKEYVALUE, 0);

        if (rsa != null) {
            RSAKeyValue kv = new RSAKeyValue(rsa, this.baseURI);
            return kv.getPublicKey();
        }

        Element dsa =
            XMLUtils.selectDsNode(
                this.constructionElement.getFirstChild(), Constants._TAG_DSAKEYVALUE, 0);

        if (dsa != null) {
            DSAKeyValue kv = new DSAKeyValue(dsa, this.baseURI);
            return kv.getPublicKey();
        }

        return null;
    }


    public String getBaseLocalName() {
        return Constants._TAG_KEYVALUE;
    }
}
