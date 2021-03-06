

package javax.xml.soap;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import javax.activation.DataHandler;



public abstract class AttachmentPart {

    public abstract int getSize() throws SOAPException;


    public abstract void clearContent();


    public abstract Object getContent() throws SOAPException;


    public abstract InputStream getRawContent() throws SOAPException;


    public abstract byte[] getRawContentBytes() throws SOAPException;


    public abstract InputStream getBase64Content() throws SOAPException;


    public abstract void setContent(Object object, String contentType);


    public abstract void setRawContent(InputStream content, String contentType) throws SOAPException;


    public abstract void setRawContentBytes(
        byte[] content, int offset, int len,  String contentType)
        throws SOAPException;



    public abstract void setBase64Content(
        InputStream content, String contentType) throws SOAPException;



    public abstract DataHandler getDataHandler()
        throws SOAPException;


    public abstract void setDataHandler(DataHandler dataHandler);



    public String getContentId() {
        String[] values = getMimeHeader("Content-ID");
        if (values != null && values.length > 0)
            return values[0];
        return null;
    }


    public String getContentLocation() {
        String[] values = getMimeHeader("Content-Location");
        if (values != null && values.length > 0)
            return values[0];
        return null;
    }


    public String getContentType() {
        String[] values = getMimeHeader("Content-Type");
        if (values != null && values.length > 0)
            return values[0];
        return null;
    }


    public void setContentId(String contentId)
    {
        setMimeHeader("Content-ID", contentId);
    }



    public void setContentLocation(String contentLocation)
    {
        setMimeHeader("Content-Location", contentLocation);
    }


    public void setContentType(String contentType)
    {
        setMimeHeader("Content-Type", contentType);
    }


    public abstract void removeMimeHeader(String header);


    public abstract void removeAllMimeHeaders();



    public abstract String[] getMimeHeader(String name);



    public abstract void setMimeHeader(String name, String value);



    public abstract void addMimeHeader(String name, String value);


    public abstract Iterator getAllMimeHeaders();


    public abstract Iterator getMatchingMimeHeaders(String[] names);


    public abstract Iterator getNonMatchingMimeHeaders(String[] names);
}
