



package org.w3c.dom.ls;

import org.w3c.dom.Document;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;


public interface LSParser {

    public DOMConfiguration getDomConfig();


    public LSParserFilter getFilter();

    public void setFilter(LSParserFilter filter);


    public boolean getAsync();


    public boolean getBusy();


    public Document parse(LSInput input)
                          throws DOMException, LSException;


    public Document parseURI(String uri)
                             throws DOMException, LSException;

    public static final short ACTION_APPEND_AS_CHILDREN = 1;

    public static final short ACTION_REPLACE_CHILDREN   = 2;

    public static final short ACTION_INSERT_BEFORE      = 3;

    public static final short ACTION_INSERT_AFTER       = 4;

    public static final short ACTION_REPLACE            = 5;


    public Node parseWithContext(LSInput input,
                                 Node contextArg,
                                 short action)
                                 throws DOMException, LSException;


    public void abort();

}
