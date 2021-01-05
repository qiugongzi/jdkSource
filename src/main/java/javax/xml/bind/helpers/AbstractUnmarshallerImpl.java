

package javax.xml.bind.helpers;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;


public abstract class AbstractUnmarshallerImpl implements Unmarshaller
{

    private ValidationEventHandler eventHandler =
        new DefaultValidationEventHandler();


    protected boolean validating = false;


    private XMLReader reader = null;


    protected XMLReader getXMLReader() throws JAXBException {
        if(reader==null) {
            try {
                SAXParserFactory parserFactory;
                parserFactory = SAXParserFactory.newInstance();
                parserFactory.setNamespaceAware(true);
                parserFactory.setValidating(false);
                reader = parserFactory.newSAXParser().getXMLReader();
            } catch( ParserConfigurationException e ) {
                throw new JAXBException(e);
            } catch( SAXException e ) {
                throw new JAXBException(e);
            }
        }
        return reader;
    }

    public Object unmarshal( Source source ) throws JAXBException {
        if( source == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "source" ) );
        }

        if(source instanceof SAXSource)
            return unmarshal( (SAXSource)source );
        if(source instanceof StreamSource)
            return unmarshal( streamSourceToInputSource((StreamSource)source));
        if(source instanceof DOMSource)
            return unmarshal( ((DOMSource)source).getNode() );

        throw new IllegalArgumentException();
    }

    private Object unmarshal( SAXSource source ) throws JAXBException {

        XMLReader r = source.getXMLReader();
        if( r == null )
            r = getXMLReader();

        return unmarshal( r, source.getInputSource() );
    }


    protected abstract Object unmarshal( XMLReader reader, InputSource source ) throws JAXBException;

    public final Object unmarshal( InputSource source ) throws JAXBException {
        if( source == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "source" ) );
        }

        return unmarshal( getXMLReader(), source );
    }


    private Object unmarshal( String url ) throws JAXBException {
        return unmarshal( new InputSource(url) );
    }

    public final Object unmarshal( URL url ) throws JAXBException {
        if( url == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "url" ) );
        }

        return unmarshal( url.toExternalForm() );
    }

    public final Object unmarshal( File f ) throws JAXBException {
        if( f == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "file" ) );
        }

        try {
            String path = f.getAbsolutePath();
            if (File.separatorChar != '/')
                path = path.replace(File.separatorChar, '/');
            if (!path.startsWith("/"))
                path = "/" + path;
            if (!path.endsWith("/") && f.isDirectory())
                path = path + "/";
            return unmarshal(new URL("file", "", path));
        } catch( MalformedURLException e ) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public final Object unmarshal( java.io.InputStream is )
        throws JAXBException {

        if( is == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "is" ) );
        }

        InputSource isrc = new InputSource( is );
        return unmarshal( isrc );
    }

    public final Object unmarshal( Reader reader ) throws JAXBException {
        if( reader == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "reader" ) );
        }

        InputSource isrc = new InputSource( reader );
        return unmarshal( isrc );
    }


    private static InputSource streamSourceToInputSource( StreamSource ss ) {
        InputSource is = new InputSource();
        is.setSystemId( ss.getSystemId() );
        is.setByteStream( ss.getInputStream() );
        is.setCharacterStream( ss.getReader() );

        return is;
    }



    public boolean isValidating() throws JAXBException {
        return validating;
    }


    public void setEventHandler(ValidationEventHandler handler)
        throws JAXBException {

        if( handler == null ) {
            eventHandler = new DefaultValidationEventHandler();
        } else {
            eventHandler = handler;
        }
    }


    public void setValidating(boolean validating) throws JAXBException {
        this.validating = validating;
    }


    public ValidationEventHandler getEventHandler() throws JAXBException {
        return eventHandler;
    }



    protected UnmarshalException createUnmarshalException( SAXException e ) {
        Exception nested = e.getException();
        if(nested instanceof UnmarshalException)
            return (UnmarshalException)nested;

        if(nested instanceof RuntimeException)
            throw (RuntimeException)nested;


        if(nested!=null)
            return new UnmarshalException(nested);
        else
            return new UnmarshalException(e);
    }


    public void setProperty( String name, Object value )
        throws PropertyException {

        if( name == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "name" ) );
        }

        throw new PropertyException(name, value);
    }


    public Object getProperty( String name )
        throws PropertyException {

        if( name == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "name" ) );
        }

        throw new PropertyException(name);
    }

    public Object unmarshal(XMLEventReader reader) throws JAXBException {

        throw new UnsupportedOperationException();
    }

    public Object unmarshal(XMLStreamReader reader) throws JAXBException {

        throw new UnsupportedOperationException();
    }

    public <T> JAXBElement<T> unmarshal(Node node, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    public <T> JAXBElement<T> unmarshal(Source source, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> expectedType) throws JAXBException {
        throw new UnsupportedOperationException();
    }

    public void setSchema(Schema schema) {
        throw new UnsupportedOperationException();
    }

    public Schema getSchema() {
        throw new UnsupportedOperationException();
    }

    public void setAdapter(XmlAdapter adapter) {
        if(adapter==null)
            throw new IllegalArgumentException();
        setAdapter((Class)adapter.getClass(),adapter);
    }

    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        throw new UnsupportedOperationException();
    }

    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        throw new UnsupportedOperationException();
    }

    public void setAttachmentUnmarshaller(AttachmentUnmarshaller au) {
        throw new UnsupportedOperationException();
    }

    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        throw new UnsupportedOperationException();
    }

    public void setListener(Listener listener) {
        throw new UnsupportedOperationException();
    }

    public Listener getListener() {
        throw new UnsupportedOperationException();
    }
}
