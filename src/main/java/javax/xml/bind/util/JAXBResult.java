

package javax.xml.bind.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.transform.sax.SAXResult;


public class JAXBResult extends SAXResult {


    public JAXBResult( JAXBContext context ) throws JAXBException {
        this( ( context == null ) ? assertionFailed() : context.createUnmarshaller() );
    }


    public JAXBResult( Unmarshaller _unmarshaller ) throws JAXBException {
        if( _unmarshaller == null )
            throw new JAXBException(
                Messages.format( Messages.RESULT_NULL_UNMARSHALLER ) );

        this.unmarshallerHandler = _unmarshaller.getUnmarshallerHandler();

        super.setHandler(unmarshallerHandler);
    }


    private final UnmarshallerHandler unmarshallerHandler;


    public Object getResult() throws JAXBException {
        return unmarshallerHandler.getResult();
    }


    private static Unmarshaller assertionFailed() throws JAXBException {
        throw new JAXBException( Messages.format( Messages.RESULT_NULL_CONTEXT ) );
    }
}
