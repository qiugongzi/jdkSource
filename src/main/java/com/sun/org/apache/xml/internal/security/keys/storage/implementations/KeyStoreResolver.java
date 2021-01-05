

package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverException;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;


public class KeyStoreResolver extends StorageResolverSpi {


    private KeyStore keyStore = null;


    public KeyStoreResolver(KeyStore keyStore) throws StorageResolverException {
        this.keyStore = keyStore;
        try {
            keyStore.aliases();
        } catch (KeyStoreException ex) {
            throw new StorageResolverException("generic.EmptyMessage", ex);
        }
    }


    public Iterator<Certificate> getIterator() {
        return new KeyStoreIterator(this.keyStore);
    }


    static class KeyStoreIterator implements Iterator<Certificate> {


        KeyStore keyStore = null;


        Enumeration<String> aliases = null;


        Certificate nextCert = null;


        public KeyStoreIterator(KeyStore keyStore) {
            try {
                this.keyStore = keyStore;
                this.aliases = this.keyStore.aliases();
            } catch (KeyStoreException ex) {
                this.aliases = new Enumeration<String>() {
                    public boolean hasMoreElements() {
                        return false;
                    }
                    public String nextElement() {
                        return null;
                    }
                };
            }
        }


        public boolean hasNext() {
            if (nextCert == null) {
                nextCert = findNextCert();
            }

            return (nextCert != null);
        }


        public Certificate next() {
            if (nextCert == null) {
                nextCert = findNextCert();

                if (nextCert == null) {
                    throw new NoSuchElementException();
                }
            }

            Certificate ret = nextCert;
            nextCert = null;
            return ret;
        }


        public void remove() {
            throw new UnsupportedOperationException("Can't remove keys from KeyStore");
        }

        private Certificate findNextCert() {
            while (this.aliases.hasMoreElements()) {
                String alias = this.aliases.nextElement();
                try {
                    Certificate cert = this.keyStore.getCertificate(alias);
                    if (cert != null) {
                        return cert;
                    }
                } catch (KeyStoreException ex) {
                    return null;
                }
            }

            return null;
        }

    }

}
