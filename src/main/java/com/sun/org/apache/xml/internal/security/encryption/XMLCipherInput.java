

package com.sun.org.apache.xml.internal.security.encryption;

import java.io.IOException;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import org.w3c.dom.Attr;
import com.sun.org.apache.xml.internal.security.utils.Base64;


public class XMLCipherInput {

    private static java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(XMLCipherInput.class.getName());


    private CipherData cipherData;


    private int mode;

    private boolean secureValidation;


    public XMLCipherInput(CipherData data) throws XMLEncryptionException {
        cipherData = data;
        mode = XMLCipher.DECRYPT_MODE;
        if (cipherData == null) {
            throw new XMLEncryptionException("CipherData is null");
        }
    }


    public XMLCipherInput(EncryptedType input) throws XMLEncryptionException {
        cipherData = ((input == null) ? null : input.getCipherData());
        mode = XMLCipher.DECRYPT_MODE;
        if (cipherData == null) {
            throw new XMLEncryptionException("CipherData is null");
        }
    }


    public void setSecureValidation(boolean secureValidation) {
        this.secureValidation = secureValidation;
    }


    public byte[] getBytes() throws XMLEncryptionException {
        if (mode == XMLCipher.DECRYPT_MODE) {
            return getDecryptBytes();
        }
        return null;
    }


    private byte[] getDecryptBytes() throws XMLEncryptionException {
        String base64EncodedEncryptedOctets = null;

        if (cipherData.getDataType() == CipherData.REFERENCE_TYPE) {
            if (logger.isLoggable(java.util.logging.Level.FINE)) {
                logger.log(java.util.logging.Level.FINE, "Found a reference type CipherData");
            }
            CipherReference cr = cipherData.getCipherReference();

            Attr uriAttr = cr.getURIAsAttr();
            XMLSignatureInput input = null;

            try {
                ResourceResolver resolver =
                    ResourceResolver.getInstance(uriAttr, null, secureValidation);
                input = resolver.resolve(uriAttr, null, secureValidation);
            } catch (ResourceResolverException ex) {
                throw new XMLEncryptionException("empty", ex);
            }

            if (input != null) {
                if (logger.isLoggable(java.util.logging.Level.FINE)) {
                    logger.log(java.util.logging.Level.FINE, "Managed to resolve URI \"" + cr.getURI() + "\"");
                }
            } else {
                if (logger.isLoggable(java.util.logging.Level.FINE)) {
                    logger.log(java.util.logging.Level.FINE, "Failed to resolve URI \"" + cr.getURI() + "\"");
                }
            }

            Transforms transforms = cr.getTransforms();
            if (transforms != null) {
                if (logger.isLoggable(java.util.logging.Level.FINE)) {
                    logger.log(java.util.logging.Level.FINE, "Have transforms in cipher reference");
                }
                try {
                    com.sun.org.apache.xml.internal.security.transforms.Transforms dsTransforms =
                        transforms.getDSTransforms();
                    dsTransforms.setSecureValidation(secureValidation);
                    input = dsTransforms.performTransforms(input);
                } catch (TransformationException ex) {
                    throw new XMLEncryptionException("empty", ex);
                }
            }

            try {
                return input.getBytes();
            } catch (IOException ex) {
                throw new XMLEncryptionException("empty", ex);
            } catch (CanonicalizationException ex) {
                throw new XMLEncryptionException("empty", ex);
            }

            } else if (cipherData.getDataType() == CipherData.VALUE_TYPE) {
            base64EncodedEncryptedOctets = cipherData.getCipherValue().getValue();
        } else {
            throw new XMLEncryptionException("CipherData.getDataType() returned unexpected value");
        }

        if (logger.isLoggable(java.util.logging.Level.FINE)) {
            logger.log(java.util.logging.Level.FINE, "Encrypted octets:\n" + base64EncodedEncryptedOctets);
        }

        try {
            return Base64.decode(base64EncodedEncryptedOctets);
        } catch (Base64DecodingException bde) {
            throw new XMLEncryptionException("empty", bde);
        }
    }
}
