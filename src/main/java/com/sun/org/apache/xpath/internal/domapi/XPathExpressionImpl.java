




package com.sun.org.apache.xpath.internal.domapi;

import javax.xml.transform.TransformerException;

import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathExpression;
import org.w3c.dom.xpath.XPathNamespace;


class XPathExpressionImpl implements XPathExpression {


  final private XPath m_xpath;


  final private Document m_doc;


    XPathExpressionImpl(XPath xpath, Document doc) {
        m_xpath = xpath;
        m_doc = doc;
    }


    public Object evaluate(
        Node contextNode,
        short type,
        Object result)
        throws XPathException, DOMException {

        if (m_doc != null) {

            if ((contextNode != m_doc) && (!contextNode.getOwnerDocument().equals(m_doc))) {
                String fmsg = XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_WRONG_DOCUMENT, null);
                throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, fmsg);
            }

            short nodeType = contextNode.getNodeType();
            if ((nodeType != Document.DOCUMENT_NODE) &&
                (nodeType != Document.ELEMENT_NODE) &&
                (nodeType != Document.ATTRIBUTE_NODE) &&
                (nodeType != Document.TEXT_NODE) &&
                (nodeType != Document.CDATA_SECTION_NODE) &&
                (nodeType != Document.COMMENT_NODE) &&
                (nodeType != Document.PROCESSING_INSTRUCTION_NODE) &&
                (nodeType != XPathNamespace.XPATH_NAMESPACE_NODE)) {
                    String fmsg = XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_WRONG_NODETYPE, null);
                    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, fmsg);
            }
        }

        if (!XPathResultImpl.isValidType(type)) {
            String fmsg = XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_INVALID_XPATH_TYPE, new Object[] {new Integer(type)});
            throw new XPathException(XPathException.TYPE_ERR,fmsg); }

        XPathContext xpathSupport = new XPathContext();

        if (null != m_doc) {
            xpathSupport.getDTMHandleFromNode(m_doc);
        }

        XObject xobj = null;
        try {
            xobj = m_xpath.execute(xpathSupport, contextNode, null);
        } catch (TransformerException te) {
            throw new XPathException(XPathException.INVALID_EXPRESSION_ERR,te.getMessageAndLocation());
        }

        return new XPathResultImpl(type,xobj,contextNode, m_xpath);
    }

}
