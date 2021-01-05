



package org.w3c.dom.ls;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;


public interface LSSerializer {

    public DOMConfiguration getDomConfig();


    public String getNewLine();

    public void setNewLine(String newLine);


    public LSSerializerFilter getFilter();

    public void setFilter(LSSerializerFilter filter);


    public boolean write(Node nodeArg,
                         LSOutput destination)
                         throws LSException;


    public boolean writeToURI(Node nodeArg,
                              String uri)
                              throws LSException;


    public String writeToString(Node nodeArg)
                                throws DOMException, LSException;

}
