

package org.xml.sax;


public class SAXException extends Exception {



    public SAXException ()
    {
        super();
        this.exception = null;
    }



    public SAXException (String message) {
        super(message);
        this.exception = null;
    }



    public SAXException (Exception e)
    {
        super();
        this.exception = e;
    }



    public SAXException (String message, Exception e)
    {
        super(message);
        this.exception = e;
    }



    public String getMessage ()
    {
        String message = super.getMessage();

        if (message == null && exception != null) {
            return exception.getMessage();
        } else {
            return message;
        }
    }



    public Exception getException ()
    {
        return exception;
    }


    public Throwable getCause() {
        return exception;
    }


    public String toString ()
    {
        if (exception != null) {
            return super.toString() + "\n" + exception.toString();
        } else {
            return super.toString();
        }
    }



    private Exception exception;

    static final long serialVersionUID = 583241635256073760L;
}

