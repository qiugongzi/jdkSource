

package javax.xml.transform.stream;

import javax.xml.transform.Result;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;


public class StreamResult implements Result {


    public static final String FEATURE =
        "http:public StreamResult() {
    }


    public StreamResult(OutputStream outputStream) {
        setOutputStream(outputStream);
    }


    public StreamResult(Writer writer) {
        setWriter(writer);
    }


    public StreamResult(String systemId) {
        this.systemId = systemId;
    }


    public StreamResult(File f) {
        setSystemId(f.toURI().toASCIIString());
    }


    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }


    public OutputStream getOutputStream() {
        return outputStream;
    }


    public void setWriter(Writer writer) {
        this.writer = writer;
    }


    public Writer getWriter() {
        return writer;
    }


    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }


    public void setSystemId(File f) {
        this.systemId = f.toURI().toASCIIString();
    }


    public String getSystemId() {
        return systemId;
    }

    private String systemId;


    private OutputStream outputStream;


    private Writer writer;
}
