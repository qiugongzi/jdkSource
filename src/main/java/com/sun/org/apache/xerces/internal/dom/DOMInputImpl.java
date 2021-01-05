


package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.ls.LSInput;

import java.io.Reader;
import java.io.InputStream;



public class DOMInputImpl implements LSInput {

        protected String fPublicId = null;
        protected String fSystemId = null;
        protected String fBaseSystemId = null;

        protected InputStream fByteStream = null;
        protected Reader fCharStream    = null;
        protected String fData = null;

        protected String fEncoding = null;

        protected boolean fCertifiedText = false;


     public DOMInputImpl() {}



    public DOMInputImpl(String publicId, String systemId,
                          String baseSystemId) {

                fPublicId = publicId;
                fSystemId = systemId;
                fBaseSystemId = baseSystemId;

    } public DOMInputImpl(String publicId, String systemId,
                          String baseSystemId, InputStream byteStream,
                          String encoding) {

                fPublicId = publicId;
                fSystemId = systemId;
                fBaseSystemId = baseSystemId;
                fByteStream = byteStream;
                fEncoding = encoding;

    } public DOMInputImpl(String publicId, String systemId,
                          String baseSystemId, Reader charStream,
                          String encoding) {

                fPublicId = publicId;
                fSystemId = systemId;
                fBaseSystemId = baseSystemId;
                fCharStream = charStream;
                fEncoding = encoding;

     } public DOMInputImpl(String publicId, String systemId,
                          String baseSystemId, String data,
                          String encoding) {
                fPublicId = publicId;
                fSystemId = systemId;
                fBaseSystemId = baseSystemId;
                fData = data;
                fEncoding = encoding;
     } public InputStream getByteStream(){
        return fByteStream;
    }



     public void setByteStream(InputStream byteStream){
        fByteStream = byteStream;
     }


    public Reader getCharacterStream(){
        return fCharStream;
    }


     public void setCharacterStream(Reader characterStream){
        fCharStream = characterStream;
     }


    public String getStringData(){
        return fData;
    }



     public void setStringData(String stringData){
                fData = stringData;
     }



    public String getEncoding(){
        return fEncoding;
    }


    public void setEncoding(String encoding){
        fEncoding = encoding;
    }


    public String getPublicId(){
        return fPublicId;
    }

    public void setPublicId(String publicId){
        fPublicId = publicId;
    }


    public String getSystemId(){
        return fSystemId;
    }

    public void setSystemId(String systemId){
        fSystemId = systemId;
    }


    public String getBaseURI(){
        return fBaseSystemId;
    }

    public void setBaseURI(String baseURI){
        fBaseSystemId = baseURI;
    }


    public boolean getCertifiedText(){
      return fCertifiedText;
    }



    public void setCertifiedText(boolean certifiedText){
      fCertifiedText = certifiedText;
    }

}