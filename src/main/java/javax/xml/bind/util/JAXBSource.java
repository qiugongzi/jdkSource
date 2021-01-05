

package javax.xml.bind.util;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.XMLFilter;


public class JAXBSource extends SAXSource {


    public JAXBSource( JAXBContext context, Object contentObject )
        throws JAXBException {

        this(
            ( context == null ) ?
                assertionFailed( Messages.format( Messages.SOURCE_NULL_CONTEXT ) ) :
                context.createMarshaller(),

            ( contentObject == null ) ?
                assertionFailed( Messages.format( Messages.SOURCE_NULL_CONTENT ) ) :
                contentObject);
    }


    public JAXBSource( Marshaller marshaller, Object contentObject )
        throws JAXBException {

        if( marshaller == null )
            throw new JAXBException(
                Messages.format( Messages.SOURCE_NULL_MARSHALLER ) );

        if( contentObject == null )
            throw new JAXBException(
                Messages.format( Messages.SOURCE_NULL_CONTENT ) );

        this.marshaller = marshaller;
        this.contentObject = contentObject;

        super.setXMLReader(pseudoParser);
        super.setInputSource(new InputSource());
    }

    private final Marshaller marshaller;
    private final Object contentObject;

    private final XMLReader pseudoParser = new XMLReader() {
        public boolean getFeature(String name) throws SAXNotRecognizedException {
            if(name.equals("http:return true;
            if(name.equals("http:return false;
            throw new SAXNotRecognizedException(name);
        }

        public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
            if(name.equals("http:return;
            if(name.equals("http:return;
            throw new SAXNotRecognizedException(name);
        }

        public Object getProperty(String name) throws SAXNotRecognizedException {
            if( "http:return lexicalHandler;
            }
            throw new SAXNotRecognizedException(name);
        }

        public void setProperty(String name, Object value) throws SAXNotRecognizedException {
            if( "http:this.lexicalHandler = (LexicalHandler)value;
                return;
            }
            throw new SAXNotRecognizedException(name);
        }

        private LexicalHandler lexicalHandler;

        private EntityResolver entityResolver;
        public void setEntityResolver(EntityResolver resolver) {
            this.entityResolver = resolver;
        }
        public EntityResolver getEntityResolver() {
            return entityResolver;
        }

        private DTDHandler dtdHandler;
        public void setDTDHandler(DTDHandler handler) {
            this.dtdHandler = handler;
        }
        public DTDHandler getDTDHandler() {
            return dtdHandler;
        }

        private XMLFilter repeater = new XMLFilterImpl();

        public void setContentHandler(ContentHandler handler) {
            repeater.setContentHandler(handler);
        }
        public ContentHandler getContentHandler() {
            return repeater.getContentHandler();
        }

        private ErrorHandler errorHandler;
        public void setErrorHandler(ErrorHandler handler) {
            this.errorHandler = handler;
        }
        public ErrorHandler getErrorHandler() {
            return errorHandler;
        }

        public void parse(InputSource input) throws SAXException {
            parse();
        }

        public void parse(String systemId) throws SAXException {
            parse();
        }

        public void parse() throws SAXException {
            try {
                marshaller.marshal( contentObject, (XMLFilterImpl)repeater );
            } catch( JAXBException e ) {
                SAXParseException se =
                    new SAXParseException( e.getMessage(),
                        null, null, -1, -1, e );

                if(errorHandler!=null)
                    errorHandler.fatalError(se);

                throw se;
            }
        }
    };


    private static Marshaller assertionFailed( String message )
        throws JAXBException {

        throw new JAXBException( message );
    }
}
