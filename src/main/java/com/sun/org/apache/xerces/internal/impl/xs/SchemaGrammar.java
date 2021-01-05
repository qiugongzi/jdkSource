


package com.sun.org.apache.xerces.internal.impl.xs;

import java.lang.ref.SoftReference;
import java.util.Vector;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.util.ObjectListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.SimpleLocator;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMap4Types;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMapImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSConstants;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSParticle;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSWildcard;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import org.xml.sax.SAXException;



public class SchemaGrammar implements XSGrammar, XSNamespaceItem {

    String fTargetNamespace;

    SymbolHash fGlobalAttrDecls;
    SymbolHash fGlobalAttrGrpDecls;
    SymbolHash fGlobalElemDecls;
    SymbolHash fGlobalGroupDecls;
    SymbolHash fGlobalNotationDecls;
    SymbolHash fGlobalIDConstraintDecls;
    SymbolHash fGlobalTypeDecls;

    SymbolHash fGlobalAttrDeclsExt;
    SymbolHash fGlobalAttrGrpDeclsExt;
    SymbolHash fGlobalElemDeclsExt;
    SymbolHash fGlobalGroupDeclsExt;
    SymbolHash fGlobalNotationDeclsExt;
    SymbolHash fGlobalIDConstraintDeclsExt;
    SymbolHash fGlobalTypeDeclsExt;

    SymbolHash fAllGlobalElemDecls;

    XSDDescription fGrammarDescription = null;

    XSAnnotationImpl [] fAnnotations = null;

    int fNumAnnotations;

    private SymbolTable fSymbolTable = null;
    private SoftReference fSAXParser = null;
    private SoftReference fDOMParser = null;

    private boolean fIsImmutable = false;

    protected SchemaGrammar() {}


    public SchemaGrammar(String targetNamespace, XSDDescription grammarDesc,
                SymbolTable symbolTable) {
        fTargetNamespace = targetNamespace;
        fGrammarDescription = grammarDesc;
        fSymbolTable = symbolTable;

        fGlobalAttrDecls  = new SymbolHash();
        fGlobalAttrGrpDecls = new SymbolHash();
        fGlobalElemDecls = new SymbolHash();
        fGlobalGroupDecls = new SymbolHash();
        fGlobalNotationDecls = new SymbolHash();
        fGlobalIDConstraintDecls = new SymbolHash();

        fGlobalAttrDeclsExt  = new SymbolHash();
        fGlobalAttrGrpDeclsExt = new SymbolHash();
        fGlobalElemDeclsExt = new SymbolHash();
        fGlobalGroupDeclsExt = new SymbolHash();
        fGlobalNotationDeclsExt = new SymbolHash();
        fGlobalIDConstraintDeclsExt = new SymbolHash();
        fGlobalTypeDeclsExt = new SymbolHash();

        fAllGlobalElemDecls = new SymbolHash();

        if (fTargetNamespace == SchemaSymbols.URI_SCHEMAFORSCHEMA)
            fGlobalTypeDecls = SG_SchemaNS.fGlobalTypeDecls.makeClone();
        else
            fGlobalTypeDecls = new SymbolHash();
    } public SchemaGrammar(SchemaGrammar grammar) {
        fTargetNamespace = grammar.fTargetNamespace;
        fGrammarDescription = grammar.fGrammarDescription.makeClone();
        fSymbolTable = grammar.fSymbolTable; fGlobalAttrDecls  = grammar.fGlobalAttrDecls.makeClone();
        fGlobalAttrGrpDecls = grammar.fGlobalAttrGrpDecls.makeClone();
        fGlobalElemDecls = grammar.fGlobalElemDecls.makeClone();
        fGlobalGroupDecls = grammar.fGlobalGroupDecls.makeClone();
        fGlobalNotationDecls = grammar.fGlobalNotationDecls.makeClone();
        fGlobalIDConstraintDecls = grammar.fGlobalIDConstraintDecls.makeClone();
        fGlobalTypeDecls = grammar.fGlobalTypeDecls.makeClone();

        fGlobalAttrDeclsExt  = grammar.fGlobalAttrDeclsExt.makeClone();
        fGlobalAttrGrpDeclsExt = grammar.fGlobalAttrGrpDeclsExt.makeClone();
        fGlobalElemDeclsExt = grammar.fGlobalElemDeclsExt.makeClone();
        fGlobalGroupDeclsExt = grammar.fGlobalGroupDeclsExt.makeClone();
        fGlobalNotationDeclsExt = grammar.fGlobalNotationDeclsExt.makeClone();
        fGlobalIDConstraintDeclsExt = grammar.fGlobalIDConstraintDeclsExt.makeClone();
        fGlobalTypeDeclsExt = grammar.fGlobalTypeDeclsExt.makeClone();

        fAllGlobalElemDecls = grammar.fAllGlobalElemDecls.makeClone();

        fNumAnnotations = grammar.fNumAnnotations;
        if (fNumAnnotations > 0) {
            fAnnotations = new XSAnnotationImpl[grammar.fAnnotations.length];
            System.arraycopy(grammar.fAnnotations, 0, fAnnotations, 0, fNumAnnotations);
        }

        fSubGroupCount = grammar.fSubGroupCount;
        if (fSubGroupCount > 0) {
            fSubGroups = new XSElementDecl[grammar.fSubGroups.length];
            System.arraycopy(grammar.fSubGroups, 0, fSubGroups, 0, fSubGroupCount);
        }

        fCTCount = grammar.fCTCount;
        if (fCTCount > 0) {
            fComplexTypeDecls = new XSComplexTypeDecl[grammar.fComplexTypeDecls.length];
            fCTLocators = new SimpleLocator[grammar.fCTLocators.length];
            System.arraycopy(grammar.fComplexTypeDecls, 0, fComplexTypeDecls, 0, fCTCount);
            System.arraycopy(grammar.fCTLocators, 0, fCTLocators, 0, fCTCount);
        }

        fRGCount = grammar.fRGCount;
        if (fRGCount > 0) {
            fRedefinedGroupDecls = new XSGroupDecl[grammar.fRedefinedGroupDecls.length];
            fRGLocators = new SimpleLocator[grammar.fRGLocators.length];
            System.arraycopy(grammar.fRedefinedGroupDecls, 0, fRedefinedGroupDecls, 0, fRGCount);
            System.arraycopy(grammar.fRGLocators, 0, fRGLocators, 0, fRGCount);
        }

        if (grammar.fImported != null) {
            fImported = new Vector();
            for (int i=0; i<grammar.fImported.size(); i++) {
                fImported.add(grammar.fImported.elementAt(i));
            }
        }

        if (grammar.fLocations != null) {
            for (int k=0; k<grammar.fLocations.size(); k++) {
                addDocument(null, (String)grammar.fLocations.elementAt(k));
            }
        }

    } private static final int BASICSET_COUNT = 29;
    private static final int FULLSET_COUNT  = 46;

