

package javax.xml.crypto.dsig;

import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NoSuchMechanismException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.*;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.dom.DOMSignContext;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.List;

import sun.security.jca.*;
import sun.security.jca.GetInstance.Instance;


public abstract class XMLSignatureFactory {

    private String mechanismType;
    private Provider provider;


    protected XMLSignatureFactory() {}


    public static XMLSignatureFactory getInstance(String mechanismType) {
        if (mechanismType == null) {
            throw new NullPointerException("mechanismType cannot be null");
        }
        Instance instance;
        try {
            instance = GetInstance.getInstance
                ("XMLSignatureFactory", null, mechanismType);
        } catch (NoSuchAlgorithmException nsae) {
            throw new NoSuchMechanismException(nsae);
        }
        XMLSignatureFactory factory = (XMLSignatureFactory) instance.impl;
        factory.mechanismType = mechanismType;
        factory.provider = instance.provider;
        return factory;
    }


    public static XMLSignatureFactory getInstance(String mechanismType,
        Provider provider) {
        if (mechanismType == null) {
            throw new NullPointerException("mechanismType cannot be null");
        } else if (provider == null) {
            throw new NullPointerException("provider cannot be null");
        }

        Instance instance;
        try {
            instance = GetInstance.getInstance
                ("XMLSignatureFactory", null, mechanismType, provider);
        } catch (NoSuchAlgorithmException nsae) {
            throw new NoSuchMechanismException(nsae);
        }
        XMLSignatureFactory factory = (XMLSignatureFactory) instance.impl;
        factory.mechanismType = mechanismType;
        factory.provider = instance.provider;
        return factory;
    }


    public static XMLSignatureFactory getInstance(String mechanismType,
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
                ("XMLSignatureFactory", null, mechanismType, provider);
        } catch (NoSuchAlgorithmException nsae) {
            throw new NoSuchMechanismException(nsae);
        }
        XMLSignatureFactory factory = (XMLSignatureFactory) instance.impl;
        factory.mechanismType = mechanismType;
        factory.provider = instance.provider;
        return factory;
    }


    public static XMLSignatureFactory getInstance() {
        return getInstance("DOM");
    }


    public final String getMechanismType() {
        return mechanismType;
    }


    public final Provider getProvider() {
        return provider;
    }


    public abstract XMLSignature newXMLSignature(SignedInfo si, KeyInfo ki);


    @SuppressWarnings("rawtypes")
    public abstract XMLSignature newXMLSignature(SignedInfo si, KeyInfo ki,
        List objects, String id, String signatureValueId);


    public abstract Reference newReference(String uri, DigestMethod dm);


    @SuppressWarnings("rawtypes")
    public abstract Reference newReference(String uri, DigestMethod dm,
        List transforms, String type, String id);


    @SuppressWarnings("rawtypes")
    public abstract Reference newReference(String uri, DigestMethod dm,
        List transforms, String type, String id, byte[] digestValue);


    @SuppressWarnings("rawtypes")
    public abstract Reference newReference(String uri, DigestMethod dm,
        List appliedTransforms, Data result, List transforms, String type,
        String id);


    @SuppressWarnings("rawtypes")
    public abstract SignedInfo newSignedInfo(CanonicalizationMethod cm,
        SignatureMethod sm, List references);


    @SuppressWarnings("rawtypes")
    public abstract SignedInfo newSignedInfo(CanonicalizationMethod cm,
        SignatureMethod sm, List references, String id);

    @SuppressWarnings("rawtypes")
    public abstract XMLObject newXMLObject(List content, String id,
        String mimeType, String encoding);


    @SuppressWarnings("rawtypes")
    public abstract Manifest newManifest(List references);


    @SuppressWarnings("rawtypes")
    public abstract Manifest newManifest(List references, String id);


    @SuppressWarnings("rawtypes")
    public abstract SignatureProperty newSignatureProperty
        (List content, String target, String id);


    @SuppressWarnings("rawtypes")
    public abstract SignatureProperties newSignatureProperties
        (List properties, String id);

    public abstract DigestMethod newDigestMethod(String algorithm,
        DigestMethodParameterSpec params) throws NoSuchAlgorithmException,
        InvalidAlgorithmParameterException;


    public abstract SignatureMethod newSignatureMethod(String algorithm,
        SignatureMethodParameterSpec params) throws NoSuchAlgorithmException,
        InvalidAlgorithmParameterException;


    public abstract Transform newTransform(String algorithm,
        TransformParameterSpec params) throws NoSuchAlgorithmException,
        InvalidAlgorithmParameterException;


    public abstract Transform newTransform(String algorithm,
        XMLStructure params) throws NoSuchAlgorithmException,
        InvalidAlgorithmParameterException;


    public abstract CanonicalizationMethod newCanonicalizationMethod(
        String algorithm, C14NMethodParameterSpec params)
        throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;


    public abstract CanonicalizationMethod newCanonicalizationMethod(
        String algorithm, XMLStructure params)
        throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;


    public final KeyInfoFactory getKeyInfoFactory() {
        return KeyInfoFactory.getInstance(getMechanismType(), getProvider());
    }


    public abstract XMLSignature unmarshalXMLSignature
        (XMLValidateContext context) throws MarshalException;


    public abstract XMLSignature unmarshalXMLSignature
        (XMLStructure xmlStructure) throws MarshalException;


    public abstract boolean isFeatureSupported(String feature);


    public abstract URIDereferencer getURIDereferencer();
}
