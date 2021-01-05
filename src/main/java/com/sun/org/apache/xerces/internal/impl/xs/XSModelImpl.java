


package com.sun.org.apache.xerces.internal.impl.xs;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMap4Types;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSNamedMapImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSConstants;
import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import com.sun.org.apache.xerces.internal.xs.XSModelGroupDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamedMap;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItemList;
import com.sun.org.apache.xerces.internal.xs.XSNotationDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;


public final class XSModelImpl extends AbstractList implements XSModel, XSNamespaceItemList {

    private static final short MAX_COMP_IDX = XSTypeDefinition.SIMPLE_TYPE;
    private static final boolean[] GLOBAL_COMP = {false,    true,     true,     true,     false,    true,     true,     false,    false,    false,    false,    true,     false,    false,    false,    true,     true      };

    private final int fGrammarCount;
    private final String[] fNamespaces;
    private final SchemaGrammar[] fGrammarList;
    private final SymbolHash fGrammarMap;
    private final SymbolHash fSubGroupMap;

    private final XSNamedMap[] fGlobalComponents;
    private final XSNamedMap[][] fNSComponents;

    private final StringList fNamespacesList;
    private XSObjectList fAnnotations = null;

    private final boolean fHasIDC;


    public XSModelImpl(SchemaGrammar[] grammars) {
        this(grammars, Constants.SCHEMA_VERSION_1_0);
    }

    public XSModelImpl(SchemaGrammar[] grammars, short s4sVersion) {
        int len = grammars.length;
        final int initialSize = Math.max(len+1, 5);
        String[] namespaces = new String[initialSize];
        SchemaGrammar[] grammarList = new SchemaGrammar[initialSize];
        boolean hasS4S = false;
        for (int i = 0; i < len; i++) {
            final SchemaGrammar sg = grammars[i];
            final String tns = sg.getTargetNamespace();
            namespaces[i] = tns;
            grammarList[i] = sg;
            if (tns == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
                hasS4S = true;
            }
        }
        if (!hasS4S) {
            namespaces[len] = SchemaSymbols.URI_SCHEMAFORSCHEMA;
            grammarList[len++] = SchemaGrammar.getS4SGrammar(s4sVersion);
        }

        SchemaGrammar sg1, sg2;
        Vector gs;
        int i, j, k;
        for (i = 0; i < len; i++) {
            sg1 = grammarList[i];
            gs = sg1.getImportedGrammars();
            for (j = gs == null ? -1 : gs.size() - 1; j >= 0; j--) {
                sg2 = (SchemaGrammar)gs.elementAt(j);
                for (k = 0; k < len; k++) {
                    if (sg2 == grammarList[k]) {
                        break;
                    }
                }
                if (k == len) {
                    if (len == grammarList.length) {
                        String[] newSA = new String[len*2];
                        System.arraycopy(namespaces, 0, newSA, 0, len);
                        namespaces = newSA;
                        SchemaGrammar[] newGA = new SchemaGrammar[len*2];
                        System.arraycopy(grammarList, 0, newGA, 0, len);
                        grammarList = newGA;
                    }
                    namespaces[len] = sg2.getTargetNamespace();
                    grammarList[len] = sg2;
                    len++;
                }
            }
        }

        fNamespaces = namespaces;
        fGrammarList = grammarList;

        boolean hasIDC = false;
        fGrammarMap = new SymbolHash(len*2);
        for (i = 0; i < len; i++) {
            fGrammarMap.put(null2EmptyString(fNamespaces[i]), fGrammarList[i]);
            if (fGrammarList[i].hasIDConstraints()) {
                hasIDC = true;
            }
        }

        fHasIDC = hasIDC;
        fGrammarCount = len;
        fGlobalComponents = new XSNamedMap[MAX_COMP_IDX+1];
        fNSComponents = new XSNamedMap[len][MAX_COMP_IDX+1];
        fNamespacesList = new StringListImpl(fNamespaces, fGrammarCount);

        fSubGroupMap = buildSubGroups();
    }

