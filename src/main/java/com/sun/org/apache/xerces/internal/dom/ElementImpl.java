


package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.w3c.dom.TypeInfo;
import com.sun.org.apache.xerces.internal.util.URI;


public class ElementImpl
    extends ParentNode
    implements Element, TypeInfo {

    static final long serialVersionUID = 3717253516652722278L;
    protected String name;


    protected AttributeMap attributes;

    public ElementImpl(CoreDocumentImpl ownerDoc, String name) {
        super(ownerDoc);
        this.name = name;
        needsSyncData(true);    }

    protected ElementImpl() {}

    void rename(String name) {
        if (needsSyncData()) {
            synchronizeData();
        }
            this.name = name;
        reconcileDefaultAttributes();
    }

    public short getNodeType() {
        return Node.ELEMENT_NODE;
    }


    public String getNodeName() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return name;
    }


    public NamedNodeMap getAttributes() {

        if (needsSyncData()) {
            synchronizeData();
        }
        if (attributes == null) {
            attributes = new AttributeMap(this, null);
        }
        return attributes;

    } public Node cloneNode(boolean deep) {

        ElementImpl newnode = (ElementImpl) super.cloneNode(deep);
        if (attributes != null) {
            newnode.attributes = (AttributeMap) attributes.cloneMap(newnode);
        }
        return newnode;

    } public String getBaseURI() {

        if (needsSyncData()) {
            synchronizeData();
        }
        if (attributes != null) {
            Attr attrNode = (Attr)attributes.getNamedItem("xml:base");
            if (attrNode != null) {
                String uri =  attrNode.getNodeValue();
                if (uri.length() != 0 ) {try {
                       uri = new URI(uri).toString();
                    }
                    catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e) {
                        String parentBaseURI = (this.ownerNode != null) ? this.ownerNode.getBaseURI() : null;
                        if (parentBaseURI != null){
                            try{
                                uri = new URI(new URI(parentBaseURI), uri).toString();
                            }
                            catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException ex){
                                return null;
                            }
                            return uri;
                        }
                        return null;
                    }
                    return uri;
                }
            }
        }

        String baseURI = (this.ownerNode != null) ? this.ownerNode.getBaseURI() : null ;
        if(baseURI != null){
            try {
                return new URI(baseURI).toString();
            }
            catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e){
                return null;
            }
        }
        return null;
    } void setOwnerDocument(CoreDocumentImpl doc) {
        super.setOwnerDocument(doc);
        if (attributes != null) {
            attributes.setOwnerDocument(doc);
        }
    }

    public String getAttribute(String name) {

        if (needsSyncData()) {
            synchronizeData();
        }
        if (attributes == null) {
            return "";
        }
        Attr attr = (Attr)(attributes.getNamedItem(name));
        return (attr == null) ? "" : attr.getValue();

    } public Attr getAttributeNode(String name) {

        if (needsSyncData()) {
            synchronizeData();
        }
        if (attributes == null) {
            return null;
        }
        return (Attr)attributes.getNamedItem(name);

    } public NodeList getElementsByTagName(String tagname) {
        return new DeepNodeListImpl(this,tagname);
    }


    public String getTagName() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return name;
    }


    public void normalize() {
        if (isNormalized()) {
            return;
        }
        if (needsSyncChildren()) {
            synchronizeChildren();
        }
        ChildNode kid, next;
        for (kid = firstChild; kid != null; kid = next) {
            next = kid.nextSibling;

            if ( kid.getNodeType() == Node.TEXT_NODE )
            {
                if ( next!=null && next.getNodeType() == Node.TEXT_NODE )
                {
                    ((Text)kid).appendData(next.getNodeValue());
                    removeChild( next );
                    next = kid; }
                else
                {
                    if ( kid.getNodeValue() == null || kid.getNodeValue().length() == 0 ) {
                        removeChild( kid );
                    }
                }
            }

            else if (kid.getNodeType() == Node.ELEMENT_NODE) {
                kid.normalize();
            }
        }

        if ( attributes!=null )
        {
            for( int i=0; i<attributes.getLength(); ++i )
            {
                Node attr = attributes.item(i);
                attr.normalize();
            }
        }

        isNormalized(true);
    } public void removeAttribute(String name) {

        if (ownerDocument.errorChecking && isReadOnly()) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);
        }

        if (needsSyncData()) {
            synchronizeData();
        }

        if (attributes == null) {
            return;
        }

        attributes.safeRemoveNamedItem(name);

    } public Attr removeAttributeNode(Attr oldAttr)
        throws DOMException {

        if (ownerDocument.errorChecking && isReadOnly()) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);
        }

        if (needsSyncData()) {
            synchronizeData();
        }

        if (attributes == null) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_FOUND_ERR", null);
            throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
        }
        return (Attr) attributes.removeItem(oldAttr, true);

    } public void setAttribute(String name, String value) {

                if (ownerDocument.errorChecking && isReadOnly()) {
                        String msg =
                                DOMMessageFormatter.formatMessage(
                                        DOMMessageFormatter.DOM_DOMAIN,
                                        "NO_MODIFICATION_ALLOWED_ERR",
                                        null);
                        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);
                }

                if (needsSyncData()) {
                        synchronizeData();
                }

                Attr newAttr = getAttributeNode(name);
                if (newAttr == null) {
                        newAttr = getOwnerDocument().createAttribute(name);

                        if (attributes == null) {
                                attributes = new AttributeMap(this, null);
                        }

                        newAttr.setNodeValue(value);
                        attributes.setNamedItem(newAttr);
                }
                else {
                        newAttr.setNodeValue(value);
                }

        } public Attr setAttributeNode(Attr newAttr)
        throws DOMException
        {

        if (needsSyncData()) {
            synchronizeData();
        }

        if (ownerDocument.errorChecking) {
            if (isReadOnly()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(
                                     DOMException.NO_MODIFICATION_ALLOWED_ERR,
                                     msg);
            }

            if (newAttr.getOwnerDocument() != ownerDocument) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null);
                    throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, msg);
            }
        }

        if (attributes == null) {
            attributes = new AttributeMap(this, null);
        }
        return (Attr) attributes.setNamedItem(newAttr);

    } public String getAttributeNS(String namespaceURI, String localName) {

        if (needsSyncData()) {
            synchronizeData();
        }

        if (attributes == null) {
            return "";
        }

        Attr attr = (Attr)(attributes.getNamedItemNS(namespaceURI, localName));
        return (attr == null) ? "" : attr.getValue();

    } public void setAttributeNS(String namespaceURI,String qualifiedName,
                                          String value) {
                if (ownerDocument.errorChecking && isReadOnly()) {
                        String msg =
                                DOMMessageFormatter.formatMessage(
                                        DOMMessageFormatter.DOM_DOMAIN,
                                        "NO_MODIFICATION_ALLOWED_ERR",
                                        null);
                        throw new DOMException(
                                DOMException.NO_MODIFICATION_ALLOWED_ERR,
                                msg);
                }
                if (needsSyncData()) {
                        synchronizeData();
                }
                int index = qualifiedName.indexOf(':');
                String prefix, localName;
                if (index < 0) {
                        prefix = null;
                        localName = qualifiedName;
                }
                else {
                        prefix = qualifiedName.substring(0, index);
                        localName = qualifiedName.substring(index + 1);
                }
                Attr newAttr = getAttributeNodeNS(namespaceURI, localName);
                if (newAttr == null) {
            newAttr = getOwnerDocument().createAttributeNS(
                                        namespaceURI,
                                        qualifiedName);
                        if (attributes == null) {
                                attributes = new AttributeMap(this, null);
                        }
                        newAttr.setNodeValue(value);
                        attributes.setNamedItemNS(newAttr);
                }
                else {
            if (newAttr instanceof AttrNSImpl){
                String origNodeName = ((AttrNSImpl) newAttr).name;
                String newName = (prefix!=null) ? (prefix+":"+localName) : localName;

                ((AttrNSImpl) newAttr).name = newName;

                if (!newName.equals(origNodeName)) {
                    newAttr = (Attr) attributes.removeItem(newAttr, false);
                    attributes.addItem(newAttr);
                }
            }
            else {
                newAttr = new AttrNSImpl((CoreDocumentImpl)getOwnerDocument(), namespaceURI, qualifiedName, localName);
                attributes.setNamedItemNS(newAttr);
            }

                        newAttr.setNodeValue(value);
                }

    } public void removeAttributeNS(String namespaceURI, String localName) {

        if (ownerDocument.errorChecking && isReadOnly()) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);
        }

        if (needsSyncData()) {
            synchronizeData();
        }

        if (attributes == null) {
            return;
        }

        attributes.safeRemoveNamedItemNS(namespaceURI, localName);

    } public Attr getAttributeNodeNS(String namespaceURI, String localName){

        if (needsSyncData()) {
            synchronizeData();
        }
        if (attributes == null) {
            return null;
        }
        return (Attr)attributes.getNamedItemNS(namespaceURI, localName);

    } public Attr setAttributeNodeNS(Attr newAttr)
        throws DOMException
        {

        if (needsSyncData()) {
            synchronizeData();
        }
        if (ownerDocument.errorChecking) {
            if (isReadOnly()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                    throw new DOMException(
                                     DOMException.NO_MODIFICATION_ALLOWED_ERR,
                                     msg);
            }
            if (newAttr.getOwnerDocument() != ownerDocument) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, msg);
            }
        }

        if (attributes == null) {
            attributes = new AttributeMap(this, null);
        }
        return (Attr) attributes.setNamedItemNS(newAttr);

    } protected int setXercesAttributeNode (Attr attr){

        if (needsSyncData()) {
            synchronizeData();
        }

        if (attributes == null) {
            attributes = new AttributeMap(this, null);
        }
        return attributes.addItem(attr);

    }


    protected int getXercesAttribute(String namespaceURI, String localName){

        if (needsSyncData()) {
            synchronizeData();
        }
        if (attributes == null) {
            return -1;
        }
        return attributes.getNamedItemIndex(namespaceURI, localName);

    }


    public boolean hasAttributes() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return (attributes != null && attributes.getLength() != 0);
    }


    public boolean hasAttribute(String name) {
        return getAttributeNode(name) != null;
    }


    public boolean hasAttributeNS(String namespaceURI, String localName) {
        return getAttributeNodeNS(namespaceURI, localName) != null;
    }


    public NodeList getElementsByTagNameNS(String namespaceURI,
                                           String localName) {
        return new DeepNodeListImpl(this, namespaceURI, localName);
    }


    public boolean isEqualNode(Node arg) {
        if (!super.isEqualNode(arg)) {
            return false;
        }
        boolean hasAttrs = hasAttributes();
        if (hasAttrs != ((Element) arg).hasAttributes()) {
            return false;
        }
        if (hasAttrs) {
            NamedNodeMap map1 = getAttributes();
            NamedNodeMap map2 = ((Element) arg).getAttributes();
            int len = map1.getLength();
            if (len != map2.getLength()) {
                return false;
            }
            for (int i = 0; i < len; i++) {
                Node n1 = map1.item(i);
                if (n1.getLocalName() == null) { Node n2 = map2.getNamedItem(n1.getNodeName());
                    if (n2 == null || !((NodeImpl) n1).isEqualNode(n2)) {
                        return false;
                    }
                }
                else {
                    Node n2 = map2.getNamedItemNS(n1.getNamespaceURI(),
                                                  n1.getLocalName());
                    if (n2 == null || !((NodeImpl) n1).isEqualNode(n2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public void setIdAttributeNode(Attr at, boolean makeId) {
        if (needsSyncData()) {
            synchronizeData();
        }
        if (ownerDocument.errorChecking) {
            if (isReadOnly()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(
                                     DOMException.NO_MODIFICATION_ALLOWED_ERR,
                                     msg);
            }

            if (at.getOwnerElement() != this) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_FOUND_ERR", null);
                throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
            }
        }
        ((AttrImpl) at).isIdAttribute(makeId);
        if (!makeId) {
            ownerDocument.removeIdentifier(at.getValue());
        }
        else {
            ownerDocument.putIdentifier(at.getValue(), this);
        }
    }


    public void setIdAttribute(String name, boolean makeId) {
        if (needsSyncData()) {
            synchronizeData();
        }
        Attr at = getAttributeNode(name);

                if( at == null){
                String msg = DOMMessageFormatter.formatMessage(
                                                                        DOMMessageFormatter.DOM_DOMAIN,
                                                                        "NOT_FOUND_ERR", null);
            throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
                }

                if (ownerDocument.errorChecking) {
            if (isReadOnly()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(
                                     DOMException.NO_MODIFICATION_ALLOWED_ERR,
                                     msg);
            }

            if (at.getOwnerElement() != this) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_FOUND_ERR", null);
                throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
            }
        }

        ((AttrImpl) at).isIdAttribute(makeId);
        if (!makeId) {
            ownerDocument.removeIdentifier(at.getValue());
        }
        else {
            ownerDocument.putIdentifier(at.getValue(), this);
        }
    }


    public void setIdAttributeNS(String namespaceURI, String localName,
                                    boolean makeId) {
        if (needsSyncData()) {
            synchronizeData();
        }
        if (namespaceURI != null) {
            namespaceURI = (namespaceURI.length() == 0)? null : namespaceURI;
        }
        Attr at = getAttributeNodeNS(namespaceURI, localName);

                if( at == null){
                String msg = DOMMessageFormatter.formatMessage(
                                                                        DOMMessageFormatter.DOM_DOMAIN,
                                                                        "NOT_FOUND_ERR", null);
            throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
                }

                if (ownerDocument.errorChecking) {
            if (isReadOnly()) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(
                                     DOMException.NO_MODIFICATION_ALLOWED_ERR,
                                     msg);
            }

            if (at.getOwnerElement() != this) {
                String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NOT_FOUND_ERR", null);
                throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
            }
        }
        ((AttrImpl) at).isIdAttribute(makeId);
        if (!makeId) {
            ownerDocument.removeIdentifier(at.getValue());
        }
        else {
            ownerDocument.putIdentifier(at.getValue(), this);
        }
   }


     public String getTypeName() {
        return null;
     }


    public String getTypeNamespace() {
        return null;
    }


    public boolean isDerivedFrom(String typeNamespaceArg,
                                 String typeNameArg,
                                 int derivationMethod) {

        return false;
    }


    public TypeInfo getSchemaTypeInfo(){
        if(needsSyncData()) {
            synchronizeData();
        }
        return this;
    }

    public void setReadOnly(boolean readOnly, boolean deep) {
        super.setReadOnly(readOnly,deep);
        if (attributes != null) {
            attributes.setReadOnly(readOnly,true);
        }
    }



    protected void synchronizeData() {

        needsSyncData(false);

        boolean orig = ownerDocument.getMutationEvents();
        ownerDocument.setMutationEvents(false);

        setupDefaultAttributes();

        ownerDocument.setMutationEvents(orig);

    } void moveSpecifiedAttributes(ElementImpl el) {
        if (needsSyncData()) {
            synchronizeData();
        }
        if (el.hasAttributes()) {
            if (attributes == null) {
                attributes = new AttributeMap(this, null);
            }
            attributes.moveSpecifiedAttributes(el.attributes);
        }
    }


    protected void setupDefaultAttributes() {
        NamedNodeMapImpl defaults = getDefaultAttributes();
        if (defaults != null) {
            attributes = new AttributeMap(this, defaults);
        }
    }


    protected void reconcileDefaultAttributes() {
        if (attributes != null) {
            NamedNodeMapImpl defaults = getDefaultAttributes();
            attributes.reconcileDefaults(defaults);
        }
    }


    protected NamedNodeMapImpl getDefaultAttributes() {

        DocumentTypeImpl doctype =
            (DocumentTypeImpl) ownerDocument.getDoctype();
        if (doctype == null) {
            return null;
        }
        ElementDefinitionImpl eldef =
            (ElementDefinitionImpl)doctype.getElements()
                                               .getNamedItem(getNodeName());
        if (eldef == null) {
            return null;
        }
        return (NamedNodeMapImpl) eldef.getAttributes();

    } }