

package javax.xml.bind;



public class TypeConstraintException extends java.lang.RuntimeException {


    private String errorCode;


    private volatile Throwable linkedException;

    static final long serialVersionUID = -3059799699420143848L;


    public TypeConstraintException(String message) {
        this( message, null, null );
    }


    public TypeConstraintException(String message, String errorCode) {
        this( message, errorCode, null );
    }


    public TypeConstraintException(Throwable exception) {
        this( null, null, exception );
    }


    public TypeConstraintException(String message, Throwable exception) {
        this( message, null, exception );
    }


    public TypeConstraintException(String message, String errorCode, Throwable exception) {
        super( message );
        this.errorCode = errorCode;
        this.linkedException = exception;
    }


    public String getErrorCode() {
        return this.errorCode;
    }


    public Throwable getLinkedException() {
        return linkedException;
    }


    public void setLinkedException( Throwable exception ) {
        this.linkedException = exception;
    }


    public String toString() {
        return linkedException == null ?
            super.toString() :
            super.toString() + "\n - with linked exception:\n[" +
                                linkedException.toString()+ "]";
    }


    public void printStackTrace( java.io.PrintStream s ) {
        if( linkedException != null ) {
          linkedException.printStackTrace(s);
          s.println("--------------- linked to ------------------");
        }

        super.printStackTrace(s);
    }


    public void printStackTrace() {
        printStackTrace(System.err);
    }

}
