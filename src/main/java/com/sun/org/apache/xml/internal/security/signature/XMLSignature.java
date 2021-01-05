

package com.sun.org.apache.xml.internal.security.signature;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.crypto.SecretKey;

import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.SignerOutputStream;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


public final class XMLSignature extends SignatureElementProxy {


    public static final String ALGO_ID_MAC_HMAC_SHA1 =
        Constants.SignatureSpecNS + "hmac-sha1";


    public static final String ALGO_ID_SIGNATURE_DSA =
        Constants.SignatureSpecNS + "dsa-sha1";


    public static final String ALGO_ID_SIGNATURE_DSA_SHA256 =
        Constants.SignatureSpec11NS + "dsa-sha256";


    public static final String ALGO_ID_SIGNATURE_RSA =
        Constants.SignatureSpecNS + "rsa-sha1";


    public static final String ALGO_ID_SIGNATURE_RSA_SHA1 =
        Constants.SignatureSpecNS + "rsa-sha1";


    public static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 =
        Constants.MoreAlgorithmsSpecNS + "rsa-md5";


    public static final String ALGO_ID_SIGNATURE_RSA_RIPEMD160 =
        Constants.MoreAlgorithmsSpecNS + "rsa-ripemd160";


    public static final String ALGO_ID_SIGNATURE_RSA_SHA256 =
        Constants.MoreAlgorithmsSpecNS + "rsa-sha256";


    public static final String ALGO_ID_SIGNATURE_RSA_SHA384 =
        Constants.MoreAlgorithmsSpecNS + "rsa-sha384";


    public static final String ALGO_ID_SIGNATURE_RSA_SHA512 =
        Constants.MoreAlgorithmsSpecNS + "rsa-sha512";


    public static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 =
        Constants.MoreAlgorithmsSpecNS + "hmac-md5";


    public static final String ALGO_ID_MAC_HMAC_RIPEMD160 =
        Constants.MoreAlgorithmsSpecNS + "hmac-ripemd160";


    public static final String ALGO_ID_MAC_HMAC_SHA256 =
        Constants.MoreAlgorithmsSpecNS + "hmac-sha256";


    public static final String ALGO_ID_MAC_HMAC_SHA384 =
        Constants.MoreAlgorithmsSpecNS + "hmac-sha384";


    public static final String ALGO_ID_MAC_HMAC_SHA512 =
        Constants.MoreAlgorithmsSpecNS + "hmac-sha512";


    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA1 =
        "http:public static final String ALGO_ID_SIGNATURE_ECDSA_SHA256 =
        "http:public static final String ALGO_ID_SIGNATURE_ECDSA_SHA384 =
        "http:public static final String ALGO_ID_SIGNATURE_ECDSA_SHA512 =
        "http:private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(XMLSignature.class.getName());


    private SignedInfo signedInfo;


    private KeyInfo keyInfo;


    private boolean followManifestsDuringValidation = false;

    private Element signatureValueElement;

    private static final int MODE_SIGN = 0;
    private static final int MODE_VERIFY = 1;
    private int state = MODE_SIGN;


