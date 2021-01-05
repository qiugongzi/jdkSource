

package com.sun.org.apache.xml.internal.security.transforms;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class Transforms extends SignatureElementProxy {


    public static final String TRANSFORM_C14N_OMIT_COMMENTS
        = Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS;


    public static final String TRANSFORM_C14N_WITH_COMMENTS
        = Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS;


    public static final String TRANSFORM_C14N11_OMIT_COMMENTS
        = Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS;


    public static final String TRANSFORM_C14N11_WITH_COMMENTS
        = Canonicalizer.ALGO_ID_C14N11_WITH_COMMENTS;


    public static final String TRANSFORM_C14N_EXCL_OMIT_COMMENTS
        = Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS;


    public static final String TRANSFORM_C14N_EXCL_WITH_COMMENTS
        = Canonicalizer.ALGO_ID_C14N_EXCL_WITH_COMMENTS;


    public static final String TRANSFORM_XSLT
        = "http:public static final String TRANSFORM_BASE64_DECODE
        = Constants.SignatureSpecNS + "base64";


    public static final String TRANSFORM_XPATH
        = "http:public static final String TRANSFORM_ENVELOPED_SIGNATURE
        = Constants.SignatureSpecNS + "enveloped-signature";


    public static final String TRANSFORM_XPOINTER
        = "http:public static final String TRANSFORM_XPATH2FILTER
        = "http:private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Transforms.class.getName());

    private Element[] transforms;

    protected Transforms() { };

    private boolean secureValidation;


    public Transforms(Document doc) {
        super(doc);
        XMLUtils.addReturnToElement(this.constructionElement);
    }


    public Transforms(Element element, String BaseURI)
        throws DOMException, XMLSignatureException, InvalidTransformException,
            TransformationException, XMLSecurityException {
        super(element, BaseURI);

        int numberOfTransformElems = this.getLength();

        if (numberOfTransformElems == 0) {
            Object exArgs[] = { Constants._TAG_TRANSFORM, Constants._TAG_TRANSFORMS };

            throw new TransformationException("xml.WrongContent", exArgs);
        }
    }


    public void setSecureValidation(boolean secureValidation) {
        this.secureValidation = secureValidation;
    }


    public void addTransform(String transformURI) throws TransformationException {
        try {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "Transforms.addTransform(" + transformURI + ")");
            }

            Transform transform = new Transform(this.doc, transformURI);

            this.addTransform(transform);
        } catch (InvalidTransformException ex) {
            throw new TransformationException("empty", ex);
        }
    }


    public void addTransform(String transformURI, Element contextElement)
       throws TransformationException {
        try {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "Transforms.addTransform(" + transformURI + ")");
            }

            Transform transform = new Transform(this.doc, transformURI, contextElement);

            this.addTransform(transform);
        } catch (InvalidTransformException ex) {
            throw new TransformationException("empty", ex);
        }
    }


    public void addTransform(String transformURI, NodeList contextNodes)
       throws TransformationException {

        try {
            Transform transform = new Transform(this.doc, transformURI, contextNodes);
            this.addTransform(transform);
        } catch (InvalidTransformException ex) {
            throw new TransformationException("empty", ex);
        }
    }


    private void addTransform(Transform transform) {
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Transforms.addTransform(" + transform.getURI() + ")");
        }

        Element transformElement = transform.getElement();

        this.constructionElement.appendChild(transformElement);
        XMLUtils.addReturnToElement(this.constructionElement);
    }


    public XMLSignatureInput performTransforms(
        XMLSignatureInput xmlSignatureInput
    ) throws TransformationException {
        return performTransforms(xmlSignatureInput, null);
    }


    public XMLSignatureInput performTransforms(
        XMLSignatureInput xmlSignatureInput, OutputStream os
    ) throws TransformationException {
        try {
            int last = this.getLength() - 1;
            for (int i = 0; i < last; i++) {
                Transform t = this.item(i);
                String uri = t.getURI();
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, "Perform the (" + i + ")th " + uri + " transform");
                }
                checkSecureValidation(t);
                xmlSignatureInput = t.performTransform(xmlSignatureInput);
            }
            if (last >= 0) {
                Transform t = this.item(last);
                checkSecureValidation(t);
                xmlSignatureInput = t.performTransform(xmlSignatureInput, os);
            }

            return xmlSignatureInput;
        } catch (IOException ex) {
            throw new TransformationException("empty", ex);
        } catch (CanonicalizationException ex) {
            throw new TransformationException("empty", ex);
        } catch (InvalidCanonicalizerException ex) {
            throw new TransformationException("empty", ex);
        }
    }

    private void checkSecureValidation(Transform transform) throws TransformationException {
        String uri = transform.getURI();
        if (secureValidation && Transforms.TRANSFORM_XSLT.equals(uri)) {
            Object exArgs[] = { uri };

            throw new TransformationException(
                "signature.Transform.ForbiddenTransform", exArgs
            );
        }
    }


    public int getLength() {
        if (transforms == null) {
            transforms =
                XMLUtils.selectDsNodes(this.constructionElement.getFirstChild(), "Transform");
        }
        return transforms.length;
    }


    public Transform item(int i) throws TransformationException {
        try {
            if (transforms == null) {
                transforms =
                    XMLUtils.selectDsNodes(this.constructionElement.getFirstChild(), "Transform");
            }
            return new Transform(transforms[i], this.baseURI);
        } catch (XMLSecurityException ex) {
            throw new TransformationException("empty", ex);
        }
    }


    public String getBaseLocalName() {
        return Constants._TAG_TRANSFORMS;
    }
}
