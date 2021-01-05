

package javax.xml.soap;

import java.util.Iterator;

import javax.xml.transform.Source;


public abstract class SOAPPart implements org.w3c.dom.Document, Node {


    public abstract SOAPEnvelope getEnvelope() throws SOAPException;


    public String getContentId() {
        String[] values = getMimeHeader("Content-Id");
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


    public void setContentId(String contentId)
    {
        setMimeHeader("Content-Id", contentId);
    }

    public void setContentLocation(String contentLocation)
    {
        setMimeHeader("Content-Location", contentLocation);
    }

    public abstract void removeMimeHeader(String header);


    public abstract void removeAllMimeHeaders();


    public abstract String[] getMimeHeader(String name);


    public abstract void setMimeHeader(String name, String value);


    public abstract void addMimeHeader(String name, String value);


    public abstract Iterator getAllMimeHeaders();


    public abstract Iterator getMatchingMimeHeaders(String[] names);


    public abstract Iterator getNonMatchingMimeHeaders(String[] names);


    public abstract void setContent(Source source) throws SOAPException;


    public abstract Source getContent() throws SOAPException;
}
