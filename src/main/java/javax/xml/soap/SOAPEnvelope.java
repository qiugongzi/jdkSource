

package javax.xml.soap;



public interface SOAPEnvelope extends SOAPElement {


    public abstract Name createName(String localName, String prefix,
                                    String uri)
        throws SOAPException;


    public abstract Name createName(String localName)
        throws SOAPException;


    public SOAPHeader getHeader() throws SOAPException;


    public SOAPBody getBody() throws SOAPException;

    public SOAPHeader addHeader() throws SOAPException;

    public SOAPBody addBody() throws SOAPException;
}
