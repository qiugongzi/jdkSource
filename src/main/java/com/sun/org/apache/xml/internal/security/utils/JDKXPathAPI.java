

package com.sun.org.apache.xml.internal.security.utils;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class JDKXPathAPI implements XPathAPI {

    private XPathFactory xpf;

    private String xpathStr;

    private XPathExpression xpathExpression;


    public NodeList selectNodeList(
        Node contextNode, Node xpathnode, String str, Node namespaceNode
    ) throws TransformerException {
        if (!str.equals(xpathStr) || xpathExpression == null) {
            if (xpf == null) {
                xpf = XPathFactory.newInstance();
                try {
                    xpf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
                } catch (XPathFactoryConfigurationException ex) {
                    throw new TransformerException("empty", ex);
                }
            }
            XPath xpath = xpf.newXPath();
            xpath.setNamespaceContext(new DOMNamespaceContext(namespaceNode));
            xpathStr = str;
            try {
                xpathExpression = xpath.compile(xpathStr);
            } catch (XPathExpressionException ex) {
                throw new TransformerException("empty", ex);
            }
        }
        try {
            return (NodeList)xpathExpression.evaluate(contextNode, XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            throw new TransformerException("empty", ex);
        }
    }


    public boolean evaluate(Node contextNode, Node xpathnode, String str, Node namespaceNode)
        throws TransformerException {
        if (!str.equals(xpathStr) || xpathExpression == null) {
            if (xpf == null) {
                xpf = XPathFactory.newInstance();
                try {
                    xpf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
                } catch (XPathFactoryConfigurationException ex) {
                    throw new TransformerException("empty", ex);
                }
            }
            XPath xpath = xpf.newXPath();
            xpath.setNamespaceContext(new DOMNamespaceContext(namespaceNode));
            xpathStr = str;
            try {
                xpathExpression = xpath.compile(xpathStr);
            } catch (XPathExpressionException ex) {
                throw new TransformerException("empty", ex);
            }
        }
        try {
            Boolean result = (Boolean)xpathExpression.evaluate(contextNode, XPathConstants.BOOLEAN);
            return result.booleanValue();
        } catch (XPathExpressionException ex) {
            throw new TransformerException("empty", ex);
        }
    }


    public void clear() {
        xpathStr = null;
        xpathExpression = null;
        xpf = null;
    }

}
