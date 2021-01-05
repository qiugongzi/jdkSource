

package javax.xml.bind;


public class UnmarshalException extends JAXBException {


    public UnmarshalException( String message ) {
        this( message, null, null );
    }


    public UnmarshalException( String message, String errorCode ) {
        this( message, errorCode, null );
    }


    public UnmarshalException( Throwable exception ) {
        this( null, null, exception );
    }


    public UnmarshalException( String message, Throwable exception ) {
        this( message, null, exception );
    }


    public UnmarshalException( String message, String errorCode, Throwable exception ) {
        super( message, errorCode, exception );
    }

}
