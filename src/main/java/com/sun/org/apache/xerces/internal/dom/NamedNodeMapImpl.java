


package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class NamedNodeMapImpl
    implements NamedNodeMap, Serializable {

    static final long serialVersionUID = -7039242451046758020L;

    protected short flags;

    protected final static short READONLY     = 0x1<<0;
    protected final static short CHANGED      = 0x1<<1;
    protected final static short HASDEFAULTS  = 0x1<<2;


    protected List nodes;

    protected NodeImpl ownerNode; protected NamedNodeMapImpl(NodeImpl ownerNode) {
        this.ownerNode = ownerNode;
    }

    public int getLength() {
        return (nodes != null) ? nodes.size() : 0;
    }


    public Node item(int index) {
        return (nodes != null && index < nodes.size()) ?
                    (Node)(nodes.get(index)) : null;
    }


    public Node getNamedItem(String name) {

        int i = findNamePoint(name,0);
        return (i < 0) ? null : (Node)(nodes.get(i));

    } public Node getNamedItemNS(String namespaceURI, String localName) {

        int i = findNamePoint(namespaceURI, localName);
        return (i < 0) ? null : (Node)(nodes.get(i));

    } public Node setNamedItem(Node arg)
    throws DOMException {

        CoreDocumentImpl ownerDocument = ownerNode.ownerDocument();
        if (ownerDocument.errorChecking) {
            if (isReadOnly()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);
            }
            if (arg.getOwnerDocument() != ownerDocument) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, msg);
            }
        }

        int i = findNamePoint(arg.getNodeName(),0);
        NodeImpl previous = null;
        if (i >= 0) {
            previous = (NodeImpl) nodes.get(i);
            nodes.set(i, arg);
        } else {
            i = -1 - i; if (null == nodes) {
                nodes = new ArrayList(5);
            }
            nodes.add(i, arg);
        }
        return previous;

    } public Node setNamedItemNS(Node arg)
    throws DOMException {

        CoreDocumentImpl ownerDocument = ownerNode.ownerDocument();
        if (ownerDocument.errorChecking) {
            if (isReadOnly()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);
            }

            if(arg.getOwnerDocument() != ownerDocument) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, msg);
            }
        }

        int i = findNamePoint(arg.getNamespaceURI(), arg.getLocalName());
        NodeImpl previous = null;
        if (i >= 0) {
            previous = (NodeImpl) nodes.get(i);
            nodes.set(i, arg);
        } else {
            i = findNamePoint(arg.getNodeName(),0);
            if (i >= 0) {
                previous = (NodeImpl) nodes.get(i);
                nodes.add(i, arg);
            } else {
                i = -1 - i; if (null == nodes) {
                    nodes = new ArrayList(5);
                }
                nodes.add(i, arg);
            }
        }
        return previous;

    } public Node removeNamedItem(String name)
        throws DOMException {

        if (isReadOnly()) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
            throw
                new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
                msg);
        }
        int i = findNamePoint(name,0);
        if (i < 0) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_FOUND_ERR", null);
            throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
        }

        NodeImpl n = (NodeImpl)nodes.get(i);
        nodes.remove(i);

        return n;

    } public Node removeNamedItemNS(String namespaceURI, String name)
        throws DOMException {

        if (isReadOnly()) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
            throw
                new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
                msg);
        }
        int i = findNamePoint(namespaceURI, name);
        if (i < 0) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_FOUND_ERR", null);
            throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
        }

        NodeImpl n = (NodeImpl)nodes.get(i);
        nodes.remove(i);

        return n;

    } public NamedNodeMapImpl cloneMap(NodeImpl ownerNode) {
        NamedNodeMapImpl newmap = new NamedNodeMapImpl(ownerNode);
        newmap.cloneContent(this);
        return newmap;
    }

    protected void cloneContent(NamedNodeMapImpl srcmap) {
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
                    NodeImpl n = (NodeImpl) srcmap.nodes.get(i);
                    NodeImpl clone = (NodeImpl) n.cloneNode(true);
                    clone.isSpecified(n.isSpecified());
                    nodes.add(clone);
                }
            }
        }
    } void setReadOnly(boolean readOnly, boolean deep) {
        isReadOnly(readOnly);
        if (deep && nodes != null) {
            for (int i = nodes.size() - 1; i >= 0; i--) {
                ((NodeImpl) nodes.get(i)).setReadOnly(readOnly,deep);
            }
        }
    } boolean getReadOnly() {
        return isReadOnly();
    } protected void setOwnerDocument(CoreDocumentImpl doc) {
        if (nodes != null) {
            final int size = nodes.size();
            for (int i = 0; i < size; ++i) {
                ((NodeImpl)item(i)).setOwnerDocument(doc);
            }
        }
    }

    final boolean isReadOnly() {
        return (flags & READONLY) != 0;
    }

    final void isReadOnly(boolean value) {
        flags = (short) (value ? flags | READONLY : flags & ~READONLY);
    }

    final boolean changed() {
        return (flags & CHANGED) != 0;
    }

    final void changed(boolean value) {
        flags = (short) (value ? flags | CHANGED : flags & ~CHANGED);
    }

    final boolean hasDefaults() {
        return (flags & HASDEFAULTS) != 0;
    }

    final void hasDefaults(boolean value) {
        flags = (short) (value ? flags | HASDEFAULTS : flags & ~HASDEFAULTS);
    }

    protected int findNamePoint(String name, int start) {

        int i = 0;
        if (nodes != null) {
            int first = start;
            int last  = nodes.size() - 1;

            while (first <= last) {
                i = (first + last) / 2;
                int test = name.compareTo(((Node)(nodes.get(i))).getNodeName());
                if (test == 0) {
                    return i; }
                else if (test < 0) {
                    last = i - 1;
                }
                else {
                    first = i + 1;
                }
            }

            if (first > i) {
                i = first;
            }
        }

        return -1 - i; } protected int findNamePoint(String namespaceURI, String name) {

        if (nodes == null) return -1;
        if (name == null) return -1;

        final int size = nodes.size();
        for (int i = 0; i < size; ++i) {
            NodeImpl a = (NodeImpl)nodes.get(i);
            String aNamespaceURI = a.getNamespaceURI();
            String aLocalName = a.getLocalName();
            if (namespaceURI == null) {
              if (aNamespaceURI == null
                  &&
                  (name.equals(aLocalName)
                   ||
                   (aLocalName == null && name.equals(a.getNodeName()))))
                return i;
            } else {
              if (namespaceURI.equals(aNamespaceURI)
                  &&
                  name.equals(aLocalName))
                return i;
            }
        }
        return -1;
    }

    protected boolean precedes(Node a, Node b) {

        if (nodes != null) {
            final int size = nodes.size();
            for (int i = 0; i < size; ++i) {
                Node n = (Node)nodes.get(i);
                if (n==a) return true;
                if (n==b) return false;
            }
        }
        return false;
    }



    protected void removeItem(int index) {
       if (nodes != null && index < nodes.size()){
           nodes.remove(index);
       }
    }


    protected Object getItem (int index){
        if (nodes != null) {
            return nodes.get(index);
        }
        return null;
    }

    protected int addItem (Node arg) {
        int i = findNamePoint(arg.getNamespaceURI(), arg.getLocalName());
        if (i >= 0) {
            nodes.set(i, arg);
        }
        else {
            i = findNamePoint(arg.getNodeName(),0);
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
        return i;
    }


    protected ArrayList cloneMap(ArrayList list) {
        if (list == null) {
            list = new ArrayList(5);
        }
        list.clear();
        if (nodes != null) {
            final int size = nodes.size();
            for (int i = 0; i < size; ++i) {
                list.add(nodes.get(i));
            }
        }
        return list;
    }

     protected int getNamedItemIndex(String namespaceURI, String localName) {
        return findNamePoint(namespaceURI, localName);
     }


    public void removeAll (){
        if (nodes != null) {
            nodes.clear();
        }
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (nodes != null) {
            nodes = new ArrayList((Vector)nodes);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        List oldNodes = this.nodes;
        try {
            if (oldNodes != null) {
                this.nodes = new Vector(oldNodes);
            }
            out.defaultWriteObject();
        }
        finally {
            this.nodes = oldNodes;
        }
    }

}