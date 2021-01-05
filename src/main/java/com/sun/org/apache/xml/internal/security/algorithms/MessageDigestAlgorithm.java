

package com.sun.org.apache.xml.internal.security.algorithms;

import java.security.MessageDigest;
import java.security.NoSuchProviderException;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.EncryptionConstants;
import org.w3c.dom.Document;


public class MessageDigestAlgorithm extends Algorithm {


    public static final String ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5 =
        Constants.MoreAlgorithmsSpecNS + "md5";

    public static final String ALGO_ID_DIGEST_SHA1 = Constants.SignatureSpecNS + "sha1";

    public static final String ALGO_ID_DIGEST_SHA256 =
        EncryptionConstants.EncryptionSpecNS + "sha256";

    public static final String ALGO_ID_DIGEST_SHA384 =
        Constants.MoreAlgorithmsSpecNS + "sha384";

    public static final String ALGO_ID_DIGEST_SHA512 =
        EncryptionConstants.EncryptionSpecNS + "sha512";

    public static final String ALGO_ID_DIGEST_RIPEMD160 =
        EncryptionConstants.EncryptionSpecNS + "ripemd160";


    private final MessageDigest algorithm;


    private MessageDigestAlgorithm(Document doc, String algorithmURI)
        throws XMLSignatureException {
        super(doc, algorithmURI);

        algorithm = getDigestInstance(algorithmURI);
    }


    public static MessageDigestAlgorithm getInstance(
        Document doc, String algorithmURI
    ) throws XMLSignatureException {
        return new MessageDigestAlgorithm(doc, algorithmURI);
    }

    private static MessageDigest getDigestInstance(String algorithmURI) throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(algorithmURI);

        if (algorithmID == null) {
            Object[] exArgs = { algorithmURI };
            throw new XMLSignatureException("algorithms.NoSuchMap", exArgs);
        }

        MessageDigest md;
        String provider = JCEMapper.getProviderId();
        try {
            if (provider == null) {
                md = MessageDigest.getInstance(algorithmID);
            } else {
                md = MessageDigest.getInstance(algorithmID, provider);
            }
        } catch (java.security.NoSuchAlgorithmException ex) {
            Object[] exArgs = { algorithmID, ex.getLocalizedMessage() };

            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
        } catch (NoSuchProviderException ex) {
            Object[] exArgs = { algorithmID, ex.getLocalizedMessage() };

            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
        }

        return md;
    }


    public java.security.MessageDigest getAlgorithm() {
        return algorithm;
    }


    public static boolean isEqual(byte[] digesta, byte[] digestb) {
        return java.security.MessageDigest.isEqual(digesta, digestb);
    }


    public byte[] digest() {
        return algorithm.digest();
    }


    public byte[] digest(byte input[]) {
        return algorithm.digest(input);
    }


    public int digest(byte buf[], int offset, int len) throws java.security.DigestException {
        return algorithm.digest(buf, offset, len);
    }


    public String getJCEAlgorithmString() {
        return algorithm.getAlgorithm();
    }


    public java.security.Provider getJCEProvider() {
        return algorithm.getProvider();
    }


    public int getDigestLength() {
        return algorithm.getDigestLength();
    }


    public void reset() {
        algorithm.reset();
    }


    public void update(byte[] input) {
        algorithm.update(input);
    }


    public void update(byte input) {
        algorithm.update(input);
    }


    public void update(byte buf[], int offset, int len) {
        algorithm.update(buf, offset, len);
    }


    public String getBaseNamespace() {
        return Constants.SignatureSpecNS;
    }


    public String getBaseLocalName() {
        return Constants._TAG_DIGESTMETHOD;
    }
}
