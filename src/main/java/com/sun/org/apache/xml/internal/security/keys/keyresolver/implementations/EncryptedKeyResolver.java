

package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import com.sun.org.apache.xml.internal.security.encryption.EncryptedKey;
import com.sun.org.apache.xml.internal.security.encryption.XMLCipher;
import com.sun.org.apache.xml.internal.security.encryption.XMLEncryptionException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.EncryptionConstants;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Element;


public class EncryptedKeyResolver extends KeyResolverSpi {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(EncryptedKeyResolver.class.getName());

    private Key kek;
    private String algorithm;
    private List<KeyResolverSpi> internalKeyResolvers;


    public EncryptedKeyResolver(String algorithm) {
        kek = null;
        this.algorithm = algorithm;
    }


    public EncryptedKeyResolver(String algorithm, Key kek) {
        this.algorithm = algorithm;
        this.kek = kek;
    }


    public void registerInternalKeyResolver(KeyResolverSpi realKeyResolver) {
        if (internalKeyResolvers == null) {
            internalKeyResolvers = new ArrayList<KeyResolverSpi>();
        }
        internalKeyResolvers.add(realKeyResolver);
    }


    public PublicKey engineLookupAndResolvePublicKey(
        Element element, String BaseURI, StorageResolver storage
    ) {
        return null;
    }


    public X509Certificate engineLookupResolveX509Certificate(
        Element element, String BaseURI, StorageResolver storage
    ) {
        return null;
    }


    public javax.crypto.SecretKey engineLookupAndResolveSecretKey(
        Element element, String BaseURI, StorageResolver storage
    ) {
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "EncryptedKeyResolver - Can I resolve " + element.getTagName());
        }

        if (element == null) {
            return null;
        }

        SecretKey key = null;
        boolean isEncryptedKey =
            XMLUtils.elementIsInEncryptionSpace(element, EncryptionConstants._TAG_ENCRYPTEDKEY);
        if (isEncryptedKey) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "Passed an Encrypted Key");
            }
            try {
                XMLCipher cipher = XMLCipher.getInstance();
                cipher.init(XMLCipher.UNWRAP_MODE, kek);
                if (internalKeyResolvers != null) {
                    int size = internalKeyResolvers.size();
                    for (int i = 0; i < size; i++) {
                        cipher.registerInternalKeyResolver(internalKeyResolvers.get(i));
                    }
                }
                EncryptedKey ek = cipher.loadEncryptedKey(element);
                key = (SecretKey) cipher.decryptKey(ek, algorithm);
            } catch (XMLEncryptionException e) {
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, e.getMessage(), e);
                }
            }
        }

        return key;
    }
}
