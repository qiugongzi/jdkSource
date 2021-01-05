

package com.sun.org.apache.xml.internal.security.signature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class SignedInfo extends Manifest {


    private SignatureAlgorithm signatureAlgorithm = null;


    private byte[] c14nizedBytes = null;

    private Element c14nMethod;
    private Element signatureMethod;


    public SignedInfo(Document doc) throws XMLSecurityException {
        this(doc, XMLSignature.ALGO_ID_SIGNATURE_DSA,
             Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
    }


    public SignedInfo(
        Document doc, String signatureMethodURI, String canonicalizationMethodURI
    ) throws XMLSecurityException {
        this(doc, signatureMethodURI, 0, canonicalizationMethodURI);
    }


    public SignedInfo(
        Document doc, String signatureMethodURI,
        int hMACOutputLength, String canonicalizationMethodURI
    ) throws XMLSecurityException {
        super(doc);

        c14nMethod =
            XMLUtils.createElementInSignatureSpace(this.doc, Constants._TAG_CANONICALIZATIONMETHOD);

        c14nMethod.setAttributeNS(null, Constants._ATT_ALGORITHM, canonicalizationMethodURI);
        this.constructionElement.appendChild(c14nMethod);
        XMLUtils.addReturnToElement(this.constructionElement);

        if (hMACOutputLength > 0) {
            this.signatureAlgorithm =
                new SignatureAlgorithm(this.doc, signatureMethodURI, hMACOutputLength);
        } else {
            this.signatureAlgorithm = new SignatureAlgorithm(this.doc, signatureMethodURI);
        }

        signatureMethod = this.signatureAlgorithm.getElement();
        this.constructionElement.appendChild(signatureMethod);
        XMLUtils.addReturnToElement(this.constructionElement);
    }


    public SignedInfo(
        Document doc, Element signatureMethodElem, Element canonicalizationMethodElem
    ) throws XMLSecurityException {
        super(doc);
        this.c14nMethod = canonicalizationMethodElem;
        this.constructionElement.appendChild(c14nMethod);
        XMLUtils.addReturnToElement(this.constructionElement);

        this.signatureAlgorithm =
            new SignatureAlgorithm(signatureMethodElem, null);

        signatureMethod = this.signatureAlgorithm.getElement();
        this.constructionElement.appendChild(signatureMethod);

        XMLUtils.addReturnToElement(this.constructionElement);
    }


    public SignedInfo(Element element, String baseURI) throws XMLSecurityException {
        this(element, baseURI, false);
    }


    public SignedInfo(
        Element element, String baseURI, boolean secureValidation
    ) throws XMLSecurityException {
        super(reparseSignedInfoElem(element), baseURI, secureValidation);

        c14nMethod = XMLUtils.getNextElement(element.getFirstChild());
        signatureMethod = XMLUtils.getNextElement(c14nMethod.getNextSibling());
        this.signatureAlgorithm =
            new SignatureAlgorithm(signatureMethod, this.getBaseURI(), secureValidation);
    }

    private static Element reparseSignedInfoElem(Element element)
        throws XMLSecurityException {

        Element c14nMethod = XMLUtils.getNextElement(element.getFirstChild());
        String c14nMethodURI =
            c14nMethod.getAttributeNS(null, Constants._ATT_ALGORITHM);
        if (!(c14nMethodURI.equals(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS) ||
            c14nMethodURI.equals(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS) ||
            c14nMethodURI.equals(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS) ||
            c14nMethodURI.equals(Canonicalizer.ALGO_ID_C14N_EXCL_WITH_COMMENTS) ||
            c14nMethodURI.equals(Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS) ||
            c14nMethodURI.equals(Canonicalizer.ALGO_ID_C14N11_WITH_COMMENTS))) {
            try {
                Canonicalizer c14nizer =
                    Canonicalizer.getInstance(c14nMethodURI);

                byte[] c14nizedBytes = c14nizer.canonicalizeSubtree(element);
                javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
                javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
                Document newdoc =
                    db.parse(new ByteArrayInputStream(c14nizedBytes));
                Node imported =
                    element.getOwnerDocument().importNode(newdoc.getDocumentElement(), true);

                element.getParentNode().replaceChild(imported, element);

                return (Element) imported;
            } catch (ParserConfigurationException ex) {
                throw new XMLSecurityException("empty", ex);
            } catch (IOException ex) {
                throw new XMLSecurityException("empty", ex);
            } catch (SAXException ex) {
                throw new XMLSecurityException("empty", ex);
            }
        }
        return element;
    }


    public boolean verify()
        throws MissingResourceFailureException, XMLSecurityException {
        return super.verifyReferences(false);
    }


    public boolean verify(boolean followManifests)
        throws MissingResourceFailureException, XMLSecurityException {
        return super.verifyReferences(followManifests);
    }


    public byte[] getCanonicalizedOctetStream()
        throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException {
        if (this.c14nizedBytes == null) {
            Canonicalizer c14nizer =
                Canonicalizer.getInstance(this.getCanonicalizationMethodURI());

            this.c14nizedBytes =
                c14nizer.canonicalizeSubtree(this.constructionElement);
        }

        return this.c14nizedBytes.clone();
    }


    public void signInOctetStream(OutputStream os)
        throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException {
        if (this.c14nizedBytes == null) {
            Canonicalizer c14nizer =
                Canonicalizer.getInstance(this.getCanonicalizationMethodURI());
            c14nizer.setWriter(os);
            String inclusiveNamespaces = this.getInclusiveNamespaces();

            if (inclusiveNamespaces == null) {
                c14nizer.canonicalizeSubtree(this.constructionElement);
            } else {
                c14nizer.canonicalizeSubtree(this.constructionElement, inclusiveNamespaces);
            }
        } else {
            try {
                os.write(this.c14nizedBytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public String getCanonicalizationMethodURI() {
        return c14nMethod.getAttributeNS(null, Constants._ATT_ALGORITHM);
    }


    public String getSignatureMethodURI() {
        Element signatureElement = this.getSignatureMethodElement();

        if (signatureElement != null) {
            return signatureElement.getAttributeNS(null, Constants._ATT_ALGORITHM);
        }

        return null;
    }


    public Element getSignatureMethodElement() {
        return signatureMethod;
    }


    public SecretKey createSecretKey(byte[] secretKeyBytes) {
        return new SecretKeySpec(secretKeyBytes, this.signatureAlgorithm.getJCEAlgorithmString());
    }

    protected SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }


    public String getBaseLocalName() {
        return Constants._TAG_SIGNEDINFO;
    }

    public String getInclusiveNamespaces() {
        String c14nMethodURI = c14nMethod.getAttributeNS(null, Constants._ATT_ALGORITHM);
        if (!(c14nMethodURI.equals("http:c14nMethodURI.equals("http:return null;
        }

        Element inclusiveElement = XMLUtils.getNextElement(c14nMethod.getFirstChild());

        if (inclusiveElement != null) {
            try {
                String inclusiveNamespaces =
                    new InclusiveNamespaces(
                        inclusiveElement,
                        InclusiveNamespaces.ExclusiveCanonicalizationNamespace
                    ).getInclusiveNamespaces();
                return inclusiveNamespaces;
            } catch (XMLSecurityException e) {
                return null;
            }
        }
        return null;
    }
}
