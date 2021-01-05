



package org.w3c.dom.xpath;


import org.w3c.dom.Node;
import org.w3c.dom.DOMException;


public interface XPathExpression {

    public Object evaluate(Node contextNode,
                           short type,
                           Object result)
                           throws XPathException, DOMException;

}