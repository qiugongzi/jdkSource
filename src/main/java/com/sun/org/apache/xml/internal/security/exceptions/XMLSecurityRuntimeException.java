

package com.sun.org.apache.xml.internal.security.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;

import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.I18n;


public class XMLSecurityRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;


    protected String msgID;


    public XMLSecurityRuntimeException() {
        super("Missing message string");

        this.msgID = null;
    }


    public XMLSecurityRuntimeException(String msgID) {
        super(I18n.getExceptionMessage(msgID));

        this.msgID = msgID;
    }


    public XMLSecurityRuntimeException(String msgID, Object exArgs[]) {
        super(MessageFormat.format(I18n.getExceptionMessage(msgID), exArgs));

        this.msgID = msgID;
    }


    public XMLSecurityRuntimeException(Exception originalException) {
        super("Missing message ID to locate message string in resource bundle \""
              + Constants.exceptionMessagesResourceBundleBase
              + "\". Original Exception was a "
              + originalException.getClass().getName() + " and message "
              + originalException.getMessage(), originalException);
    }


    public XMLSecurityRuntimeException(String msgID, Exception originalException) {
        super(I18n.getExceptionMessage(msgID, originalException), originalException);

        this.msgID = msgID;
    }


    public XMLSecurityRuntimeException(String msgID, Object exArgs[], Exception originalException) {
        super(MessageFormat.format(I18n.getExceptionMessage(msgID), exArgs));

        this.msgID = msgID;
    }


    public String getMsgID() {
        if (msgID == null) {
            return "Missing message ID";
        }
        return msgID;
    }


    public String toString() {
        String s = this.getClass().getName();
        String message = super.getLocalizedMessage();

        if (message != null) {
            message = s + ": " + message;
        } else {
            message = s;
        }

        if (this.getCause() != null) {
            message = message + "\nOriginal Exception was " + this.getCause().toString();
        }

        return message;
    }


    public void printStackTrace() {
        synchronized (System.err) {
            super.printStackTrace(System.err);
        }
    }


    public void printStackTrace(PrintWriter printwriter) {
        super.printStackTrace(printwriter);
    }


    public void printStackTrace(PrintStream printstream) {
        super.printStackTrace(printstream);
    }


    public Exception getOriginalException() {
        if (this.getCause() instanceof Exception) {
            return (Exception)this.getCause();
        }
        return null;
    }

}
