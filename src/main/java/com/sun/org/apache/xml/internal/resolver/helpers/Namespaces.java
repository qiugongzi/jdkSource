
package com.sun.org.apache.xml.internal.resolver.helpers;

import org.w3c.dom.*;


public class Namespaces {

    public static String getPrefix(Element element) {
        String name = element.getTagName();
        String prefix = "";

        if (name.indexOf(':') > 0) {
            prefix = name.substring(0, name.indexOf(':'));
        }

        return prefix;
    }


    public static String getLocalName(Element element) {
        String name = element.getTagName();

        if (name.indexOf(':') > 0) {
            name = name.substring(name.indexOf(':')+1);
        }

        return name;
    }


    public static String getNamespaceURI(Node node, String prefix) {
        if (node == null || node.getNodeType() != Node.ELEMENT_NODE) {
            return null;
        }

        if (prefix.equals("")) {
            if (((Element) node).hasAttribute("xmlns")) {
                return ((Element) node).getAttribute("xmlns");
            }
        } else {
            String nsattr = "xmlns:" + prefix;
            if (((Element) node).hasAttribute(nsattr)) {
                return ((Element) node).getAttribute(nsattr);
            }
        }

        return getNamespaceURI(node.getParentNode(), prefix);
    }


    public static String getNamespaceURI(Element element) {
        String prefix = getPrefix(element);
        return getNamespaceURI(element, prefix);
    }
}
