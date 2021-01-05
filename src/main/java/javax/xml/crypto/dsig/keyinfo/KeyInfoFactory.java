

package javax.xml.crypto.dsig.keyinfo;

import java.math.BigInteger;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509CRL;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NoSuchMechanismException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.*;

import sun.security.jca.*;
import sun.security.jca.GetInstance.Instance;


public abstract class KeyInfoFactory {

    private String mechanismType;
    private Provider provider;


    protected KeyInfoFactory() {}


    public static KeyInfoFactory getInstance(String mechanismType) {
        if (mechanismType == null) {
            throw new NullPointerException("mechanismType cannot be null");
        }
        Instance instance;
        try {
            instance = GetInstance.getInstance
                ("KeyInfoFactory", null, mechanismType);
        } catch (NoSuchAlgorithmException nsae) {
            throw new NoSuchMechanismException(nsae);
        }
        KeyInfoFactory factory = (KeyInfoFactory) instance.impl;
        factory.mechanismType = mechanismType;
        factory.provider = instance.provider;
        return factory;
    }


    public static KeyInfoFactory getInstance(String mechanismType,
        Provider provider) {
        if (mechanismType == null) {
            throw new NullPointerException("mechanismType cannot be null");
        } else if (provider == null) {
            throw new NullPointerException("provider cannot be null");
        }

        Instance instance;
        try {
            instance = GetInstance.getInstance
                ("KeyInfoFactory", null, mechanismType, provider);
        } catch (NoSuchAlgorithmException nsae) {
            throw new NoSuchMechanismException(nsae);
        }
        KeyInfoFactory factory = (KeyInfoFactory) instance.impl;
        factory.mechanismType = mechanismType;
        factory.provider = instance.provider;
        return factory;
    }


    public static KeyInfoFactory getInstance(String mechanismType,
        String provider) throws NoSuchProviderException {
        if (mechanismType == null) {
            throw new NullPointerException("mechanismType cannot be null");
        } else if (provider == null) {
            throw new NullPointerException("provider cannot be null");
        } else if (provider.length() == 0) {
            throw new NoSuchProviderException();
        }

        Instance instance;
        try {
            instance = GetInstance.getInstance
                ("KeyInfoFactory", null, mechanismType, provider);
        } catch (NoSuchAlgorithmException nsae) {
            throw new NoSuchMechanismException(nsae);
        }
        KeyInfoFactory factory = (KeyInfoFactory) instance.impl;
        factory.mechanismType = mechanismType;
        factory.provider = instance.provider;
        return factory;
    }


    public static KeyInfoFactory getInstance() {
        return getInstance("DOM");
    }


    public final String getMechanismType() {
        return mechanismType;
    }


    public final Provider getProvider() {
        return provider;
    }


    @SuppressWarnings("rawtypes")
    public abstract KeyInfo newKeyInfo(List content);


    @SuppressWarnings("rawtypes")
    public abstract KeyInfo newKeyInfo(List content, String id);


    public abstract KeyName newKeyName(String name);


    public abstract KeyValue newKeyValue(PublicKey key) throws KeyException;


    public abstract PGPData newPGPData(byte[] keyId);


    @SuppressWarnings("rawtypes")
    public abstract PGPData newPGPData(byte[] keyId, byte[] keyPacket,
        List other);


    @SuppressWarnings("rawtypes")
    public abstract PGPData newPGPData(byte[] keyPacket, List other);


    public abstract RetrievalMethod newRetrievalMethod(String uri);


    @SuppressWarnings("rawtypes")
    public abstract RetrievalMethod newRetrievalMethod(String uri, String type,
        List transforms);


    @SuppressWarnings("rawtypes")
    public abstract X509Data newX509Data(List content);


    public abstract X509IssuerSerial newX509IssuerSerial
        (String issuerName, BigInteger serialNumber);


    public abstract boolean isFeatureSupported(String feature);


    public abstract URIDereferencer getURIDereferencer();


    public abstract KeyInfo unmarshalKeyInfo(XMLStructure xmlStructure)
        throws MarshalException;
}
