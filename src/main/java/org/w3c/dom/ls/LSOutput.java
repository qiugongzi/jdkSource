



package org.w3c.dom.ls;


public interface LSOutput {

    public java.io.Writer getCharacterStream();

    public void setCharacterStream(java.io.Writer characterStream);


    public java.io.OutputStream getByteStream();

    public void setByteStream(java.io.OutputStream byteStream);


    public String getSystemId();

    public void setSystemId(String systemId);


    public String getEncoding();

    public void setEncoding(String encoding);

}
