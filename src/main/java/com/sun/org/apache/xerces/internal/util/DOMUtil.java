


package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.impl.xs.opti.ElementImpl;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSException;


public class DOMUtil {

    protected DOMUtil() {}

    public static void copyInto(Node src, Node dest) throws DOMException {

        Document factory = dest.getOwnerDocument();
        boolean domimpl = factory instanceof DocumentImpl;

        Node start  = src;
        Node parent = src;
        Node place  = src;

        while (place != null) {

            Node node = null;
            int  type = place.getNodeType();
            switch (type) {
            case Node.CDATA_SECTION_NODE: {
                node = factory.createCDATASection(place.getNodeValue());
                break;
            }
            case Node.COMMENT_NODE: {
                node = factory.createComment(place.getNodeValue());
                break;
            }
            case Node.ELEMENT_NODE: {
                Element element = factory.createElement(place.getNodeName());
                node = element;
                NamedNodeMap attrs  = place.getAttributes();
                int attrCount = attrs.getLength();
                for (int i = 0; i < attrCount; i++) {
                    Attr attr = (Attr)attrs.item(i);
                    String attrName = attr.getNodeName();
                    String attrValue = attr.getNodeValue();
                    element.setAttribute(attrName, attrValue);
                    if (domimpl && !attr.getSpecified()) {
                        ((AttrImpl)element.getAttributeNode(attrName)).setSpecified(false);
                    }
                }
                break;
            }
            case Node.ENTITY_REFERENCE_NODE: {
                node = factory.createEntityReference(place.getNodeName());
                break;
            }
            case Node.PROCESSING_INSTRUCTION_NODE: {
                node = factory.createProcessingInstruction(place.getNodeName(),
                        place.getNodeValue());
                break;
            }
            case Node.TEXT_NODE: {
                node = factory.createTextNode(place.getNodeValue());
                break;
            }
            default: {
                throw new IllegalArgumentException("can't copy node type, "+
                        type+" ("+
                        place.getNodeName()+')');
            }
            }
            dest.appendChild(node);

            if (place.hasChildNodes()) {
                parent = place;
                place  = place.getFirstChild();
                dest   = node;
            }

            else {
                place = place.getNextSibling();
                while (place == null && parent != start) {
                    place  = parent.getNextSibling();
                    parent = parent.getParentNode();
                    dest   = dest.getParentNode();
                }
            }

        }

    } public static Element getFirstChildElement(Node parent) {

        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return (Element)child;
            }
            child = child.getNextSibling();
        }

        return null;

    } public static Element getFirstVisibleChildElement(Node parent) {

        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                    !isHidden(child)) {
                return (Element)child;
            }
            child = child.getNextSibling();
        }

        return null;

    } public static Element getFirstVisibleChildElement(Node parent, Map<Node, String> hiddenNodes) {

        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                    !isHidden(child, hiddenNodes)) {
                return (Element)child;
            }
            child = child.getNextSibling();
        }

        return null;

    } public static Element getLastChildElement(Node parent) {

        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return (Element)child;
            }
            child = child.getPreviousSibling();
        }

        return null;

    } public static Element getLastVisibleChildElement(Node parent) {

        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                    !isHidden(child)) {
                return (Element)child;
            }
            child = child.getPreviousSibling();
        }

        return null;

    } public static Element getLastVisibleChildElement(Node parent, Map<Node, String> hiddenNodes) {

        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                    !isHidden(child, hiddenNodes)) {
                return (Element)child;
            }
            child = child.getPreviousSibling();
        }

        return null;

    } public static Element getNextSiblingElement(Node node) {

        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                return (Element)sibling;
            }
            sibling = sibling.getNextSibling();
        }

        return null;

    } public static Element getNextVisibleSiblingElement(Node node) {

        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE &&
                    !isHidden(sibling)) {
                return (Element)sibling;
            }
            sibling = sibling.getNextSibling();
        }

        return null;

    } public static Element getNextVisibleSiblingElement(Node node, Map<Node, String> hiddenNodes) {

        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE &&
                    !isHidden(sibling, hiddenNodes)) {
                return (Element)sibling;
            }
            sibling = sibling.getNextSibling();
        }

        return null;

    } public static void setHidden(Node node) {
        if (node instanceof com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)
            ((com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)node).setReadOnly(true, false);
        else if (node instanceof com.sun.org.apache.xerces.internal.dom.NodeImpl)
            ((com.sun.org.apache.xerces.internal.dom.NodeImpl)node).setReadOnly(true, false);
    } public static void setHidden(Node node, Map<Node, String> hiddenNodes) {
        if (node instanceof com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl) {
            ((com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)node).setReadOnly(true, false);
        }
        else {
                hiddenNodes.put(node, "");
        }
    } public static void setVisible(Node node) {
        if (node instanceof com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)
            ((com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)node).setReadOnly(false, false);
        else if (node instanceof com.sun.org.apache.xerces.internal.dom.NodeImpl)
            ((com.sun.org.apache.xerces.internal.dom.NodeImpl)node).setReadOnly(false, false);
    } public static void setVisible(Node node, Map<Node, String> hiddenNodes) {
        if (node instanceof com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl) {
            ((com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)node).setReadOnly(false, false);
        }
        else {
            hiddenNodes.remove(node);
        }
    } public static boolean isHidden(Node node) {
        if (node instanceof com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)
            return ((com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)node).getReadOnly();
        else if (node instanceof com.sun.org.apache.xerces.internal.dom.NodeImpl)
            return ((com.sun.org.apache.xerces.internal.dom.NodeImpl)node).getReadOnly();
        return false;
    } public static boolean isHidden(Node node, Map<Node, String> hiddenNodes) {
        if (node instanceof com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl) {
            return ((com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)node).getReadOnly();
        }
        else {
            return hiddenNodes.containsKey(node);
        }
    } public static Element getFirstChildElement(Node parent, String elemName) {

        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (child.getNodeName().equals(elemName)) {
                    return (Element)child;
                }
            }
            child = child.getNextSibling();
        }

        return null;

    } public static Element getLastChildElement(Node parent, String elemName) {

        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (child.getNodeName().equals(elemName)) {
                    return (Element)child;
                }
            }
            child = child.getPreviousSibling();
        }

        return null;

    } public static Element getNextSiblingElement(Node node, String elemName) {

        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                if (sibling.getNodeName().equals(elemName)) {
                    return (Element)sibling;
                }
            }
            sibling = sibling.getNextSibling();
        }

        return null;

    } public static Element getFirstChildElementNS(Node parent,
            String uri, String localpart) {

        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childURI = child.getNamespaceURI();
                if (childURI != null && childURI.equals(uri) &&
                        child.getLocalName().equals(localpart)) {
                    return (Element)child;
                }
            }
            child = child.getNextSibling();
        }

        return null;

    } public static Element getLastChildElementNS(Node parent,
            String uri, String localpart) {

        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childURI = child.getNamespaceURI();
                if (childURI != null && childURI.equals(uri) &&
                        child.getLocalName().equals(localpart)) {
                    return (Element)child;
                }
            }
            child = child.getPreviousSibling();
        }

        return null;

    } public static Element getNextSiblingElementNS(Node node,
            String uri, String localpart) {

        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                String siblingURI = sibling.getNamespaceURI();
                if (siblingURI != null && siblingURI.equals(uri) &&
                        sibling.getLocalName().equals(localpart)) {
                    return (Element)sibling;
                }
            }
            sibling = sibling.getNextSibling();
        }

        return null;

    } public static Element getFirstChildElement(Node parent, String elemNames[]) {

        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    if (child.getNodeName().equals(elemNames[i])) {
                        return (Element)child;
                    }
                }
            }
            child = child.getNextSibling();
        }

        return null;

    } public static Element getLastChildElement(Node parent, String elemNames[]) {

        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    if (child.getNodeName().equals(elemNames[i])) {
                        return (Element)child;
                    }
                }
            }
            child = child.getPreviousSibling();
        }

        return null;

    } public static Element getNextSiblingElement(Node node, String elemNames[]) {

        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    if (sibling.getNodeName().equals(elemNames[i])) {
                        return (Element)sibling;
                    }
                }
            }
            sibling = sibling.getNextSibling();
        }

        return null;

    } public static Element getFirstChildElementNS(Node parent,
            String[][] elemNames) {

        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    String uri = child.getNamespaceURI();
                    if (uri != null && uri.equals(elemNames[i][0]) &&
                            child.getLocalName().equals(elemNames[i][1])) {
                        return (Element)child;
                    }
                }
            }
            child = child.getNextSibling();
        }

        return null;

    } public static Element getLastChildElementNS(Node parent,
            String[][] elemNames) {

        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    String uri = child.getNamespaceURI();
                    if (uri != null && uri.equals(elemNames[i][0]) &&
                            child.getLocalName().equals(elemNames[i][1])) {
                        return (Element)child;
                    }
                }
            }
            child = child.getPreviousSibling();
        }

        return null;

    } public static Element getNextSiblingElementNS(Node node,
            String[][] elemNames) {

        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    String uri = sibling.getNamespaceURI();
                    if (uri != null && uri.equals(elemNames[i][0]) &&
                            sibling.getLocalName().equals(elemNames[i][1])) {
                        return (Element)sibling;
                    }
                }
            }
            sibling = sibling.getNextSibling();
        }

        return null;

    } public static Element getFirstChildElement(Node   parent,
            String elemName,
            String attrName,
            String attrValue) {

        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)child;
                if (element.getNodeName().equals(elemName) &&
                        element.getAttribute(attrName).equals(attrValue)) {
                    return element;
                }
            }
            child = child.getNextSibling();
        }

        return null;

    } public static Element getLastChildElement(Node   parent,
            String elemName,
            String attrName,
            String attrValue) {

        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)child;
                if (element.getNodeName().equals(elemName) &&
                        element.getAttribute(attrName).equals(attrValue)) {
                    return element;
                }
            }
            child = child.getPreviousSibling();
        }

        return null;

    } public static Element getNextSiblingElement(Node   node,
            String elemName,
            String attrName,
            String attrValue) {

        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)sibling;
                if (element.getNodeName().equals(elemName) &&
                        element.getAttribute(attrName).equals(attrValue)) {
                    return element;
                }
            }
            sibling = sibling.getNextSibling();
        }

        return null;

    } public static String getChildText(Node node) {

        if (node == null) {
            return null;
        }

        StringBuffer str = new StringBuffer();
        Node child = node.getFirstChild();
        while (child != null) {
            short type = child.getNodeType();
            if (type == Node.TEXT_NODE) {
                str.append(child.getNodeValue());
            }
            else if (type == Node.CDATA_SECTION_NODE) {
                str.append(getChildText(child));
            }
            child = child.getNextSibling();
        }

        return str.toString();

    } public static String getName(Node node) {
        return node.getNodeName();
    } public static String getLocalName(Node node) {
        String name = node.getLocalName();
        return (name!=null)? name:node.getNodeName();
    } public static Element getParent(Element elem) {
        Node parent = elem.getParentNode();
        if (parent instanceof Element)
            return (Element)parent;
        return null;
    } public static Document getDocument(Node node) {
        return node.getOwnerDocument();
    } public static Element getRoot(Document doc) {
        return doc.getDocumentElement();
    } public static Attr getAttr(Element elem, String name) {
        return elem.getAttributeNode(name);
    } public static Attr getAttrNS(Element elem, String nsUri,
            String localName) {
        return elem.getAttributeNodeNS(nsUri, localName);
    } public static Attr[] getAttrs(Element elem) {
        NamedNodeMap attrMap = elem.getAttributes();
        Attr [] attrArray = new Attr[attrMap.getLength()];
        for (int i=0; i<attrMap.getLength(); i++)
            attrArray[i] = (Attr)attrMap.item(i);
        return attrArray;
    } public static String getValue(Attr attribute) {
        return attribute.getValue();
    } public static String getAttrValue(Element elem, String name) {
        return elem.getAttribute(name);
    } public static String getAttrValueNS(Element elem, String nsUri,
            String localName) {
        return elem.getAttributeNS(nsUri, localName);
    } public static String getPrefix(Node node) {
        return node.getPrefix();
    }

    public static String getNamespaceURI(Node node) {
        return node.getNamespaceURI();
    }

    public static String getAnnotation(Node node) {
        if (node instanceof ElementImpl) {
            return ((ElementImpl)node).getAnnotation();
        }
        return null;
    }

    public static String getSyntheticAnnotation(Node node) {
        if (node instanceof ElementImpl) {
            return ((ElementImpl)node).getSyntheticAnnotation();
        }
        return null;
    }


    public static DOMException createDOMException(short code, Throwable cause) {
        DOMException de = new DOMException(code, cause != null ? cause.getMessage() : null);
        if (cause != null && ThrowableMethods.fgThrowableMethodsAvailable) {
            try {
                ThrowableMethods.fgThrowableInitCauseMethod.invoke(de, new Object [] {cause});
            }
            catch (Exception e) {}
        }
        return de;
    }


    public static LSException createLSException(short code, Throwable cause) {
        LSException lse = new LSException(code, cause != null ? cause.getMessage() : null);
        if (cause != null && ThrowableMethods.fgThrowableMethodsAvailable) {
            try {
                ThrowableMethods.fgThrowableInitCauseMethod.invoke(lse, new Object [] {cause});
            }
            catch (Exception e) {}
        }
        return lse;
    }


    static class ThrowableMethods {

        private static java.lang.reflect.Method fgThrowableInitCauseMethod = null;

        private static boolean fgThrowableMethodsAvailable = false;

        private ThrowableMethods() {}

        static {
            try {
                fgThrowableInitCauseMethod = Throwable.class.getMethod("initCause", new Class [] {Throwable.class});
                fgThrowableMethodsAvailable = true;
            }
            catch (Exception exc) {
                fgThrowableInitCauseMethod = null;
                fgThrowableMethodsAvailable = false;
            }
        }
    }

}