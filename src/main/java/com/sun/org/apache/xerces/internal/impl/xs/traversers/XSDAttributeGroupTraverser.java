

package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import org.w3c.dom.Element;


class XSDAttributeGroupTraverser extends XSDAbstractTraverser {

    XSDAttributeGroupTraverser (XSDHandler handler,
            XSAttributeChecker gAttrCheck) {

        super(handler, gAttrCheck);
    }


    XSAttributeGroupDecl traverseLocal(Element elmNode,
            XSDocumentInfo schemaDoc,
            SchemaGrammar grammar) {

        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, false, schemaDoc);

        QName   refAttr = (QName)   attrValues[XSAttributeChecker.ATTIDX_REF];

        XSAttributeGroupDecl attrGrp = null;

        if (refAttr == null) {
            reportSchemaError("s4s-att-must-appear", new Object[]{"attributeGroup (local)", "ref"}, elmNode);
            fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return null;
        }

        attrGrp = (XSAttributeGroupDecl)fSchemaHandler.getGlobalDecl(schemaDoc, XSDHandler.ATTRIBUTEGROUP_TYPE, refAttr, elmNode);

        Element child = DOMUtil.getFirstChildElement(elmNode);
        if (child != null) {
            String childName = DOMUtil.getLocalName(child);
            if (childName.equals(SchemaSymbols.ELT_ANNOTATION)) {
                traverseAnnotationDecl(child, attrValues, false, schemaDoc);
                child = DOMUtil.getNextSiblingElement(child);
            } else {
                String text = DOMUtil.getSyntheticAnnotation(child);
                if (text != null) {
                    traverseSyntheticAnnotation(child, text, attrValues, false, schemaDoc);
                }
            }

            if (child != null) {
                Object[] args = new Object [] {refAttr.rawname, "(annotation?)", DOMUtil.getLocalName(child)};
                reportSchemaError("s4s-elt-must-match.1", args, child);
            }
        } fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return attrGrp;

    } XSAttributeGroupDecl traverseGlobal(Element elmNode,
            XSDocumentInfo schemaDoc,
            SchemaGrammar grammar) {

        XSAttributeGroupDecl attrGrp = new XSAttributeGroupDecl();

        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, true, schemaDoc);

        String  nameAttr   = (String) attrValues[XSAttributeChecker.ATTIDX_NAME];

        if (nameAttr == null) {
            reportSchemaError("s4s-att-must-appear", new Object[]{"attributeGroup (global)", "name"}, elmNode);
            nameAttr = NO_NAME;
        }

        attrGrp.fName = nameAttr;
        attrGrp.fTargetNamespace = schemaDoc.fTargetNamespace;

        Element child = DOMUtil.getFirstChildElement(elmNode);
        XSAnnotationImpl annotation = null;

        if (child!=null && DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
            annotation = traverseAnnotationDecl(child, attrValues, false, schemaDoc);
            child = DOMUtil.getNextSiblingElement(child);
        }
        else {
            String text = DOMUtil.getSyntheticAnnotation(elmNode);
            if (text != null) {
                annotation = traverseSyntheticAnnotation(elmNode, text, attrValues, false, schemaDoc);
            }
        }

        Element nextNode = traverseAttrsAndAttrGrps(child, attrGrp, schemaDoc, grammar, null);
        if (nextNode!=null) {
            Object[] args = new Object [] {nameAttr, "(annotation?, ((attribute | attributeGroup)*, anyAttribute?))", DOMUtil.getLocalName(nextNode)};
            reportSchemaError("s4s-elt-must-match.1", args, nextNode);
        }

        if (nameAttr.equals(NO_NAME)) {
            fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return null;
        }

        attrGrp.removeProhibitedAttrs();

        XSAttributeGroupDecl redefinedAttrGrp = (XSAttributeGroupDecl)fSchemaHandler.getGrpOrAttrGrpRedefinedByRestriction(
                XSDHandler.ATTRIBUTEGROUP_TYPE,
                new QName(XMLSymbols.EMPTY_STRING, nameAttr, nameAttr, schemaDoc.fTargetNamespace),
                schemaDoc, elmNode);
        if(redefinedAttrGrp != null) {
            Object[] errArgs = attrGrp.validRestrictionOf(nameAttr, redefinedAttrGrp);
            if (errArgs != null) {
                reportSchemaError((String)errArgs[errArgs.length-1], errArgs, child);
                reportSchemaError("src-redefine.7.2.2", new Object [] {nameAttr, errArgs[errArgs.length-1]}, child);
            }
        }

        XSObjectList annotations;
        if (annotation != null) {
            annotations = new XSObjectListImpl();
            ((XSObjectListImpl)annotations).addXSObject (annotation);
        } else {
            annotations = XSObjectListImpl.EMPTY_LIST;
        }

        attrGrp.fAnnotations = annotations;

        if (grammar.getGlobalAttributeGroupDecl(attrGrp.fName) == null) {
            grammar.addGlobalAttributeGroupDecl(attrGrp);
        }

        final String loc = fSchemaHandler.schemaDocument2SystemId(schemaDoc);
        final XSAttributeGroupDecl attrGrp2 = grammar.getGlobalAttributeGroupDecl(attrGrp.fName, loc);
        if (attrGrp2 == null) {
            grammar.addGlobalAttributeGroupDecl(attrGrp, loc);
        }

        if (fSchemaHandler.fTolerateDuplicates) {
            if (attrGrp2 != null) {
                attrGrp = attrGrp2;
            }
            fSchemaHandler.addGlobalAttributeGroupDecl(attrGrp);
        }

        fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        return attrGrp;

    } }