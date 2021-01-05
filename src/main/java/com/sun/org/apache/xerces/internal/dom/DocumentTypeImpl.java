


package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;


public class DocumentTypeImpl
    extends ParentNode
    implements DocumentType {

    static final long serialVersionUID = 7751299192316526485L;

    protected String name;


    protected NamedNodeMapImpl entities;


    protected NamedNodeMapImpl notations;

    protected NamedNodeMapImpl elements;

    protected String publicID;

    protected String systemID;

    protected String internalSubset;


    private int doctypeNumber=0;

    private Map<String, UserDataRecord> userData =  null;



    private static final ObjectStreamField[] serialPersistentFields =
        new ObjectStreamField[] {
            new ObjectStreamField("name", String.class),
            new ObjectStreamField("entities", NamedNodeMapImpl.class),
            new ObjectStreamField("notations", NamedNodeMapImpl.class),
            new ObjectStreamField("elements", NamedNodeMapImpl.class),
            new ObjectStreamField("publicID", String.class),
            new ObjectStreamField("systemID", String.class),
            new ObjectStreamField("internalSubset", String.class),
            new ObjectStreamField("doctypeNumber", int.class),
            new ObjectStreamField("userData", Hashtable.class),
        };

    public DocumentTypeImpl(CoreDocumentImpl ownerDocument, String name) {
        super(ownerDocument);

        this.name = name;
        entities  = new NamedNodeMapImpl(this);
        notations = new NamedNodeMapImpl(this);

        elements = new NamedNodeMapImpl(this);

    } public DocumentTypeImpl(CoreDocumentImpl ownerDocument,
                            String qualifiedName,
                            String publicID, String systemID) {
        this(ownerDocument, qualifiedName);
        this.publicID = publicID;
        this.systemID = systemID;

    } public String getPublicId() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return publicID;
    }

    public String getSystemId() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return systemID;
    }


    public void setInternalSubset(String internalSubset) {
        if (needsSyncData()) {
            synchronizeData();
        }
        this.internalSubset = internalSubset;
    }


    public String getInternalSubset() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return internalSubset;
    }

    public short getNodeType() {
        return Node.DOCUMENT_TYPE_NODE;
    }


    public String getNodeName() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return name;
    }


    public Node cloneNode(boolean deep) {

        DocumentTypeImpl newnode = (DocumentTypeImpl)super.cloneNode(deep);
        newnode.entities  = entities.cloneMap(newnode);
        newnode.notations = notations.cloneMap(newnode);
        newnode.elements  = elements.cloneMap(newnode);

        return newnode;

    } public String getTextContent() throws DOMException {
        return null;
    }


    public void setTextContent(String textContent)
        throws DOMException {
        }


    public boolean isEqualNode(Node arg) {

        if (!super.isEqualNode(arg)) {
            return false;
        }

        if (needsSyncData()) {
            synchronizeData();
        }
        DocumentTypeImpl argDocType = (DocumentTypeImpl) arg;

        if ((getPublicId() == null && argDocType.getPublicId() != null)
            || (getPublicId() != null && argDocType.getPublicId() == null)
            || (getSystemId() == null && argDocType.getSystemId() != null)
            || (getSystemId() != null && argDocType.getSystemId() == null)
            || (getInternalSubset() == null
                && argDocType.getInternalSubset() != null)
            || (getInternalSubset() != null
                && argDocType.getInternalSubset() == null)) {
            return false;
        }

        if (getPublicId() != null) {
            if (!getPublicId().equals(argDocType.getPublicId())) {
                return false;
            }
        }

        if (getSystemId() != null) {
            if (!getSystemId().equals(argDocType.getSystemId())) {
                return false;
            }
        }

        if (getInternalSubset() != null) {
            if (!getInternalSubset().equals(argDocType.getInternalSubset())) {
                return false;
            }
        }

        NamedNodeMapImpl argEntities = argDocType.entities;

        if ((entities == null && argEntities != null)
            || (entities != null && argEntities == null))
            return false;

        if (entities != null && argEntities != null) {
            if (entities.getLength() != argEntities.getLength())
                return false;

            for (int index = 0; entities.item(index) != null; index++) {
                Node entNode1 = entities.item(index);
                Node entNode2 =
                    argEntities.getNamedItem(entNode1.getNodeName());

                if (!((NodeImpl) entNode1).isEqualNode((NodeImpl) entNode2))
                    return false;
            }
        }

        NamedNodeMapImpl argNotations = argDocType.notations;

        if ((notations == null && argNotations != null)
            || (notations != null && argNotations == null))
            return false;

        if (notations != null && argNotations != null) {
            if (notations.getLength() != argNotations.getLength())
                return false;

            for (int index = 0; notations.item(index) != null; index++) {
                Node noteNode1 = notations.item(index);
                Node noteNode2 =
                    argNotations.getNamedItem(noteNode1.getNodeName());

                if (!((NodeImpl) noteNode1).isEqualNode((NodeImpl) noteNode2))
                    return false;
            }
        }

        return true;
    } void setOwnerDocument(CoreDocumentImpl doc) {
        super.setOwnerDocument(doc);
        entities.setOwnerDocument(doc);
        notations.setOwnerDocument(doc);
        elements.setOwnerDocument(doc);
    }


    protected int getNodeNumber() {
         if (getOwnerDocument()!=null)
            return super.getNodeNumber();

         if (doctypeNumber==0) {

            CoreDOMImplementationImpl cd = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
            doctypeNumber = cd.assignDocTypeNumber();
         }
         return doctypeNumber;
    }

    public String getName() {

        if (needsSyncData()) {
            synchronizeData();
        }
        return name;

    } public NamedNodeMap getEntities() {
        if (needsSyncChildren()) {
            synchronizeChildren();
            }
        return entities;
    }


    public NamedNodeMap getNotations() {
        if (needsSyncChildren()) {
            synchronizeChildren();
            }
        return notations;
    }

    public void setReadOnly(boolean readOnly, boolean deep) {

        if (needsSyncChildren()) {
            synchronizeChildren();
        }
        super.setReadOnly(readOnly, deep);

        elements.setReadOnly(readOnly, true);
        entities.setReadOnly(readOnly, true);
        notations.setReadOnly(readOnly, true);

    } public NamedNodeMap getElements() {
        if (needsSyncChildren()) {
            synchronizeChildren();
        }
        return elements;
    }

    public Object setUserData(String key,
    Object data, UserDataHandler handler) {
        if(userData == null)
            userData = new HashMap<>();
        if (data == null) {
            if (userData != null) {
                UserDataRecord udr = userData.remove(key);
                if (udr != null) {
                    return udr.fData;
                }
            }
            return null;
        }
        else {
            UserDataRecord udr = userData.put(key, new UserDataRecord(data, handler));
            if (udr != null) {
                return udr.fData;
            }
        }
        return null;
    }

    public Object getUserData(String key) {
        if (userData == null) {
            return null;
        }
        UserDataRecord udr = userData.get(key);
        if (udr != null) {
            return udr.fData;
        }
        return null;
    }

    @Override
    protected Map<String, UserDataRecord> getUserDataRecord(){
        return userData;
    }


    private void writeObject(ObjectOutputStream out) throws IOException {
        Hashtable<String, UserDataRecord> ud = (userData == null)? null : new Hashtable<>(userData);

        ObjectOutputStream.PutField pf = out.putFields();
        pf.put("name", name);
        pf.put("entities", entities);
        pf.put("notations", notations);
        pf.put("elements", elements);
        pf.put("publicID", publicID);
        pf.put("systemID", systemID);
        pf.put("internalSubset", internalSubset);
        pf.put("doctypeNumber", doctypeNumber);
        pf.put("userData", ud);
        out.writeFields();
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in)
                        throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField gf = in.readFields();
        name = (String)gf.get("name", null);
        entities = (NamedNodeMapImpl)gf.get("entities", null);
        notations = (NamedNodeMapImpl)gf.get("notations", null);
        elements = (NamedNodeMapImpl)gf.get("elements", null);
        publicID = (String)gf.get("publicID", null);
        systemID = (String)gf.get("systemID", null);
        internalSubset = (String)gf.get("internalSubset", null);
        doctypeNumber = gf.get("doctypeNumber", 0);

        Hashtable<String, UserDataRecord> ud =
                (Hashtable<String, UserDataRecord>)gf.get("userData", null);

        if (ud != null) userData = new HashMap<>(ud);
    }
}