

package com.sun.org.apache.xml.internal.security.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class HelperNodeList implements NodeList {


    List<Node> nodes = new ArrayList<Node>();
    boolean allNodesMustHaveSameParent = false;


    public HelperNodeList() {
        this(false);
    }



    public HelperNodeList(boolean allNodesMustHaveSameParent) {
        this.allNodesMustHaveSameParent = allNodesMustHaveSameParent;
    }


    public Node item(int index) {
        return nodes.get(index);
    }


    public int getLength() {
        return nodes.size();
    }


    public void appendChild(Node node) throws IllegalArgumentException {
        if (this.allNodesMustHaveSameParent && this.getLength() > 0
            && this.item(0).getParentNode() != node.getParentNode()) {
            throw new IllegalArgumentException("Nodes have not the same Parent");
        }
        nodes.add(node);
    }


    public Document getOwnerDocument() {
        if (this.getLength() == 0) {
            return null;
        }
        return XMLUtils.getOwnerDocument(this.item(0));
    }
}
