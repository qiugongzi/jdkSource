

package com.sun.org.apache.xml.internal.security.keys.storage;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver;
import com.sun.org.apache.xml.internal.security.keys.storage.implementations.SingleCertificateResolver;


public class StorageResolver {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(StorageResolver.class.getName());


    private List<StorageResolverSpi> storageResolvers = null;


    public StorageResolver() {}


    public StorageResolver(StorageResolverSpi resolver) {
        this.add(resolver);
    }


    public void add(StorageResolverSpi resolver) {
        if (storageResolvers == null) {
            storageResolvers = new ArrayList<StorageResolverSpi>();
        }
        this.storageResolvers.add(resolver);
    }


    public StorageResolver(KeyStore keyStore) {
        this.add(keyStore);
    }


    public void add(KeyStore keyStore) {
        try {
            this.add(new KeyStoreResolver(keyStore));
        } catch (StorageResolverException ex) {
            log.log(java.util.logging.Level.SEVERE, "Could not add KeyStore because of: ", ex);
        }
    }


    public StorageResolver(X509Certificate x509certificate) {
        this.add(x509certificate);
    }


    public void add(X509Certificate x509certificate) {
        this.add(new SingleCertificateResolver(x509certificate));
    }


    public Iterator<Certificate> getIterator() {
        return new StorageResolverIterator(this.storageResolvers.iterator());
    }


    static class StorageResolverIterator implements Iterator<Certificate> {


        Iterator<StorageResolverSpi> resolvers = null;


        Iterator<Certificate> currentResolver = null;


        public StorageResolverIterator(Iterator<StorageResolverSpi> resolvers) {
            this.resolvers = resolvers;
            currentResolver = findNextResolver();
        }


        public boolean hasNext() {
            if (currentResolver == null) {
                return false;
            }

            if (currentResolver.hasNext()) {
                return true;
            }

            currentResolver = findNextResolver();
            return (currentResolver != null);
        }


        public Certificate next() {
            if (hasNext()) {
                return currentResolver.next();
            }

            throw new NoSuchElementException();
        }


        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }

        private Iterator<Certificate> findNextResolver() {
            while (resolvers.hasNext()) {
                StorageResolverSpi resolverSpi = resolvers.next();
                Iterator<Certificate> iter = resolverSpi.getIterator();
                if (iter.hasNext()) {
                    return iter;
                }
            }

            return null;
        }
    }
}
