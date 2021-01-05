

package javax.xml.soap;

import java.util.Iterator;
import java.util.Locale;

import javax.xml.namespace.QName;


public interface SOAPFault extends SOAPBodyElement {


    public void setFaultCode(Name faultCodeQName) throws SOAPException;


    public void setFaultCode(QName faultCodeQName) throws SOAPException;


    public void setFaultCode(String faultCode) throws SOAPException;


    public Name getFaultCodeAsName();



    public QName getFaultCodeAsQName();


    public Iterator getFaultSubcodes();


    public void removeAllFaultSubcodes();


    public void appendFaultSubcode(QName subcode) throws SOAPException;


    public String getFaultCode();


    public void setFaultActor(String faultActor) throws SOAPException;


    public String getFaultActor();


    public void setFaultString(String faultString) throws SOAPException;


    public void setFaultString(String faultString, Locale locale)
        throws SOAPException;


    public String getFaultString();


    public Locale getFaultStringLocale();


    public boolean hasDetail();


    public Detail getDetail();


    public Detail addDetail() throws SOAPException;


    public Iterator getFaultReasonLocales() throws SOAPException;


    public Iterator getFaultReasonTexts() throws SOAPException;


    public String getFaultReasonText(Locale locale) throws SOAPException;


    public void addFaultReasonText(String text, java.util.Locale locale)
        throws SOAPException;


    public String getFaultNode();


    public void setFaultNode(String uri) throws SOAPException;


    public String getFaultRole();


    public void setFaultRole(String uri) throws SOAPException;

}
