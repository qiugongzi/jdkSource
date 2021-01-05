

package javax.xml.soap;

import java.util.Iterator;

import javax.xml.namespace.QName;


public interface Detail extends SOAPFaultElement {


    public DetailEntry addDetailEntry(Name name) throws SOAPException;


    public DetailEntry addDetailEntry(QName qname) throws SOAPException;


    public Iterator getDetailEntries();
}
