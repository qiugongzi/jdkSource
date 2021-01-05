

package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAKey;
import java.security.spec.AlgorithmParameterSpec;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignature;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;

public class SignatureDSA extends SignatureAlgorithmSpi {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(SignatureDSA.class.getName());


    private java.security.Signature signatureAlgorithm = null;


    private int size;


    protected String engineGetURI() {
        return XMLSignature.ALGO_ID_SIGNATURE_DSA;
    }


    public SignatureDSA() throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(engineGetURI());
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Created SignatureDSA using " + algorithmID);
        }

        String provider = JCEMapper.getProviderId();
        try {
            if (provider == null) {
                this.signatureAlgorithm = Signature.getInstance(algorithmID);
            } else {
                this.signatureAlgorithm =
                    Signature.getInstance(algorithmID, provider);
            }
        } catch (java.security.NoSuchAlgorithmException ex) {
            Object[] exArgs = { algorithmID, ex.getLocalizedMessage() };
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
        } catch (java.security.NoSuchProviderException ex) {
            Object[] exArgs = { algorithmID, ex.getLocalizedMessage() };
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
        }
    }


    protected void engineSetParameter(AlgorithmParameterSpec params)
        throws XMLSignatureException {
        try {
            this.signatureAlgorithm.setParameter(params);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected boolean engineVerify(byte[] signature)
        throws XMLSignatureException {
        try {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "Called DSA.verify() on " + Base64.encode(signature));
            }

            byte[] jcebytes = JavaUtils.convertDsaXMLDSIGtoASN1(signature,
                                                                size/8);

            return this.signatureAlgorithm.verify(jcebytes);
        } catch (SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
        } catch (IOException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected void engineInitVerify(Key publicKey) throws XMLSignatureException {
        if (!(publicKey instanceof PublicKey)) {
            String supplied = publicKey.getClass().getName();
            String needed = PublicKey.class.getName();
            Object exArgs[] = { supplied, needed };

            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }

        try {
            this.signatureAlgorithm.initVerify((PublicKey) publicKey);
        } catch (InvalidKeyException ex) {
            Signature sig = this.signatureAlgorithm;
            try {
                this.signatureAlgorithm = Signature.getInstance(signatureAlgorithm.getAlgorithm());
            } catch (Exception e) {
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, "Exception when reinstantiating Signature:" + e);
                }
                this.signatureAlgorithm = sig;
            }
            throw new XMLSignatureException("empty", ex);
        }
        size = ((DSAKey)publicKey).getParams().getQ().bitLength();
    }


    protected byte[] engineSign() throws XMLSignatureException {
        try {
            byte jcebytes[] = this.signatureAlgorithm.sign();

            return JavaUtils.convertDsaASN1toXMLDSIG(jcebytes, size/8);
        } catch (IOException ex) {
            throw new XMLSignatureException("empty", ex);
        } catch (SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected void engineInitSign(Key privateKey, SecureRandom secureRandom)
        throws XMLSignatureException {
        if (!(privateKey instanceof PrivateKey)) {
            String supplied = privateKey.getClass().getName();
            String needed = PrivateKey.class.getName();
            Object exArgs[] = { supplied, needed };

            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }

        try {
            this.signatureAlgorithm.initSign((PrivateKey) privateKey, secureRandom);
        } catch (InvalidKeyException ex) {
            throw new XMLSignatureException("empty", ex);
        }
        size = ((DSAKey)privateKey).getParams().getQ().bitLength();
    }


    protected void engineInitSign(Key privateKey) throws XMLSignatureException {
        if (!(privateKey instanceof PrivateKey)) {
            String supplied = privateKey.getClass().getName();
            String needed = PrivateKey.class.getName();
            Object exArgs[] = { supplied, needed };

            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }

        try {
            this.signatureAlgorithm.initSign((PrivateKey) privateKey);
        } catch (InvalidKeyException ex) {
            throw new XMLSignatureException("empty", ex);
        }
        size = ((DSAKey)privateKey).getParams().getQ().bitLength();
    }


    protected void engineUpdate(byte[] input) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.update(input);
        } catch (SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected void engineUpdate(byte input) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.update(input);
        } catch (SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected void engineUpdate(byte buf[], int offset, int len) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.update(buf, offset, len);
        } catch (SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected String engineGetJCEAlgorithmString() {
        return this.signatureAlgorithm.getAlgorithm();
    }


    protected String engineGetJCEProviderName() {
        return this.signatureAlgorithm.getProvider().getName();
    }


    protected void engineSetHMACOutputLength(int HMACOutputLength) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.HMACOutputLengthOnlyForHMAC");
    }


    protected void engineInitSign(
        Key signingKey, AlgorithmParameterSpec algorithmParameterSpec
    ) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnDSA");
    }

    public static class SHA256 extends SignatureDSA {

        public SHA256() throws XMLSignatureException {
            super();
        }

        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_DSA_SHA256;
        }
    }
}
