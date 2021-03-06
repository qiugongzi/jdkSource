

package com.sun.org.apache.xml.internal.security.keys.content.x509;

import java.security.cert.X509Certificate;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.RFC2253Parser;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class XMLX509SubjectName extends SignatureElementProxy implements XMLX509DataContent {


    public XMLX509SubjectName(Element element, String BaseURI)
        throws XMLSecurityException {
        super(element, BaseURI);
    }


    public XMLX509SubjectName(Document doc, String X509SubjectNameString) {
        super(doc);

        this.addText(X509SubjectNameString);
    }


    public XMLX509SubjectName(Document doc, X509Certificate x509certificate) {
        this(doc, x509certificate.getSubjectX500Principal().getName());
    }


    public String getSubjectName() {
        return RFC2253Parser.normalize(this.getTextFromTextChild());
    }


    public boolean equals(Object obj) {
        if (!(obj instanceof XMLX509SubjectName)) {
            return false;
        }

        XMLX509SubjectName other = (XMLX509SubjectName) obj;
        String otherSubject = other.getSubjectName();
        String thisSubject = this.getSubjectName();

        return thisSubject.equals(otherSubject);
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + this.getSubjectName().hashCode();
        return result;
    }


    public String getBaseLocalName() {
        return Constants._TAG_X509SUBJECTNAME;
    }
}
