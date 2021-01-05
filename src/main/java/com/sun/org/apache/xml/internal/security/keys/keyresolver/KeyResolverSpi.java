

package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.crypto.SecretKey;

import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import org.w3c.dom.Element;


public abstract class KeyResolverSpi {


    protected java.util.Map<String, String> properties = null;

    protected boolean globalResolver = false;

    protected boolean secureValidation;


    public void setSecureValidation(boolean secureValidation) {
        this.secureValidation = secureValidation;
    }


    public boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        throw new UnsupportedOperationException();
    }


    public PublicKey engineResolvePublicKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        throw new UnsupportedOperationException();
    };


    public PublicKey engineLookupAndResolvePublicKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        KeyResolverSpi tmp = cloneIfNeeded();
        if (!tmp.engineCanResolve(element, baseURI, storage)) {
            return null;
        }
        return tmp.engineResolvePublicKey(element, baseURI, storage);
    }

    private KeyResolverSpi cloneIfNeeded() throws KeyResolverException {
        KeyResolverSpi tmp = this;
        if (globalResolver) {
            try {
                tmp = getClass().newInstance();
            } catch (InstantiationException e) {
                throw new KeyResolverException("", e);
            } catch (IllegalAccessException e) {
                throw new KeyResolverException("", e);
            }
        }
        return tmp;
    }


    public X509Certificate engineResolveX509Certificate(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException{
        throw new UnsupportedOperationException();
    };


    public X509Certificate engineLookupResolveX509Certificate(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        KeyResolverSpi tmp = cloneIfNeeded();
        if (!tmp.engineCanResolve(element, baseURI, storage)) {
            return null;
        }
        return tmp.engineResolveX509Certificate(element, baseURI, storage);

    }

    public SecretKey engineResolveSecretKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException{
        throw new UnsupportedOperationException();
    };


    public SecretKey engineLookupAndResolveSecretKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        KeyResolverSpi tmp = cloneIfNeeded();
        if (!tmp.engineCanResolve(element, baseURI, storage)) {
            return null;
        }
        return tmp.engineResolveSecretKey(element, baseURI, storage);
    }


    public PrivateKey engineLookupAndResolvePrivateKey(
        Element element, String baseURI, StorageResolver storage
    ) throws KeyResolverException {
        return null;
    }


    public void engineSetProperty(String key, String value) {
        if (properties == null) {
            properties = new HashMap<String, String>();
        }
        properties.put(key, value);
    }


    public String engineGetProperty(String key) {
        if (properties == null) {
            return null;
        }

        return properties.get(key);
    }


    public boolean understandsProperty(String propertyToTest) {
        if (properties == null) {
            return false;
        }

        return properties.get(propertyToTest) != null;
    }

    public void setGlobalResolver(boolean globalResolver) {
        this.globalResolver = globalResolver;
    }

}
