


package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMLocator;
import org.w3c.dom.Node;




public class DOMLocatorImpl implements DOMLocator {

    public int fColumnNumber = -1;


   public int fLineNumber = -1;


   public Node fRelatedNode = null;


   public String fUri = null;


   public int fByteOffset = -1;


   public int fUtf16Offset = -1;

   public DOMLocatorImpl(){
   }

   public DOMLocatorImpl (int lineNumber, int columnNumber, String uri ){
        fLineNumber = lineNumber ;
        fColumnNumber = columnNumber ;
        fUri = uri;
   } public DOMLocatorImpl (int lineNumber, int columnNumber, int utf16Offset, String uri ){
        fLineNumber = lineNumber ;
        fColumnNumber = columnNumber ;
        fUri = uri;
        fUtf16Offset = utf16Offset;
   } public DOMLocatorImpl (int lineNumber, int columnNumber, int byteoffset, Node relatedData, String uri ){
        fLineNumber = lineNumber ;
        fColumnNumber = columnNumber ;
        fByteOffset = byteoffset ;
        fRelatedNode = relatedData ;
        fUri = uri;
   } public DOMLocatorImpl (int lineNumber, int columnNumber, int byteoffset, Node relatedData, String uri, int utf16Offset ){
        fLineNumber = lineNumber ;
        fColumnNumber = columnNumber ;
        fByteOffset = byteoffset ;
        fRelatedNode = relatedData ;
        fUri = uri;
        fUtf16Offset = utf16Offset;
   } public int getLineNumber(){
        return fLineNumber;
   }


  public int getColumnNumber(){
        return fColumnNumber;
  }



  public String getUri(){
        return fUri;
  }


  public Node getRelatedNode(){
    return fRelatedNode;
  }



  public int getByteOffset(){
        return fByteOffset;
  }


  public int getUtf16Offset(){
        return fUtf16Offset;
  }

}