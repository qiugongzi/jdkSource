

package javax.xml.soap;


public interface Node extends org.w3c.dom.Node {

    public String getValue();


    public void setValue(String value);


    public void setParentElement(SOAPElement parent) throws SOAPException;


    public SOAPElement getParentElement();


    public void detachNode();


    public void recycleNode();

}
