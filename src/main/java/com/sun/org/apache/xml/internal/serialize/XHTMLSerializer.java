



package com.sun.org.apache.xml.internal.serialize;


import java.io.OutputStream;
import java.io.Writer;



public class XHTMLSerializer
    extends HTMLSerializer
{



    public XHTMLSerializer()
    {
        super( true, new OutputFormat( Method.XHTML, null, false ) );
    }



    public XHTMLSerializer( OutputFormat format )
    {
        super( true, format != null ? format : new OutputFormat( Method.XHTML, null, false ) );
    }



    public XHTMLSerializer( Writer writer, OutputFormat format )
    {
        super( true, format != null ? format : new OutputFormat( Method.XHTML, null, false ) );
        setOutputCharStream( writer );
    }



    public XHTMLSerializer( OutputStream output, OutputFormat format )
    {
        super( true, format != null ? format : new OutputFormat( Method.XHTML, null, false ) );
        setOutputByteStream( output );
    }


    public void setOutputFormat( OutputFormat format )
    {
        super.setOutputFormat( format != null ? format : new OutputFormat( Method.XHTML, null, false ) );
    }


}
