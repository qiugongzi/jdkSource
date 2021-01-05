

package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import java.io.IOException;
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
import com.sun.org.apache.xml.internal.security.utils.Base64;


public abstract class SignatureECDSA extends SignatureAlgorithmSpi {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(SignatureECDSA.class.getName());


    public abstract String engineGetURI();


    private java.security.Signature signatureAlgorithm = null;


    public static byte[] convertASN1toXMLDSIG(byte asn1Bytes[]) throws IOException {

        if (asn1Bytes.length < 8 || asn1Bytes[0] != 48) {
            throw new IOException("Invalid ASN.1 format of ECDSA signature");
        }
        int offset;
        if (asn1Bytes[1] > 0) {
            offset = 2;
        } else if (asn1Bytes[1] == (byte) 0x81) {
            offset = 3;
        } else {
            throw new IOException("Invalid ASN.1 format of ECDSA signature");
        }

        byte rLength = asn1Bytes[offset + 1];
        int i;

        for (i = rLength; (i > 0) && (asn1Bytes[(offset + 2 + rLength) - i] == 0); i--);

        byte sLength = asn1Bytes[offset + 2 + rLength + 1];
        int j;

        for (j = sLength;
            (j > 0) && (asn1Bytes[(offset + 2 + rLength + 2 + sLength) - j] == 0); j--);

        int rawLen = Math.max(i, j);

        if ((asn1Bytes[offset - 1] & 0xff) != asn1Bytes.length - offset
            || (asn1Bytes[offset - 1] & 0xff) != 2 + rLength + 2 + sLength
            || asn1Bytes[offset] != 2
            || asn1Bytes[offset + 2 + rLength] != 2) {
            throw new IOException("Invalid ASN.1 format of ECDSA signature");
        }
        byte xmldsigBytes[] = new byte[2*rawLen];

        System.arraycopy(asn1Bytes, (offset + 2 + rLength) - i, xmldsigBytes, rawLen - i, i);
        System.arraycopy(asn1Bytes, (offset + 2 + rLength + 2 + sLength) - j, xmldsigBytes,
                         2*rawLen - j, j);

        return xmldsigBytes;
    }


    public static byte[] convertXMLDSIGtoASN1(byte xmldsigBytes[]) throws IOException {

        int rawLen = xmldsigBytes.length/2;

        int i;

        for (i = rawLen; (i > 0) && (xmldsigBytes[rawLen - i] == 0); i--);

        int j = i;

        if (xmldsigBytes[rawLen - i] < 0) {
            j += 1;
        }

        int k;

        for (k = rawLen; (k > 0) && (xmldsigBytes[2*rawLen - k] == 0); k--);

        int l = k;

        if (xmldsigBytes[2*rawLen - k] < 0) {
            l += 1;
        }

        int len = 2 + j + 2 + l;
        if (len > 255) {
            throw new IOException("Invalid XMLDSIG format of ECDSA signature");
        }
        int offset;
        byte asn1Bytes[];
        if (len < 128) {
            asn1Bytes = new byte[2 + 2 + j + 2 + l];
            offset = 1;
        } else {
            asn1Bytes = new byte[3 + 2 + j + 2 + l];
            asn1Bytes[1] = (byte) 0x81;
            offset = 2;
        }
        asn1Bytes[0] = 48;
        asn1Bytes[offset++] = (byte) len;
        asn1Bytes[offset++] = 2;
        asn1Bytes[offset++] = (byte) j;

        System.arraycopy(xmldsigBytes, rawLen - i, asn1Bytes, (offset + j) - i, i);

        offset += j;

        asn1Bytes[offset++] = 2;
        asn1Bytes[offset++] = (byte) l;

        System.arraycopy(xmldsigBytes, 2*rawLen - k, asn1Bytes, (offset + l) - k, k);

        return asn1Bytes;
    }


    public SignatureECDSA() throws XMLSignatureException {

        String algorithmID = JCEMapper.translateURItoJCEID(this.engineGetURI());

        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Created SignatureECDSA using " + algorithmID);
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
            byte[] jcebytes = SignatureECDSA.convertXMLDSIGtoASN1(signature);

            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, "Called ECDSA.verify() on " + Base64.encode(signature));
            }

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
    }


    protected byte[] engineSign() throws XMLSignatureException {
        try {
            byte jcebytes[] = this.signatureAlgorithm.sign();

            return SignatureECDSA.convertASN1toXMLDSIG(jcebytes);
        } catch (SignatureException ex) {
            throw new XMLSignatureException("empty", ex);
        } catch (IOException ex) {
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


    public static class SignatureECDSASHA1 extends SignatureECDSA {

        public SignatureECDSASHA1() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA1;
        }
    }


    public static class SignatureECDSASHA256 extends SignatureECDSA {


        public SignatureECDSASHA256() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256;
        }
    }


    public static class SignatureECDSASHA384 extends SignatureECDSA {


        public SignatureECDSASHA384() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA384;
        }
    }


    public static class SignatureECDSASHA512 extends SignatureECDSA {


        public SignatureECDSASHA512() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA512;
        }
    }

}
