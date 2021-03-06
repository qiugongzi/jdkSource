

package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.crypto.SecretKey;

import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.DEREncodedKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.DSAKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.KeyInfoReferenceResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.RetrievalMethodResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509CertificateResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509DigestResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509IssuerSerialResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509SKIResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509SubjectNameResolver;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class KeyResolver {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(KeyResolver.class.getName());


    private static List<KeyResolver> resolverVector = new CopyOnWriteArrayList<KeyResolver>();


    private final KeyResolverSpi resolverSpi;


    private KeyResolver(KeyResolverSpi keyResolverSpi) {
        resolverSpi = keyResolverSpi;
    }


    public static int length() {
        return resolverVector.size();
    }


    public static final X509Certificate getX509Certificate(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        for (KeyResolver resolver : resolverVector) {
            if (resolver == null) {
                Object exArgs[] = {
                                   (((element != null)
                                       && (element.getNodeType() == Node.ELEMENT_NODE))
                                       ? element.getTagName() : "null")
                };

                throw new KeyResolverException("utils.resolver.noClass", exArgs);
            }
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "check resolvability by class " + resolver.getClass());
            }

            X509Certificate cert = resolver.resolveX509Certificate(element, baseURI, storage);
            if (cert != null) {
                return cert;
            }
        }

        Object exArgs[] = {
                           (((element != null) && (element.getNodeType() == Node.ELEMENT_NODE))
                           ? element.getTagName() : "null")
                          };

        throw new KeyResolverException("utils.resolver.noClass", exArgs);
    }


    public static final PublicKey getPublicKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        for (KeyResolver resolver : resolverVector) {
            if (resolver == null) {
                Object exArgs[] = {
                                   (((element != null)
                                       && (element.getNodeType() == Node.ELEMENT_NODE))
                                       ? element.getTagName() : "null")
                };

                throw new KeyResolverException("utils.resolver.noClass", exArgs);
            }
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "check resolvability by class " + resolver.getClass());
            }

            PublicKey cert = resolver.resolvePublicKey(element, baseURI, storage);
            if (cert != null) {
                return cert;
            }
        }

        Object exArgs[] = {
                           (((element != null) && (element.getNodeType() == Node.ELEMENT_NODE))
                           ? element.getTagName() : "null")
                          };

        throw new KeyResolverException("utils.resolver.noClass", exArgs);
    }


    public static void register(String className, boolean globalResolver)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        JavaUtils.checkRegisterPermission();
        KeyResolverSpi keyResolverSpi =
            (KeyResolverSpi) Class.forName(className).newInstance();
        keyResolverSpi.setGlobalResolver(globalResolver);
        register(keyResolverSpi, false);
    }


    public static void registerAtStart(String className, boolean globalResolver) {
        JavaUtils.checkRegisterPermission();
        KeyResolverSpi keyResolverSpi = null;
        Exception ex = null;
        try {
            keyResolverSpi = (KeyResolverSpi) Class.forName(className).newInstance();
        } catch (ClassNotFoundException e) {
            ex = e;
        } catch (IllegalAccessException e) {
            ex = e;
        } catch (InstantiationException e) {
            ex = e;
        }

        if (ex != null) {
            throw (IllegalArgumentException) new
            IllegalArgumentException("Invalid KeyResolver class name").initCause(ex);
        }
        keyResolverSpi.setGlobalResolver(globalResolver);
        register(keyResolverSpi, true);
    }


    public static void register(
        KeyResolverSpi keyResolverSpi,
        boolean start
    ) {
        JavaUtils.checkRegisterPermission();
        KeyResolver resolver = new KeyResolver(keyResolverSpi);
        if (start) {
            resolverVector.add(0, resolver);
        } else {
            resolverVector.add(resolver);
        }
    }


    public static void registerClassNames(List<String> classNames)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        JavaUtils.checkRegisterPermission();
        List<KeyResolver> keyResolverList = new ArrayList<KeyResolver>(classNames.size());
        for (String className : classNames) {
            KeyResolverSpi keyResolverSpi =
                (KeyResolverSpi) Class.forName(className).newInstance();
            keyResolverSpi.setGlobalResolver(false);
            keyResolverList.add(new KeyResolver(keyResolverSpi));
        }
        resolverVector.addAll(keyResolverList);
    }


    public static void registerDefaultResolvers() {

        List<KeyResolver> keyResolverList = new ArrayList<KeyResolver>();
        keyResolverList.add(new KeyResolver(new RSAKeyValueResolver()));
        keyResolverList.add(new KeyResolver(new DSAKeyValueResolver()));
        keyResolverList.add(new KeyResolver(new X509CertificateResolver()));
        keyResolverList.add(new KeyResolver(new X509SKIResolver()));
        keyResolverList.add(new KeyResolver(new RetrievalMethodResolver()));
        keyResolverList.add(new KeyResolver(new X509SubjectNameResolver()));
        keyResolverList.add(new KeyResolver(new X509IssuerSerialResolver()));
        keyResolverList.add(new KeyResolver(new DEREncodedKeyValueResolver()));
        keyResolverList.add(new KeyResolver(new KeyInfoReferenceResolver()));
        keyResolverList.add(new KeyResolver(new X509DigestResolver()));

        resolverVector.addAll(keyResolverList);
    }


    public PublicKey resolvePublicKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        return resolverSpi.engineLookupAndResolvePublicKey(element, baseURI, storage);
    }


    public X509Certificate resolveX509Certificate(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        return resolverSpi.engineLookupResolveX509Certificate(element, baseURI, storage);
    }


    public SecretKey resolveSecretKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        return resolverSpi.engineLookupAndResolveSecretKey(element, baseURI, storage);
    }


    public void setProperty(String key, String value) {
        resolverSpi.engineSetProperty(key, value);
    }


    public String getProperty(String key) {
        return resolverSpi.engineGetProperty(key);
    }



    public boolean understandsProperty(String propertyToTest) {
        return resolverSpi.understandsProperty(propertyToTest);
    }



    public String resolverClassName() {
        return resolverSpi.getClass().getName();
    }


    static class ResolverIterator implements Iterator<KeyResolverSpi> {
        List<KeyResolver> res;
        Iterator<KeyResolver> it;

        public ResolverIterator(List<KeyResolver> list) {
            res = list;
            it = res.iterator();
        }

        public boolean hasNext() {
            return it.hasNext();
        }

        public KeyResolverSpi next() {
            KeyResolver resolver = it.next();
            if (resolver == null) {
                throw new RuntimeException("utils.resolver.noClass");
            }

            return resolver.resolverSpi;
        }

        public void remove() {
            throw new UnsupportedOperationException("Can't remove resolvers using the iterator");
        }
    };

    public static Iterator<KeyResolverSpi> iterator() {
        return new ResolverIterator(resolverVector);
    }
}
