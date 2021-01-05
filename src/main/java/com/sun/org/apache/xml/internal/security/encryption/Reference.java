

package com.sun.org.apache.xml.internal.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;


public interface Reference {

    String getType();


    String getURI();


    void setURI(String uri);


    Iterator<Element> getElementRetrievalInformation();


    void addElementRetrievalInformation(Element info);


    void removeElementRetrievalInformation(Element info);
}
