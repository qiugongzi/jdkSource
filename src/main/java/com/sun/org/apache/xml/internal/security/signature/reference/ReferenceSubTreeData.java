



package com.sun.org.apache.xml.internal.security.signature.reference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class ReferenceSubTreeData implements ReferenceNodeSetData {

    private boolean excludeComments;
    private Node root;

    public ReferenceSubTreeData(Node root, boolean excludeComments) {
        this.root = root;
        this.excludeComments = excludeComments;
    }

    public Iterator<Node> iterator() {
        return new DelayedNodeIterator(root, excludeComments);
    }

    public Node getRoot() {
        return root;
    }

    public boolean excludeComments() {
        return excludeComments;
    }


    static class DelayedNodeIterator implements Iterator<Node> {
        private Node root;
        private List<Node> nodeSet;
        private ListIterator<Node> li;
        private boolean withComments;

        DelayedNodeIterator(Node root, boolean excludeComments) {
            this.root = root;
            this.withComments = !excludeComments;
        }

        public boolean hasNext() {
            if (nodeSet == null) {
                nodeSet = dereferenceSameDocumentURI(root);
                li = nodeSet.listIterator();
            }
            return li.hasNext();
        }

        public Node next() {
            if (nodeSet == null) {
                nodeSet = dereferenceSameDocumentURI(root);
                li = nodeSet.listIterator();
            }
            if (li.hasNext()) {
                return li.next();
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }


        private List<Node> dereferenceSameDocumentURI(Node node) {
            List<Node> nodeSet = new ArrayList<Node>();
            if (node != null) {
                nodeSetMinusCommentNodes(node, nodeSet, null);
            }
            return nodeSet;
        }


        @SuppressWarnings("fallthrough")
        private void nodeSetMinusCommentNodes(Node node, List<Node> nodeSet,
                                              Node prevSibling)
        {
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE :
                    nodeSet.add(node);
                    NamedNodeMap attrs = node.getAttributes();
                    if (attrs != null) {
                        for (int i = 0, len = attrs.getLength(); i < len; i++) {
                            nodeSet.add(attrs.item(i));
                        }
                    }
                    Node pSibling = null;
                    for (Node child = node.getFirstChild(); child != null;
                        child = child.getNextSibling()) {
                        nodeSetMinusCommentNodes(child, nodeSet, pSibling);
                        pSibling = child;
                    }
                    break;
                case Node.DOCUMENT_NODE :
                    pSibling = null;
                    for (Node child = node.getFirstChild(); child != null;
                        child = child.getNextSibling()) {
                        nodeSetMinusCommentNodes(child, nodeSet, pSibling);
                        pSibling = child;
                    }
                    break;
                case Node.TEXT_NODE :
                case Node.CDATA_SECTION_NODE:
                    if (prevSibling != null &&
                        (prevSibling.getNodeType() == Node.TEXT_NODE ||
                         prevSibling.getNodeType() == Node.CDATA_SECTION_NODE)) {
                        return;
                    }
                    nodeSet.add(node);
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE :
                    nodeSet.add(node);
                    break;
                case Node.COMMENT_NODE:
                    if (withComments) {
                        nodeSet.add(node);
                    }
            }
        }
    }
}
