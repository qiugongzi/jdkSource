



package org.w3c.dom.ls;

import org.w3c.dom.DOMException;


public interface DOMImplementationLS {
    public static final short MODE_SYNCHRONOUS          = 1;

    public static final short MODE_ASYNCHRONOUS         = 2;


    public LSParser createLSParser(short mode,
                                   String schemaType)
                                   throws DOMException;


    public LSSerializer createLSSerializer();


    public LSInput createLSInput();


    public LSOutput createLSOutput();

}
