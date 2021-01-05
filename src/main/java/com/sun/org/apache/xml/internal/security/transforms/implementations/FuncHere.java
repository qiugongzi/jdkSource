

package com.sun.org.apache.xml.internal.security.transforms.implementations;

import javax.xml.transform.TransformerException;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class FuncHere extends Function {


    private static final long serialVersionUID = 1L;


    @Override
    public XObject execute(XPathContext xctxt)
        throws javax.xml.transform.TransformerException {

        Node xpathOwnerNode = (Node) xctxt.getOwnerObject();

        if (xpathOwnerNode == null) {
            return null;
        }

        int xpathOwnerNodeDTM = xctxt.getDTMHandleFromNode(xpathOwnerNode);

        int currentNode = xctxt.getCurrentNode();
        DTM dtm = xctxt.getDTM(currentNode);
        int docContext = dtm.getDocument();

        if (DTM.NULL == docContext) {
            error(xctxt, XPATHErrorResources.ER_CONTEXT_HAS_NO_OWNERDOC, null);
        }

        {
            Document currentDoc =
                XMLUtils.getOwnerDocument(dtm.getNode(currentNode));
            Document xpathOwnerDoc = XMLUtils.getOwnerDocument(xpathOwnerNode);

            if (currentDoc != xpathOwnerDoc) {
                throw new TransformerException(I18n.translate("xpath.funcHere.documentsDiffer"));
            }
        }

        XNodeSet nodes = new XNodeSet(xctxt.getDTMManager());
        NodeSetDTM nodeSet = nodes.mutableNodeset();

        {
            int hereNode = DTM.NULL;

            switch (dtm.getNodeType(xpathOwnerNodeDTM)) {

            case Node.ATTRIBUTE_NODE :
            case Node.PROCESSING_INSTRUCTION_NODE : {
                hereNode = xpathOwnerNodeDTM;

                nodeSet.addNode(hereNode);

                break;
            }
            case Node.TEXT_NODE : {
                hereNode = dtm.getParent(xpathOwnerNodeDTM);

                nodeSet.addNode(hereNode);

                break;
            }
            default :
                break;
            }
        }


        nodeSet.detach();

        return nodes;
    }


    @SuppressWarnings("rawtypes")
    public void fixupVariables(java.util.Vector vars, int globalsSize) {
        }
}
