


package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;



public class TreeWalkerImpl implements TreeWalker {

    private boolean fEntityReferenceExpansion = false;

    int fWhatToShow = NodeFilter.SHOW_ALL;

    NodeFilter fNodeFilter;

    Node fCurrentNode;

    Node fRoot;

    public TreeWalkerImpl(Node root,
                          int whatToShow,
                          NodeFilter nodeFilter,
                          boolean entityReferenceExpansion) {
        fCurrentNode = root;
        fRoot = root;
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

    public void setWhatShow(int whatToShow){
        fWhatToShow = whatToShow;
    }

    public NodeFilter         getFilter() {
        return fNodeFilter;
    }


    public boolean            getExpandEntityReferences() {
        return fEntityReferenceExpansion;
    }


    public Node               getCurrentNode() {
        return fCurrentNode;
    }

    public void               setCurrentNode(Node node) {
        if (node == null) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_SUPPORTED_ERR", null);
              throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
        }

        fCurrentNode = node;
    }


    public Node               parentNode() {

        if (fCurrentNode == null) return null;

        Node node = getParentNode(fCurrentNode);
        if (node !=null) {
            fCurrentNode = node;
        }
        return node;

    }


    public Node               firstChild() {

        if (fCurrentNode == null) return null;

        Node node = getFirstChild(fCurrentNode);
        if (node !=null) {
            fCurrentNode = node;
        }
        return node;
    }

    public Node               lastChild() {

        if (fCurrentNode == null) return null;

        Node node = getLastChild(fCurrentNode);
        if (node !=null) {
            fCurrentNode = node;
        }
        return node;
    }


    public Node               previousSibling() {

        if (fCurrentNode == null) return null;

        Node node = getPreviousSibling(fCurrentNode);
        if (node !=null) {
            fCurrentNode = node;
        }
        return node;
    }


    public Node               nextSibling(){
        if (fCurrentNode == null) return null;

        Node node = getNextSibling(fCurrentNode);
        if (node !=null) {
            fCurrentNode = node;
        }
        return node;
    }


    public Node               previousNode() {
        Node result;

        if (fCurrentNode == null) return null;

        result = getPreviousSibling(fCurrentNode);
        if (result == null) {
            result = getParentNode(fCurrentNode);
            if (result != null) {
                fCurrentNode = result;
                return fCurrentNode;
            }
            return null;
        }

        Node lastChild  = getLastChild(result);

        Node prev = lastChild ;
        while (lastChild != null) {
          prev = lastChild ;
          lastChild = getLastChild(prev) ;
        }

        lastChild = prev ;

        if (lastChild != null) {
            fCurrentNode = lastChild;
            return fCurrentNode;
        }

        if (result != null) {
            fCurrentNode = result;
            return fCurrentNode;
        }

        return null;
    }


    public Node               nextNode() {

        if (fCurrentNode == null) return null;

        Node result = getFirstChild(fCurrentNode);

        if (result != null) {
            fCurrentNode = result;
            return result;
        }

        result = getNextSibling(fCurrentNode);

        if (result != null) {
            fCurrentNode = result;
            return result;
        }

        Node parent = getParentNode(fCurrentNode);
        while (parent != null) {
            result = getNextSibling(parent);
            if (result != null) {
                fCurrentNode = result;
                return result;
            } else {
                parent = getParentNode(parent);
            }
        }

        return null;
    }


    Node getParentNode(Node node) {

        if (node == null || node == fRoot) return null;

        Node newNode = node.getParentNode();
        if (newNode == null)  return null;

        int accept = acceptNode(newNode);

        if (accept == NodeFilter.FILTER_ACCEPT)
            return newNode;
        else
        {
            return getParentNode(newNode);
        }


    }


    Node getNextSibling(Node node) {
                return getNextSibling(node, fRoot);
        }


    Node getNextSibling(Node node, Node root) {

        if (node == null || node == root) return null;

        Node newNode = node.getNextSibling();
        if (newNode == null) {

            newNode = node.getParentNode();

            if (newNode == null || newNode == root)  return null;

            int parentAccept = acceptNode(newNode);

            if (parentAccept==NodeFilter.FILTER_SKIP) {
                return getNextSibling(newNode, root);
            }

            return null;
        }

        int accept = acceptNode(newNode);

        if (accept == NodeFilter.FILTER_ACCEPT)
            return newNode;
        else
        if (accept == NodeFilter.FILTER_SKIP) {
            Node fChild = getFirstChild(newNode);
            if (fChild == null) {
                return getNextSibling(newNode, root);
            }
            return fChild;
        }
        else
        {
            return getNextSibling(newNode, root);
        }

    } Node getPreviousSibling(Node node) {
                return getPreviousSibling(node, fRoot);
        }


    Node getPreviousSibling(Node node, Node root) {

        if (node == null || node == root) return null;

        Node newNode = node.getPreviousSibling();
        if (newNode == null) {

            newNode = node.getParentNode();
            if (newNode == null || newNode == root)  return null;

            int parentAccept = acceptNode(newNode);

            if (parentAccept==NodeFilter.FILTER_SKIP) {
                return getPreviousSibling(newNode, root);
            }

            return null;
        }

        int accept = acceptNode(newNode);

        if (accept == NodeFilter.FILTER_ACCEPT)
            return newNode;
        else
        if (accept == NodeFilter.FILTER_SKIP) {
            Node fChild =  getLastChild(newNode);
            if (fChild == null) {
                return getPreviousSibling(newNode, root);
            }
            return fChild;
        }
        else
        {
            return getPreviousSibling(newNode, root);
        }

    } Node getFirstChild(Node node) {
        if (node == null) return null;

        if ( !fEntityReferenceExpansion
             && node.getNodeType() == Node.ENTITY_REFERENCE_NODE)
            return null;
        Node newNode = node.getFirstChild();
        if (newNode == null)  return null;
        int accept = acceptNode(newNode);

        if (accept == NodeFilter.FILTER_ACCEPT)
            return newNode;
        else
        if (accept == NodeFilter.FILTER_SKIP
            && newNode.hasChildNodes())
        {
            Node fChild = getFirstChild(newNode);

            if (fChild == null) {
                return getNextSibling(newNode, node);
            }
            return fChild;
        }
        else
        {
            return getNextSibling(newNode, node);
        }


    }


    Node getLastChild(Node node) {

        if (node == null) return null;

        if ( !fEntityReferenceExpansion
             && node.getNodeType() == Node.ENTITY_REFERENCE_NODE)
            return null;

        Node newNode = node.getLastChild();
        if (newNode == null)  return null;

        int accept = acceptNode(newNode);

        if (accept == NodeFilter.FILTER_ACCEPT)
            return newNode;
        else
        if (accept == NodeFilter.FILTER_SKIP
            && newNode.hasChildNodes())
        {
            Node lChild = getLastChild(newNode);
            if (lChild == null) {
                return getPreviousSibling(newNode, node);
            }
            return lChild;
        }
        else
        {
            return getPreviousSibling(newNode, node);
        }


    }


    short acceptNode(Node node) {


        if (fNodeFilter == null) {
            if ( ( fWhatToShow & (1 << node.getNodeType()-1)) != 0) {
                return NodeFilter.FILTER_ACCEPT;
            } else {
                return NodeFilter.FILTER_SKIP;
            }
        } else {
            if ((fWhatToShow & (1 << node.getNodeType()-1)) != 0 ) {
                return fNodeFilter.acceptNode(node);
            } else {
                return NodeFilter.FILTER_SKIP;
            }
        }
    }
}
