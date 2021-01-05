



package com.sun.org.apache.xml.internal.security.signature.reference;

import java.io.InputStream;


public class ReferenceOctetStreamData implements ReferenceData {
    private InputStream octetStream;
    private String uri;
    private String mimeType;


    public ReferenceOctetStreamData(InputStream octetStream) {
        if (octetStream == null) {
            throw new NullPointerException("octetStream is null");
        }
        this.octetStream = octetStream;
    }


    public ReferenceOctetStreamData(InputStream octetStream, String uri,
        String mimeType) {
        if (octetStream == null) {
            throw new NullPointerException("octetStream is null");
        }
        this.octetStream = octetStream;
        this.uri = uri;
        this.mimeType = mimeType;
    }


    public InputStream getOctetStream() {
        return octetStream;
    }


    public String getURI() {
        return uri;
    }


    public String getMimeType() {
        return mimeType;
    }

}
