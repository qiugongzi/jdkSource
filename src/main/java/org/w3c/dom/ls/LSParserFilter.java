



package org.w3c.dom.ls;

import org.w3c.dom.Node;
import org.w3c.dom.Element;


public interface LSParserFilter {
    public static final short FILTER_ACCEPT             = 1;

    public static final short FILTER_REJECT             = 2;

    public static final short FILTER_SKIP               = 3;

    public static final short FILTER_INTERRUPT          = 4;


    public short startElement(Element elementArg);


    public short acceptNode(Node nodeArg);


    public int getWhatToShow();

}
