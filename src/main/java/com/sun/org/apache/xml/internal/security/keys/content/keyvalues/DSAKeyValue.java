

package com.sun.org.apache.xml.internal.security.keys.content.keyvalues;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DSAKeyValue extends SignatureElementProxy implements KeyValueContent {


    public DSAKeyValue(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }


    public DSAKeyValue(Document doc, BigInteger P, BigInteger Q, BigInteger G, BigInteger Y) {
        super(doc);

        XMLUtils.addReturnToElement(this.constructionElement);
        this.addBigIntegerElement(P, Constants._TAG_P);
        this.addBigIntegerElement(Q, Constants._TAG_Q);
        this.addBigIntegerElement(G, Constants._TAG_G);
        this.addBigIntegerElement(Y, Constants._TAG_Y);
    }


    public DSAKeyValue(Document doc, Key key) throws IllegalArgumentException {
        super(doc);

        XMLUtils.addReturnToElement(this.constructionElement);

        if (key instanceof java.security.interfaces.DSAPublicKey) {
            this.addBigIntegerElement(((DSAPublicKey) key).getParams().getP(), Constants._TAG_P);
            this.addBigIntegerElement(((DSAPublicKey) key).getParams().getQ(), Constants._TAG_Q);
            this.addBigIntegerElement(((DSAPublicKey) key).getParams().getG(), Constants._TAG_G);
            this.addBigIntegerElement(((DSAPublicKey) key).getY(), Constants._TAG_Y);
        } else {
            Object exArgs[] = { Constants._TAG_DSAKEYVALUE, key.getClass().getName() };

            throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", exArgs));
        }
    }


    public PublicKey getPublicKey() throws XMLSecurityException {
        try {
            DSAPublicKeySpec pkspec =
                new DSAPublicKeySpec(
                    this.getBigIntegerFromChildElement(
                        Constants._TAG_Y, Constants.SignatureSpecNS
                    ),
                    this.getBigIntegerFromChildElement(
                        Constants._TAG_P, Constants.SignatureSpecNS
                    ),
                    this.getBigIntegerFromChildElement(
                        Constants._TAG_Q, Constants.SignatureSpecNS
                    ),
                    this.getBigIntegerFromChildElement(
                        Constants._TAG_G, Constants.SignatureSpecNS
                    )
                );
            KeyFactory dsaFactory = KeyFactory.getInstance("DSA");
            PublicKey pk = dsaFactory.generatePublic(pkspec);

            return pk;
        } catch (NoSuchAlgorithmException ex) {
            throw new XMLSecurityException("empty", ex);
        } catch (InvalidKeySpecException ex) {
            throw new XMLSecurityException("empty", ex);
        }
    }


    public String getBaseLocalName() {
        return Constants._TAG_DSAKEYVALUE;
    }
}
