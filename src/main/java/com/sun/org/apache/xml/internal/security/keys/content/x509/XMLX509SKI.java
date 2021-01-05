

package com.sun.org.apache.xml.internal.security.keys.content.x509;

import java.security.cert.X509Certificate;
import java.util.Arrays;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class XMLX509SKI extends SignatureElementProxy implements XMLX509DataContent {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(XMLX509SKI.class.getName());


    public static final String SKI_OID = "2.5.29.14";


    public XMLX509SKI(Document doc, byte[] skiBytes) {
        super(doc);
        this.addBase64Text(skiBytes);
    }


    public XMLX509SKI(Document doc, X509Certificate x509certificate)
        throws XMLSecurityException {
        super(doc);
        this.addBase64Text(XMLX509SKI.getSKIBytesFromCert(x509certificate));
    }


    public XMLX509SKI(Element element, String BaseURI) throws XMLSecurityException {
        super(element, BaseURI);
    }


    public byte[] getSKIBytes() throws XMLSecurityException {
        return this.getBytesFromTextChild();
    }


    public static byte[] getSKIBytesFromCert(X509Certificate cert)
        throws XMLSecurityException {

        if (cert.getVersion() < 3) {
            Object exArgs[] = { Integer.valueOf(cert.getVersion()) };
            throw new XMLSecurityException("certificate.noSki.lowVersion", exArgs);
        }


        byte[] extensionValue = cert.getExtensionValue(XMLX509SKI.SKI_OID);
        if (extensionValue == null) {
            throw new XMLSecurityException("certificate.noSki.null");
        }


        byte skidValue[] = new byte[extensionValue.length - 4];

        System.arraycopy(extensionValue, 4, skidValue, 0, skidValue.length);

        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Base64 of SKI is " + Base64.encode(skidValue));
        }

        return skidValue;
    }


    public boolean equals(Object obj) {
        if (!(obj instanceof XMLX509SKI)) {
            return false;
        }

        XMLX509SKI other = (XMLX509SKI) obj;

        try {
            return Arrays.equals(other.getSKIBytes(), this.getSKIBytes());
        } catch (XMLSecurityException ex) {
            return false;
        }
    }

    public int hashCode() {
        int result = 17;
        try {
            byte[] bytes = getSKIBytes();
            for (int i = 0; i < bytes.length; i++) {
                result = 31 * result + bytes[i];
            }
        } catch (XMLSecurityException e) {
            if (log.isLoggable(java.util.logging.Level.FINE)) {
                log.log(java.util.logging.Level.FINE, e.getMessage(), e);
            }
        }
        return result;

    }


    public String getBaseLocalName() {
        return Constants._TAG_X509SKI;
    }
}
