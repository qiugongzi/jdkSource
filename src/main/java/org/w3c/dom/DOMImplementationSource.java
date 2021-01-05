



package org.w3c.dom;


public interface DOMImplementationSource {

    public DOMImplementation getDOMImplementation(String features);


    public DOMImplementationList getDOMImplementationList(String features);

}
