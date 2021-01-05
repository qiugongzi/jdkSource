


package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Entity;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;


public class EntityImpl
    extends ParentNode
    implements Entity {

    static final long serialVersionUID = -3575760943444303423L;

    protected String name;


    protected String publicId;


    protected String systemId;


    protected String encoding;



    protected String inputEncoding;


    protected String version;



    protected String notationName;


    protected String baseURI;

    public EntityImpl(CoreDocumentImpl ownerDoc, String name) {
        super(ownerDoc);
        this.name = name;
        isReadOnly(true);
    }

    public short getNodeType() {
        return Node.ENTITY_NODE;
    }


    public String getNodeName() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return name;
    }

    public void setNodeValue(String x)
        throws DOMException {
        if (ownerDocument.errorChecking && isReadOnly()) {
            String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, msg);
        }
    }

    public void setPrefix(String prefix)
        throws DOMException
    {
        if (ownerDocument.errorChecking && isReadOnly()) {
            throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,
                  DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN,
                    "NO_MODIFICATION_ALLOWED_ERR", null));
        }
    }

    public Node cloneNode(boolean deep) {
        EntityImpl newentity = (EntityImpl)super.cloneNode(deep);
        newentity.setReadOnly(true, deep);
        return newentity;
    }

    public String getPublicId() {

        if (needsSyncData()) {
            synchronizeData();
        }
        return publicId;

    } public String getSystemId() {

        if (needsSyncData()) {
            synchronizeData();
        }
        return systemId;

    } public String getXmlVersion() {

       if (needsSyncData()) {
           synchronizeData();
       }
       return version;

   } public String getXmlEncoding() {

       if (needsSyncData()) {
           synchronizeData();
       }

       return encoding;

   } public String getNotationName() {

        if (needsSyncData()) {
            synchronizeData();
        }
        return notationName;

    } public void setPublicId(String id) {

        if (needsSyncData()) {
            synchronizeData();
        }
        publicId = id;

    } public void setXmlEncoding(String value) {
        if (needsSyncData()) {
            synchronizeData();
        }
        encoding = value;
    } public String getInputEncoding(){
        if (needsSyncData()) {
            synchronizeData();
        }
        return inputEncoding;
    }


    public void setInputEncoding(String inputEncoding){
        if (needsSyncData()) {
            synchronizeData();
        }
        this.inputEncoding = inputEncoding;
    }


    public void setXmlVersion(String value) {
        if (needsSyncData()) {
            synchronizeData();
        }
        version = value;
    } public void setSystemId(String id) {
        if (needsSyncData()) {
            synchronizeData();
        }
        systemId = id;

    } public void setNotationName(String name) {
        if (needsSyncData()) {
            synchronizeData();
        }
        notationName = name;

    } public String getBaseURI() {

        if (needsSyncData()) {
            synchronizeData();
        }
        return (baseURI!=null)?baseURI:((CoreDocumentImpl)getOwnerDocument()).getBaseURI();
    }


    public void setBaseURI(String uri){
        if (needsSyncData()) {
            synchronizeData();
        }
        baseURI = uri;
    }



}