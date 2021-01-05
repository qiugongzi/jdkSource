


package com.sun.org.apache.xerces.internal.xni;


public class XNIException
    extends RuntimeException {


    static final long serialVersionUID = 9019819772686063775L;

    private Exception fException;

    public XNIException(String message) {
        super(message);
    } public XNIException(Exception exception) {
        super(exception.getMessage());
        fException = exception;
    } public XNIException(String message, Exception exception) {
        super(message);
        fException = exception;
    } public Exception getException() {
        return fException;
    }

    public Throwable getCause() {
       return fException;
    }
}