    public XMLSignature(Document doc, String baseURI, String signatureMethodURI)
        throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, 0, Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
    }


    public XMLSignature(Document doc, String baseURI, String signatureMethodURI,
                        int hmacOutputLength) throws XMLSecurityException {
        this(
            doc, baseURI, signatureMethodURI, hmacOutputLength,
            Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS
        );
    }


    public XMLSignature(
        Document doc,
        String baseURI,
        String signatureMethodURI,
        String canonicalizationMethodURI
    ) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, 0, canonicalizationMethodURI);
    }


    public XMLSignature(
        Document doc,
        String baseURI,
        String signatureMethodURI,
        int hmacOutputLength,
        String canonicalizationMethodURI
    ) throws XMLSecurityException {
        super(doc);

        String xmlnsDsPrefix = getDefaultPrefix(Constants.SignatureSpecNS);
        if (xmlnsDsPrefix == null || xmlnsDsPrefix.length() == 0) {
            this.constructionElement.setAttributeNS(
                Constants.NamespaceSpecNS, "xmlns", Constants.SignatureSpecNS
            );
        } else {
            this.constructionElement.setAttributeNS(
                Constants.NamespaceSpecNS, "xmlns:" + xmlnsDsPrefix, Constants.SignatureSpecNS
            );
        }
        XMLUtils.addReturnToElement(this.constructionElement);

        this.baseURI = baseURI;
        this.signedInfo =
            new SignedInfo(
                this.doc, signatureMethodURI, hmacOutputLength, canonicalizationMethodURI
            );

        this.constructionElement.appendChild(this.signedInfo.getElement());
        XMLUtils.addReturnToElement(this.constructionElement);

        signatureValueElement =
            XMLUtils.createElementInSignatureSpace(this.doc, Constants._TAG_SIGNATUREVALUE);

        this.constructionElement.appendChild(signatureValueElement);
        XMLUtils.addReturnToElement(this.constructionElement);
    }


    public XMLSignature(
        Document doc,
        String baseURI,
        Element SignatureMethodElem,
        Element CanonicalizationMethodElem
    ) throws XMLSecurityException {
        super(doc);

        String xmlnsDsPrefix = getDefaultPrefix(Constants.SignatureSpecNS);
        if (xmlnsDsPrefix == null || xmlnsDsPrefix.length() == 0) {
            this.constructionElement.setAttributeNS(
                Constants.NamespaceSpecNS, "xmlns", Constants.SignatureSpecNS
            );
        } else {
            this.constructionElement.setAttributeNS(
                Constants.NamespaceSpecNS, "xmlns:" + xmlnsDsPrefix, Constants.SignatureSpecNS
            );
        }
        XMLUtils.addReturnToElement(this.constructionElement);

        this.baseURI = baseURI;
        this.signedInfo =
            new SignedInfo(this.doc, SignatureMethodElem, CanonicalizationMethodElem);

        this.constructionElement.appendChild(this.signedInfo.getElement());
        XMLUtils.addReturnToElement(this.constructionElement);

        signatureValueElement =
            XMLUtils.createElementInSignatureSpace(this.doc, Constants._TAG_SIGNATUREVALUE);

        this.constructionElement.appendChild(signatureValueElement);
        XMLUtils.addReturnToElement(this.constructionElement);
    }


    public XMLSignature(Element element, String baseURI)
        throws XMLSignatureException, XMLSecurityException {
        this(element, baseURI, false);
    }


    public XMLSignature(Element element, String baseURI, boolean secureValidation)
        throws XMLSignatureException, XMLSecurityException {
        super(element, baseURI);

        Element signedInfoElem = XMLUtils.getNextElement(element.getFirstChild());

        if (signedInfoElem == null) {
            Object exArgs[] = { Constants._TAG_SIGNEDINFO, Constants._TAG_SIGNATURE };
            throw new XMLSignatureException("xml.WrongContent", exArgs);
        }

        this.signedInfo = new SignedInfo(signedInfoElem, baseURI, secureValidation);
        signedInfoElem = XMLUtils.getNextElement(element.getFirstChild());

        this.signatureValueElement =
            XMLUtils.getNextElement(signedInfoElem.getNextSibling());

        if (signatureValueElement == null) {
            Object exArgs[] = { Constants._TAG_SIGNATUREVALUE, Constants._TAG_SIGNATURE };
            throw new XMLSignatureException("xml.WrongContent", exArgs);
        }
        Attr signatureValueAttr = signatureValueElement.getAttributeNodeNS(null, "Id");
        if (signatureValueAttr != null) {
            signatureValueElement.setIdAttributeNode(signatureValueAttr, true);
        }

        Element keyInfoElem =
            XMLUtils.getNextElement(signatureValueElement.getNextSibling());

        if (keyInfoElem != null
            && keyInfoElem.getNamespaceURI().equals(Constants.SignatureSpecNS)
            && keyInfoElem.getLocalName().equals(Constants._TAG_KEYINFO)) {
            this.keyInfo = new KeyInfo(keyInfoElem, baseURI);
            this.keyInfo.setSecureValidation(secureValidation);
        }

        Element objectElem =
            XMLUtils.getNextElement(signatureValueElement.getNextSibling());
        while (objectElem != null) {
            Attr objectAttr = objectElem.getAttributeNodeNS(null, "Id");
            if (objectAttr != null) {
                objectElem.setIdAttributeNode(objectAttr, true);
            }

            NodeList nodes = objectElem.getChildNodes();
            int length = nodes.getLength();
            for (int i = 0; i < length; i++) {
                Node child = nodes.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElem = (Element)child;
                    String tag = childElem.getLocalName();
                    if (tag.equals("Manifest")) {
                        new Manifest(childElem, baseURI);
                    } else if (tag.equals("SignatureProperties")) {
                        new SignatureProperties(childElem, baseURI);
                    }
                }
            }

            objectElem = XMLUtils.getNextElement(objectElem.getNextSibling());
        }

        this.state = MODE_VERIFY;
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


    public SignedInfo getSignedInfo() {
        return this.signedInfo;
    }


    public byte[] getSignatureValue() throws XMLSignatureException {
        try {
            return Base64.decode(signatureValueElement);
        } catch (Base64DecodingException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    private void setSignatureValueElement(byte[] bytes) {

        while (signatureValueElement.hasChildNodes()) {
            signatureValueElement.removeChild(signatureValueElement.getFirstChild());
        }

        String base64codedValue = Base64.encode(bytes);

        if (base64codedValue.length() > 76 && !XMLUtils.ignoreLineBreaks()) {
            base64codedValue = "\n" + base64codedValue + "\n";
        }

        Text t = this.doc.createTextNode(base64codedValue);
        signatureValueElement.appendChild(t);
    }


    public KeyInfo getKeyInfo() {
        if (this.state == MODE_SIGN && this.keyInfo == null) {

            this.keyInfo = new KeyInfo(this.doc);

            Element keyInfoElement = this.keyInfo.getElement();
            Element firstObject =
                XMLUtils.selectDsNode(
                    this.constructionElement.getFirstChild(), Constants._TAG_OBJECT, 0
                );

            if (firstObject != null) {
                this.constructionElement.insertBefore(keyInfoElement, firstObject);
                XMLUtils.addReturnBeforeChild(this.constructionElement, firstObject);
            } else {
                this.constructionElement.appendChild(keyInfoElement);
                XMLUtils.addReturnToElement(this.constructionElement);
            }
        }

        return this.keyInfo;
    }


    public void appendObject(ObjectContainer object) throws XMLSignatureException {
        this.constructionElement.appendChild(object.getElement());
        XMLUtils.addReturnToElement(this.constructionElement);
        }


    public ObjectContainer getObjectItem(int i) {
        Element objElem =
            XMLUtils.selectDsNode(
                this.constructionElement.getFirstChild(), Constants._TAG_OBJECT, i
            );

        try {
            return new ObjectContainer(objElem, this.baseURI);
        } catch (XMLSecurityException ex) {
            return null;
        }
    }


    public int getObjectLength() {
        return this.length(Constants.SignatureSpecNS, Constants._TAG_OBJECT);
    }


    public void sign(Key signingKey) throws XMLSignatureException {

        if (signingKey instanceof PublicKey) {
            throw new IllegalArgumentException(
                I18n.translate("algorithms.operationOnlyVerification")
            );
        }

        try {
            SignedInfo si = this.getSignedInfo();
            SignatureAlgorithm sa = si.getSignatureAlgorithm();
            OutputStream so = null;
            try {
                sa.initSign(signingKey);

                si.generateDigestValues();
                so = new UnsyncBufferedOutputStream(new SignerOutputStream(sa));
                si.signInOctetStream(so);
            } catch (XMLSecurityException ex) {
                throw ex;
            } finally {
                if (so != null) {
                    try {
                        so.close();
                    } catch (IOException ex) {
                        if (log.isLoggable(java.util.logging.Level.FINE)) {
                            log.log(java.util.logging.Level.FINE, ex.getMessage(), ex);
                        }
                    }
                }
            }

            this.setSignatureValueElement(sa.sign());
        } catch (XMLSignatureException ex) {
            throw ex;
        } catch (CanonicalizationException ex) {
            throw new XMLSignatureException("empty", ex);
        } catch (InvalidCanonicalizerException ex) {
            throw new XMLSignatureException("empty", ex);
        } catch (XMLSecurityException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    public void addResourceResolver(ResourceResolver resolver) {
        this.getSignedInfo().addResourceResolver(resolver);
    }


    public void addResourceResolver(ResourceResolverSpi resolver) {
        this.getSignedInfo().addResourceResolver(resolver);
    }


    public boolean checkSignatureValue(X509Certificate cert)
        throws XMLSignatureException {
        if (cert != null) {
            return this.checkSignatureValue(cert.getPublicKey());
        }

        Object exArgs[] = { "Didn't get a certificate" };
        throw new XMLSignatureException("empty", exArgs);
    }


    public boolean checkSignatureValue(Key pk) throws XMLSignatureException {
        if (pk == null) {
            Object exArgs[] = { "Didn't get a key" };
            throw new XMLSignatureException("empty", exArgs);
        }
        try {
            SignedInfo si = this.getSignedInfo();
            SignatureAlgorithm sa = si.getSignatureAlgorithm();
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "signatureMethodURI = " + sa.getAlgorithmURI());
                log.log(java.util.logging.Level.FINE, "jceSigAlgorithm    = " + sa.getJCEAlgorithmString());
                log.log(java.util.logging.Level.FINE, "jceSigProvider     = " + sa.getJCEProviderName());
                log.log(java.util.logging.Level.FINE, "PublicKey = " + pk);
            }
            byte sigBytes[] = null;
            try {
                sa.initVerify(pk);

                SignerOutputStream so = new SignerOutputStream(sa);
                OutputStream bos = new UnsyncBufferedOutputStream(so);

                si.signInOctetStream(bos);
                bos.close();
                sigBytes = this.getSignatureValue();
            } catch (IOException ex) {
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, ex.getMessage(), ex);
                }
                } catch (XMLSecurityException ex) {
                throw ex;
            }

            if (!sa.verify(sigBytes)) {
                log.log(java.util.logging.Level.WARNING, "Signature verification failed.");
                return false;
            }

            return si.verify(this.followManifestsDuringValidation);
        } catch (XMLSignatureException ex) {
            throw ex;
        } catch (XMLSecurityException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    public void addDocument(
        String referenceURI,
        Transforms trans,
        String digestURI,
        String referenceId,
        String referenceType
    ) throws XMLSignatureException {
        this.signedInfo.addDocument(
            this.baseURI, referenceURI, trans, digestURI, referenceId, referenceType
        );
    }


    public void addDocument(
        String referenceURI,
        Transforms trans,
        String digestURI
    ) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, referenceURI, trans, digestURI, null, null);
    }


    public void addDocument(String referenceURI, Transforms trans)
        throws XMLSignatureException {
        this.signedInfo.addDocument(
            this.baseURI, referenceURI, trans, Constants.ALGO_ID_DIGEST_SHA1, null, null
        );
    }


    public void addDocument(String referenceURI) throws XMLSignatureException {
        this.signedInfo.addDocument(
            this.baseURI, referenceURI, null, Constants.ALGO_ID_DIGEST_SHA1, null, null
        );
    }


    public void addKeyInfo(X509Certificate cert) throws XMLSecurityException {
        X509Data x509data = new X509Data(this.doc);

        x509data.addCertificate(cert);
        this.getKeyInfo().add(x509data);
    }


    public void addKeyInfo(PublicKey pk) {
        this.getKeyInfo().add(pk);
    }


    public SecretKey createSecretKey(byte[] secretKeyBytes) {
        return this.getSignedInfo().createSecretKey(secretKeyBytes);
    }


    public void setFollowNestedManifests(boolean followManifests) {
        this.followManifestsDuringValidation = followManifests;
    }


    public String getBaseLocalName() {
        return Constants._TAG_SIGNATURE;
    }
}
