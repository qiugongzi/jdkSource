

package javax.xml.bind;

import org.w3c.dom.Node;

import javax.xml.validation.Schema;


public abstract class Binder<XmlNode> {

    public abstract Object unmarshal( XmlNode xmlNode ) throws JAXBException;


    public abstract <T> JAXBElement<T>
        unmarshal( XmlNode xmlNode, Class<T> declaredType )
        throws JAXBException;


    public abstract void marshal( Object jaxbObject, XmlNode xmlNode ) throws JAXBException;


    public abstract XmlNode getXMLNode( Object jaxbObject );


    public abstract Object getJAXBNode( XmlNode xmlNode );


    public abstract XmlNode updateXML( Object jaxbObject ) throws JAXBException;


    public abstract XmlNode updateXML( Object jaxbObject, XmlNode xmlNode ) throws JAXBException;


    public abstract Object updateJAXB( XmlNode xmlNode ) throws JAXBException;



    public abstract void setSchema( Schema schema );


    public abstract Schema getSchema();


    public abstract void setEventHandler( ValidationEventHandler handler ) throws JAXBException;


    public abstract ValidationEventHandler getEventHandler() throws JAXBException;


    abstract public void setProperty( String name, Object value ) throws PropertyException;



    abstract public Object getProperty( String name ) throws PropertyException;

}
