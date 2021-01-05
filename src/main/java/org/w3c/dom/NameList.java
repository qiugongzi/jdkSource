



package org.w3c.dom;


public interface NameList {

    public String getName(int index);


    public String getNamespaceURI(int index);


    public int getLength();


    public boolean contains(String str);


    public boolean containsNS(String namespaceURI,
                              String name);

}
