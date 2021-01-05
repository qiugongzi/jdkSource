



package com.sun.org.apache.xml.internal.dtm.ref;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public interface IncrementalSAXSource
{
  public void setContentHandler(ContentHandler handler);


  public void setLexicalHandler(org.xml.sax.ext.LexicalHandler handler);


  public void setDTDHandler(org.xml.sax.DTDHandler handler);

  public Object deliverMoreNodes (boolean parsemore);

  public void startParse(InputSource source) throws SAXException;

}