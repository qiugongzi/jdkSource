

package com.sun.org.apache.xml.internal.security.keys.content;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DEREncodedKeyValue extends Signature11ElementProxy implements KeyInfoContent {


    private static final String supportedKeyTypes[] = { "RSA", "DSA", "EC"};


    public DEREncodedKeyValue(Element element, String BaseURI) throws XMLSecurityException {
        super(element, BaseURI);
    }


    public DEREncodedKeyValue(Document doc, PublicKey publicKey) throws XMLSecurityException {
        super(doc);

        this.addBase64Text(getEncodedDER(publicKey));
    }


    public DEREncodedKeyValue(Document doc, byte[] encodedKey) {
        super(doc);

        this.addBase64Text(encodedKey);
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
        return Constants._TAG_DERENCODEDKEYVALUE;
    }


    public PublicKey getPublicKey() throws XMLSecurityException {
        byte[] encodedKey = getBytesFromTextChild();

        for (String keyType : supportedKeyTypes) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance(keyType);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
                PublicKey publicKey = keyFactory.generatePublic(keySpec);
                if (publicKey != null) {
                    return publicKey;
                }
            } catch (NoSuchAlgorithmException e) {
                } catch (InvalidKeySpecException e) {
                }
        }
        throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedEncodedKey");
    }


    protected byte[] getEncodedDER(PublicKey publicKey) throws XMLSecurityException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(publicKey.getAlgorithm());
            X509EncodedKeySpec keySpec = keyFactory.getKeySpec(publicKey, X509EncodedKeySpec.class);
            return keySpec.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            Object exArgs[] = { publicKey.getAlgorithm(), publicKey.getFormat(), publicKey.getClass().getName() };
            throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedPublicKey", exArgs, e);
        } catch (InvalidKeySpecException e) {
            Object exArgs[] = { publicKey.getAlgorithm(), publicKey.getFormat(), publicKey.getClass().getName() };
            throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedPublicKey", exArgs, e);
        }
    }

}
