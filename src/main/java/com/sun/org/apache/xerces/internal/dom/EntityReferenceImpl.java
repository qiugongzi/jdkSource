


package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.URI;
import org.w3c.dom.DocumentType;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class EntityReferenceImpl
extends ParentNode
implements EntityReference {

    static final long serialVersionUID = -7381452955687102062L;

    protected String name;

    protected String baseURI;



    public EntityReferenceImpl(CoreDocumentImpl ownerDoc, String name) {
        super(ownerDoc);
        this.name = name;
        isReadOnly(true);
        needsSyncChildren(true);
    }

    public short getNodeType() {
        return Node.ENTITY_REFERENCE_NODE;
    }


    public String getNodeName() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return name;
    }


    public Node cloneNode(boolean deep) {
        EntityReferenceImpl er = (EntityReferenceImpl)super.cloneNode(deep);
        er.setReadOnly(true, deep);
        return er;
    }


    public String getBaseURI() {
        if (needsSyncData()) {
            synchronizeData();
        }
        if (baseURI == null) {
            DocumentType doctype;
            NamedNodeMap entities;
            EntityImpl entDef;
            if (null != (doctype = getOwnerDocument().getDoctype()) &&
                null != (entities = doctype.getEntities())) {

                entDef = (EntityImpl)entities.getNamedItem(getNodeName());
                if (entDef !=null) {
                    return entDef.getBaseURI();
                }
            }
        } else if (baseURI != null && baseURI.length() != 0 ) {try {
                return new URI(baseURI).toString();
            }
            catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e){
                return null;
            }
        }
        return baseURI;
    }



    public void setBaseURI(String uri){
        if (needsSyncData()) {
            synchronizeData();
        }
        baseURI = uri;
    }


    protected String getEntityRefValue (){
        if (needsSyncChildren()){
            synchronizeChildren();
        }

        String value = "";
        if (firstChild != null){
          if (firstChild.getNodeType() == Node.ENTITY_REFERENCE_NODE){
              value = ((EntityReferenceImpl)firstChild).getEntityRefValue();
          }
          else if (firstChild.getNodeType() == Node.TEXT_NODE){
            value = firstChild.getNodeValue();
          }
          else {
             return null;
          }

          if (firstChild.nextSibling == null){
            return value;
          }
          else {
            StringBuffer buff = new StringBuffer(value);
            ChildNode next = firstChild.nextSibling;
            while (next != null){

                if (next.getNodeType() == Node.ENTITY_REFERENCE_NODE){
                   value = ((EntityReferenceImpl)next).getEntityRefValue();
                }
                else if (next.getNodeType() == Node.TEXT_NODE){
                  value = next.getNodeValue();
                }
                else {
                    return null;
                }
                buff.append(value);
                next = next.nextSibling;

            }
            return buff.toString();
          }
        }
        return "";
    }


    protected void synchronizeChildren() {
        needsSyncChildren(false);

        DocumentType doctype;
        NamedNodeMap entities;
        EntityImpl entDef;
        if (null != (doctype = getOwnerDocument().getDoctype()) &&
            null != (entities = doctype.getEntities())) {

            entDef = (EntityImpl)entities.getNamedItem(getNodeName());

            if (entDef == null)
                return;

            isReadOnly(false);
            for (Node defkid = entDef.getFirstChild();
                defkid != null;
                defkid = defkid.getNextSibling()) {
                Node newkid = defkid.cloneNode(true);
                insertBefore(newkid, null);
            }
            setReadOnly(true, true);
        }
    }



    public void setReadOnly(boolean readOnly, boolean deep) {

        if (needsSyncData()) {
            synchronizeData();
        }
        if (deep) {

            if (needsSyncChildren()) {
                synchronizeChildren();
            }
            for (ChildNode mykid = firstChild;
                 mykid != null;
                 mykid = mykid.nextSibling) {

                mykid.setReadOnly(readOnly,true);

            }
        }
        isReadOnly(readOnly);
    } }