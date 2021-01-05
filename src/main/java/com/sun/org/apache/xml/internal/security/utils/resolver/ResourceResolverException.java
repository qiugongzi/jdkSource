

package com.sun.org.apache.xml.internal.security.utils.resolver;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Attr;


public class ResourceResolverException extends XMLSecurityException {

    private static final long serialVersionUID = 1L;

    private Attr uri = null;

    private String baseURI = null;


    public ResourceResolverException(String msgID, Attr uri, String baseURI) {
        super(msgID);

        this.uri = uri;
        this.baseURI = baseURI;
    }


    public ResourceResolverException(String msgID, Object exArgs[], Attr uri,
                                     String baseURI) {
        super(msgID, exArgs);

        this.uri = uri;
        this.baseURI = baseURI;
    }


    public ResourceResolverException(String msgID, Exception originalException,
                                     Attr uri, String baseURI) {
        super(msgID, originalException);

        this.uri = uri;
        this.baseURI = baseURI;
    }


    public ResourceResolverException(String msgID, Object exArgs[],
                                     Exception originalException, Attr uri,
                                     String baseURI) {
        super(msgID, exArgs, originalException);

        this.uri = uri;
        this.baseURI = baseURI;
    }


    public void setURI(Attr uri) {
        this.uri = uri;
    }


    public Attr getURI() {
        return this.uri;
    }


    public void setbaseURI(String baseURI) {
        this.baseURI = baseURI;
    }


    public String getbaseURI() {
        return this.baseURI;
    }

}
