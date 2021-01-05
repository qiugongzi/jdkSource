


package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xs.*;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import org.w3c.dom.TypeInfo;


public class XSComplexTypeDecl implements XSComplexTypeDefinition, TypeInfo {

    String fName = null;

    String fTargetNamespace = null;

    XSTypeDefinition fBaseType = null;

    short fDerivedBy = XSConstants.DERIVATION_RESTRICTION;

    short fFinal = XSConstants.DERIVATION_NONE;

    short fBlock = XSConstants.DERIVATION_NONE;

    short fMiscFlags = 0;

    XSAttributeGroupDecl fAttrGrp = null;

    short fContentType = CONTENTTYPE_EMPTY;

    XSSimpleType fXSSimpleType = null;

    XSParticleDecl fParticle = null;

    volatile XSCMValidator fCMValidator = null;

    XSCMValidator fUPACMValidator = null;

    XSObjectListImpl fAnnotations = null;

    private XSNamespaceItem fNamespaceItem = null;

    static final int DERIVATION_ANY = 0;
    static final int DERIVATION_RESTRICTION = 1;
    static final int DERIVATION_EXTENSION = 2;
    static final int DERIVATION_UNION = 4;
    static final int DERIVATION_LIST = 8;

    public XSComplexTypeDecl() {
        }

    public void setValues(String name, String targetNamespace,
            XSTypeDefinition baseType, short derivedBy, short schemaFinal,
            short block, short contentType,
            boolean isAbstract, XSAttributeGroupDecl attrGrp,
            XSSimpleType simpleType, XSParticleDecl particle,
            XSObjectListImpl annotations) {
        fTargetNamespace = targetNamespace;
        fBaseType = baseType;
        fDerivedBy = derivedBy;
        fFinal = schemaFinal;
        fBlock = block;
        fContentType = contentType;
        if(isAbstract)
            fMiscFlags |= CT_IS_ABSTRACT;
        fAttrGrp = attrGrp;
        fXSSimpleType = simpleType;
        fParticle = particle;
        fAnnotations = annotations;
   }

   public void setName(String name) {
        fName = name;
   }

    public short getTypeCategory() {
        return COMPLEX_TYPE;
    }

    public String getTypeName() {
        return fName;
    }

    public short getFinalSet(){
        return fFinal;
    }

    public String getTargetNamespace(){
        return fTargetNamespace;
    }

    private static final short CT_IS_ABSTRACT = 1;
    private static final short CT_HAS_TYPE_ID = 2;
    private static final short CT_IS_ANONYMOUS = 4;

    public boolean containsTypeID () {
        return((fMiscFlags & CT_HAS_TYPE_ID) != 0);
    }

    public void setIsAbstractType() {
        fMiscFlags |= CT_IS_ABSTRACT;
    }
    public void setContainsTypeID() {
        fMiscFlags |= CT_HAS_TYPE_ID;
    }
    public void setIsAnonymous() {
        fMiscFlags |= CT_IS_ANONYMOUS;
    }

    public XSCMValidator getContentModel(CMBuilder cmBuilder) {
        if (fContentType == XSComplexTypeDecl.CONTENTTYPE_SIMPLE ||
            fContentType == XSComplexTypeDecl.CONTENTTYPE_EMPTY) {
            return null;
        }
        if (fCMValidator == null)
            synchronized (this) {
                if (fCMValidator == null) {
                    fCMValidator = cmBuilder.getContentModel(this);
                }
            }
        return fCMValidator;
    }

    public XSAttributeGroupDecl getAttrGrp() {
        return fAttrGrp;
    }

    public String toString() {
        StringBuilder str = new StringBuilder(192);
        appendTypeInfo(str);
        return str.toString();
    }

    void appendTypeInfo(StringBuilder str) {
        String contentType[] = {"EMPTY", "SIMPLE", "ELEMENT", "MIXED"};
        String derivedBy[] = {"EMPTY", "EXTENSION", "RESTRICTION"};

        str.append("Complex type name='").append(fTargetNamespace).append(',').append(getTypeName()).append("', ");
        if (fBaseType != null) {
            str.append(" base type name='").append(fBaseType.getName()).append("', ");
        }
        str.append(" content type='").append(contentType[fContentType]).append("', ");
        str.append(" isAbstract='").append(getAbstract()).append("', ");
        str.append(" hasTypeId='").append(containsTypeID()).append("', ");
        str.append(" final='").append(fFinal).append("', ");
        str.append(" block='").append(fBlock).append("', ");
        if (fParticle != null) {
            str.append(" particle='").append(fParticle.toString()).append("', ");
        }
        str.append(" derivedBy='").append(derivedBy[fDerivedBy]).append("'. ");

    }

    public boolean derivedFromType(XSTypeDefinition ancestor, short derivationMethod) {
        if (ancestor == null)
            return false;
        if (ancestor == SchemaGrammar.fAnyType)
            return true;
        XSTypeDefinition type = this;
        while (type != ancestor &&                     type != SchemaGrammar.fAnySimpleType &&  type != SchemaGrammar.fAnyType) {        type = type.getBaseType();
        }

        return type == ancestor;
    }

