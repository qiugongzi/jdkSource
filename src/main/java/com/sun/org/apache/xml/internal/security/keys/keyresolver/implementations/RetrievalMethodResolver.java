

package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class RetrievalMethodResolver extends KeyResolverSpi {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(RetrievalMethodResolver.class.getName());


    public PublicKey engineLookupAndResolvePublicKey(
           Element element, String baseURI, StorageResolver storage
    ) {
        if (!XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_RETRIEVALMETHOD)) {
            return null;
        }

        try {
            RetrievalMethod rm = new RetrievalMethod(element, baseURI);
            String type = rm.getType();
            XMLSignatureInput resource = resolveInput(rm, baseURI, secureValidation);
            if (RetrievalMethod.TYPE_RAWX509.equals(type)) {
                X509Certificate cert = getRawCertificate(resource);
                if (cert != null) {
                    return cert.getPublicKey();
                }
                return null;
             }
             Element e = obtainReferenceElement(resource);

             if (XMLUtils.elementIsInSignatureSpace(e, Constants._TAG_RETRIEVALMETHOD)) {
                 if (secureValidation) {
                     String error = "Error: It is forbidden to have one RetrievalMethod "
                         + "point to another with secure validation";
                     if (log.isLoggable(java.util.logging.Level.FINE)) {
                         log.log(java.util.logging.Level.FINE, error);
                     }
                     return null;
                 }
                 RetrievalMethod rm2 = new RetrievalMethod(e, baseURI);
                 XMLSignatureInput resource2 = resolveInput(rm2, baseURI, secureValidation);
                 Element e2 = obtainReferenceElement(resource2);
                 if (e2 == element) {
                     if (log.isLoggable(java.util.logging.Level.FINE)) {
                         log.log(java.util.logging.Level.FINE, "Error: Can't have RetrievalMethods pointing to each other");
                     }
                     return null;
                 }
             }

             return resolveKey(e, baseURI, storage);
         } catch (XMLSecurityException ex) {
             if (log.isLoggable(java.util.logging.Level.FINE)) {
                 log.log(java.util.logging.Level.FINE, "XMLSecurityException", ex);
             }
         } catch (CertificateException ex) {
             if (log.isLoggable(java.util.logging.Level.FINE)) {
                 log.log(java.util.logging.Level.FINE, "CertificateException", ex);
             }
         } catch (IOException ex) {
             if (log.isLoggable(java.util.logging.Level.FINE)) {
                 log.log(java.util.logging.Level.FINE, "IOException", ex);
             }
         } catch (ParserConfigurationException e) {
             if (log.isLoggable(java.util.logging.Level.FINE)) {
                 log.log(java.util.logging.Level.FINE, "ParserConfigurationException", e);
             }
         } catch (SAXException e) {
             if (log.isLoggable(java.util.logging.Level.FINE)) {
                 log.log(java.util.logging.Level.FINE, "SAXException", e);
             }
         }
         return null;
    }


    public X509Certificate engineLookupResolveX509Certificate(
        Element element, String baseURI, StorageResolver storage) {
        if (!XMLUtils.elementIsInSignatureSpace(element, Constants._TAG_RETRIEVALMETHOD)) {
             return null;
        }

        try {
            RetrievalMethod rm = new RetrievalMethod(element, baseURI);
            String type = rm.getType();
            XMLSignatureInput resource = resolveInput(rm, baseURI, secureValidation);
            if (RetrievalMethod.TYPE_RAWX509.equals(type)) {
                return getRawCertificate(resource);
            }

            Element e = obtainReferenceElement(resource);

            if (XMLUtils.elementIsInSignatureSpace(e, Constants._TAG_RETRIEVALMETHOD)) {
                if (secureValidation) {
                    String error = "Error: It is forbidden to have one RetrievalMethod "
                        + "point to another with secure validation";
                    if (log.isLoggable(java.util.logging.Level.FINE)) {
                        log.log(java.util.logging.Level.FINE, error);
                    }
                    return null;
                }
                RetrievalMethod rm2 = new RetrievalMethod(e, baseURI);
                XMLSignatureInput resource2 = resolveInput(rm2, baseURI, secureValidation);
                Element e2 = obtainReferenceElement(resource2);
                if (e2 == element) {
                    if (log.isLoggable(java.util.logging.Level.FINE)) {
                        log.log(java.util.logging.Level.FINE, "Error: Can't have RetrievalMethods pointing to each other");
                    }
                    return null;
                }
            }

            return resolveCertificate(e, baseURI, storage);
        } catch (XMLSecurityException ex) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "XMLSecurityException", ex);
            }
        } catch (CertificateException ex) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "CertificateException", ex);
            }
        } catch (IOException ex) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "IOException", ex);
            }
        } catch (ParserConfigurationException e) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "ParserConfigurationException", e);
            }
        } catch (SAXException e) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "SAXException", e);
            }
        }
        return null;
    }


    private static X509Certificate resolveCertificate(
        Element e, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Now we have a {" + e.getNamespaceURI() + "}"
                + e.getLocalName() + " Element");
        }
        if (e != null) {
            return KeyResolver.getX509Certificate(e, baseURI, storage);
        }
        return null;
    }


    private static PublicKey resolveKey(
        Element e, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Now we have a {" + e.getNamespaceURI() + "}"
                + e.getLocalName() + " Element");
        }
        if (e != null) {
            return KeyResolver.getPublicKey(e, baseURI, storage);
        }
        return null;
    }

    private static Element obtainReferenceElement(XMLSignatureInput resource)
        throws CanonicalizationException, ParserConfigurationException,
        IOException, SAXException, KeyResolverException {
        Element e;
        if (resource.isElement()){
            e = (Element) resource.getSubNode();
        } else if (resource.isNodeSet()) {
            e = getDocumentElement(resource.getNodeSet());
        } else {
            byte inputBytes[] = resource.getBytes();
            e = getDocFromBytes(inputBytes);
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "we have to parse " + inputBytes.length + " bytes");
            }
        }
        return e;
    }

    private static X509Certificate getRawCertificate(XMLSignatureInput resource)
        throws CanonicalizationException, IOException, CertificateException {
        byte inputBytes[] = resource.getBytes();
        CertificateFactory certFact =
            CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
        X509Certificate cert = (X509Certificate)
            certFact.generateCertificate(new ByteArrayInputStream(inputBytes));
        return cert;
    }


    private static XMLSignatureInput resolveInput(
        RetrievalMethod rm, String baseURI, boolean secureValidation
    ) throws XMLSecurityException {
        Attr uri = rm.getURIAttr();
        Transforms transforms = rm.getTransforms();
        ResourceResolver resRes = ResourceResolver.getInstance(uri, baseURI, secureValidation);
        XMLSignatureInput resource = resRes.resolve(uri, baseURI, secureValidation);
        if (transforms != null) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "We have Transforms");
            }
            resource = transforms.performTransforms(resource);
        }
        return resource;
    }


    private static Element getDocFromBytes(byte[] bytes) throws KeyResolverException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(bytes));
            return doc.getDocumentElement();
        } catch (SAXException ex) {
            throw new KeyResolverException("empty", ex);
        } catch (IOException ex) {
            throw new KeyResolverException("empty", ex);
        } catch (ParserConfigurationException ex) {
            throw new KeyResolverException("empty", ex);
        }
    }


    public javax.crypto.SecretKey engineLookupAndResolveSecretKey(
        Element element, String baseURI, StorageResolver storage
    ) {
        return null;
    }

    private static Element getDocumentElement(Set<Node> set) {
        Iterator<Node> it = set.iterator();
        Element e = null;
        while (it.hasNext()) {
            Node currentNode = it.next();
            if (currentNode != null && Node.ELEMENT_NODE == currentNode.getNodeType()) {
                e = (Element) currentNode;
                break;
            }
        }
        List<Node> parents = new ArrayList<Node>();

        while (e != null) {
            parents.add(e);
            Node n = e.getParentNode();
            if (n == null || Node.ELEMENT_NODE != n.getNodeType()) {
                break;
            }
            e = (Element) n;
        }
        ListIterator<Node> it2 = parents.listIterator(parents.size()-1);
        Element ele = null;
        while (it2.hasPrevious()) {
            ele = (Element) it2.previous();
            if (set.contains(ele)) {
                return ele;
            }
        }
        return null;
    }
}
