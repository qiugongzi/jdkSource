


package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import java.util.Stack;
import java.util.Vector;

import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;


class XSDocumentInfo {

    protected SchemaNamespaceSupport fNamespaceSupport;
    protected SchemaNamespaceSupport fNamespaceSupportRoot;
    protected Stack SchemaNamespaceSupportStack = new Stack();

    protected boolean fAreLocalAttributesQualified;

    protected boolean fAreLocalElementsQualified;

    protected short fBlockDefault;
    protected short fFinalDefault;

    String fTargetNamespace;

    protected boolean fIsChameleonSchema;

    protected Element fSchemaElement;

    Vector fImportedNS = new Vector();

    protected ValidationState fValidationContext = new ValidationState();

    SymbolTable fSymbolTable = null;

    protected XSAttributeChecker fAttrChecker;

    protected Object [] fSchemaAttrs;

    protected XSAnnotationInfo fAnnotations = null;

    XSDocumentInfo (Element schemaRoot, XSAttributeChecker attrChecker, SymbolTable symbolTable)
                    throws XMLSchemaException {
        fSchemaElement = schemaRoot;
        initNamespaceSupport(schemaRoot);
        fIsChameleonSchema = false;

        fSymbolTable = symbolTable;
        fAttrChecker = attrChecker;

        if (schemaRoot != null) {
            Element root = schemaRoot;
            fSchemaAttrs = attrChecker.checkAttributes(root, true, this);
            if (fSchemaAttrs == null) {
                throw new XMLSchemaException(null, null);
            }
            fAreLocalAttributesQualified =
                ((XInt)fSchemaAttrs[XSAttributeChecker.ATTIDX_AFORMDEFAULT]).intValue() == SchemaSymbols.FORM_QUALIFIED;
            fAreLocalElementsQualified =
                ((XInt)fSchemaAttrs[XSAttributeChecker.ATTIDX_EFORMDEFAULT]).intValue() == SchemaSymbols.FORM_QUALIFIED;
            fBlockDefault =
                ((XInt)fSchemaAttrs[XSAttributeChecker.ATTIDX_BLOCKDEFAULT]).shortValue();
            fFinalDefault =
                ((XInt)fSchemaAttrs[XSAttributeChecker.ATTIDX_FINALDEFAULT]).shortValue();
            fTargetNamespace =
                (String)fSchemaAttrs[XSAttributeChecker.ATTIDX_TARGETNAMESPACE];
            if (fTargetNamespace != null)
                fTargetNamespace = symbolTable.addSymbol(fTargetNamespace);

            fNamespaceSupportRoot = new SchemaNamespaceSupport(fNamespaceSupport);

            fValidationContext.setNamespaceSupport(fNamespaceSupport);
            fValidationContext.setSymbolTable(symbolTable);
            }
    }


    private void initNamespaceSupport(Element schemaRoot) {
        fNamespaceSupport = new SchemaNamespaceSupport();
        fNamespaceSupport.reset();

        Node parent = schemaRoot.getParentNode();
        while (parent != null && parent.getNodeType() == Node.ELEMENT_NODE
                && !parent.getNodeName().equals("DOCUMENT_NODE"))
        {
            Element eparent = (Element) parent;
            NamedNodeMap map = eparent.getAttributes();
            int length = (map != null) ? map.getLength() : 0;
            for (int i = 0; i < length; i++) {
                Attr attr = (Attr) map.item(i);
                String uri = attr.getNamespaceURI();

                if (uri != null && uri.equals("http:String prefix = attr.getLocalName().intern();
                    if (prefix == "xmlns") prefix = "";
                    if (fNamespaceSupport.getURI(prefix) == null) {
                        fNamespaceSupport.declarePrefix(prefix,
                                attr.getValue().intern());
                    }
                }
            }
            parent = parent.getParentNode();
        }
    }

    void backupNSSupport(SchemaNamespaceSupport nsSupport) {
        SchemaNamespaceSupportStack.push(fNamespaceSupport);
        if (nsSupport == null)
            nsSupport = fNamespaceSupportRoot;
        fNamespaceSupport = new SchemaNamespaceSupport(nsSupport);

        fValidationContext.setNamespaceSupport(fNamespaceSupport);
    }

    void restoreNSSupport() {
        fNamespaceSupport = (SchemaNamespaceSupport)SchemaNamespaceSupportStack.pop();
        fValidationContext.setNamespaceSupport(fNamespaceSupport);
    }

    public String toString() {
        return fTargetNamespace == null?"no targetNamspace":"targetNamespace is " + fTargetNamespace;
    }

    public void addAllowedNS(String namespace) {
        fImportedNS.addElement(namespace == null ? "" : namespace);
    }

    public boolean isAllowedNS(String namespace) {
        return fImportedNS.contains(namespace == null ? "" : namespace);
    }

    private Vector fReportedTNS = null;
    final boolean needReportTNSError(String uri) {
        if (fReportedTNS == null)
            fReportedTNS = new Vector();
        else if (fReportedTNS.contains(uri))
            return false;
        fReportedTNS.addElement(uri);
        return true;
    }

    Object [] getSchemaAttrs () {
        return fSchemaAttrs;
    }

    void returnSchemaAttrs () {
        fAttrChecker.returnAttrArray (fSchemaAttrs, null);
        fSchemaAttrs = null;
    }

    void addAnnotation(XSAnnotationInfo info) {
        info.next = fAnnotations;
        fAnnotations = info;
    }

    XSAnnotationInfo getAnnotations() {
        return fAnnotations;
    }

    void removeAnnotations() {
        fAnnotations = null;
    }

}