    public boolean derivedFrom(String ancestorNS, String ancestorName, short derivationMethod) {
        if (ancestorName == null)
            return false;
        if (ancestorNS != null &&
            ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) &&
            ancestorName.equals(SchemaSymbols.ATTVAL_ANYTYPE)) {
            return true;
        }

        XSTypeDefinition type = this;
        while (!(ancestorName.equals(type.getName()) &&
                 ((ancestorNS == null && type.getNamespace() == null) ||
                  (ancestorNS != null && ancestorNS.equals(type.getNamespace())))) &&   type != SchemaGrammar.fAnySimpleType &&  type != SchemaGrammar.fAnyType) {        type = (XSTypeDefinition)type.getBaseType();
        }

        return type != SchemaGrammar.fAnySimpleType &&
        type != SchemaGrammar.fAnyType;
    }


    public boolean isDOMDerivedFrom(String ancestorNS, String ancestorName,
            int derivationMethod) {
        if (ancestorName == null)
            return false;

        if (ancestorNS != null
                && ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)
                && ancestorName.equals(SchemaSymbols.ATTVAL_ANYTYPE)
                && (derivationMethod == DERIVATION_RESTRICTION
                && derivationMethod == DERIVATION_EXTENSION)) {
            return true;
        }

        if ((derivationMethod & DERIVATION_RESTRICTION) != 0) {
            if (isDerivedByRestriction(ancestorNS, ancestorName,
                    derivationMethod, this)) {
                return true;
            }
        }

        if ((derivationMethod & DERIVATION_EXTENSION) != 0) {
            if (isDerivedByExtension(ancestorNS, ancestorName,
                    derivationMethod, this)) {
                return true;
            }
        }

        if ((((derivationMethod & DERIVATION_LIST) != 0) || ((derivationMethod & DERIVATION_UNION) != 0))
                && ((derivationMethod & DERIVATION_RESTRICTION) == 0)
                && ((derivationMethod & DERIVATION_EXTENSION) == 0)) {

            if (ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)
                    && ancestorName.equals(SchemaSymbols.ATTVAL_ANYTYPE)) {
                ancestorName = SchemaSymbols.ATTVAL_ANYSIMPLETYPE;
            }

            if(!(fName.equals(SchemaSymbols.ATTVAL_ANYTYPE)
                            && fTargetNamespace.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA))){
                if (fBaseType != null && fBaseType instanceof XSSimpleTypeDecl) {

                    return ((XSSimpleTypeDecl) fBaseType).isDOMDerivedFrom(ancestorNS,
                            ancestorName, derivationMethod);
                } else if (fBaseType != null
                        && fBaseType instanceof XSComplexTypeDecl) {
                    return ((XSComplexTypeDecl) fBaseType).isDOMDerivedFrom(
                            ancestorNS, ancestorName, derivationMethod);
                }
            }
        }

        if (((derivationMethod  & DERIVATION_EXTENSION) == 0)
                && (((derivationMethod & DERIVATION_RESTRICTION) == 0)
                        && ((derivationMethod & DERIVATION_LIST) == 0)
                        && ((derivationMethod & DERIVATION_UNION) == 0))) {
            return isDerivedByAny(ancestorNS, ancestorName, derivationMethod, this);
        }

        return false;
    }


    private boolean isDerivedByAny(String ancestorNS, String ancestorName,
            int derivationMethod, XSTypeDefinition type) {
        XSTypeDefinition oldType = null;
        boolean derivedFrom = false;
        while (type != null && type != oldType) {

            if ((ancestorName.equals(type.getName()))
                    && ((ancestorNS == null && type.getNamespace() == null)
                        || (ancestorNS != null && ancestorNS.equals(type.getNamespace())))) {
                derivedFrom = true;
                break;
            }

            if (isDerivedByRestriction(ancestorNS, ancestorName,
                    derivationMethod, type)) {
                return true;
            } else if (!isDerivedByExtension(ancestorNS, ancestorName,
                    derivationMethod, type)) {
                return true;
            }
            oldType = type;
            type = type.getBaseType();
        }

        return derivedFrom;
    }


    private boolean isDerivedByRestriction(String ancestorNS,
            String ancestorName, int derivationMethod, XSTypeDefinition type) {

        XSTypeDefinition oldType = null;
        while (type != null && type != oldType) {

            if (ancestorNS != null
                    && ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)
                    && ancestorName.equals(SchemaSymbols.ATTVAL_ANYSIMPLETYPE)) {
                return false;
            }

            if ((ancestorName.equals(type.getName()))
                    && (ancestorNS != null && ancestorNS.equals(type.getNamespace()))
                            || ((type.getNamespace() == null && ancestorNS == null))) {

                return true;
            }

            if (type instanceof XSSimpleTypeDecl) {
                if (ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)
                        && ancestorName.equals(SchemaSymbols.ATTVAL_ANYTYPE)) {
                    ancestorName = SchemaSymbols.ATTVAL_ANYSIMPLETYPE;
                }
                return ((XSSimpleTypeDecl) type).isDOMDerivedFrom(ancestorNS,
                        ancestorName, derivationMethod);
            } else {
                if (((XSComplexTypeDecl) type).getDerivationMethod() != XSConstants.DERIVATION_RESTRICTION) {
                    return false;
                }
            }
            oldType = type;
            type = type.getBaseType();

        }

        return false;
    }


    private boolean isDerivedByExtension(String ancestorNS,
            String ancestorName, int derivationMethod, XSTypeDefinition type) {

        boolean extension = false;
        XSTypeDefinition oldType = null;
        while (type != null && type != oldType) {
            if (ancestorNS != null
                    && ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)
                    && ancestorName.equals(SchemaSymbols.ATTVAL_ANYSIMPLETYPE)
                    && SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(type.getNamespace())
                            && SchemaSymbols.ATTVAL_ANYTYPE.equals(type.getName())) {
                break;
            }

            if ((ancestorName.equals(type.getName()))
                    && ((ancestorNS == null && type.getNamespace() == null)
                        || (ancestorNS != null && ancestorNS.equals(type.getNamespace())))) {
                return extension;
            }

            if (type instanceof XSSimpleTypeDecl) {
                if (ancestorNS.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)
                        && ancestorName.equals(SchemaSymbols.ATTVAL_ANYTYPE)) {
                    ancestorName = SchemaSymbols.ATTVAL_ANYSIMPLETYPE;
                }

                if ((derivationMethod & DERIVATION_EXTENSION) != 0) {
                    return extension
                    & ((XSSimpleTypeDecl) type).isDOMDerivedFrom(
                            ancestorNS, ancestorName,
                            (derivationMethod & DERIVATION_RESTRICTION));
                } else {
                    return extension
                    & ((XSSimpleTypeDecl) type).isDOMDerivedFrom(
                            ancestorNS, ancestorName, derivationMethod);
                }

            } else {
                if (((XSComplexTypeDecl) type).getDerivationMethod() == XSConstants.DERIVATION_EXTENSION) {
                    extension = extension | true;
                }
            }
            oldType = type;
            type = type.getBaseType();
        }

        return false;
    }



    public void reset(){
        fName = null;
        fTargetNamespace = null;
        fBaseType = null;
        fDerivedBy = XSConstants.DERIVATION_RESTRICTION;
        fFinal = XSConstants.DERIVATION_NONE;
        fBlock = XSConstants.DERIVATION_NONE;

        fMiscFlags = 0;

        fAttrGrp.reset();
        fContentType = CONTENTTYPE_EMPTY;
        fXSSimpleType = null;
        fParticle = null;
        fCMValidator = null;
        fUPACMValidator = null;
        if(fAnnotations != null) {
            fAnnotations.clearXSObjectList();
        }
        fAnnotations = null;
    }


    public short getType() {
        return XSConstants.TYPE_DEFINITION;
    }


    public String getName() {
        return getAnonymous() ? null : fName;
    }


    public boolean getAnonymous() {
        return((fMiscFlags & CT_IS_ANONYMOUS) != 0);
    }


    public String getNamespace() {
        return fTargetNamespace;
    }


    public XSTypeDefinition getBaseType() {
        return fBaseType;
    }


    public short getDerivationMethod() {
        return fDerivedBy;
    }


    public boolean isFinal(short derivation) {
        return (fFinal & derivation) != 0;
    }


    public short getFinal() {
        return fFinal;
    }


    public boolean getAbstract() {
        return((fMiscFlags & CT_IS_ABSTRACT) != 0);
    }


    public XSObjectList getAttributeUses() {
        return fAttrGrp.getAttributeUses();
    }


    public XSWildcard getAttributeWildcard() {
        return fAttrGrp.getAttributeWildcard();
    }


    public short getContentType() {
        return fContentType;
    }


    public XSSimpleTypeDefinition getSimpleType() {
        return fXSSimpleType;
    }


    public XSParticle getParticle() {
        return fParticle;
    }


    public boolean isProhibitedSubstitution(short prohibited) {
        return (fBlock & prohibited) != 0;
    }


    public short getProhibitedSubstitutions() {
        return fBlock;
    }


    public XSObjectList getAnnotations() {
        return (fAnnotations != null) ? fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }


    public XSNamespaceItem getNamespaceItem() {
        return fNamespaceItem;
    }

    void setNamespaceItem(XSNamespaceItem namespaceItem) {
        fNamespaceItem = namespaceItem;
    }


    public XSAttributeUse getAttributeUse(String namespace, String name) {
         return fAttrGrp.getAttributeUse(namespace, name);
    }

    public String getTypeNamespace() {
        return getNamespace();
    }

    public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
        return isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod);
    }

}