package com.sun.org.apache.xerces.internal.xinclude;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;



public interface XPointerSchema extends XMLComponent, XMLDocumentFilter{


    public String getXPointerSchemaPointer();

    public boolean isSubResourceIndentified();

    public void reset();

}
