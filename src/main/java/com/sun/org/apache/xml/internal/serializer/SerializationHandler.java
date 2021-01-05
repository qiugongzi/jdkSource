


package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;

import javax.xml.transform.Transformer;

import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;


public interface SerializationHandler
    extends
        ExtendedContentHandler,
        ExtendedLexicalHandler,
        XSLOutputAttributes,
        DeclHandler,
        org.xml.sax.DTDHandler,
        ErrorHandler,
        DOMSerializer,
        Serializer
{

    public void setContentHandler(ContentHandler ch);

    public void close();


    public void serialize(Node node) throws IOException;

    public boolean setEscaping(boolean escape) throws SAXException;


    public void setIndentAmount(int spaces);


    public void setTransformer(Transformer transformer);


    public Transformer getTransformer();


    public void setNamespaceMappings(NamespaceMappings mappings);


    public void flushPending() throws SAXException;


    public void setDTDEntityExpansion(boolean expand);


    public void setIsStandalone(boolean b);

}
