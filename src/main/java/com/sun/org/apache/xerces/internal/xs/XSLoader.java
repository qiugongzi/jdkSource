


package com.sun.org.apache.xerces.internal.xs;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.ls.LSInput;


public interface XSLoader {

    public DOMConfiguration getConfig();


    public XSModel loadURIList(StringList uriList);


    public XSModel loadInputList(LSInputList is);


    public XSModel loadURI(String uri);


    public XSModel load(LSInput is);

}
