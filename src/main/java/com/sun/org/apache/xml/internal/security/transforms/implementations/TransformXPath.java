

package com.sun.org.apache.xml.internal.security.transforms.implementations;

import java.io.OutputStream;

import javax.xml.transform.TransformerException;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityRuntimeException;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.XPathAPI;
import com.sun.org.apache.xml.internal.security.utils.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class TransformXPath extends TransformSpi {


    public static final String implementedTransformURI = Transforms.TRANSFORM_XPATH;


    protected String engineGetURI() {
        return implementedTransformURI;
    }


    protected XMLSignatureInput enginePerformTransform(
        XMLSignatureInput input, OutputStream os, Transform transformObject
    ) throws TransformationException {
        try {

            Element xpathElement =
                XMLUtils.selectDsNode(
                    transformObject.getElement().getFirstChild(), Constants._TAG_XPATH, 0);

            if (xpathElement == null) {
                Object exArgs[] = { "ds:XPath", "Transform" };

                throw new TransformationException("xml.WrongContent", exArgs);
            }
            Node xpathnode = xpathElement.getChildNodes().item(0);
            String str = XMLUtils.getStrFromNode(xpathnode);
            input.setNeedsToBeExpanded(needsCircumvent(str));
            if (xpathnode == null) {
                throw new DOMException(
                    DOMException.HIERARCHY_REQUEST_ERR, "Text must be in ds:Xpath"
                );
            }

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPathAPI xpathAPIInstance = xpathFactory.newXPathAPI();
            input.addNodeFilter(new XPathNodeFilter(xpathElement, xpathnode, str, xpathAPIInstance));
            input.setNodeSet(true);
            return input;
        } catch (DOMException ex) {
            throw new TransformationException("empty", ex);
        }
    }


    private boolean needsCircumvent(String str) {
        return (str.indexOf("namespace") != -1) || (str.indexOf("name()") != -1);
    }

    static class XPathNodeFilter implements NodeFilter {

        XPathAPI xPathAPI;
        Node xpathnode;
        Element xpathElement;
        String str;

        XPathNodeFilter(Element xpathElement, Node xpathnode, String str, XPathAPI xPathAPI) {
            this.xpathnode = xpathnode;
            this.str = str;
            this.xpathElement = xpathElement;
            this.xPathAPI = xPathAPI;
        }


        public int isNodeInclude(Node currentNode) {
            try {
                boolean include = xPathAPI.evaluate(currentNode, xpathnode, str, xpathElement);
                if (include) {
                    return 1;
                }
                return 0;
            } catch (TransformerException e) {
                Object[] eArgs = {currentNode};
                throw new XMLSecurityRuntimeException("signature.Transform.node", eArgs, e);
            } catch (Exception e) {
                Object[] eArgs = {currentNode, Short.valueOf(currentNode.getNodeType())};
                throw new XMLSecurityRuntimeException("signature.Transform.nodeAndType",eArgs, e);
            }
        }

        public int isNodeIncludeDO(Node n, int level) {
            return isNodeInclude(n);
        }

    }
}
