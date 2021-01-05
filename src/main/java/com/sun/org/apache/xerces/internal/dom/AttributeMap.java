


package com.sun.org.apache.xerces.internal.dom;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;


public class AttributeMap extends NamedNodeMapImpl {


    static final long serialVersionUID = 8872606282138665383L;

    protected AttributeMap(ElementImpl ownerNode, NamedNodeMapImpl defaults) {
        super(ownerNode);
        if (defaults != null) {
            cloneContent(defaults);
            if (nodes != null) {
                hasDefaults(true);
            }
        }
    }


    public Node setNamedItem(Node arg)
    throws DOMException {

        boolean errCheck = ownerNode.ownerDocument().errorChecking;
        if (errCheck) {
            if (isReadOnly()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);
            }
            if (arg.getOwnerDocument() != ownerNode.ownerDocument()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, msg);
            }
            if (arg.getNodeType() != Node.ATTRIBUTE_NODE) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, msg);
            }
        }
        AttrImpl argn = (AttrImpl)arg;

        if (argn.isOwned()){
            if (errCheck && argn.getOwnerElement() != ownerNode) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INUSE_ATTRIBUTE_ERR", null);
                throw new DOMException(DOMException.INUSE_ATTRIBUTE_ERR, msg);
            }
            return arg;
        }


        argn.ownerNode = ownerNode;
        argn.isOwned(true);

        int i = findNamePoint(argn.getNodeName(),0);
        AttrImpl previous = null;
        if (i >= 0) {
            previous = (AttrImpl) nodes.get(i);
            nodes.set(i, arg);
            previous.ownerNode = ownerNode.ownerDocument();
            previous.isOwned(false);
            previous.isSpecified(true);
        } else {
            i = -1 - i; if (null == nodes) {
                nodes = new ArrayList(5);
            }
            nodes.add(i, arg);
        }

        ownerNode.ownerDocument().setAttrNode(argn, previous);

        if (!argn.isNormalized()) {
            ownerNode.isNormalized(false);
        }
        return previous;

    } public Node setNamedItemNS(Node arg)
    throws DOMException {

        boolean errCheck = ownerNode.ownerDocument().errorChecking;
        if (errCheck) {
            if (isReadOnly()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);
            }
            if(arg.getOwnerDocument() != ownerNode.ownerDocument()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, msg);
            }
            if (arg.getNodeType() != Node.ATTRIBUTE_NODE) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, msg);
            }
        }
        AttrImpl argn = (AttrImpl)arg;

        if (argn.isOwned()){
            if (errCheck && argn.getOwnerElement() != ownerNode) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INUSE_ATTRIBUTE_ERR", null);
                throw new DOMException(DOMException.INUSE_ATTRIBUTE_ERR, msg);
            }
            return arg;
        }

        argn.ownerNode = ownerNode;
        argn.isOwned(true);

        int i = findNamePoint(argn.getNamespaceURI(), argn.getLocalName());
        AttrImpl previous = null;
        if (i >= 0) {
            previous = (AttrImpl) nodes.get(i);
            nodes.set(i, arg);
            previous.ownerNode = ownerNode.ownerDocument();
            previous.isOwned(false);
            previous.isSpecified(true);
        } else {
            i = findNamePoint(arg.getNodeName(),0);
            if (i >=0) {
                previous = (AttrImpl) nodes.get(i);
                nodes.add(i, arg);
            } else {
                i = -1 - i; if (null == nodes) {
                    nodes = new ArrayList(5);
                }
                nodes.add(i, arg);
            }
        }
        ownerNode.ownerDocument().setAttrNode(argn, previous);

        if (!argn.isNormalized()) {
            ownerNode.isNormalized(false);
        }
        return previous;

    } public Node removeNamedItem(String name)
        throws DOMException {
        return internalRemoveNamedItem(name, true);
    }


    Node safeRemoveNamedItem(String name) {
        return internalRemoveNamedItem(name, false);
    }



    protected Node removeItem(Node item, boolean addDefault)
        throws DOMException {

        int index = -1;
        if (nodes != null) {
            final int size = nodes.size();
            for (int i = 0; i < size; ++i) {
                if (nodes.get(i) == item) {
                    index = i;
                    break;
                }
            }
        }
        if (index < 0) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_FOUND_ERR", null);
            throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
        }

        return remove((AttrImpl)item, index, addDefault);
    }


    final protected Node internalRemoveNamedItem(String name, boolean raiseEx){
        if (isReadOnly()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);
        }
        int i = findNamePoint(name,0);
        if (i < 0) {
            if (raiseEx) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_FOUND_ERR", null);
                throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
            } else {
                return null;
            }
        }

        return remove((AttrImpl)nodes.get(i), i, true);

    } private final Node remove(AttrImpl attr, int index,
                              boolean addDefault) {

        CoreDocumentImpl ownerDocument = ownerNode.ownerDocument();
        String name = attr.getNodeName();
        if (attr.isIdAttribute()) {
            ownerDocument.removeIdentifier(attr.getValue());
        }

        if (hasDefaults() && addDefault) {
            NamedNodeMapImpl defaults =
                ((ElementImpl) ownerNode).getDefaultAttributes();

            Node d;
            if (defaults != null &&
                (d = defaults.getNamedItem(name)) != null &&
                findNamePoint(name, index+1) < 0) {
                    NodeImpl clone = (NodeImpl)d.cloneNode(true);
                    if (d.getLocalName() !=null){
                            ((AttrNSImpl)clone).namespaceURI = attr.getNamespaceURI();
                    }
                    clone.ownerNode = ownerNode;
                    clone.isOwned(true);
                    clone.isSpecified(false);

                    nodes.set(index, clone);
                    if (attr.isIdAttribute()) {
                        ownerDocument.putIdentifier(clone.getNodeValue(),
                                                (ElementImpl)ownerNode);
                    }
            } else {
                nodes.remove(index);
            }
        } else {
            nodes.remove(index);
        }

        attr.ownerNode = ownerDocument;
        attr.isOwned(false);

        attr.isSpecified(true);
        attr.isIdAttribute(false);

        ownerDocument.removedAttrNode(attr, ownerNode, name);

        return attr;
    }


    public Node removeNamedItemNS(String namespaceURI, String name)
        throws DOMException {
        return internalRemoveNamedItemNS(namespaceURI, name, true);
    }


    Node safeRemoveNamedItemNS(String namespaceURI, String name) {
        return internalRemoveNamedItemNS(namespaceURI, name, false);
    }


    final protected Node internalRemoveNamedItemNS(String namespaceURI,
            String name,
            boolean raiseEx) {

        CoreDocumentImpl ownerDocument = ownerNode.ownerDocument();
        if (ownerDocument.errorChecking && isReadOnly()) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);
        }
        int i = findNamePoint(namespaceURI, name);
        if (i < 0) {
            if (raiseEx) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_FOUND_ERR", null);
                throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
            } else {
                return null;
            }
        }

        AttrImpl n = (AttrImpl)nodes.get(i);

        if (n.isIdAttribute()) {
            ownerDocument.removeIdentifier(n.getValue());
        }
        String nodeName = n.getNodeName();
        if (hasDefaults()) {
            NamedNodeMapImpl defaults = ((ElementImpl) ownerNode).getDefaultAttributes();
            Node d;
            if (defaults != null
                    && (d = defaults.getNamedItem(nodeName)) != null)
            {
                int j = findNamePoint(nodeName,0);
                if (j>=0 && findNamePoint(nodeName, j+1) < 0) {
                    NodeImpl clone = (NodeImpl)d.cloneNode(true);
                    clone.ownerNode = ownerNode;
                    if (d.getLocalName() != null) {
                        ((AttrNSImpl)clone).namespaceURI = namespaceURI;
                    }
                    clone.isOwned(true);
                    clone.isSpecified(false);
                    nodes.set(i, clone);
                    if (clone.isIdAttribute()) {
                        ownerDocument.putIdentifier(clone.getNodeValue(),
                                (ElementImpl)ownerNode);
                    }
                } else {
                    nodes.remove(i);
                }
            } else {
                nodes.remove(i);
            }
        } else {
            nodes.remove(i);
        }

        n.ownerNode = ownerDocument;
        n.isOwned(false);
        n.isSpecified(true);
        n.isIdAttribute(false);

        ownerDocument.removedAttrNode(n, ownerNode, name);

        return n;

    } public NamedNodeMapImpl cloneMap(NodeImpl ownerNode) {
        AttributeMap newmap =
            new AttributeMap((ElementImpl) ownerNode, null);
        newmap.hasDefaults(hasDefaults());
        newmap.cloneContent(this);
        return newmap;
    } protected void cloneContent(NamedNodeMapImpl srcmap) {
        List srcnodes = srcmap.nodes;
        if (srcnodes != null) {
            int size = srcnodes.size();
            if (size != 0) {
                if (nodes == null) {
                    nodes = new ArrayList(size);
                }
                else {
                    nodes.clear();
                }
                for (int i = 0; i < size; ++i) {
                    NodeImpl n = (NodeImpl) srcnodes.get(i);
                    NodeImpl clone = (NodeImpl) n.cloneNode(true);
                    clone.isSpecified(n.isSpecified());
                    nodes.add(clone);
                    clone.ownerNode = ownerNode;
                    clone.isOwned(true);
                }
            }
        }
    } void moveSpecifiedAttributes(AttributeMap srcmap) {
        int nsize = (srcmap.nodes != null) ? srcmap.nodes.size() : 0;
        for (int i = nsize - 1; i >= 0; i--) {
            AttrImpl attr = (AttrImpl) srcmap.nodes.get(i);
            if (attr.isSpecified()) {
                srcmap.remove(attr, i, false);
                if (attr.getLocalName() != null) {
                    setNamedItem(attr);
                }
                else {
                    setNamedItemNS(attr);
                }
            }
        }
    } protected void reconcileDefaults(NamedNodeMapImpl defaults) {

        int nsize = (nodes != null) ? nodes.size() : 0;
        for (int i = nsize - 1; i >= 0; --i) {
            AttrImpl attr = (AttrImpl) nodes.get(i);
            if (!attr.isSpecified()) {
                remove(attr, i, false);
            }
        }
        if (defaults == null) {
            return;
        }
        if (nodes == null || nodes.size() == 0) {
            cloneContent(defaults);
        }
        else {
            int dsize = defaults.nodes.size();
            for (int n = 0; n < dsize; ++n) {
                AttrImpl d = (AttrImpl) defaults.nodes.get(n);
                int i = findNamePoint(d.getNodeName(), 0);
                if (i < 0) {
                        i = -1 - i;
                    NodeImpl clone = (NodeImpl) d.cloneNode(true);
                    clone.ownerNode = ownerNode;
                    clone.isOwned(true);
                    clone.isSpecified(false);
                        nodes.add(i, clone);
                }
            }
        }

    } protected final int addItem (Node arg) {

        final AttrImpl argn = (AttrImpl) arg;

        argn.ownerNode = ownerNode;
        argn.isOwned(true);

        int i = findNamePoint(argn.getNamespaceURI(), argn.getLocalName());
        if (i >= 0) {
            nodes.set(i, arg);
        }
        else {
            i = findNamePoint(argn.getNodeName(),0);
            if (i >= 0) {
                nodes.add(i, arg);
            }
            else {
                i = -1 - i; if (null == nodes) {
                    nodes = new ArrayList(5);
                }
                nodes.add(i, arg);
            }
        }

        ownerNode.ownerDocument().setAttrNode(argn, null);
        return i;
    }

}