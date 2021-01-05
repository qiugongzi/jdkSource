

package javax.xml.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.validation.Schema;
import java.io.Reader;


public interface Unmarshaller {


    public Object unmarshal( java.io.File f ) throws JAXBException;


    public Object unmarshal( java.io.InputStream is ) throws JAXBException;


    public Object unmarshal( Reader reader ) throws JAXBException;


    public Object unmarshal( java.net.URL url ) throws JAXBException;


    public Object unmarshal( org.xml.sax.InputSource source ) throws JAXBException;


    public Object unmarshal( org.w3c.dom.Node node ) throws JAXBException;


    public <T> JAXBElement<T> unmarshal( org.w3c.dom.Node node, Class<T> declaredType ) throws JAXBException;


    public Object unmarshal( javax.xml.transform.Source source )
        throws JAXBException;



    public <T> JAXBElement<T> unmarshal( javax.xml.transform.Source source, Class<T> declaredType )
        throws JAXBException;


    public Object unmarshal( javax.xml.stream.XMLStreamReader reader )
        throws JAXBException;


    public <T> JAXBElement<T> unmarshal( javax.xml.stream.XMLStreamReader reader, Class<T> declaredType ) throws JAXBException;


    public Object unmarshal( javax.xml.stream.XMLEventReader reader )
        throws JAXBException;


    public <T> JAXBElement<T> unmarshal( javax.xml.stream.XMLEventReader reader, Class<T> declaredType ) throws JAXBException;


    public UnmarshallerHandler getUnmarshallerHandler();


    public void setValidating( boolean validating )
        throws JAXBException;


    public boolean isValidating()
        throws JAXBException;


    public void setEventHandler( ValidationEventHandler handler )
        throws JAXBException;


    public ValidationEventHandler getEventHandler()
        throws JAXBException;


    public void setProperty( String name, Object value )
        throws PropertyException;


    public Object getProperty( String name ) throws PropertyException;


    public void setSchema( javax.xml.validation.Schema schema );


    public javax.xml.validation.Schema getSchema();


    public void setAdapter( XmlAdapter adapter );


    public <A extends XmlAdapter> void setAdapter( Class<A> type, A adapter );


    public <A extends XmlAdapter> A getAdapter( Class<A> type );


    void setAttachmentUnmarshaller(AttachmentUnmarshaller au);

    AttachmentUnmarshaller getAttachmentUnmarshaller();


    public static abstract class Listener {

        public void beforeUnmarshal(Object target, Object parent) {
        }


        public void afterUnmarshal(Object target, Object parent) {
        }
    }


    public void     setListener(Listener listener);


    public Listener getListener();
}
