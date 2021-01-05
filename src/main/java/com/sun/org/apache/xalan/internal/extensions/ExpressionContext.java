


package com.sun.org.apache.xalan.internal.extensions;

import javax.xml.transform.ErrorListener;

import com.sun.org.apache.xpath.internal.objects.XObject;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;


public interface ExpressionContext
{


  public Node getContextNode();


  public NodeIterator getContextNodes();


  public ErrorListener getErrorListener();


  public double toNumber(Node n);


  public String toString(Node n);


  public XObject getVariableOrParam(com.sun.org.apache.xml.internal.utils.QName qname)
            throws javax.xml.transform.TransformerException;


  public com.sun.org.apache.xpath.internal.XPathContext getXPathContext()
            throws javax.xml.transform.TransformerException;

}
