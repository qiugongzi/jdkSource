

package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignature;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;

public abstract class SignatureBaseRSA extends SignatureAlgorithmSpi {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(SignatureBaseRSA.class.getName());


    public abstract String engineGetURI();


    private java.security.Signature signatureAlgorithm = null;


    public SignatureBaseRSA() throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(this.engineGetURI());

        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Created SignatureRSA using " + algorithmID);
        }
        String provider = JCEMapper.getProviderId();
        try {
            if (provider == null) {
                this.signatureAlgorithm = Signature.getInstance(algorithmID);
            } else {
                this.signatureAlgorithm = Signature.getInstance(algorithmID,provider);
            }
        } catch (java.security.NoSuchAlgorithmException ex) {
            Object[] exArgs = { algorithmID, ex.getLocalizedMessage() };

            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
        } catch (NoSuchProviderException ex) {
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


    protected boolean engineVerify(byte[] signature) throws XMLSignatureException {
        try {
            return this.signatureAlgorithm.verify(signature);
        } catch (SignatureException ex) {
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
    }


    protected byte[] engineSign() throws XMLSignatureException {
        try {
            return this.signatureAlgorithm.sign();
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


    protected void engineSetHMACOutputLength(int HMACOutputLength)
        throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.HMACOutputLengthOnlyForHMAC");
    }


    protected void engineInitSign(
        Key signingKey, AlgorithmParameterSpec algorithmParameterSpec
    ) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnRSA");
    }


    public static class SignatureRSASHA1 extends SignatureBaseRSA {


        public SignatureRSASHA1() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1;
        }
    }


    public static class SignatureRSASHA256 extends SignatureBaseRSA {


        public SignatureRSASHA256() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256;
        }
    }


    public static class SignatureRSASHA384 extends SignatureBaseRSA {


        public SignatureRSASHA384() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384;
        }
    }


    public static class SignatureRSASHA512 extends SignatureBaseRSA {


        public SignatureRSASHA512() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA512;
        }
    }


    public static class SignatureRSARIPEMD160 extends SignatureBaseRSA {


        public SignatureRSARIPEMD160() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_RSA_RIPEMD160;
        }
    }


    public static class SignatureRSAMD5 extends SignatureBaseRSA {


        public SignatureRSAMD5() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5;
        }
    }
}
