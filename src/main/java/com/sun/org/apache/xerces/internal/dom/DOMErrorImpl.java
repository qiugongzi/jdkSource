


package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMError;
import org.w3c.dom.DOMLocator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;




public class DOMErrorImpl implements DOMError {

    public short fSeverity = DOMError.SEVERITY_WARNING;
    public String fMessage = null;
    public DOMLocatorImpl fLocator = new DOMLocatorImpl();
    public Exception fException = null;
    public String fType;
    public Object fRelatedData;



    public DOMErrorImpl () {
    }


    public DOMErrorImpl (short severity, XMLParseException exception) {
        fSeverity = severity;
        fException = exception;
        fLocator = createDOMLocator (exception);
    }



    public short getSeverity() {
        return fSeverity;
    }



    public String getMessage() {
        return fMessage;
    }



    public DOMLocator getLocation() {
        return fLocator;
    }

    private DOMLocatorImpl createDOMLocator(XMLParseException exception) {
        return new DOMLocatorImpl(exception.getLineNumber(),
                                  exception.getColumnNumber(),
                                  exception.getCharacterOffset(),
                                  exception.getExpandedSystemId());
    } public Object getRelatedException(){
        return fException;
    }

    public void reset(){
        fSeverity = DOMError.SEVERITY_WARNING;
        fException = null;
    }

    public String getType(){
        return fType;
    }

    public Object getRelatedData(){
        return fRelatedData;
    }


}