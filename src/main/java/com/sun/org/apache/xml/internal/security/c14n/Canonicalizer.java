

package com.sun.org.apache.xml.internal.security.c14n;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_WithComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclWithComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315OmitComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315WithComments;
import com.sun.org.apache.xml.internal.security.c14n.implementations.CanonicalizerPhysical;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class Canonicalizer {


    public static final String ENCODING = "UTF8";


    public static final String XPATH_C14N_WITH_COMMENTS_SINGLE_NODE =
        "(.public static final String ALGO_ID_C14N_OMIT_COMMENTS =
        "http:public static final String ALGO_ID_C14N_WITH_COMMENTS =
        ALGO_ID_C14N_OMIT_COMMENTS + "#WithComments";

    public static final String ALGO_ID_C14N_EXCL_OMIT_COMMENTS =
        "http:public static final String ALGO_ID_C14N_EXCL_WITH_COMMENTS =
        ALGO_ID_C14N_EXCL_OMIT_COMMENTS + "WithComments";

    public static final String ALGO_ID_C14N11_OMIT_COMMENTS =
        "http:public static final String ALGO_ID_C14N11_WITH_COMMENTS =
        ALGO_ID_C14N11_OMIT_COMMENTS + "#WithComments";

    public static final String ALGO_ID_C14N_PHYSICAL =
        "http:private static Map<String, Class<? extends CanonicalizerSpi>> canonicalizerHash =
        new ConcurrentHashMap<String, Class<? extends CanonicalizerSpi>>();

    private final CanonicalizerSpi canonicalizerSpi;


    private Canonicalizer(String algorithmURI) throws InvalidCanonicalizerException {
        try {
            Class<? extends CanonicalizerSpi> implementingClass =
                canonicalizerHash.get(algorithmURI);

            canonicalizerSpi = implementingClass.newInstance();
            canonicalizerSpi.reset = true;
        } catch (Exception e) {
            Object exArgs[] = { algorithmURI };
            throw new InvalidCanonicalizerException(
                "signature.Canonicalizer.UnknownCanonicalizer", exArgs, e
            );
        }
    }


    public static final Canonicalizer getInstance(String algorithmURI)
        throws InvalidCanonicalizerException {
        return new Canonicalizer(algorithmURI);
    }


    @SuppressWarnings("unchecked")
    public static void register(String algorithmURI, String implementingClass)
        throws AlgorithmAlreadyRegisteredException, ClassNotFoundException {
        JavaUtils.checkRegisterPermission();
        Class<? extends CanonicalizerSpi> registeredClass =
            canonicalizerHash.get(algorithmURI);

        if (registeredClass != null)  {
            Object exArgs[] = { algorithmURI, registeredClass };
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", exArgs);
        }

        canonicalizerHash.put(
            algorithmURI, (Class<? extends CanonicalizerSpi>)Class.forName(implementingClass)
        );
    }


    public static void register(String algorithmURI, Class<? extends CanonicalizerSpi> implementingClass)
        throws AlgorithmAlreadyRegisteredException, ClassNotFoundException {
        JavaUtils.checkRegisterPermission();
        Class<? extends CanonicalizerSpi> registeredClass = canonicalizerHash.get(algorithmURI);

        if (registeredClass != null)  {
            Object exArgs[] = { algorithmURI, registeredClass };
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", exArgs);
        }

        canonicalizerHash.put(algorithmURI, implementingClass);
    }


    public static void registerDefaultAlgorithms() {
        canonicalizerHash.put(
            Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS,
            Canonicalizer20010315OmitComments.class
        );
        canonicalizerHash.put(
            Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS,
            Canonicalizer20010315WithComments.class
        );
        canonicalizerHash.put(
            Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS,
            Canonicalizer20010315ExclOmitComments.class
        );
        canonicalizerHash.put(
            Canonicalizer.ALGO_ID_C14N_EXCL_WITH_COMMENTS,
            Canonicalizer20010315ExclWithComments.class
        );
        canonicalizerHash.put(
            Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS,
            Canonicalizer11_OmitComments.class
        );
        canonicalizerHash.put(
            Canonicalizer.ALGO_ID_C14N11_WITH_COMMENTS,
            Canonicalizer11_WithComments.class
        );
        canonicalizerHash.put(
            Canonicalizer.ALGO_ID_C14N_PHYSICAL,
            CanonicalizerPhysical.class
        );
    }


    public final String getURI() {
        return canonicalizerSpi.engineGetURI();
    }


    public boolean getIncludeComments() {
        return canonicalizerSpi.engineGetIncludeComments();
    }


    public byte[] canonicalize(byte[] inputBytes)
        throws javax.xml.parsers.ParserConfigurationException,
        java.io.IOException, org.xml.sax.SAXException, CanonicalizationException {
        InputStream bais = new ByteArrayInputStream(inputBytes);
        InputSource in = new InputSource(bais);
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        dfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);

        dfactory.setNamespaceAware(true);

        dfactory.setValidating(true);

        DocumentBuilder db = dfactory.newDocumentBuilder();


        db.setErrorHandler(new com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler());

        Document document = db.parse(in);
        return this.canonicalizeSubtree(document);
    }


    public byte[] canonicalizeSubtree(Node node) throws CanonicalizationException {
        return canonicalizerSpi.engineCanonicalizeSubTree(node);
    }


    public byte[] canonicalizeSubtree(Node node, String inclusiveNamespaces)
        throws CanonicalizationException {
        return canonicalizerSpi.engineCanonicalizeSubTree(node, inclusiveNamespaces);
    }


    public byte[] canonicalizeXPathNodeSet(NodeList xpathNodeSet)
        throws CanonicalizationException {
        return canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet);
    }


    public byte[] canonicalizeXPathNodeSet(
        NodeList xpathNodeSet, String inclusiveNamespaces
    ) throws CanonicalizationException {
        return
            canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet, inclusiveNamespaces);
    }


    public byte[] canonicalizeXPathNodeSet(Set<Node> xpathNodeSet)
        throws CanonicalizationException {
        return canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet);
    }


    public byte[] canonicalizeXPathNodeSet(
        Set<Node> xpathNodeSet, String inclusiveNamespaces
    ) throws CanonicalizationException {
        return
            canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet, inclusiveNamespaces);
    }


    public void setWriter(OutputStream os) {
        canonicalizerSpi.setWriter(os);
    }


    public String getImplementingCanonicalizerClass() {
        return canonicalizerSpi.getClass().getName();
    }


    public void notReset() {
        canonicalizerSpi.reset = false;
    }

}
