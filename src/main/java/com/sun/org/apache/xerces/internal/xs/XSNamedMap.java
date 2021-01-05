


package com.sun.org.apache.xerces.internal.xs;

import java.util.Map;


public interface XSNamedMap extends Map {

    public int getLength();


    public XSObject item(int index);


    public XSObject itemByName(String namespace,
                               String localName);

}