    private SymbolHash buildSubGroups_Org() {
        SubstitutionGroupHandler sgHandler = new SubstitutionGroupHandler(null);
        for (int i = 0 ; i < fGrammarCount; i++) {
            sgHandler.addSubstitutionGroup(fGrammarList[i].getSubstitutionGroups());
        }

        final XSNamedMap elements = getComponents(XSConstants.ELEMENT_DECLARATION);
        final int len = elements.getLength();
        final SymbolHash subGroupMap = new SymbolHash(len*2);
        XSElementDecl head;
        XSElementDeclaration[] subGroup;
        for (int i = 0; i < len; i++) {
            head = (XSElementDecl)elements.item(i);
            subGroup = sgHandler.getSubstitutionGroup(head);
            subGroupMap.put(head, subGroup.length > 0 ?
                    new XSObjectListImpl(subGroup, subGroup.length) : XSObjectListImpl.EMPTY_LIST);
        }
        return subGroupMap;
    }

    private SymbolHash buildSubGroups() {
        SubstitutionGroupHandler sgHandler = new SubstitutionGroupHandler(null);
        for (int i = 0 ; i < fGrammarCount; i++) {
            sgHandler.addSubstitutionGroup(fGrammarList[i].getSubstitutionGroups());
        }

        final XSObjectListImpl elements = getGlobalElements();
        final int len = elements.getLength();
        final SymbolHash subGroupMap = new SymbolHash(len*2);
        XSElementDecl head;
        XSElementDeclaration[] subGroup;
        for (int i = 0; i < len; i++) {
            head = (XSElementDecl)elements.item(i);
            subGroup = sgHandler.getSubstitutionGroup(head);
            subGroupMap.put(head, subGroup.length > 0 ?
                    new XSObjectListImpl(subGroup, subGroup.length) : XSObjectListImpl.EMPTY_LIST);
        }
        return subGroupMap;
    }

    private XSObjectListImpl getGlobalElements() {
        final SymbolHash[] tables = new SymbolHash[fGrammarCount];
        int length = 0;

        for (int i = 0; i < fGrammarCount; i++) {
            tables[i] = fGrammarList[i].fAllGlobalElemDecls;
            length += tables[i].getLength();
        }

        if (length == 0) {
            return XSObjectListImpl.EMPTY_LIST;
        }

        final XSObject[] components = new XSObject[length];

        int start = 0;
        for (int i = 0; i < fGrammarCount; i++) {
            tables[i].getValues(components, start);
            start += tables[i].getLength();
        }

        return new XSObjectListImpl(components, length);
    }


    public StringList getNamespaces() {
        return fNamespacesList;
    }


    public XSNamespaceItemList getNamespaceItems() {
        return this;
    }


    public synchronized XSNamedMap getComponents(short objectType) {
        if (objectType <= 0 || objectType > MAX_COMP_IDX ||
            !GLOBAL_COMP[objectType]) {
            return XSNamedMapImpl.EMPTY_MAP;
        }

        SymbolHash[] tables = new SymbolHash[fGrammarCount];
        if (fGlobalComponents[objectType] == null) {
            for (int i = 0; i < fGrammarCount; i++) {
                switch (objectType) {
                case XSConstants.TYPE_DEFINITION:
                case XSTypeDefinition.COMPLEX_TYPE:
                case XSTypeDefinition.SIMPLE_TYPE:
                    tables[i] = fGrammarList[i].fGlobalTypeDecls;
                    break;
                case XSConstants.ATTRIBUTE_DECLARATION:
                    tables[i] = fGrammarList[i].fGlobalAttrDecls;
                    break;
                case XSConstants.ELEMENT_DECLARATION:
                    tables[i] = fGrammarList[i].fGlobalElemDecls;
                    break;
                case XSConstants.ATTRIBUTE_GROUP:
                    tables[i] = fGrammarList[i].fGlobalAttrGrpDecls;
                    break;
                case XSConstants.MODEL_GROUP_DEFINITION:
                    tables[i] = fGrammarList[i].fGlobalGroupDecls;
                    break;
                case XSConstants.NOTATION_DECLARATION:
                    tables[i] = fGrammarList[i].fGlobalNotationDecls;
                    break;
                }
            }
            if (objectType == XSTypeDefinition.COMPLEX_TYPE ||
                objectType == XSTypeDefinition.SIMPLE_TYPE) {
                fGlobalComponents[objectType] = new XSNamedMap4Types(fNamespaces, tables, fGrammarCount, objectType);
            }
            else {
                fGlobalComponents[objectType] = new XSNamedMapImpl(fNamespaces, tables, fGrammarCount);
            }
        }

        return fGlobalComponents[objectType];
    }


