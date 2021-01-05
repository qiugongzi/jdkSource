


package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;



public class NodeIteratorImpl implements NodeIterator {

    private DocumentImpl fDocument;

    private Node fRoot;

    private int fWhatToShow = NodeFilter.SHOW_ALL;

    private NodeFilter fNodeFilter;

    private boolean fDetach = false;

    private Node fCurrentNode;


    private boolean fForward = true;


    private boolean fEntityReferenceExpansion;

    public NodeIteratorImpl( DocumentImpl document,
                             Node root,
                             int whatToShow,
                             NodeFilter nodeFilter,
                             boolean entityReferenceExpansion) {
        fDocument = document;
        fRoot = root;
        fCurrentNode = null;
        fWhatToShow = whatToShow;
        fNodeFilter = nodeFilter;
        fEntityReferenceExpansion = entityReferenceExpansion;
    }

    public Node getRoot() {
        return fRoot;
    }

    public int                getWhatToShow() {
        return fWhatToShow;
    }


    public NodeFilter         getFilter() {
        return fNodeFilter;
    }


    public boolean            getExpandEntityReferences() {
        return fEntityReferenceExpansion;
    }


    public Node               nextNode() {

        if( fDetach) {
                throw new DOMException(
                DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }

        if (fRoot == null) return null;

        Node nextNode = fCurrentNode;
        boolean accepted = false; accepted_loop:
        while (!accepted) {

            if (!fForward && nextNode!=null) {
                nextNode = fCurrentNode;
            } else {
            if (!fEntityReferenceExpansion
                    && nextNode != null
                    && nextNode.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
                    nextNode = nextNode(nextNode, false);
                } else {
                    nextNode = nextNode(nextNode, true);
                }
            }

            fForward = true; if (nextNode == null) return null;

            accepted = acceptNode(nextNode);
            if (accepted) {
                fCurrentNode = nextNode;
                return fCurrentNode;
            } else
                continue accepted_loop;

        } return null;

    }


    public Node               previousNode() {

        if( fDetach) {
                throw new DOMException(
                DOMException.INVALID_STATE_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }

        if (fRoot == null || fCurrentNode == null) return null;

        Node previousNode = fCurrentNode;
        boolean accepted = false;

        accepted_loop:
        while (!accepted) {

            if (fForward && previousNode != null) {
                previousNode = fCurrentNode;
            } else {
                previousNode = previousNode(previousNode);
            }

            fForward = false;

            if (previousNode == null) return null;

            accepted = acceptNode(previousNode);
            if (accepted) {
                fCurrentNode = previousNode;
                return fCurrentNode;
            } else
                continue accepted_loop;
        }
        return null;
    }


    boolean acceptNode(Node node) {

        if (fNodeFilter == null) {
            return ( fWhatToShow & (1 << node.getNodeType()-1)) != 0 ;
        } else {
            return ((fWhatToShow & (1 << node.getNodeType()-1)) != 0 )
                && fNodeFilter.acceptNode(node) == NodeFilter.FILTER_ACCEPT;
        }
    }


    Node matchNodeOrParent(Node node) {
        if (fCurrentNode == null) return null;

        for (Node n = fCurrentNode; n != fRoot; n = n.getParentNode()) {
            if (node == n) return n;
        }
        return null;
    }


    Node nextNode(Node node, boolean visitChildren) {

        if (node == null) return fRoot;

        Node result;
        if (visitChildren) {
            if (node.hasChildNodes()) {
                result = node.getFirstChild();
                return result;
            }
        }

        if (node == fRoot) { return null;
        }

        result = node.getNextSibling();
        if (result != null) return result;


        Node parent = node.getParentNode();
        while (parent != null && parent != fRoot) {
            result = parent.getNextSibling();
            if (result != null) {
                return result;
            } else {
                parent = parent.getParentNode();
            }

        } return null;
    }


    Node previousNode(Node node) {

        Node result;

        if (node == fRoot) return null;

        result = node.getPreviousSibling();
        if (result == null) {
            result = node.getParentNode();
            return result;
        }

        if (result.hasChildNodes()
            && !(!fEntityReferenceExpansion
                && result != null
                && result.getNodeType() == Node.ENTITY_REFERENCE_NODE))

        {
            while (result.hasChildNodes()) {
                result = result.getLastChild();
            }
        }

        return result;
    }


    public void removeNode(Node node) {

        if (node == null) return;

        Node deleted = matchNodeOrParent(node);

        if (deleted == null) return;

        if (fForward) {
            fCurrentNode = previousNode(deleted);
        } else
        {
            Node next = nextNode(deleted, false);
            if (next!=null) {
                fCurrentNode = next;
            } else {
                fCurrentNode = previousNode(deleted);
                fForward = true;
            }

        }

    }

    public void               detach() {
        fDetach = true;
        fDocument.removeNodeIterator(this);
    }

}