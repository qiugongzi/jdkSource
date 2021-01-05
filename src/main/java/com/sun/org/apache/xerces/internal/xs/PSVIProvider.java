


package com.sun.org.apache.xerces.internal.xs;


public interface PSVIProvider {

    public ElementPSVI getElementPSVI();


    public AttributePSVI getAttributePSVI(int index);


    public AttributePSVI getAttributePSVIByName(String uri,
                                                String localname);

}