    public synchronized XSNamedMap getComponentsByNamespace(short objectType,
                                                            String namespace) {
        if (objectType <= 0 || objectType > MAX_COMP_IDX ||
            !GLOBAL_COMP[objectType]) {
            return XSNamedMapImpl.EMPTY_MAP;
        }

        int i = 0;
        if (namespace != null) {
            for (; i < fGrammarCount; ++i) {
                if (namespace.equals(fNamespaces[i])) {
                    break;
                }
            }
        }
        else {
            for (; i < fGrammarCount; ++i) {
                if (fNamespaces[i] == null) {
                    break;
                }
            }
        }
        if (i == fGrammarCount) {
            return XSNamedMapImpl.EMPTY_MAP;
        }

        if (fNSComponents[i][objectType] == null) {
            SymbolHash table = null;
            switch (objectType) {
            case XSConstants.TYPE_DEFINITION:
            case XSTypeDefinition.COMPLEX_TYPE:
            case XSTypeDefinition.SIMPLE_TYPE:
                table = fGrammarList[i].fGlobalTypeDecls;
                break;
            case XSConstants.ATTRIBUTE_DECLARATION:
                table = fGrammarList[i].fGlobalAttrDecls;
                break;
            case XSConstants.ELEMENT_DECLARATION:
                table = fGrammarList[i].fGlobalElemDecls;
                break;
            case XSConstants.ATTRIBUTE_GROUP:
                table = fGrammarList[i].fGlobalAttrGrpDecls;
                break;
            case XSConstants.MODEL_GROUP_DEFINITION:
                table = fGrammarList[i].fGlobalGroupDecls;
                break;
            case XSConstants.NOTATION_DECLARATION:
                table = fGrammarList[i].fGlobalNotationDecls;
                break;
            }

            if (objectType == XSTypeDefinition.COMPLEX_TYPE ||
                objectType == XSTypeDefinition.SIMPLE_TYPE) {
                fNSComponents[i][objectType] = new XSNamedMap4Types(namespace, table, objectType);
            }
            else {
                fNSComponents[i][objectType] = new XSNamedMapImpl(namespace, table);
            }
        }

        return fNSComponents[i][objectType];
    }


    public XSTypeDefinition getTypeDefinition(String name,
                                              String namespace) {
        SchemaGrammar sg = (SchemaGrammar)fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return (XSTypeDefinition)sg.fGlobalTypeDecls.get(name);
    }


    public XSTypeDefinition getTypeDefinition(String name,
                                              String namespace,
                                              String loc) {
        SchemaGrammar sg = (SchemaGrammar)fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return sg.getGlobalTypeDecl(name, loc);
    }


    public XSAttributeDeclaration getAttributeDeclaration(String name,
                                                   String namespace) {
        SchemaGrammar sg = (SchemaGrammar)fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return (XSAttributeDeclaration)sg.fGlobalAttrDecls.get(name);
    }


    public XSAttributeDeclaration getAttributeDeclaration(String name,
                                                   String namespace,
                                                   String loc) {
        SchemaGrammar sg = (SchemaGrammar)fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return sg.getGlobalAttributeDecl(name, loc);
    }


    public XSElementDeclaration getElementDeclaration(String name,
                                               String namespace) {
        SchemaGrammar sg = (SchemaGrammar)fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return (XSElementDeclaration)sg.fGlobalElemDecls.get(name);
    }


    public XSElementDeclaration getElementDeclaration(String name,
                                               String namespace,
                                               String loc) {
        SchemaGrammar sg = (SchemaGrammar)fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return sg.getGlobalElementDecl(name, loc);
    }


    public XSAttributeGroupDefinition getAttributeGroup(String name,
                                                        String namespace) {
        SchemaGrammar sg = (SchemaGrammar)fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return (XSAttributeGroupDefinition)sg.fGlobalAttrGrpDecls.get(name);
    }


    public XSAttributeGroupDefinition getAttributeGroup(String name,
                                                        String namespace,
                                                        String loc) {
        SchemaGrammar sg = (SchemaGrammar)fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return sg.getGlobalAttributeGroupDecl(name, loc);
    }


    public XSModelGroupDefinition getModelGroupDefinition(String name,
                                                          String namespace) {
        SchemaGrammar sg = (SchemaGrammar)fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return (XSModelGroupDefinition)sg.fGlobalGroupDecls.get(name);
    }