    private static final int GRAMMAR_XS  = 1;
    private static final int GRAMMAR_XSI = 2;

    public static class BuiltinSchemaGrammar extends SchemaGrammar {

        private static final String EXTENDED_SCHEMA_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.xs.ExtendedSchemaDVFactoryImpl";


        public BuiltinSchemaGrammar(int grammar, short schemaVersion) {
            SchemaDVFactory schemaFactory;
            if (schemaVersion == Constants.SCHEMA_VERSION_1_0) {
                schemaFactory = SchemaDVFactory.getInstance();
            }
            else {
                schemaFactory = SchemaDVFactory.getInstance(EXTENDED_SCHEMA_FACTORY_CLASS);
            }

            if (grammar == GRAMMAR_XS) {
                fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;

                fGrammarDescription = new XSDDescription();
                fGrammarDescription.fContextType = XSDDescription.CONTEXT_PREPARSE;
                fGrammarDescription.setNamespace(SchemaSymbols.URI_SCHEMAFORSCHEMA);

                fGlobalAttrDecls  = new SymbolHash(1);
                fGlobalAttrGrpDecls = new SymbolHash(1);
                fGlobalElemDecls = new SymbolHash(1);
                fGlobalGroupDecls = new SymbolHash(1);
                fGlobalNotationDecls = new SymbolHash(1);
                fGlobalIDConstraintDecls = new SymbolHash(1);

                fGlobalAttrDeclsExt  = new SymbolHash(1);
                fGlobalAttrGrpDeclsExt = new SymbolHash(1);
                fGlobalElemDeclsExt = new SymbolHash(1);
                fGlobalGroupDeclsExt = new SymbolHash(1);
                fGlobalNotationDeclsExt = new SymbolHash(1);
                fGlobalIDConstraintDeclsExt = new SymbolHash(1);
                fGlobalTypeDeclsExt = new SymbolHash(1);

                fAllGlobalElemDecls = new SymbolHash(1);

                fGlobalTypeDecls = schemaFactory.getBuiltInTypes();

                int length = fGlobalTypeDecls.getLength();
                XSTypeDefinition [] typeDefinitions = new XSTypeDefinition[length];
                fGlobalTypeDecls.getValues(typeDefinitions, 0);
                for (int i = 0; i < length; ++i) {
                    XSTypeDefinition xtd = typeDefinitions[i];
                    if (xtd instanceof XSSimpleTypeDecl) {
                        ((XSSimpleTypeDecl) xtd).setNamespaceItem(this);
                    }
                }

                fGlobalTypeDecls.put(fAnyType.getName(), fAnyType);
            }
            else if (grammar == GRAMMAR_XSI) {
                fTargetNamespace = SchemaSymbols.URI_XSI;
                fGrammarDescription = new XSDDescription();
                fGrammarDescription.fContextType = XSDDescription.CONTEXT_PREPARSE;
                fGrammarDescription.setNamespace(SchemaSymbols.URI_XSI);

                fGlobalAttrGrpDecls = new SymbolHash(1);
                fGlobalElemDecls = new SymbolHash(1);
                fGlobalGroupDecls = new SymbolHash(1);
                fGlobalNotationDecls = new SymbolHash(1);
                fGlobalIDConstraintDecls = new SymbolHash(1);
                fGlobalTypeDecls = new SymbolHash(1);

                fGlobalAttrDeclsExt  = new SymbolHash(1);
                fGlobalAttrGrpDeclsExt = new SymbolHash(1);
                fGlobalElemDeclsExt = new SymbolHash(1);
                fGlobalGroupDeclsExt = new SymbolHash(1);
                fGlobalNotationDeclsExt = new SymbolHash(1);
                fGlobalIDConstraintDeclsExt = new SymbolHash(1);
                fGlobalTypeDeclsExt = new SymbolHash(1);

                fAllGlobalElemDecls = new SymbolHash(1);

                fGlobalAttrDecls  = new SymbolHash(8);
                String name = null;
                String tns = null;
                XSSimpleType type = null;
                short scope = XSConstants.SCOPE_GLOBAL;

                name = SchemaSymbols.XSI_TYPE;
                tns = SchemaSymbols.URI_XSI;
                type = schemaFactory.getBuiltInType(SchemaSymbols.ATTVAL_QNAME);
                fGlobalAttrDecls.put(name, new BuiltinAttrDecl(name, tns, type, scope));

                name = SchemaSymbols.XSI_NIL;
                tns = SchemaSymbols.URI_XSI;
                type = schemaFactory.getBuiltInType(SchemaSymbols.ATTVAL_BOOLEAN);
                fGlobalAttrDecls.put(name, new BuiltinAttrDecl(name, tns, type, scope));

                XSSimpleType anyURI = schemaFactory.getBuiltInType(SchemaSymbols.ATTVAL_ANYURI);

                name = SchemaSymbols.XSI_SCHEMALOCATION;
                tns = SchemaSymbols.URI_XSI;
                type = schemaFactory.createTypeList("#AnonType_schemaLocation", SchemaSymbols.URI_XSI, (short)0, anyURI, null);
                if (type instanceof XSSimpleTypeDecl) {
                    ((XSSimpleTypeDecl)type).setAnonymous(true);
                }
                fGlobalAttrDecls.put(name, new BuiltinAttrDecl(name, tns, type, scope));

                name = SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION;
                tns = SchemaSymbols.URI_XSI;
                type = anyURI;
                fGlobalAttrDecls.put(name, new BuiltinAttrDecl(name, tns, type, scope));
            }
        } public XMLGrammarDescription getGrammarDescription() {
            return fGrammarDescription.makeClone();
        } public void setImportedGrammars(Vector importedGrammars) {
            }
        public void addGlobalAttributeDecl(XSAttributeDecl decl) {
            }
        public void addGlobalAttributeDecl(XSAttributeDecl decl, String location) {
            }
        public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl) {
            }
        public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl, String location) {
            }
        public void addGlobalElementDecl(XSElementDecl decl) {
            }
        public void addGlobalElementDecl(XSElementDecl decl, String location) {
            }
        public void addGlobalElementDeclAll(XSElementDecl decl) {
            }
        public void addGlobalGroupDecl(XSGroupDecl decl) {
            }
        public void addGlobalGroupDecl(XSGroupDecl decl, String location) {
            }
        public void addGlobalNotationDecl(XSNotationDecl decl) {
            }
        public void addGlobalNotationDecl(XSNotationDecl decl, String location) {
            }
        public void addGlobalTypeDecl(XSTypeDefinition decl) {
            }
        public void addGlobalTypeDecl(XSTypeDefinition decl, String location) {
            }
        public void addGlobalComplexTypeDecl(XSComplexTypeDecl decl) {
            }
        public void addGlobalComplexTypeDecl(XSComplexTypeDecl decl, String location) {
            }
        public void addGlobalSimpleTypeDecl(XSSimpleType decl) {
            }
        public void addGlobalSimpleTypeDecl(XSSimpleType decl, String location) {
            }
        public void addComplexTypeDecl(XSComplexTypeDecl decl, SimpleLocator locator) {
            }
        public void addRedefinedGroupDecl(XSGroupDecl derived, XSGroupDecl base, SimpleLocator locator) {
            }
        public synchronized void addDocument(Object document, String location) {
            }

        synchronized DOMParser getDOMParser() {
            return null;
        }
        synchronized SAXParser getSAXParser() {
            return null;
        }
    }


    public static final class Schema4Annotations extends SchemaGrammar {


        public static final Schema4Annotations INSTANCE = new Schema4Annotations();


        private Schema4Annotations() {

            fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;

            fGrammarDescription = new XSDDescription();
            fGrammarDescription.fContextType = XSDDescription.CONTEXT_PREPARSE;
            fGrammarDescription.setNamespace(SchemaSymbols.URI_SCHEMAFORSCHEMA);

            fGlobalAttrDecls  = new SymbolHash(1);
            fGlobalAttrGrpDecls = new SymbolHash(1);
            fGlobalElemDecls = new SymbolHash(6);
            fGlobalGroupDecls = new SymbolHash(1);
            fGlobalNotationDecls = new SymbolHash(1);
            fGlobalIDConstraintDecls = new SymbolHash(1);

            fGlobalAttrDeclsExt  = new SymbolHash(1);
            fGlobalAttrGrpDeclsExt = new SymbolHash(1);
            fGlobalElemDeclsExt = new SymbolHash(6);
            fGlobalGroupDeclsExt = new SymbolHash(1);
            fGlobalNotationDeclsExt = new SymbolHash(1);
            fGlobalIDConstraintDeclsExt = new SymbolHash(1);
            fGlobalTypeDeclsExt = new SymbolHash(1);

            fAllGlobalElemDecls = new SymbolHash(6);

            fGlobalTypeDecls = SG_SchemaNS.fGlobalTypeDecls;

            XSElementDecl annotationDecl = createAnnotationElementDecl(SchemaSymbols.ELT_ANNOTATION);
            XSElementDecl documentationDecl = createAnnotationElementDecl(SchemaSymbols.ELT_DOCUMENTATION);
            XSElementDecl appinfoDecl = createAnnotationElementDecl(SchemaSymbols.ELT_APPINFO);

            fGlobalElemDecls.put(annotationDecl.fName, annotationDecl);
            fGlobalElemDecls.put(documentationDecl.fName, documentationDecl);
            fGlobalElemDecls.put(appinfoDecl.fName, appinfoDecl);

            fGlobalElemDeclsExt.put(","+annotationDecl.fName, annotationDecl);
            fGlobalElemDeclsExt.put(","+documentationDecl.fName, documentationDecl);
            fGlobalElemDeclsExt.put(","+appinfoDecl.fName, appinfoDecl);

            fAllGlobalElemDecls.put(annotationDecl, annotationDecl);
            fAllGlobalElemDecls.put(documentationDecl, documentationDecl);
            fAllGlobalElemDecls.put(appinfoDecl, appinfoDecl);

            XSComplexTypeDecl annotationType = new XSComplexTypeDecl();
            XSComplexTypeDecl documentationType = new XSComplexTypeDecl();
            XSComplexTypeDecl appinfoType = new XSComplexTypeDecl();

            annotationDecl.fType = annotationType;
            documentationDecl.fType = documentationType;
            appinfoDecl.fType = appinfoType;

            XSAttributeGroupDecl annotationAttrs = new XSAttributeGroupDecl();
            XSAttributeGroupDecl documentationAttrs = new XSAttributeGroupDecl();
            XSAttributeGroupDecl appinfoAttrs = new XSAttributeGroupDecl();

            {
                XSAttributeUseImpl annotationIDAttr = new XSAttributeUseImpl();
                annotationIDAttr.fAttrDecl = new XSAttributeDecl();
                annotationIDAttr.fAttrDecl.setValues(SchemaSymbols.ATT_ID, null, (XSSimpleType) fGlobalTypeDecls.get(SchemaSymbols.ATTVAL_ID),
                        XSConstants.VC_NONE, XSConstants.SCOPE_LOCAL, null, annotationType, null);
                annotationIDAttr.fUse = SchemaSymbols.USE_OPTIONAL;
                annotationIDAttr.fConstraintType = XSConstants.VC_NONE;

                XSAttributeUseImpl documentationSourceAttr = new XSAttributeUseImpl();
                documentationSourceAttr.fAttrDecl = new XSAttributeDecl();
                documentationSourceAttr.fAttrDecl.setValues(SchemaSymbols.ATT_SOURCE, null, (XSSimpleType) fGlobalTypeDecls.get(SchemaSymbols.ATTVAL_ANYURI),
                        XSConstants.VC_NONE, XSConstants.SCOPE_LOCAL, null, documentationType, null);
                documentationSourceAttr.fUse = SchemaSymbols.USE_OPTIONAL;
                documentationSourceAttr.fConstraintType = XSConstants.VC_NONE;

                XSAttributeUseImpl documentationLangAttr = new XSAttributeUseImpl();
                documentationLangAttr.fAttrDecl = new XSAttributeDecl();
                documentationLangAttr.fAttrDecl.setValues("lang".intern(), NamespaceContext.XML_URI, (XSSimpleType) fGlobalTypeDecls.get(SchemaSymbols.ATTVAL_LANGUAGE),
                        XSConstants.VC_NONE, XSConstants.SCOPE_LOCAL, null, documentationType, null);
                documentationLangAttr.fUse = SchemaSymbols.USE_OPTIONAL;
                documentationLangAttr.fConstraintType = XSConstants.VC_NONE;

                XSAttributeUseImpl appinfoSourceAttr = new XSAttributeUseImpl();
                appinfoSourceAttr.fAttrDecl = new XSAttributeDecl();
                appinfoSourceAttr.fAttrDecl.setValues(SchemaSymbols.ATT_SOURCE, null, (XSSimpleType) fGlobalTypeDecls.get(SchemaSymbols.ATTVAL_ANYURI),
                        XSConstants.VC_NONE, XSConstants.SCOPE_LOCAL, null, appinfoType, null);
                appinfoSourceAttr.fUse = SchemaSymbols.USE_OPTIONAL;
                appinfoSourceAttr.fConstraintType = XSConstants.VC_NONE;

                XSWildcardDecl otherAttrs = new XSWildcardDecl();
                otherAttrs.fNamespaceList = new String [] {fTargetNamespace, null};
                otherAttrs.fType = XSWildcard.NSCONSTRAINT_NOT;
                otherAttrs.fProcessContents = XSWildcard.PC_LAX;

                annotationAttrs.addAttributeUse(annotationIDAttr);
                annotationAttrs.fAttributeWC = otherAttrs;

                documentationAttrs.addAttributeUse(documentationSourceAttr);
                documentationAttrs.addAttributeUse(documentationLangAttr);
                documentationAttrs.fAttributeWC = otherAttrs;

                appinfoAttrs.addAttributeUse(appinfoSourceAttr);
                appinfoAttrs.fAttributeWC = otherAttrs;
            }

            XSParticleDecl annotationParticle = createUnboundedModelGroupParticle();
            {
                XSModelGroupImpl annotationChoice = new XSModelGroupImpl();
                annotationChoice.fCompositor = XSModelGroupImpl.MODELGROUP_CHOICE;
                annotationChoice.fParticleCount = 2;
                annotationChoice.fParticles = new XSParticleDecl[2];
                annotationChoice.fParticles[0] = createChoiceElementParticle(appinfoDecl);
                annotationChoice.fParticles[1] = createChoiceElementParticle(documentationDecl);
                annotationParticle.fValue = annotationChoice;
            }

            XSParticleDecl anyWCSequenceParticle = createUnboundedAnyWildcardSequenceParticle();

            annotationType.setValues("#AnonType_" + SchemaSymbols.ELT_ANNOTATION, fTargetNamespace, SchemaGrammar.fAnyType,
                    XSConstants.DERIVATION_RESTRICTION, XSConstants.DERIVATION_NONE, (short) (XSConstants.DERIVATION_EXTENSION | XSConstants.DERIVATION_RESTRICTION),
                    XSComplexTypeDecl.CONTENTTYPE_ELEMENT, false, annotationAttrs, null, annotationParticle, new XSObjectListImpl(null, 0));
            annotationType.setName("#AnonType_" + SchemaSymbols.ELT_ANNOTATION);
            annotationType.setIsAnonymous();

            documentationType.setValues("#AnonType_" + SchemaSymbols.ELT_DOCUMENTATION, fTargetNamespace, SchemaGrammar.fAnyType,
                    XSConstants.DERIVATION_RESTRICTION, XSConstants.DERIVATION_NONE, (short) (XSConstants.DERIVATION_EXTENSION | XSConstants.DERIVATION_RESTRICTION),
                    XSComplexTypeDecl.CONTENTTYPE_MIXED, false, documentationAttrs, null, anyWCSequenceParticle, new XSObjectListImpl(null, 0));
            documentationType.setName("#AnonType_" + SchemaSymbols.ELT_DOCUMENTATION);
            documentationType.setIsAnonymous();

            appinfoType.setValues("#AnonType_" + SchemaSymbols.ELT_APPINFO, fTargetNamespace, SchemaGrammar.fAnyType,
                    XSConstants.DERIVATION_RESTRICTION, XSConstants.DERIVATION_NONE, (short) (XSConstants.DERIVATION_EXTENSION | XSConstants.DERIVATION_RESTRICTION),
                    XSComplexTypeDecl.CONTENTTYPE_MIXED, false, appinfoAttrs, null, anyWCSequenceParticle, new XSObjectListImpl(null, 0));
            appinfoType.setName("#AnonType_" + SchemaSymbols.ELT_APPINFO);
            appinfoType.setIsAnonymous();

        } public XMLGrammarDescription getGrammarDescription() {
            return fGrammarDescription.makeClone();
        } public void setImportedGrammars(Vector importedGrammars) {
            }
        public void addGlobalAttributeDecl(XSAttributeDecl decl) {
            }
        public void addGlobalAttributeDecl(XSAttributeGroupDecl decl, String location) {
            }
        public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl) {
            }
        public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl, String location) {
            }
        public void addGlobalElementDecl(XSElementDecl decl) {
            }
        public void addGlobalElementDecl(XSElementDecl decl, String location) {
            }
        public void addGlobalElementDeclAll(XSElementDecl decl) {
            }
        public void addGlobalGroupDecl(XSGroupDecl decl) {
            }
        public void addGlobalGroupDecl(XSGroupDecl decl, String location) {
            }
        public void addGlobalNotationDecl(XSNotationDecl decl) {
            }
        public void addGlobalNotationDecl(XSNotationDecl decl, String location) {
            }
        public void addGlobalTypeDecl(XSTypeDefinition decl) {
            }
        public void addGlobalTypeDecl(XSTypeDefinition decl, String location) {
            }
        public void addGlobalComplexTypeDecl(XSComplexTypeDecl decl) {
            }
        public void addGlobalComplexTypeDecl(XSComplexTypeDecl decl, String location) {
            }
        public void addGlobalSimpleTypeDecl(XSSimpleType decl) {
            }
        public void addGlobalSimpleTypeDecl(XSSimpleType decl, String location) {
            }
        public void addComplexTypeDecl(XSComplexTypeDecl decl, SimpleLocator locator) {
            }
        public void addRedefinedGroupDecl(XSGroupDecl derived, XSGroupDecl base, SimpleLocator locator) {
            }
        public synchronized void addDocument(Object document, String location) {
            }

        synchronized DOMParser getDOMParser() {
            return null;
        }
        synchronized SAXParser getSAXParser() {
            return null;
        }

        private XSElementDecl createAnnotationElementDecl(String localName) {
            XSElementDecl eDecl = new XSElementDecl();
            eDecl.fName = localName;
            eDecl.fTargetNamespace = fTargetNamespace;
            eDecl.setIsGlobal();
            eDecl.fBlock = (XSConstants.DERIVATION_EXTENSION |
                    XSConstants.DERIVATION_RESTRICTION | XSConstants.DERIVATION_SUBSTITUTION);
            eDecl.setConstraintType(XSConstants.VC_NONE);
            return eDecl;
        }

        private XSParticleDecl createUnboundedModelGroupParticle() {
            XSParticleDecl particle = new XSParticleDecl();
            particle.fMinOccurs = 0;
            particle.fMaxOccurs = SchemaSymbols.OCCURRENCE_UNBOUNDED;
            particle.fType = XSParticleDecl.PARTICLE_MODELGROUP;
            return particle;
        }

        private XSParticleDecl createChoiceElementParticle(XSElementDecl ref) {
            XSParticleDecl particle = new XSParticleDecl();
            particle.fMinOccurs = 1;
            particle.fMaxOccurs = 1;
            particle.fType = XSParticleDecl.PARTICLE_ELEMENT;
            particle.fValue = ref;
            return particle;
        }

        private XSParticleDecl createUnboundedAnyWildcardSequenceParticle() {
            XSParticleDecl particle = createUnboundedModelGroupParticle();
            XSModelGroupImpl sequence = new XSModelGroupImpl();
            sequence.fCompositor = XSModelGroupImpl.MODELGROUP_SEQUENCE;
            sequence.fParticleCount = 1;
            sequence.fParticles = new XSParticleDecl[1];
            sequence.fParticles[0] = createAnyLaxWildcardParticle();
            particle.fValue = sequence;
            return particle;
        }

        private XSParticleDecl createAnyLaxWildcardParticle() {
            XSParticleDecl particle = new XSParticleDecl();
            particle.fMinOccurs = 1;
            particle.fMaxOccurs = 1;
            particle.fType = XSParticleDecl.PARTICLE_WILDCARD;

            XSWildcardDecl anyWC = new XSWildcardDecl();
            anyWC.fNamespaceList = null;
            anyWC.fType = XSWildcard.NSCONSTRAINT_ANY;
            anyWC.fProcessContents = XSWildcard.PC_LAX;

            particle.fValue = anyWC;
            return particle;
        }
    }

    public XMLGrammarDescription getGrammarDescription() {
        return fGrammarDescription;
    } public boolean isNamespaceAware () {
        return true;
    } Vector fImported = null;

    public void setImportedGrammars(Vector importedGrammars) {
        fImported = importedGrammars;
    }

    public Vector getImportedGrammars() {
        return fImported;
    }


    public final String getTargetNamespace() {
        return fTargetNamespace;
    } public void addGlobalAttributeDecl(XSAttributeDecl decl) {
        fGlobalAttrDecls.put(decl.fName, decl);
        decl.setNamespaceItem(this);
    }

    public void addGlobalAttributeDecl(XSAttributeDecl decl, String location) {
        fGlobalAttrDeclsExt.put(((location!=null) ? location : "") + "," + decl.fName, decl);
        if (decl.getNamespaceItem() == null) {
            decl.setNamespaceItem(this);
        }
    }


    public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl) {
        fGlobalAttrGrpDecls.put(decl.fName, decl);
        decl.setNamespaceItem(this);
    }

    public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl decl, String location) {
        fGlobalAttrGrpDeclsExt.put(((location!=null) ? location : "") + "," + decl.fName, decl);
        if (decl.getNamespaceItem() == null) {
            decl.setNamespaceItem(this);
        }
    }


    public void addGlobalElementDeclAll(XSElementDecl decl) {
        if (fAllGlobalElemDecls.get(decl) == null) {
            fAllGlobalElemDecls.put(decl, decl);
            if (decl.fSubGroup != null) {
               if (fSubGroupCount == fSubGroups.length)
                    fSubGroups = resize(fSubGroups, fSubGroupCount+INC_SIZE);
                fSubGroups[fSubGroupCount++] = decl;
            }
        }
    }

    public void addGlobalElementDecl(XSElementDecl decl) {
        fGlobalElemDecls.put(decl.fName, decl);
        decl.setNamespaceItem(this);
    }

    public void addGlobalElementDecl(XSElementDecl decl, String location) {
        fGlobalElemDeclsExt.put(((location != null) ? location : "") + "," + decl.fName, decl);
        if (decl.getNamespaceItem() == null) {
            decl.setNamespaceItem(this);
        }
    }


    public void addGlobalGroupDecl(XSGroupDecl decl) {
        fGlobalGroupDecls.put(decl.fName, decl);
        decl.setNamespaceItem(this);
    }

    public void addGlobalGroupDecl(XSGroupDecl decl, String location) {
        fGlobalGroupDeclsExt.put(((location!=null) ? location : "") + "," + decl.fName, decl);
        if (decl.getNamespaceItem() == null) {
            decl.setNamespaceItem(this);
        }
    }


    public void addGlobalNotationDecl(XSNotationDecl decl) {
        fGlobalNotationDecls.put(decl.fName, decl);
        decl.setNamespaceItem(this);
    }

    public void addGlobalNotationDecl(XSNotationDecl decl, String location) {
        fGlobalNotationDeclsExt.put(((location!=null) ? location : "") + "," +decl.fName, decl);
        if (decl.getNamespaceItem() == null) {
            decl.setNamespaceItem(this);
        }
    }


    public void addGlobalTypeDecl(XSTypeDefinition decl) {
        fGlobalTypeDecls.put(decl.getName(), decl);
        if (decl instanceof XSComplexTypeDecl) {
            ((XSComplexTypeDecl) decl).setNamespaceItem(this);
        }
        else if (decl instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl) decl).setNamespaceItem(this);
        }
    }

    public void addGlobalTypeDecl(XSTypeDefinition decl, String location) {
        fGlobalTypeDeclsExt.put(((location!=null) ? location : "") + "," + decl.getName(), decl);
        if (decl.getNamespaceItem() == null) {
            if (decl instanceof XSComplexTypeDecl) {
                ((XSComplexTypeDecl) decl).setNamespaceItem(this);
            }
            else if (decl instanceof XSSimpleTypeDecl) {
                ((XSSimpleTypeDecl) decl).setNamespaceItem(this);
            }
        }
    }


    public void addGlobalComplexTypeDecl(XSComplexTypeDecl decl) {
        fGlobalTypeDecls.put(decl.getName(), decl);
        decl.setNamespaceItem(this);
    }

    public void addGlobalComplexTypeDecl(XSComplexTypeDecl decl, String location) {
        fGlobalTypeDeclsExt.put(((location!=null) ? location : "") + "," + decl.getName(), decl);
        if (decl.getNamespaceItem() == null) {
            decl.setNamespaceItem(this);
        }
    }


    public void addGlobalSimpleTypeDecl(XSSimpleType decl) {
        fGlobalTypeDecls.put(decl.getName(), decl);
        if (decl instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl) decl).setNamespaceItem(this);
        }
    }

    public void addGlobalSimpleTypeDecl(XSSimpleType decl, String location) {
        fGlobalTypeDeclsExt.put(((location != null) ? location : "") + "," + decl.getName(), decl);
        if (decl.getNamespaceItem() == null && decl instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl) decl).setNamespaceItem(this);
        }
    }


    public final void addIDConstraintDecl(XSElementDecl elmDecl, IdentityConstraint decl) {
        elmDecl.addIDConstraint(decl);
        fGlobalIDConstraintDecls.put(decl.getIdentityConstraintName(), decl);
    }

    public final void addIDConstraintDecl(XSElementDecl elmDecl, IdentityConstraint decl, String location) {
        fGlobalIDConstraintDeclsExt.put(((location != null) ? location : "") + "," + decl.getIdentityConstraintName(), decl);
    }


    public final XSAttributeDecl getGlobalAttributeDecl(String declName) {
        return(XSAttributeDecl)fGlobalAttrDecls.get(declName);
    }

    public final XSAttributeDecl getGlobalAttributeDecl(String declName, String location) {
        return(XSAttributeDecl)fGlobalAttrDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }


    public final XSAttributeGroupDecl getGlobalAttributeGroupDecl(String declName) {
        return(XSAttributeGroupDecl)fGlobalAttrGrpDecls.get(declName);
    }

    public final XSAttributeGroupDecl getGlobalAttributeGroupDecl(String declName, String location) {
        return(XSAttributeGroupDecl)fGlobalAttrGrpDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }


    public final XSElementDecl getGlobalElementDecl(String declName) {
        return(XSElementDecl)fGlobalElemDecls.get(declName);
    }

    public final XSElementDecl getGlobalElementDecl(String declName, String location) {
        return(XSElementDecl)fGlobalElemDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }


    public final XSGroupDecl getGlobalGroupDecl(String declName) {
        return(XSGroupDecl)fGlobalGroupDecls.get(declName);
    }

    public final XSGroupDecl getGlobalGroupDecl(String declName, String location) {
        return(XSGroupDecl)fGlobalGroupDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }


    public final XSNotationDecl getGlobalNotationDecl(String declName) {
        return(XSNotationDecl)fGlobalNotationDecls.get(declName);
    }

    public final XSNotationDecl getGlobalNotationDecl(String declName, String location) {
        return(XSNotationDecl)fGlobalNotationDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }


    public final XSTypeDefinition getGlobalTypeDecl(String declName) {
        return(XSTypeDefinition)fGlobalTypeDecls.get(declName);
    }

    public final XSTypeDefinition getGlobalTypeDecl(String declName, String location) {
        return(XSTypeDefinition)fGlobalTypeDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }


    public final IdentityConstraint getIDConstraintDecl(String declName) {
        return(IdentityConstraint)fGlobalIDConstraintDecls.get(declName);
    }

    public final IdentityConstraint getIDConstraintDecl(String declName, String location) {
        return(IdentityConstraint)fGlobalIDConstraintDeclsExt.get(((location != null) ? location : "") + "," + declName);
    }


    public final boolean hasIDConstraints() {
        return fGlobalIDConstraintDecls.getLength() > 0;
    }

    private static final int INITIAL_SIZE = 16;
    private static final int INC_SIZE     = 16;

    private int fCTCount = 0;
    private XSComplexTypeDecl[] fComplexTypeDecls = new XSComplexTypeDecl[INITIAL_SIZE];
    private SimpleLocator[] fCTLocators = new SimpleLocator[INITIAL_SIZE];

    private static final int REDEFINED_GROUP_INIT_SIZE = 2;
    private int fRGCount = 0;
    private XSGroupDecl[] fRedefinedGroupDecls = new XSGroupDecl[REDEFINED_GROUP_INIT_SIZE];
    private SimpleLocator[] fRGLocators = new SimpleLocator[REDEFINED_GROUP_INIT_SIZE/2];

    boolean fFullChecked = false;


    public void addComplexTypeDecl(XSComplexTypeDecl decl, SimpleLocator locator) {
        if (fCTCount == fComplexTypeDecls.length) {
            fComplexTypeDecls = resize(fComplexTypeDecls, fCTCount+INC_SIZE);
            fCTLocators = resize(fCTLocators, fCTCount+INC_SIZE);
        }
        fCTLocators[fCTCount] = locator;
        fComplexTypeDecls[fCTCount++] = decl;
    }


    public void addRedefinedGroupDecl(XSGroupDecl derived, XSGroupDecl base, SimpleLocator locator) {
        if (fRGCount == fRedefinedGroupDecls.length) {
            fRedefinedGroupDecls = resize(fRedefinedGroupDecls, fRGCount << 1);
            fRGLocators = resize(fRGLocators, fRGCount);
        }
        fRGLocators[fRGCount/2] = locator;
        fRedefinedGroupDecls[fRGCount++] = derived;
        fRedefinedGroupDecls[fRGCount++] = base;
    }


    final XSComplexTypeDecl[] getUncheckedComplexTypeDecls() {
        if (fCTCount < fComplexTypeDecls.length) {
            fComplexTypeDecls = resize(fComplexTypeDecls, fCTCount);
            fCTLocators = resize(fCTLocators, fCTCount);
        }
        return fComplexTypeDecls;
    }


    final SimpleLocator[] getUncheckedCTLocators() {
        if (fCTCount < fCTLocators.length) {
            fComplexTypeDecls = resize(fComplexTypeDecls, fCTCount);
            fCTLocators = resize(fCTLocators, fCTCount);
        }
        return fCTLocators;
    }


    final XSGroupDecl[] getRedefinedGroupDecls() {
        if (fRGCount < fRedefinedGroupDecls.length) {
            fRedefinedGroupDecls = resize(fRedefinedGroupDecls, fRGCount);
            fRGLocators = resize(fRGLocators, fRGCount/2);
        }
        return fRedefinedGroupDecls;
    }


    final SimpleLocator[] getRGLocators() {
        if (fRGCount < fRedefinedGroupDecls.length) {
            fRedefinedGroupDecls = resize(fRedefinedGroupDecls, fRGCount);
            fRGLocators = resize(fRGLocators, fRGCount/2);
        }
        return fRGLocators;
    }


    final void setUncheckedTypeNum(int newSize) {
        fCTCount = newSize;
        fComplexTypeDecls = resize(fComplexTypeDecls, fCTCount);
        fCTLocators = resize(fCTLocators, fCTCount);
    }

    private int fSubGroupCount = 0;
    private XSElementDecl[] fSubGroups = new XSElementDecl[INITIAL_SIZE];


    final XSElementDecl[] getSubstitutionGroups() {
        if (fSubGroupCount < fSubGroups.length)
            fSubGroups = resize(fSubGroups, fSubGroupCount);
        return fSubGroups;
    }

    public final static XSComplexTypeDecl fAnyType = new XSAnyType();
    private static class XSAnyType extends XSComplexTypeDecl {
        public XSAnyType () {
            fName = SchemaSymbols.ATTVAL_ANYTYPE;
            super.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
            fBaseType = this;
            fDerivedBy = XSConstants.DERIVATION_RESTRICTION;
            fContentType = XSComplexTypeDecl.CONTENTTYPE_MIXED;

            fParticle = null;
            fAttrGrp = null;
        }

        public void setValues(String name, String targetNamespace,
                XSTypeDefinition baseType, short derivedBy, short schemaFinal,
                short block, short contentType,
                boolean isAbstract, XSAttributeGroupDecl attrGrp,
                XSSimpleType simpleType, XSParticleDecl particle) {
            }

        public void setName(String name){
            }

        public void setIsAbstractType() {
            }

        public void setContainsTypeID() {
            }

        public void setIsAnonymous() {
            }

        public void reset() {
            }

        public XSObjectList getAttributeUses() {
            return XSObjectListImpl.EMPTY_LIST;
        }

        public XSAttributeGroupDecl getAttrGrp() {
            XSWildcardDecl wildcard = new XSWildcardDecl();
            wildcard.fProcessContents = XSWildcardDecl.PC_LAX;
            XSAttributeGroupDecl attrGrp = new XSAttributeGroupDecl();
            attrGrp.fAttributeWC = wildcard;
            return attrGrp;
        }

        public XSWildcard getAttributeWildcard() {
            XSWildcardDecl wildcard = new XSWildcardDecl();
            wildcard.fProcessContents = XSWildcardDecl.PC_LAX;
            return wildcard;
        }

        public XSParticle getParticle() {
            XSWildcardDecl wildcard = new XSWildcardDecl();
            wildcard.fProcessContents = XSWildcardDecl.PC_LAX;
            XSParticleDecl particleW = new XSParticleDecl();
            particleW.fMinOccurs = 0;
            particleW.fMaxOccurs = SchemaSymbols.OCCURRENCE_UNBOUNDED;
            particleW.fType = XSParticleDecl.PARTICLE_WILDCARD;
            particleW.fValue = wildcard;
            XSModelGroupImpl group = new XSModelGroupImpl();
            group.fCompositor = XSModelGroupImpl.MODELGROUP_SEQUENCE;
            group.fParticleCount = 1;
            group.fParticles = new XSParticleDecl[1];
            group.fParticles[0] = particleW;
            XSParticleDecl particleG = new XSParticleDecl();
            particleG.fType = XSParticleDecl.PARTICLE_MODELGROUP;
            particleG.fValue = group;

            return particleG;
        }

        public XSObjectList getAnnotations() {
            return XSObjectListImpl.EMPTY_LIST;
        }

        public XSNamespaceItem getNamespaceItem() {
            return SG_SchemaNS;
        }
    }
    private static class BuiltinAttrDecl extends XSAttributeDecl {
        public BuiltinAttrDecl(String name, String tns,
                XSSimpleType type, short scope) {
            fName = name;
            super.fTargetNamespace = tns;
            fType = type;
            fScope = scope;
        }

        public void setValues(String name, String targetNamespace,
                XSSimpleType simpleType, short constraintType, short scope,
                ValidatedInfo valInfo, XSComplexTypeDecl enclosingCT) {
            }

        public void reset () {
            }

        public XSAnnotation getAnnotation() {
            return null;
        }

        public XSNamespaceItem getNamespaceItem() {
            return SG_XSI;
        }

    } public final static BuiltinSchemaGrammar SG_SchemaNS = new BuiltinSchemaGrammar(GRAMMAR_XS, Constants.SCHEMA_VERSION_1_0);
    private final static BuiltinSchemaGrammar SG_SchemaNSExtended = new BuiltinSchemaGrammar(GRAMMAR_XS, Constants.SCHEMA_VERSION_1_0_EXTENDED);

    public final static XSSimpleType fAnySimpleType = (XSSimpleType)SG_SchemaNS.getGlobalTypeDecl(SchemaSymbols.ATTVAL_ANYSIMPLETYPE);

    public final static BuiltinSchemaGrammar SG_XSI = new BuiltinSchemaGrammar(GRAMMAR_XSI, Constants.SCHEMA_VERSION_1_0);

    public static SchemaGrammar getS4SGrammar(short schemaVersion) {
        if (schemaVersion == Constants.SCHEMA_VERSION_1_0) {
            return SG_SchemaNS;
        }
        else {
            return SG_SchemaNSExtended;
        }
    }

    static final XSComplexTypeDecl[] resize(XSComplexTypeDecl[] oldArray, int newSize) {
        XSComplexTypeDecl[] newArray = new XSComplexTypeDecl[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }

    static final XSGroupDecl[] resize(XSGroupDecl[] oldArray, int newSize) {
        XSGroupDecl[] newArray = new XSGroupDecl[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }

    static final XSElementDecl[] resize(XSElementDecl[] oldArray, int newSize) {
        XSElementDecl[] newArray = new XSElementDecl[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }

    static final SimpleLocator[] resize(SimpleLocator[] oldArray, int newSize) {
        SimpleLocator[] newArray = new SimpleLocator[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }

    private static final short MAX_COMP_IDX = XSTypeDefinition.SIMPLE_TYPE;
    private static final boolean[] GLOBAL_COMP = {false,    true,     true,     true,     false,    true,     true,     false,    false,    false,    false,    true,     false,    false,    false,    true,     true      };

    private XSNamedMap[] fComponents = null;
    private ObjectList[] fComponentsExt = null;

    private Vector fDocuments = null;
    private Vector fLocations = null;

    public synchronized void addDocument(Object document, String location) {
        if (fDocuments == null) {
            fDocuments = new Vector();
            fLocations = new Vector();
        }
        fDocuments.addElement(document);
        fLocations.addElement(location);
    }

    public synchronized void removeDocument(int index) {
        if (fDocuments != null &&
            index >= 0 &&
            index < fDocuments.size()) {
            fDocuments.removeElementAt(index);
            fLocations.removeElementAt(index);
        }
    }


    public String getSchemaNamespace() {
        return fTargetNamespace;
    }

    synchronized DOMParser getDOMParser() {
        if (fDOMParser != null) {
            DOMParser parser = (DOMParser) fDOMParser.get();
            if (parser != null) {
                return parser;
            }
        }
        XML11Configuration config = new XML11Configuration(fSymbolTable);
        config.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE, true);
        config.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE, false);

        DOMParser parser = new DOMParser(config);
        try {
            parser.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.DEFER_NODE_EXPANSION_FEATURE, false);
        }
        catch (SAXException exc) {}
        fDOMParser = new SoftReference(parser);
        return parser;
    }

    synchronized SAXParser getSAXParser() {
        if (fSAXParser != null) {
            SAXParser parser = (SAXParser) fSAXParser.get();
            if (parser != null) {
                return parser;
            }
        }
        XML11Configuration config = new XML11Configuration(fSymbolTable);
        config.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE, true);
        config.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.VALIDATION_FEATURE, false);
        SAXParser parser = new SAXParser(config);
        fSAXParser = new SoftReference(parser);
        return parser;
    }


    public synchronized XSNamedMap getComponents(short objectType) {
        if (objectType <= 0 || objectType > MAX_COMP_IDX ||
            !GLOBAL_COMP[objectType]) {
            return XSNamedMapImpl.EMPTY_MAP;
        }

        if (fComponents == null)
            fComponents = new XSNamedMap[MAX_COMP_IDX+1];

        if (fComponents[objectType] == null) {
            SymbolHash table = null;
            switch (objectType) {
            case XSConstants.TYPE_DEFINITION:
            case XSTypeDefinition.COMPLEX_TYPE:
            case XSTypeDefinition.SIMPLE_TYPE:
                table = fGlobalTypeDecls;
                break;
            case XSConstants.ATTRIBUTE_DECLARATION:
                table = fGlobalAttrDecls;
                break;
            case XSConstants.ELEMENT_DECLARATION:
                table = fGlobalElemDecls;
                break;
            case XSConstants.ATTRIBUTE_GROUP:
                table = fGlobalAttrGrpDecls;
                break;
            case XSConstants.MODEL_GROUP_DEFINITION:
                table = fGlobalGroupDecls;
                break;
            case XSConstants.NOTATION_DECLARATION:
                table = fGlobalNotationDecls;
                break;
            }

            if (objectType == XSTypeDefinition.COMPLEX_TYPE ||
                objectType == XSTypeDefinition.SIMPLE_TYPE) {
                fComponents[objectType] = new XSNamedMap4Types(fTargetNamespace, table, objectType);
            }
            else {
                fComponents[objectType] = new XSNamedMapImpl(fTargetNamespace, table);
            }
        }

        return fComponents[objectType];
    }

    public synchronized ObjectList getComponentsExt(short objectType) {
        if (objectType <= 0 || objectType > MAX_COMP_IDX ||
            !GLOBAL_COMP[objectType]) {
            return ObjectListImpl.EMPTY_LIST;
        }

        if (fComponentsExt == null)
            fComponentsExt = new ObjectList[MAX_COMP_IDX+1];

        if (fComponentsExt[objectType] == null) {
            SymbolHash table = null;
            switch (objectType) {
            case XSConstants.TYPE_DEFINITION:
            case XSTypeDefinition.COMPLEX_TYPE:
            case XSTypeDefinition.SIMPLE_TYPE:
                table = fGlobalTypeDeclsExt;
                break;
            case XSConstants.ATTRIBUTE_DECLARATION:
                table = fGlobalAttrDeclsExt;
                break;
            case XSConstants.ELEMENT_DECLARATION:
                table = fGlobalElemDeclsExt;
                break;
            case XSConstants.ATTRIBUTE_GROUP:
                table = fGlobalAttrGrpDeclsExt;
                break;
            case XSConstants.MODEL_GROUP_DEFINITION:
                table = fGlobalGroupDeclsExt;
                break;
            case XSConstants.NOTATION_DECLARATION:
                table = fGlobalNotationDeclsExt;
                break;
            }

            Object[] entries = table.getEntries();
            fComponentsExt[objectType] = new ObjectListImpl(entries, entries.length);
        }

        return fComponentsExt[objectType];
    }

    public synchronized void resetComponents() {
        fComponents = null;
        fComponentsExt = null;
    }


    public XSTypeDefinition getTypeDefinition(String name) {
        return getGlobalTypeDecl(name);
    }


    public XSAttributeDeclaration getAttributeDeclaration(String name) {
        return getGlobalAttributeDecl(name);
    }


    public XSElementDeclaration getElementDeclaration(String name) {
        return getGlobalElementDecl(name);
    }


    public XSAttributeGroupDefinition getAttributeGroup(String name) {
        return getGlobalAttributeGroupDecl(name);
    }


    public XSModelGroupDefinition getModelGroupDefinition(String name) {
        return getGlobalGroupDecl(name);
    }


    public XSNotationDeclaration getNotationDeclaration(String name) {
        return getGlobalNotationDecl(name);
    }



    public StringList getDocumentLocations() {
        return new StringListImpl(fLocations);
    }


    public XSModel toXSModel() {
        return new XSModelImpl(new SchemaGrammar[]{this});
    }

    public XSModel toXSModel(XSGrammar[] grammars) {
        if (grammars == null || grammars.length == 0)
            return toXSModel();

        int len = grammars.length;
        boolean hasSelf = false;
        for (int i = 0; i < len; i++) {
            if (grammars[i] == this) {
                hasSelf = true;
                break;
            }
        }

        SchemaGrammar[] gs = new SchemaGrammar[hasSelf ? len : len+1];
        for (int i = 0; i < len; i++)
            gs[i] = (SchemaGrammar)grammars[i];
        if (!hasSelf)
            gs[len] = this;
        return new XSModelImpl(gs);
    }


    public XSObjectList getAnnotations() {
        if (fNumAnnotations == 0) {
            return XSObjectListImpl.EMPTY_LIST;
        }
        return new XSObjectListImpl(fAnnotations, fNumAnnotations);
    }

    public void addAnnotation(XSAnnotationImpl annotation) {
        if (annotation == null) {
            return;
        }
        if (fAnnotations == null) {
            fAnnotations = new XSAnnotationImpl[2];
        }
        else if (fNumAnnotations == fAnnotations.length) {
            XSAnnotationImpl[] newArray = new XSAnnotationImpl[fNumAnnotations << 1];
            System.arraycopy(fAnnotations, 0, newArray, 0, fNumAnnotations);
            fAnnotations = newArray;
        }
        fAnnotations[fNumAnnotations++] = annotation;
    }

    public void setImmutable(boolean isImmutable) {
        fIsImmutable = isImmutable;
    }

    public boolean isImmutable() {
        return fIsImmutable;
    }

}