


package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.impl.xpath.XPathException;
import com.sun.org.apache.xerces.internal.impl.xs.util.ShortListImpl;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSConstants;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;


public class Field {

    protected Field.XPath fXPath;



    protected IdentityConstraint fIdentityConstraint;

    public Field(Field.XPath xpath,
                 IdentityConstraint identityConstraint) {
        fXPath = xpath;
        fIdentityConstraint = identityConstraint;
    } public com.sun.org.apache.xerces.internal.impl.xpath.XPath getXPath() {
        return fXPath;
    } public IdentityConstraint getIdentityConstraint() {
        return fIdentityConstraint;
    } public XPathMatcher createMatcher(FieldActivator activator, ValueStore store) {
        return new Field.Matcher(fXPath, activator, store);
    } public String toString() {
        return fXPath.toString();
    } public static class XPath
        extends com.sun.org.apache.xerces.internal.impl.xpath.XPath {

        public XPath(String xpath,
                     SymbolTable symbolTable,
                     NamespaceContext context) throws XPathException {
            super(((xpath.trim().startsWith("/") ||xpath.trim().startsWith("."))?
                    xpath:"./"+xpath),
                  symbolTable, context);

            for (int i=0;i<fLocationPaths.length;i++) {
                for(int j=0; j<fLocationPaths[i].steps.length; j++) {
                    com.sun.org.apache.xerces.internal.impl.xpath.XPath.Axis axis =
                        fLocationPaths[i].steps[j].axis;
                    if (axis.type == XPath.Axis.ATTRIBUTE &&
                            (j < fLocationPaths[i].steps.length-1)) {
                        throw new XPathException("c-fields-xpaths");
                    }
                }
            }
        } } protected class Matcher
        extends XPathMatcher {

        protected FieldActivator fFieldActivator;


        protected ValueStore fStore;

        public Matcher(Field.XPath xpath, FieldActivator activator, ValueStore store) {
            super(xpath);
            fFieldActivator = activator;
            fStore = store;
        } protected void matched(Object actualValue, short valueType, ShortList itemValueType, boolean isNil) {
            super.matched(actualValue, valueType, itemValueType, isNil);
            if(isNil && (fIdentityConstraint.getCategory() == IdentityConstraint.IC_KEY)) {
                String code = "KeyMatchesNillable";
                fStore.reportError(code,
                    new Object[]{fIdentityConstraint.getElementName(), fIdentityConstraint.getIdentityConstraintName()});
            }
            fStore.addValue(Field.this, actualValue, convertToPrimitiveKind(valueType), convertToPrimitiveKind(itemValueType));
            fFieldActivator.setMayMatch(Field.this, Boolean.FALSE);
        } private short convertToPrimitiveKind(short valueType) {

            if (valueType <= XSConstants.NOTATION_DT) {
                return valueType;
            }

            if (valueType <= XSConstants.ENTITY_DT) {
                return XSConstants.STRING_DT;
            }

            if (valueType <= XSConstants.POSITIVEINTEGER_DT) {
                return XSConstants.DECIMAL_DT;
            }

            return valueType;
        }

        private ShortList convertToPrimitiveKind(ShortList itemValueType) {
            if (itemValueType != null) {
                int i;
                final int length = itemValueType.getLength();
                for (i = 0; i < length; ++i) {
                    short type = itemValueType.item(i);
                    if (type != convertToPrimitiveKind(type)) {
                        break;
                    }
                }
                if (i != length) {
                    final short [] arr = new short[length];
                    for (int j = 0; j < i; ++j) {
                        arr[j] = itemValueType.item(j);
                    }
                    for(; i < length; ++i) {
                        arr[i] = convertToPrimitiveKind(itemValueType.item(i));
                    }
                    return new ShortListImpl(arr, arr.length);
                }
            }
            return itemValueType;
        }

        protected void handleContent(XSTypeDefinition type, boolean nillable, Object actualValue, short valueType, ShortList itemValueType) {
            if (type == null ||
               type.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE &&
               ((XSComplexTypeDefinition) type).getContentType()
                != XSComplexTypeDefinition.CONTENTTYPE_SIMPLE) {

                    fStore.reportError( "cvc-id.3", new Object[] {
                            fIdentityConstraint.getName(),
                            fIdentityConstraint.getElementName()});

            }
            fMatchedString = actualValue;
            matched(fMatchedString, valueType, itemValueType, nillable);
        } } }