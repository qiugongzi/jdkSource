

package javax.xml.bind.helpers;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
public abstract class AbstractMarshallerImpl implements Marshaller
{

    private ValidationEventHandler eventHandler =
        new DefaultValidationEventHandler();

    private String encoding = "UTF-8";


    private String schemaLocation = null;


    private String noNSSchemaLocation = null;


    private boolean formattedOutput = false;


    private boolean fragment = false;

    public final void marshal( Object obj, java.io.OutputStream os )
        throws JAXBException {

        checkNotNull( obj, "obj", os, "os" );
        marshal( obj, new StreamResult(os) );
    }

    public void marshal(Object jaxbElement, File output) throws JAXBException {
        checkNotNull(jaxbElement, "jaxbElement", output, "output" );
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(output));
            try {
                marshal( jaxbElement, new StreamResult(os) );
            } finally {
                os.close();
            }
        } catch (IOException e) {
            throw new JAXBException(e);
        }
    }

    public final void marshal( Object obj, java.io.Writer w )
        throws JAXBException {

        checkNotNull( obj, "obj", w, "writer" );
        marshal( obj, new StreamResult(w) );
    }

    public final void marshal( Object obj, org.xml.sax.ContentHandler handler )
        throws JAXBException {

        checkNotNull( obj, "obj", handler, "handler" );
        marshal( obj, new SAXResult(handler) );
    }

    public final void marshal( Object obj, org.w3c.dom.Node node )
        throws JAXBException {

        checkNotNull( obj, "obj", node, "node" );
        marshal( obj, new DOMResult(node) );
    }


    public org.w3c.dom.Node getNode( Object obj ) throws JAXBException {

        checkNotNull( obj, "obj", Boolean.TRUE, "foo" );

        throw new UnsupportedOperationException();
    }


    protected String getEncoding() {
        return encoding;
    }


    protected void setEncoding( String encoding ) {
        this.encoding = encoding;
    }


    protected String getSchemaLocation() {
        return schemaLocation;
    }


    protected void setSchemaLocation( String location ) {
        schemaLocation = location;
    }


    protected String getNoNSSchemaLocation() {
        return noNSSchemaLocation;
    }


    protected void setNoNSSchemaLocation( String location ) {
        noNSSchemaLocation = location;
    }


    protected boolean isFormattedOutput() {
        return formattedOutput;
    }


    protected void setFormattedOutput( boolean v ) {
        formattedOutput = v;
    }



    protected boolean isFragment() {
        return fragment;
    }


    protected void setFragment( boolean v ) {
        fragment = v;
    }


    static String[] aliases = {
        "UTF-8", "UTF8",
        "UTF-16", "Unicode",
        "UTF-16BE", "UnicodeBigUnmarked",
        "UTF-16LE", "UnicodeLittleUnmarked",
        "US-ASCII", "ASCII",
        "TIS-620", "TIS620",

        "ISO-10646-UCS-2", "Unicode",

        "EBCDIC-CP-US", "cp037",
        "EBCDIC-CP-CA", "cp037",
        "EBCDIC-CP-NL", "cp037",
        "EBCDIC-CP-WT", "cp037",

        "EBCDIC-CP-DK", "cp277",
        "EBCDIC-CP-NO", "cp277",
        "EBCDIC-CP-FI", "cp278",
        "EBCDIC-CP-SE", "cp278",

        "EBCDIC-CP-IT", "cp280",
        "EBCDIC-CP-ES", "cp284",
        "EBCDIC-CP-GB", "cp285",
        "EBCDIC-CP-FR", "cp297",

        "EBCDIC-CP-AR1", "cp420",
        "EBCDIC-CP-HE", "cp424",
        "EBCDIC-CP-BE", "cp500",
        "EBCDIC-CP-CH", "cp500",

        "EBCDIC-CP-ROECE", "cp870",
        "EBCDIC-CP-YU", "cp870",
        "EBCDIC-CP-IS", "cp871",
        "EBCDIC-CP-AR2", "cp918",

        };


    protected String getJavaEncoding( String encoding ) throws UnsupportedEncodingException {
        try {
            "1".getBytes(encoding);
            return encoding;
        } catch( UnsupportedEncodingException e ) {
            for( int i=0; i<aliases.length; i+=2 ) {
                if(encoding.equals(aliases[i])) {
                    "1".getBytes(aliases[i+1]);
                    return aliases[i+1];
                }
            }

            throw new UnsupportedEncodingException(encoding);
        }

    }


    public void setProperty( String name, Object value )
        throws PropertyException {

        if( name == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "name" ) );
        }

        if( JAXB_ENCODING.equals(name) ) {
            checkString( name, value );
            setEncoding( (String)value );
            return;
        }
        if( JAXB_FORMATTED_OUTPUT.equals(name) ) {
            checkBoolean( name, value );
            setFormattedOutput((Boolean) value );
            return;
        }
        if( JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(name) ) {
            checkString( name, value );
            setNoNSSchemaLocation( (String)value );
            return;
        }
        if( JAXB_SCHEMA_LOCATION.equals(name) ) {
            checkString( name, value );
            setSchemaLocation( (String)value );
            return;
        }
        if( JAXB_FRAGMENT.equals(name) )  {
            checkBoolean(name, value);
            setFragment((Boolean) value );
            return;
        }

        throw new PropertyException(name, value);
    }


    public Object getProperty( String name )
        throws PropertyException {

        if( name == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, "name" ) );
        }

        if( JAXB_ENCODING.equals(name) )
            return getEncoding();
        if( JAXB_FORMATTED_OUTPUT.equals(name) )
            return isFormattedOutput()?Boolean.TRUE:Boolean.FALSE;
        if( JAXB_NO_NAMESPACE_SCHEMA_LOCATION.equals(name) )
            return getNoNSSchemaLocation();
        if( JAXB_SCHEMA_LOCATION.equals(name) )
            return getSchemaLocation();
        if( JAXB_FRAGMENT.equals(name) )
            return isFragment()?Boolean.TRUE:Boolean.FALSE;

        throw new PropertyException(name);
    }

    public ValidationEventHandler getEventHandler() throws JAXBException {
        return eventHandler;
    }


    public void setEventHandler(ValidationEventHandler handler)
        throws JAXBException {

        if( handler == null ) {
            eventHandler = new DefaultValidationEventHandler();
        } else {
            eventHandler = handler;
        }
    }





    private void checkBoolean( String name, Object value ) throws PropertyException {
        if(!(value instanceof Boolean))
            throw new PropertyException(
                Messages.format( Messages.MUST_BE_BOOLEAN, name ) );
    }


    private void checkString( String name, Object value ) throws PropertyException {
        if(!(value instanceof String))
            throw new PropertyException(
                Messages.format( Messages.MUST_BE_STRING, name ) );
    }


    private void checkNotNull( Object o1, String o1Name,
                               Object o2, String o2Name ) {

        if( o1 == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, o1Name ) );
        }
        if( o2 == null ) {
            throw new IllegalArgumentException(
                Messages.format( Messages.MUST_NOT_BE_NULL, o2Name ) );
        }
    }

    public void marshal(Object obj, XMLEventWriter writer)
        throws JAXBException {

        throw new UnsupportedOperationException();
    }

    public void marshal(Object obj, XMLStreamWriter writer)
        throws JAXBException {

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

    public void setAttachmentMarshaller(AttachmentMarshaller am) {
        throw new UnsupportedOperationException();
    }

    public AttachmentMarshaller getAttachmentMarshaller() {
        throw new UnsupportedOperationException();
    }

    public void setListener(Listener listener) {
        throw new UnsupportedOperationException();
    }

    public Listener getListener() {
        throw new UnsupportedOperationException();
    }
}
