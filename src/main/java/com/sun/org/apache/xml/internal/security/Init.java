

package com.sun.org.apache.xml.internal.security;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



public class Init {


    public static final String CONF_NS = "http:private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(Init.class.getName());


    private static boolean alreadyInitialized = false;


    public static synchronized final boolean isInitialized() {
        return Init.alreadyInitialized;
    }


    public static synchronized void init() {
        if (alreadyInitialized) {
            return;
        }

        InputStream is =
            AccessController.doPrivileged(
                new PrivilegedAction<InputStream>() {
                    public InputStream run() {
                        String cfile =
                            System.getProperty("com.sun.org.apache.xml.internal.security.resource.config");
                        if (cfile == null) {
                            return null;
                        }
                        return getClass().getResourceAsStream(cfile);
                    }
                });
        if (is == null) {
            dynamicInit();
        } else {
            fileInit(is);
        }

        alreadyInitialized = true;
    }


    private static void dynamicInit() {
        I18n.init("en", "US");

        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Registering default algorithms");
        }
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>(){
                @Override public Void run() throws XMLSecurityException {
                    ElementProxy.registerDefaultPrefixes();

                    Transform.registerDefaultAlgorithms();

                    SignatureAlgorithm.registerDefaultAlgorithms();

                    JCEMapper.registerDefaultAlgorithms();

                    Canonicalizer.registerDefaultAlgorithms();

                    ResourceResolver.registerDefaultResolvers();

                    KeyResolver.registerDefaultResolvers();

                    return null;
                }
           });
        } catch (PrivilegedActionException ex) {
            XMLSecurityException xse = (XMLSecurityException)ex.getException();
            log.log(java.util.logging.Level.SEVERE, xse.getMessage(), xse);
            xse.printStackTrace();
        }
    }


    private static void fileInit(InputStream is) {
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);

            dbf.setNamespaceAware(true);
            dbf.setValidating(false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            Node config = doc.getFirstChild();
            for (; config != null; config = config.getNextSibling()) {
                if ("Configuration".equals(config.getLocalName())) {
                    break;
                }
            }
            if (config == null) {
                log.log(java.util.logging.Level.SEVERE, "Error in reading configuration file - Configuration element not found");
                return;
            }
            for (Node el = config.getFirstChild(); el != null; el = el.getNextSibling()) {
                if (Node.ELEMENT_NODE != el.getNodeType()) {
                    continue;
                }
                String tag = el.getLocalName();
                if (tag.equals("ResourceBundles")) {
                    Element resource = (Element)el;

                    Attr langAttr = resource.getAttributeNode("defaultLanguageCode");
                    Attr countryAttr = resource.getAttributeNode("defaultCountryCode");
                    String languageCode =
                        (langAttr == null) ? null : langAttr.getNodeValue();
                    String countryCode =
                        (countryAttr == null) ? null : countryAttr.getNodeValue();
                    I18n.init(languageCode, countryCode);
                }

                if (tag.equals("CanonicalizationMethods")) {
                    Element[] list =
                        XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "CanonicalizationMethod");

                    for (int i = 0; i < list.length; i++) {
                        String uri = list[i].getAttributeNS(null, "URI");
                        String javaClass =
                            list[i].getAttributeNS(null, "JAVACLASS");
                        try {
                            Canonicalizer.register(uri, javaClass);
                            if (log.isLoggable(java.util.logging.Level.FINE)) {
                                log.log(java.util.logging.Level.FINE, "Canonicalizer.register(" + uri + ", " + javaClass + ")");
                            }
                        } catch (ClassNotFoundException e) {
                            Object exArgs[] = { uri, javaClass };
                            log.log(java.util.logging.Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", exArgs));
                        }
                    }
                }

                if (tag.equals("TransformAlgorithms")) {
                    Element[] tranElem =
                        XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "TransformAlgorithm");

                    for (int i = 0; i < tranElem.length; i++) {
                        String uri = tranElem[i].getAttributeNS(null, "URI");
                        String javaClass =
                            tranElem[i].getAttributeNS(null, "JAVACLASS");
                        try {
                            Transform.register(uri, javaClass);
                            if (log.isLoggable(java.util.logging.Level.FINE)) {
                                log.log(java.util.logging.Level.FINE, "Transform.register(" + uri + ", " + javaClass + ")");
                            }
                        } catch (ClassNotFoundException e) {
                            Object exArgs[] = { uri, javaClass };

                            log.log(java.util.logging.Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", exArgs));
                        } catch (NoClassDefFoundError ex) {
                            log.log(java.util.logging.Level.WARNING, "Not able to found dependencies for algorithm, I'll keep working.");
                        }
                    }
                }

                if ("JCEAlgorithmMappings".equals(tag)) {
                    Node algorithmsNode = ((Element)el).getElementsByTagName("Algorithms").item(0);
                    if (algorithmsNode != null) {
                        Element[] algorithms =
                            XMLUtils.selectNodes(algorithmsNode.getFirstChild(), CONF_NS, "Algorithm");
                        for (int i = 0; i < algorithms.length; i++) {
                            Element element = algorithms[i];
                            String id = element.getAttribute("URI");
                            JCEMapper.register(id, new JCEMapper.Algorithm(element));
                        }
                    }
                }

                if (tag.equals("SignatureAlgorithms")) {
                    Element[] sigElems =
                        XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "SignatureAlgorithm");

                    for (int i = 0; i < sigElems.length; i++) {
                        String uri = sigElems[i].getAttributeNS(null, "URI");
                        String javaClass =
                            sigElems[i].getAttributeNS(null, "JAVACLASS");



                        try {
                            SignatureAlgorithm.register(uri, javaClass);
                            if (log.isLoggable(java.util.logging.Level.FINE)) {
                                log.log(java.util.logging.Level.FINE, "SignatureAlgorithm.register(" + uri + ", "
                                          + javaClass + ")");
                            }
                        } catch (ClassNotFoundException e) {
                            Object exArgs[] = { uri, javaClass };

                            log.log(java.util.logging.Level.SEVERE, I18n.translate("algorithm.classDoesNotExist", exArgs));
                        }
                    }
                }

                if (tag.equals("ResourceResolvers")) {
                    Element[]resolverElem =
                        XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "Resolver");

                    for (int i = 0; i < resolverElem.length; i++) {
                        String javaClass =
                            resolverElem[i].getAttributeNS(null, "JAVACLASS");
                        String description =
                            resolverElem[i].getAttributeNS(null, "DESCRIPTION");

                        if ((description != null) && (description.length() > 0)) {
                            if (log.isLoggable(java.util.logging.Level.FINE)) {
                                log.log(java.util.logging.Level.FINE, "Register Resolver: " + javaClass + ": "
                                          + description);
                            }
                        } else {
                            if (log.isLoggable(java.util.logging.Level.FINE)) {
                                log.log(java.util.logging.Level.FINE, "Register Resolver: " + javaClass
                                          + ": For unknown purposes");
                            }
                        }
                        try {
                            ResourceResolver.register(javaClass);
                        } catch (Throwable e) {
                            log.log(java.util.logging.Level.WARNING,
                                 "Cannot register:" + javaClass
                                 + " perhaps some needed jars are not installed",
                                 e
                             );
                        }
                    }
                }

                if (tag.equals("KeyResolver")){
                    Element[] resolverElem =
                        XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "Resolver");
                    List<String> classNames = new ArrayList<String>(resolverElem.length);
                    for (int i = 0; i < resolverElem.length; i++) {
                        String javaClass =
                            resolverElem[i].getAttributeNS(null, "JAVACLASS");
                        String description =
                            resolverElem[i].getAttributeNS(null, "DESCRIPTION");

                        if ((description != null) && (description.length() > 0)) {
                            if (log.isLoggable(java.util.logging.Level.FINE)) {
                                log.log(java.util.logging.Level.FINE, "Register Resolver: " + javaClass + ": "
                                          + description);
                            }
                        } else {
                            if (log.isLoggable(java.util.logging.Level.FINE)) {
                                log.log(java.util.logging.Level.FINE, "Register Resolver: " + javaClass
                                          + ": For unknown purposes");
                            }
                        }
                        classNames.add(javaClass);
                    }
                    KeyResolver.registerClassNames(classNames);
                }


                if (tag.equals("PrefixMappings")){
                    if (log.isLoggable(java.util.logging.Level.FINE)) {
                        log.log(java.util.logging.Level.FINE, "Now I try to bind prefixes:");
                    }

                    Element[] nl =
                        XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "PrefixMapping");

                    for (int i = 0; i < nl.length; i++) {
                        String namespace = nl[i].getAttributeNS(null, "namespace");
                        String prefix = nl[i].getAttributeNS(null, "prefix");
                        if (log.isLoggable(java.util.logging.Level.FINE)) {
                            log.log(java.util.logging.Level.FINE, "Now I try to bind " + prefix + " to " + namespace);
                        }
                        ElementProxy.setDefaultPrefix(namespace, prefix);
                    }
                }
            }
        } catch (Exception e) {
            log.log(java.util.logging.Level.SEVERE, "Bad: ", e);
            e.printStackTrace();
        }
    }

}

