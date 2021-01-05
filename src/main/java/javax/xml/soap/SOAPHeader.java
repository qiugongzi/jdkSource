

package javax.xml.soap;

import java.util.Iterator;

import javax.xml.namespace.QName;


public interface SOAPHeader extends SOAPElement {

    public SOAPHeaderElement addHeaderElement(Name name)
        throws SOAPException;


    public SOAPHeaderElement addHeaderElement(QName qname)
        throws SOAPException;


    public Iterator examineMustUnderstandHeaderElements(String actor);


    public Iterator examineHeaderElements(String actor);


    public Iterator extractHeaderElements(String actor);


    public SOAPHeaderElement addNotUnderstoodHeaderElement(QName name)
        throws SOAPException;


    public SOAPHeaderElement addUpgradeHeaderElement(Iterator supportedSOAPURIs)
        throws SOAPException;


    public SOAPHeaderElement addUpgradeHeaderElement(String[] supportedSoapUris)
        throws SOAPException;


    public SOAPHeaderElement addUpgradeHeaderElement(String supportedSoapUri)
        throws SOAPException;


    public Iterator examineAllHeaderElements();


    public Iterator extractAllHeaderElements();

}
