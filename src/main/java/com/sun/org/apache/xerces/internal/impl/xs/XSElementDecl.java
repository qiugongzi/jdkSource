


package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMapImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSConstants;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;


public class XSElementDecl implements XSElementDeclaration {

    public final static short     SCOPE_ABSENT        = 0;
    public final static short     SCOPE_GLOBAL        = 1;
    public final static short     SCOPE_LOCAL         = 2;

    public String fName = null;
    public String fTargetNamespace = null;
    public XSTypeDefinition fType = null;
    public QName fUnresolvedTypeName = null;
    short fMiscFlags = 0;
    public short fScope = XSConstants.SCOPE_ABSENT;
    XSComplexTypeDecl fEnclosingCT = null;
    public short fBlock = XSConstants.DERIVATION_NONE;
    public short fFinal = XSConstants.DERIVATION_NONE;
    public XSObjectList fAnnotations = null;
    public ValidatedInfo fDefault = null;
    public XSElementDecl fSubGroup = null;
    static final int INITIAL_SIZE = 2;
    int fIDCPos = 0;
    IdentityConstraint[] fIDConstraints = new IdentityConstraint[INITIAL_SIZE];
    private XSNamespaceItem fNamespaceItem = null;

    private static final short CONSTRAINT_MASK = 3;
    private static final short NILLABLE        = 4;
    private static final short ABSTRACT        = 8;

    public void setConstraintType(short constraintType) {
        fMiscFlags ^= (fMiscFlags & CONSTRAINT_MASK);
        fMiscFlags |= (constraintType & CONSTRAINT_MASK);
    }
    public void setIsNillable() {
        fMiscFlags |= NILLABLE;
    }
    public void setIsAbstract() {
        fMiscFlags |= ABSTRACT;
    }
    public void setIsGlobal() {
        fScope = SCOPE_GLOBAL;
    }
    public void setIsLocal(XSComplexTypeDecl enclosingCT) {
        fScope = SCOPE_LOCAL;
        fEnclosingCT = enclosingCT;
    }

    public void addIDConstraint(IdentityConstraint idc) {
        if (fIDCPos == fIDConstraints.length) {
            fIDConstraints = resize(fIDConstraints, fIDCPos*2);
        }
        fIDConstraints[fIDCPos++] = idc;
    }

    public IdentityConstraint[] getIDConstraints() {
        if (fIDCPos == 0) {
            return null;
        }
        if (fIDCPos < fIDConstraints.length) {
            fIDConstraints = resize(fIDConstraints, fIDCPos);
        }
        return fIDConstraints;
    }

    static final IdentityConstraint[] resize(IdentityConstraint[] oldArray, int newSize) {
        IdentityConstraint[] newArray = new IdentityConstraint[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }


    private String fDescription = null;
    public String toString() {
        if (fDescription == null) {
            if (fTargetNamespace != null) {
                StringBuffer buffer = new StringBuffer(
                    fTargetNamespace.length() +
                    ((fName != null) ? fName.length() : 4) + 3);
                buffer.append('"');
                buffer.append(fTargetNamespace);
                buffer.append('"');
                buffer.append(':');
                buffer.append(fName);
                fDescription = buffer.toString();
            }
            else {
                fDescription = fName;
            }
        }
        return fDescription;
    }


    public int hashCode() {
        int code = fName.hashCode();
        if (fTargetNamespace != null)
            code = (code<<16)+fTargetNamespace.hashCode();
        return code;
    }


    public boolean equals(Object o) {
        return o == this;
    }


    public void reset(){
        fScope = XSConstants.SCOPE_ABSENT;
        fName = null;
        fTargetNamespace = null;
        fType = null;
        fUnresolvedTypeName = null;
        fMiscFlags = 0;
        fBlock = XSConstants.DERIVATION_NONE;
        fFinal = XSConstants.DERIVATION_NONE;
        fDefault = null;
        fAnnotations = null;
        fSubGroup = null;
        for (int i=0;i<fIDCPos;i++) {
            fIDConstraints[i] = null;
        }

        fIDCPos = 0;
    }


    public short getType() {
        return XSConstants.ELEMENT_DECLARATION;
    }


    public String getName() {
        return fName;
    }


    public String getNamespace() {
        return fTargetNamespace;
    }


    public XSTypeDefinition getTypeDefinition() {
        return fType;
    }


    public short getScope() {
        return fScope;
    }


    public XSComplexTypeDefinition getEnclosingCTDefinition() {
        return fEnclosingCT;
    }


    public short getConstraintType() {
        return (short)(fMiscFlags & CONSTRAINT_MASK);
    }


    public String getConstraintValue() {
        return getConstraintType() == XSConstants.VC_NONE ?
               null :
               fDefault.stringValue();
    }


    public boolean getNillable() {
        return ((fMiscFlags & NILLABLE) != 0);
    }


    public XSNamedMap getIdentityConstraints() {
        return new XSNamedMapImpl(fIDConstraints, fIDCPos);
    }


    public XSElementDeclaration getSubstitutionGroupAffiliation() {
        return fSubGroup;
    }


    public boolean isSubstitutionGroupExclusion(short exclusion) {
        return (fFinal & exclusion) != 0;
    }


    public short getSubstitutionGroupExclusions() {
        return fFinal;
    }


    public boolean isDisallowedSubstitution(short disallowed) {
        return (fBlock & disallowed) != 0;
    }


    public short getDisallowedSubstitutions() {
        return fBlock;
    }


    public boolean getAbstract() {
        return ((fMiscFlags & ABSTRACT) != 0);
    }


    public XSAnnotation getAnnotation() {
        return (fAnnotations != null) ? (XSAnnotation) fAnnotations.item(0) : null;
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

    public Object getActualVC() {
        return getConstraintType() == XSConstants.VC_NONE ?
               null :
               fDefault.actualValue;
    }

    public short getActualVCType() {
        return getConstraintType() == XSConstants.VC_NONE ?
               XSConstants.UNAVAILABLE_DT :
               fDefault.actualValueType;
    }

    public ShortList getItemValueTypes() {
        return getConstraintType() == XSConstants.VC_NONE ?
               null :
               fDefault.itemValueTypes;
    }

}