    public XSModelGroupDefinition getModelGroupDefinition(String name,
                                                          String namespace,
                                                          String loc) {
        SchemaGrammar sg = (SchemaGrammar)fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return sg.getGlobalGroupDecl(name, loc);
    }



    public XSNotationDeclaration getNotationDeclaration(String name,
                                                 String namespace) {
        SchemaGrammar sg = (SchemaGrammar)fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return (XSNotationDeclaration)sg.fGlobalNotationDecls.get(name);
    }

    public XSNotationDeclaration getNotationDeclaration(String name,
                                                 String namespace,
                                                 String loc) {
        SchemaGrammar sg = (SchemaGrammar)fGrammarMap.get(null2EmptyString(namespace));
        if (sg == null) {
            return null;
        }
        return sg.getGlobalNotationDecl(name, loc);
    }


    public synchronized XSObjectList getAnnotations() {
        if (fAnnotations != null) {
            return fAnnotations;
        }

        int totalAnnotations = 0;
        for (int i = 0; i < fGrammarCount; i++) {
            totalAnnotations += fGrammarList[i].fNumAnnotations;
        }
        if (totalAnnotations == 0) {
            fAnnotations = XSObjectListImpl.EMPTY_LIST;
            return fAnnotations;
        }
        XSAnnotationImpl [] annotations = new XSAnnotationImpl [totalAnnotations];
        int currPos = 0;
        for (int i = 0; i < fGrammarCount; i++) {
            SchemaGrammar currGrammar = fGrammarList[i];
            if (currGrammar.fNumAnnotations > 0) {
                System.arraycopy(currGrammar.fAnnotations, 0, annotations, currPos, currGrammar.fNumAnnotations);
                currPos += currGrammar.fNumAnnotations;
            }
        }
        fAnnotations = new XSObjectListImpl(annotations, annotations.length);
        return fAnnotations;
    }

    private static final String null2EmptyString(String str) {
        return str == null ? XMLSymbols.EMPTY_STRING : str;
    }


    public boolean hasIDConstraints() {
        return fHasIDC;
    }


    public XSObjectList getSubstitutionGroup(XSElementDeclaration head) {
        return (XSObjectList)fSubGroupMap.get(head);
    }

    public int getLength() {
        return fGrammarCount;
    }


    public XSNamespaceItem item(int index) {
        if (index < 0 || index >= fGrammarCount) {
            return null;
        }
        return fGrammarList[index];
    }

    public Object get(int index) {
        if (index >= 0 && index < fGrammarCount) {
            return fGrammarList[index];
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    public int size() {
        return getLength();
    }

    public Iterator iterator() {
        return listIterator0(0);
    }

    public ListIterator listIterator() {
        return listIterator0(0);
    }

    public ListIterator listIterator(int index) {
        if (index >= 0 && index < fGrammarCount) {
            return listIterator0(index);
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    private ListIterator listIterator0(int index) {
        return new XSNamespaceItemListIterator(index);
    }

    public Object[] toArray() {
        Object[] a = new Object[fGrammarCount];
        toArray0(a);
        return a;
    }

    public Object[] toArray(Object[] a) {
        if (a.length < fGrammarCount) {
            Class arrayClass = a.getClass();
            Class componentType = arrayClass.getComponentType();
            a = (Object[]) Array.newInstance(componentType, fGrammarCount);
        }
        toArray0(a);
        if (a.length > fGrammarCount) {
            a[fGrammarCount] = null;
        }
        return a;
    }

    private void toArray0(Object[] a) {
        if (fGrammarCount > 0) {
            System.arraycopy(fGrammarList, 0, a, 0, fGrammarCount);
        }
    }

    private final class XSNamespaceItemListIterator implements ListIterator {
        private int index;
        public XSNamespaceItemListIterator(int index) {
            this.index = index;
        }
        public boolean hasNext() {
            return (index < fGrammarCount);
        }
        public Object next() {
            if (index < fGrammarCount) {
                return fGrammarList[index++];
            }
            throw new NoSuchElementException();
        }
        public boolean hasPrevious() {
            return (index > 0);
        }
        public Object previous() {
            if (index > 0) {
                return fGrammarList[--index];
            }
            throw new NoSuchElementException();
        }
        public int nextIndex() {
            return index;
        }
        public int previousIndex() {
            return index - 1;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
        public void set(Object o) {
            throw new UnsupportedOperationException();
        }
        public void add(Object o) {
            throw new UnsupportedOperationException();
        }
    }

}