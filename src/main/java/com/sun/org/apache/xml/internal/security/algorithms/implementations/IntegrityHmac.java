

package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignature;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public abstract class IntegrityHmac extends SignatureAlgorithmSpi {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(IntegrityHmac.class.getName());


    private Mac macAlgorithm = null;


    private int HMACOutputLength = 0;
    private boolean HMACOutputLengthSet = false;


    public abstract String engineGetURI();


    abstract int getDigestLength();


    public IntegrityHmac() throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Created IntegrityHmacSHA1 using " + algorithmID);
        }

        try {
            this.macAlgorithm = Mac.getInstance(algorithmID);
        } catch (java.security.NoSuchAlgorithmException ex) {
            Object[] exArgs = { algorithmID, ex.getLocalizedMessage() };

            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
        }
    }


    protected void engineSetParameter(AlgorithmParameterSpec params) throws XMLSignatureException {
        throw new XMLSignatureException("empty");
    }

    public void reset() {
        HMACOutputLength = 0;
        HMACOutputLengthSet = false;
        this.macAlgorithm.reset();
    }


    protected boolean engineVerify(byte[] signature) throws XMLSignatureException {
        try {
            if (this.HMACOutputLengthSet && this.HMACOutputLength < getDigestLength()) {
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, "HMACOutputLength must not be less than " + getDigestLength());
                }
                Object[] exArgs = { String.valueOf(getDigestLength()) };
                throw new XMLSignatureException("algorithms.HMACOutputLengthMin", exArgs);
            } else {
                byte[] completeResult = this.macAlgorithm.doFinal();
                return MessageDigestAlgorithm.isEqual(completeResult, signature);
            }
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected void engineInitVerify(Key secretKey) throws XMLSignatureException {
        if (!(secretKey instanceof SecretKey)) {
            String supplied = secretKey.getClass().getName();
            String needed = SecretKey.class.getName();
            Object exArgs[] = { supplied, needed };

            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }

        try {
            this.macAlgorithm.init(secretKey);
        } catch (InvalidKeyException ex) {
            Mac mac = this.macAlgorithm;
            try {
                this.macAlgorithm = Mac.getInstance(macAlgorithm.getAlgorithm());
            } catch (Exception e) {
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, "Exception when reinstantiating Mac:" + e);
                }
                this.macAlgorithm = mac;
            }
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected byte[] engineSign() throws XMLSignatureException {
        try {
            if (this.HMACOutputLengthSet && this.HMACOutputLength < getDigestLength()) {
                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE, "HMACOutputLength must not be less than " + getDigestLength());
                }
                Object[] exArgs = { String.valueOf(getDigestLength()) };
                throw new XMLSignatureException("algorithms.HMACOutputLengthMin", exArgs);
            } else {
                return this.macAlgorithm.doFinal();
            }
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected void engineInitSign(Key secretKey) throws XMLSignatureException {
        if (!(secretKey instanceof SecretKey)) {
            String supplied = secretKey.getClass().getName();
            String needed = SecretKey.class.getName();
            Object exArgs[] = { supplied, needed };

            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }

        try {
            this.macAlgorithm.init(secretKey);
        } catch (InvalidKeyException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected void engineInitSign(
        Key secretKey, AlgorithmParameterSpec algorithmParameterSpec
    ) throws XMLSignatureException {
        if (!(secretKey instanceof SecretKey)) {
            String supplied = secretKey.getClass().getName();
            String needed = SecretKey.class.getName();
            Object exArgs[] = { supplied, needed };

            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }

        try {
            this.macAlgorithm.init(secretKey, algorithmParameterSpec);
        } catch (InvalidKeyException ex) {
            throw new XMLSignatureException("empty", ex);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected void engineInitSign(Key secretKey, SecureRandom secureRandom)
        throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.CannotUseSecureRandomOnMAC");
    }


    protected void engineUpdate(byte[] input) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(input);
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected void engineUpdate(byte input) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(input);
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected void engineUpdate(byte buf[], int offset, int len) throws XMLSignatureException {
        try {
            this.macAlgorithm.update(buf, offset, len);
        } catch (IllegalStateException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }


    protected String engineGetJCEAlgorithmString() {
        return this.macAlgorithm.getAlgorithm();
    }


    protected String engineGetJCEProviderName() {
        return this.macAlgorithm.getProvider().getName();
    }


    protected void engineSetHMACOutputLength(int HMACOutputLength) {
        this.HMACOutputLength = HMACOutputLength;
        this.HMACOutputLengthSet = true;
    }


    protected void engineGetContextFromElement(Element element) {
        super.engineGetContextFromElement(element);

        if (element == null) {
            throw new IllegalArgumentException("element null");
        }

        Text hmaclength =
            XMLUtils.selectDsNodeText(element.getFirstChild(), Constants._TAG_HMACOUTPUTLENGTH, 0);

        if (hmaclength != null) {
            this.HMACOutputLength = Integer.parseInt(hmaclength.getData());
            this.HMACOutputLengthSet = true;
        }
    }


    public void engineAddContextToElement(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("null element");
        }

        if (this.HMACOutputLengthSet) {
            Document doc = element.getOwnerDocument();
            Element HMElem =
                XMLUtils.createElementInSignatureSpace(doc, Constants._TAG_HMACOUTPUTLENGTH);
            Text HMText =
                doc.createTextNode(Integer.valueOf(this.HMACOutputLength).toString());

            HMElem.appendChild(HMText);
            XMLUtils.addReturnToElement(element);
            element.appendChild(HMElem);
            XMLUtils.addReturnToElement(element);
        }
    }


    public static class IntegrityHmacSHA1 extends IntegrityHmac {


        public IntegrityHmacSHA1() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_SHA1;
        }

        int getDigestLength() {
            return 160;
        }
    }


    public static class IntegrityHmacSHA256 extends IntegrityHmac {


        public IntegrityHmacSHA256() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_SHA256;
        }

        int getDigestLength() {
            return 256;
        }
    }


    public static class IntegrityHmacSHA384 extends IntegrityHmac {


        public IntegrityHmacSHA384() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_SHA384;
        }

        int getDigestLength() {
            return 384;
        }
    }


    public static class IntegrityHmacSHA512 extends IntegrityHmac {


        public IntegrityHmacSHA512() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_SHA512;
        }

        int getDigestLength() {
            return 512;
        }
    }


    public static class IntegrityHmacRIPEMD160 extends IntegrityHmac {


        public IntegrityHmacRIPEMD160() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_RIPEMD160;
        }

        int getDigestLength() {
            return 160;
        }
    }


    public static class IntegrityHmacMD5 extends IntegrityHmac {


        public IntegrityHmacMD5() throws XMLSignatureException {
            super();
        }


        public String engineGetURI() {
            return XMLSignature.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5;
        }

        int getDigestLength() {
            return 128;
        }
    }
}
