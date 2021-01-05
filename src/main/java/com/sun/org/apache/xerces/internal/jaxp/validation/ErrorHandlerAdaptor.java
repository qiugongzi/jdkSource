

package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;


public abstract class ErrorHandlerAdaptor implements XMLErrorHandler
{

    private boolean hadError = false;


    public boolean hadError() { return hadError; }

    public void reset() { hadError = false; }


    protected abstract ErrorHandler getErrorHandler();

    public void fatalError( String domain, String key, XMLParseException e ) {
        try {
            hadError = true;
            getErrorHandler().fatalError( Util.toSAXParseException(e) );
        } catch( SAXException se ) {
            throw new WrappedSAXException(se);
        }
    }

    public void error( String domain, String key, XMLParseException e ) {
        try {
            hadError = true;
            getErrorHandler().error( Util.toSAXParseException(e) );
        } catch( SAXException se ) {
            throw new WrappedSAXException(se);
        }
    }

    public void warning( String domain, String key, XMLParseException e ) {
        try {
            getErrorHandler().warning( Util.toSAXParseException(e) );
        } catch( SAXException se ) {
            throw new WrappedSAXException(se);
        }
    }

}
