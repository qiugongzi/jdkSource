

package com.sun.org.apache.xml.internal.security.keys.content.x509;

import java.math.BigInteger;
import java.security.cert.X509Certificate;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.RFC2253Parser;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLX509IssuerSerial extends SignatureElementProxy implements XMLX509DataContent {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(XMLX509IssuerSerial.class.getName());


    public XMLX509IssuerSerial(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }


    public XMLX509IssuerSerial(Document doc, String x509IssuerName, BigInteger x509SerialNumber) {
        super(doc);
        XMLUtils.addReturnToElement(this.constructionElement);
        addTextElement(x509IssuerName, Constants._TAG_X509ISSUERNAME);
        addTextElement(x509SerialNumber.toString(), Constants._TAG_X509SERIALNUMBER);
    }


    public XMLX509IssuerSerial(Document doc, String x509IssuerName, String x509SerialNumber) {
        this(doc, x509IssuerName, new BigInteger(x509SerialNumber));
    }


    public XMLX509IssuerSerial(Document doc, String x509IssuerName, int x509SerialNumber) {
        this(doc, x509IssuerName, new BigInteger(Integer.toString(x509SerialNumber)));
    }


    public XMLX509IssuerSerial(Document doc, X509Certificate x509certificate) {
        this(
            doc,
            x509certificate.getIssuerX500Principal().getName(),
            x509certificate.getSerialNumber()
        );
    }


    public BigInteger getSerialNumber() {
        String text =
            this.getTextFromChildElement(Constants._TAG_X509SERIALNUMBER, Constants.SignatureSpecNS);
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "X509SerialNumber text: " + text);
        }

        return new BigInteger(text);
    }


    public int getSerialNumberInteger() {
        return this.getSerialNumber().intValue();
    }


    public String getIssuerName()  {
        return RFC2253Parser.normalize(
            this.getTextFromChildElement(Constants._TAG_X509ISSUERNAME, Constants.SignatureSpecNS)
        );
    }


    public boolean equals(Object obj) {
        if (!(obj instanceof XMLX509IssuerSerial)) {
            return false;
        }

        XMLX509IssuerSerial other = (XMLX509IssuerSerial) obj;

        return this.getSerialNumber().equals(other.getSerialNumber())
            && this.getIssuerName().equals(other.getIssuerName());
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + getSerialNumber().hashCode();
        result = 31 * result + getIssuerName().hashCode();
        return result;
    }


    public String getBaseLocalName() {
        return Constants._TAG_X509ISSUERSERIAL;
    }
}
