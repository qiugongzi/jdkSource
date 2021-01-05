

package javax.xml.soap;

import java.util.Locale;

import org.w3c.dom.Document;

import javax.xml.namespace.QName;


public interface SOAPBody extends SOAPElement {


    public SOAPFault addFault() throws SOAPException;



    public SOAPFault addFault(Name faultCode, String faultString, Locale locale) throws SOAPException;


    public SOAPFault addFault(QName faultCode, String faultString, Locale locale)
        throws SOAPException;


    public SOAPFault addFault(Name faultCode, String faultString)
        throws SOAPException;


    public SOAPFault addFault(QName faultCode, String faultString)
        throws SOAPException;


    public boolean hasFault();


    public SOAPFault getFault();


    public SOAPBodyElement addBodyElement(Name name) throws SOAPException;



    public SOAPBodyElement addBodyElement(QName qname) throws SOAPException;


    public SOAPBodyElement addDocument(org.w3c.dom.Document document)
        throws SOAPException;


    public org.w3c.dom.Document extractContentAsDocument()
        throws SOAPException;
}
