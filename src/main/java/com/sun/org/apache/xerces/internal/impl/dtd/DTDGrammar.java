


package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMAny;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMBinOp;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMLeaf;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMUniOp;
import com.sun.org.apache.xerces.internal.impl.dtd.models.ContentModelValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.models.DFAContentModel;
import com.sun.org.apache.xerces.internal.impl.dtd.models.MixedContentModel;
import com.sun.org.apache.xerces.internal.impl.dtd.models.SimpleContentModel;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DTDGrammar
    implements XMLDTDHandler, XMLDTDContentModelHandler, EntityState, Grammar {

    public static final int TOP_LEVEL_SCOPE = -1;

    private static final int CHUNK_SHIFT = 8; private static final int CHUNK_SIZE = (1 << CHUNK_SHIFT);


    private static final int CHUNK_MASK = CHUNK_SIZE - 1;


    private static final int INITIAL_CHUNK_COUNT = (1 << (10 - CHUNK_SHIFT)); private static final short LIST_FLAG = 0x80;


    private static final short LIST_MASK = ~LIST_FLAG;

    private static final boolean DEBUG = false;

    protected XMLDTDSource fDTDSource = null;
    protected XMLDTDContentModelSource fDTDContentModelSource = null;


    protected int fCurrentElementIndex;


    protected int fCurrentAttributeIndex;


    protected boolean fReadingExternalDTD = false;


    private SymbolTable fSymbolTable;

    protected XMLDTDDescription fGrammarDescription = null;

    private int fElementDeclCount = 0;


    private QName fElementDeclName[][] = new QName[INITIAL_CHUNK_COUNT][];


    private short fElementDeclType[][] = new short[INITIAL_CHUNK_COUNT][];


    private int fElementDeclContentSpecIndex[][] = new int[INITIAL_CHUNK_COUNT][];


    private ContentModelValidator fElementDeclContentModelValidator[][] = new ContentModelValidator[INITIAL_CHUNK_COUNT][];


    private int fElementDeclFirstAttributeDeclIndex[][] = new int[INITIAL_CHUNK_COUNT][];


    private int fElementDeclLastAttributeDeclIndex[][] = new int[INITIAL_CHUNK_COUNT][];

    private int fAttributeDeclCount = 0 ;


    private QName fAttributeDeclName[][] = new QName[INITIAL_CHUNK_COUNT][];

    private boolean fIsImmutable = false;


    private short fAttributeDeclType[][] = new short[INITIAL_CHUNK_COUNT][];


    private String[] fAttributeDeclEnumeration[][] = new String[INITIAL_CHUNK_COUNT][][];
    private short fAttributeDeclDefaultType[][] = new short[INITIAL_CHUNK_COUNT][];
    private DatatypeValidator fAttributeDeclDatatypeValidator[][] = new DatatypeValidator[INITIAL_CHUNK_COUNT][];
    private String fAttributeDeclDefaultValue[][] = new String[INITIAL_CHUNK_COUNT][];
    private String fAttributeDeclNonNormalizedDefaultValue[][] = new String[INITIAL_CHUNK_COUNT][];
    private int fAttributeDeclNextAttributeDeclIndex[][] = new int[INITIAL_CHUNK_COUNT][];

    private int fContentSpecCount = 0;
    private short fContentSpecType[][] = new short[INITIAL_CHUNK_COUNT][];
    private Object fContentSpecValue[][] = new Object[INITIAL_CHUNK_COUNT][];
    private Object fContentSpecOtherValue[][] = new Object[INITIAL_CHUNK_COUNT][];

    private int fEntityCount = 0;
    private String fEntityName[][] = new String[INITIAL_CHUNK_COUNT][];
    private String[][] fEntityValue = new String[INITIAL_CHUNK_COUNT][];
    private String[][] fEntityPublicId = new String[INITIAL_CHUNK_COUNT][];
    private String[][] fEntitySystemId = new String[INITIAL_CHUNK_COUNT][];
    private String[][] fEntityBaseSystemId = new String[INITIAL_CHUNK_COUNT][];
    private String[][] fEntityNotation = new String[INITIAL_CHUNK_COUNT][];
    private byte[][] fEntityIsPE = new byte[INITIAL_CHUNK_COUNT][];
    private byte[][] fEntityInExternal = new byte[INITIAL_CHUNK_COUNT][];

    private int fNotationCount = 0;
    private String fNotationName[][] = new String[INITIAL_CHUNK_COUNT][];
    private String[][] fNotationPublicId = new String[INITIAL_CHUNK_COUNT][];
    private String[][] fNotationSystemId = new String[INITIAL_CHUNK_COUNT][];
    private String[][] fNotationBaseSystemId = new String[INITIAL_CHUNK_COUNT][];

    private final Map<String, Integer> fElementIndexMap = new HashMap<>();


    private final Map<String, Integer> fEntityIndexMap = new HashMap<>();


    private final Map<String, Integer> fNotationIndexMap = new HashMap<>();

    private boolean fMixed;


    private final QName fQName = new QName();


    private final QName fQName2 = new QName();


    protected final XMLAttributeDecl fAttributeDecl = new XMLAttributeDecl();

    private int fLeafCount = 0;
    private int fEpsilonIndex = -1;


    private XMLElementDecl fElementDecl = new XMLElementDecl();


    private XMLEntityDecl fEntityDecl = new XMLEntityDecl();


    private XMLSimpleType fSimpleType = new XMLSimpleType();


    private XMLContentSpec fContentSpec = new XMLContentSpec();


    Map<String, XMLElementDecl> fElementDeclTab = new HashMap<>();


    private short[] fOpStack = null;


    private int[] fNodeIndexStack = null;


    private int[] fPrevNodeIndexStack = null;


    private int fDepth = 0;


    private boolean[] fPEntityStack = new boolean[4];
    private int fPEDepth = 0;

    private int fElementDeclIsExternal[][] = new int[INITIAL_CHUNK_COUNT][];


    private int fAttributeDeclIsExternal[][] = new int[INITIAL_CHUNK_COUNT][];

    int valueIndex            = -1;
    int prevNodeIndex         = -1;
    int nodeIndex             = -1;

    public DTDGrammar(SymbolTable symbolTable, XMLDTDDescription desc) {
        fSymbolTable = symbolTable;
        fGrammarDescription = desc;
    } public XMLGrammarDescription getGrammarDescription() {
        return fGrammarDescription;
    } public boolean getElementDeclIsExternal(int elementDeclIndex) {

        if (elementDeclIndex < 0) {
            return false;
        }

        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;
        return (fElementDeclIsExternal[chunk][index] != 0);

    } public boolean getAttributeDeclIsExternal(int attributeDeclIndex) {

        if (attributeDeclIndex < 0) {
            return false;
        }

        int chunk = attributeDeclIndex >> CHUNK_SHIFT;
        int index = attributeDeclIndex & CHUNK_MASK;
        return (fAttributeDeclIsExternal[chunk][index] != 0);
    }

    public int getAttributeDeclIndex(int elementDeclIndex, String attributeDeclName) {
        if (elementDeclIndex == -1) {
            return -1;
        }
        int attDefIndex = getFirstAttributeDeclIndex(elementDeclIndex);
        while (attDefIndex != -1) {
            getAttributeDecl(attDefIndex, fAttributeDecl);

            if (fAttributeDecl.name.rawname == attributeDeclName
                || attributeDeclName.equals(fAttributeDecl.name.rawname) ) {
                return attDefIndex;
            }
            attDefIndex = getNextAttributeDeclIndex(attDefIndex);
        }
        return -1;
    } public void startDTD(XMLLocator locator, Augmentations augs) throws XNIException {
        fOpStack = null;
        fNodeIndexStack = null;
        fPrevNodeIndexStack = null;
    } public void startParameterEntity(String name,
                                     XMLResourceIdentifier identifier,
                                     String encoding,
                                     Augmentations augs) throws XNIException {

        if (fPEDepth == fPEntityStack.length) {
            boolean[] entityarray = new boolean[fPEntityStack.length * 2];
            System.arraycopy(fPEntityStack, 0, entityarray, 0, fPEntityStack.length);
            fPEntityStack = entityarray;
        }
        fPEntityStack[fPEDepth] = fReadingExternalDTD;
        fPEDepth++;

    } public void startExternalSubset(XMLResourceIdentifier identifier,
                                    Augmentations augs) throws XNIException {
        fReadingExternalDTD = true;
    } public void endParameterEntity(String name, Augmentations augs) throws XNIException {

        fPEDepth--;
        fReadingExternalDTD = fPEntityStack[fPEDepth];

    } public void endExternalSubset(Augmentations augs) throws XNIException {
        fReadingExternalDTD = false;
    } public void elementDecl(String name, String contentModel, Augmentations augs)
        throws XNIException {

        XMLElementDecl tmpElementDecl = fElementDeclTab.get(name) ;

        if ( tmpElementDecl != null ) {
            if (tmpElementDecl.type == -1) {
                fCurrentElementIndex = getElementDeclIndex(name);
            }
            else {
                return;
            }
        }
        else {
            fCurrentElementIndex = createElementDecl();}

        XMLElementDecl elementDecl       = new XMLElementDecl();

        fQName.setValues(null, name, name, null);

        elementDecl.name.setValues(fQName);

        elementDecl.contentModelValidator = null;
        elementDecl.scope= -1;
        if (contentModel.equals("EMPTY")) {
            elementDecl.type = XMLElementDecl.TYPE_EMPTY;
        }
        else if (contentModel.equals("ANY")) {
            elementDecl.type = XMLElementDecl.TYPE_ANY;
        }
        else if (contentModel.startsWith("(") ) {
            if (contentModel.indexOf("#PCDATA") > 0 ) {
                elementDecl.type = XMLElementDecl.TYPE_MIXED;
            }
            else {
                elementDecl.type = XMLElementDecl.TYPE_CHILDREN;
            }
        }


        this.fElementDeclTab.put(name, elementDecl );

        fElementDecl = elementDecl;
        addContentSpecToElement(elementDecl);

        if ( DEBUG ) {
            System.out.println(  "name = " + fElementDecl.name.localpart );
            System.out.println(  "Type = " + fElementDecl.type );
        }

        setElementDecl(fCurrentElementIndex, fElementDecl );int chunk = fCurrentElementIndex >> CHUNK_SHIFT;
        int index = fCurrentElementIndex & CHUNK_MASK;
        ensureElementDeclCapacity(chunk);
        fElementDeclIsExternal[chunk][index] = (fReadingExternalDTD || fPEDepth > 0) ? 1 : 0;

    } public void attributeDecl(String elementName, String attributeName,
                              String type, String[] enumeration,
                              String defaultType, XMLString defaultValue,
                              XMLString nonNormalizedDefaultValue, Augmentations augs) throws XNIException {

        if ( this.fElementDeclTab.containsKey(elementName) ) {
            }
        else {
            fCurrentElementIndex = createElementDecl();XMLElementDecl elementDecl       = new XMLElementDecl();
            elementDecl.name.setValues(null, elementName, elementName, null);

            elementDecl.scope= -1;

            this.fElementDeclTab.put(elementName, elementDecl );

            setElementDecl(fCurrentElementIndex, elementDecl );
        }

        int elementIndex       = getElementDeclIndex(elementName);

        if (getAttributeDeclIndex(elementIndex, attributeName) != -1) {
            return;
        }

        fCurrentAttributeIndex = createAttributeDecl();fSimpleType.clear();
        if ( defaultType != null ) {
            if ( defaultType.equals( "#FIXED") ) {
                fSimpleType.defaultType = XMLSimpleType.DEFAULT_TYPE_FIXED;
            } else if ( defaultType.equals( "#IMPLIED") ) {
                fSimpleType.defaultType = XMLSimpleType.DEFAULT_TYPE_IMPLIED;
            } else if ( defaultType.equals( "#REQUIRED") ) {
                fSimpleType.defaultType = XMLSimpleType.DEFAULT_TYPE_REQUIRED;
            }
        }
        if ( DEBUG ) {
            System.out.println("defaultvalue = " + defaultValue.toString() );
        }
        fSimpleType.defaultValue      = defaultValue!=null ?  defaultValue.toString() : null;
        fSimpleType.nonNormalizedDefaultValue      = nonNormalizedDefaultValue!=null ?  nonNormalizedDefaultValue.toString() : null;
        fSimpleType.enumeration       = enumeration;

        if (type.equals("CDATA")) {
            fSimpleType.type = XMLSimpleType.TYPE_CDATA;
        }
        else if ( type.equals("ID") ) {
            fSimpleType.type = XMLSimpleType.TYPE_ID;
        }
        else if ( type.startsWith("IDREF") ) {
            fSimpleType.type = XMLSimpleType.TYPE_IDREF;
            if (type.indexOf("S") > 0) {
                fSimpleType.list = true;
            }
        }
        else if (type.equals("ENTITIES")) {
            fSimpleType.type = XMLSimpleType.TYPE_ENTITY;
            fSimpleType.list = true;
        }
        else if (type.equals("ENTITY")) {
            fSimpleType.type = XMLSimpleType.TYPE_ENTITY;
        }
        else if (type.equals("NMTOKENS")) {
            fSimpleType.type = XMLSimpleType.TYPE_NMTOKEN;
            fSimpleType.list = true;
        }
        else if (type.equals("NMTOKEN")) {
            fSimpleType.type = XMLSimpleType.TYPE_NMTOKEN;
        }
        else if (type.startsWith("NOTATION") ) {
            fSimpleType.type = XMLSimpleType.TYPE_NOTATION;
        }
        else if (type.startsWith("ENUMERATION") ) {
            fSimpleType.type = XMLSimpleType.TYPE_ENUMERATION;
        }
        else {
            System.err.println("!!! unknown attribute type "+type);
        }
        fQName.setValues(null, attributeName, attributeName, null);
        fAttributeDecl.setValues( fQName, fSimpleType, false );

        setAttributeDecl(elementIndex, fCurrentAttributeIndex, fAttributeDecl);

        int chunk = fCurrentAttributeIndex >> CHUNK_SHIFT;
        int index = fCurrentAttributeIndex & CHUNK_MASK;
        ensureAttributeDeclCapacity(chunk);
        fAttributeDeclIsExternal[chunk][index] = (fReadingExternalDTD || fPEDepth > 0) ?  1 : 0;

    } public void internalEntityDecl(String name, XMLString text,
                                   XMLString nonNormalizedText,
                                   Augmentations augs) throws XNIException {

        int entityIndex = getEntityDeclIndex(name);
        if( entityIndex == -1){
            entityIndex = createEntityDecl();
            boolean isPE = name.startsWith("%");
            boolean inExternal = (fReadingExternalDTD || fPEDepth > 0);
            XMLEntityDecl  entityDecl = new XMLEntityDecl();
            entityDecl.setValues(name,null,null, null, null,
                                 text.toString(), isPE, inExternal);

            setEntityDecl(entityIndex, entityDecl);
        }

    } public void externalEntityDecl(String name,
                                   XMLResourceIdentifier identifier,
                                   Augmentations augs) throws XNIException {

        int entityIndex = getEntityDeclIndex(name);
        if( entityIndex == -1){
            entityIndex = createEntityDecl();
            boolean isPE = name.startsWith("%");
            boolean inExternal = (fReadingExternalDTD || fPEDepth > 0);

            XMLEntityDecl  entityDecl = new XMLEntityDecl();
            entityDecl.setValues(name, identifier.getPublicId(), identifier.getLiteralSystemId(),
                                identifier.getBaseSystemId(),
                                null, null, isPE, inExternal);

            setEntityDecl(entityIndex, entityDecl);
        }
    } public void unparsedEntityDecl(String name, XMLResourceIdentifier identifier,
                                   String notation,
                                   Augmentations augs) throws XNIException {

        XMLEntityDecl  entityDecl = new XMLEntityDecl();
        boolean isPE = name.startsWith("%");
        boolean inExternal = (fReadingExternalDTD || fPEDepth > 0);

        entityDecl.setValues(name,identifier.getPublicId(),identifier.getLiteralSystemId(),
                            identifier.getBaseSystemId(), notation,
                            null, isPE, inExternal);
        int entityIndex = getEntityDeclIndex(name);
        if (entityIndex == -1) {
            entityIndex = createEntityDecl();
            setEntityDecl(entityIndex, entityDecl);
        }

    } public void notationDecl(String name, XMLResourceIdentifier identifier,
                             Augmentations augs) throws XNIException {

        XMLNotationDecl  notationDecl = new XMLNotationDecl();
        notationDecl.setValues(name,identifier.getPublicId(),identifier.getLiteralSystemId(),
                identifier.getBaseSystemId());
        int notationIndex = getNotationDeclIndex(name);
        if (notationIndex == -1) {
            notationIndex = createNotationDecl();
            setNotationDecl(notationIndex, notationDecl);
        }

    } public void endDTD(Augmentations augs) throws XNIException {
        fIsImmutable = true;
        if (fGrammarDescription.getRootName() == null) {
            int chunk, index = 0;
            String currName = null;
            final int size = fElementDeclCount;
            ArrayList elements = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                chunk = i >> CHUNK_SHIFT;
                index = i & CHUNK_MASK;
                currName = fElementDeclName[chunk][index].rawname;
                elements.add(currName);
            }
            fGrammarDescription.setPossibleRoots(elements);
        }
    } public void setDTDSource(XMLDTDSource source) {
        fDTDSource = source;
    } public XMLDTDSource getDTDSource() {
        return fDTDSource;
    } public void textDecl(String version, String encoding, Augmentations augs)
        throws XNIException {}


    public void comment(XMLString text, Augmentations augs) throws XNIException {}


    public void processingInstruction(String target, XMLString data,
                                      Augmentations augs) throws XNIException {}


    public void startAttlist(String elementName, Augmentations augs)
        throws XNIException {}


    public void endAttlist(Augmentations augs) throws XNIException {}


    public void startConditional(short type, Augmentations augs)
        throws XNIException {}


    public void ignoredCharacters(XMLString text, Augmentations augs)
        throws XNIException {}


    public void endConditional(Augmentations augs) throws XNIException {}

    public void setDTDContentModelSource(XMLDTDContentModelSource source) {
        fDTDContentModelSource = source;
    }

    public XMLDTDContentModelSource getDTDContentModelSource() {
        return fDTDContentModelSource;
    }


    public void startContentModel(String elementName, Augmentations augs)
        throws XNIException {

        XMLElementDecl elementDecl = this.fElementDeclTab.get(elementName);
        if ( elementDecl != null ) {
            fElementDecl = elementDecl;
        }
        fDepth = 0;
        initializeContentModelStack();

    } public void startGroup(Augmentations augs) throws XNIException {
        fDepth++;
        initializeContentModelStack();
        fMixed = false;
    } public void pcdata(Augmentations augs) throws XNIException {
        fMixed = true;
    } public void element(String elementName, Augmentations augs) throws XNIException {
        if (fMixed) {
            if (fNodeIndexStack[fDepth] == -1 ) {
                fNodeIndexStack[fDepth] = addUniqueLeafNode(elementName);
            }
            else {
                fNodeIndexStack[fDepth] = addContentSpecNode(XMLContentSpec.CONTENTSPECNODE_CHOICE,
                                                             fNodeIndexStack[fDepth],
                                                             addUniqueLeafNode(elementName));
            }
        }
        else {
            fNodeIndexStack[fDepth] = addContentSpecNode(XMLContentSpec.CONTENTSPECNODE_LEAF, elementName);
        }
    } public void separator(short separator, Augmentations augs) throws XNIException {

        if (!fMixed) {
            if (fOpStack[fDepth] != XMLContentSpec.CONTENTSPECNODE_SEQ && separator == XMLDTDContentModelHandler.SEPARATOR_CHOICE ) {
                if (fPrevNodeIndexStack[fDepth] != -1) {
                    fNodeIndexStack[fDepth] = addContentSpecNode(fOpStack[fDepth], fPrevNodeIndexStack[fDepth], fNodeIndexStack[fDepth]);
                }
                fPrevNodeIndexStack[fDepth] = fNodeIndexStack[fDepth];
                fOpStack[fDepth] = XMLContentSpec.CONTENTSPECNODE_CHOICE;
            } else if (fOpStack[fDepth] != XMLContentSpec.CONTENTSPECNODE_CHOICE && separator == XMLDTDContentModelHandler.SEPARATOR_SEQUENCE) {
                if (fPrevNodeIndexStack[fDepth] != -1) {
                    fNodeIndexStack[fDepth] = addContentSpecNode(fOpStack[fDepth], fPrevNodeIndexStack[fDepth], fNodeIndexStack[fDepth]);
                }
                fPrevNodeIndexStack[fDepth] = fNodeIndexStack[fDepth];
                fOpStack[fDepth] = XMLContentSpec.CONTENTSPECNODE_SEQ;
            }
        }

    } public void occurrence(short occurrence, Augmentations augs) throws XNIException {

        if (!fMixed) {
            if (occurrence == XMLDTDContentModelHandler.OCCURS_ZERO_OR_ONE ) {
                fNodeIndexStack[fDepth] = addContentSpecNode(XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE, fNodeIndexStack[fDepth], -1);
            } else if ( occurrence == XMLDTDContentModelHandler.OCCURS_ZERO_OR_MORE ) {
                fNodeIndexStack[fDepth] = addContentSpecNode(XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE, fNodeIndexStack[fDepth], -1 );
            } else if ( occurrence == XMLDTDContentModelHandler.OCCURS_ONE_OR_MORE) {
                fNodeIndexStack[fDepth] = addContentSpecNode(XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE, fNodeIndexStack[fDepth], -1 );
            }
        }

    } public void endGroup(Augmentations augs) throws XNIException {

        if (!fMixed) {
            if (fPrevNodeIndexStack[fDepth] != -1) {
                fNodeIndexStack[fDepth] = addContentSpecNode(fOpStack[fDepth], fPrevNodeIndexStack[fDepth], fNodeIndexStack[fDepth]);
            }
            int nodeIndex = fNodeIndexStack[fDepth--];
            fNodeIndexStack[fDepth] = nodeIndex;
        }

    } public void any(Augmentations augs) throws XNIException {}


    public void empty(Augmentations augs) throws XNIException {}


    public void endContentModel(Augmentations augs) throws XNIException {}

    public boolean isNamespaceAware() {
        return false;
    } public SymbolTable getSymbolTable() {
        return fSymbolTable;
    } public int getFirstElementDeclIndex() {
        return fElementDeclCount >= 0 ? 0 : -1;
    } public int getNextElementDeclIndex(int elementDeclIndex) {
        return elementDeclIndex < fElementDeclCount - 1
             ? elementDeclIndex + 1 : -1;
    } public int getElementDeclIndex(String elementDeclName) {
        Integer mapping = fElementIndexMap.get(elementDeclName);
        if (mapping == null) {
            mapping = -1;
        }
        return mapping;
    } public int getElementDeclIndex(QName elementDeclQName) {
        return getElementDeclIndex(elementDeclQName.rawname);
    } public short getContentSpecType(int elementIndex){
        if (elementIndex < 0 || elementIndex >= fElementDeclCount) {
            return -1 ;
        }

        int chunk = elementIndex >> CHUNK_SHIFT;
        int index = elementIndex &  CHUNK_MASK;

        if(fElementDeclType[chunk][index] == -1){
            return -1 ;
                            }
        else{
                                       return (short) (fElementDeclType[chunk][index] & LIST_MASK);
                            }

                }public boolean getElementDecl(int elementDeclIndex,
                                  XMLElementDecl elementDecl) {

        if (elementDeclIndex < 0 || elementDeclIndex >= fElementDeclCount) {
            return false;
        }

        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex &  CHUNK_MASK;

        elementDecl.name.setValues(fElementDeclName[chunk][index]);

        if (fElementDeclType[chunk][index] == -1) {
            elementDecl.type                    = -1;
            elementDecl.simpleType.list = false;
        } else {
            elementDecl.type            = (short) (fElementDeclType[chunk][index] & LIST_MASK);
            elementDecl.simpleType.list = (fElementDeclType[chunk][index] & LIST_FLAG) != 0;
        }


        if (elementDecl.type == XMLElementDecl.TYPE_CHILDREN || elementDecl.type == XMLElementDecl.TYPE_MIXED) {
            elementDecl.contentModelValidator = getElementContentModelValidator(elementDeclIndex);
        }

        elementDecl.simpleType.datatypeValidator = null;
        elementDecl.simpleType.defaultType       = -1;
        elementDecl.simpleType.defaultValue      = null;

        return true;

    } QName getElementDeclName(int elementDeclIndex) {
        if (elementDeclIndex < 0 || elementDeclIndex >= fElementDeclCount) {
            return null;
        }
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex &  CHUNK_MASK;
        return fElementDeclName[chunk][index];
    }

    public int getFirstAttributeDeclIndex(int elementDeclIndex) {
        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex &  CHUNK_MASK;

        return  fElementDeclFirstAttributeDeclIndex[chunk][index];
    } public int getNextAttributeDeclIndex(int attributeDeclIndex) {
        int chunk = attributeDeclIndex >> CHUNK_SHIFT;
        int index = attributeDeclIndex &  CHUNK_MASK;

        return fAttributeDeclNextAttributeDeclIndex[chunk][index];
    } public boolean getAttributeDecl(int attributeDeclIndex, XMLAttributeDecl attributeDecl) {
        if (attributeDeclIndex < 0 || attributeDeclIndex >= fAttributeDeclCount) {
            return false;
        }
        int chunk = attributeDeclIndex >> CHUNK_SHIFT;
        int index = attributeDeclIndex & CHUNK_MASK;

        attributeDecl.name.setValues(fAttributeDeclName[chunk][index]);

        short attributeType;
        boolean isList;

        if (fAttributeDeclType[chunk][index] == -1) {

            attributeType = -1;
            isList = false;
        } else {
            attributeType = (short) (fAttributeDeclType[chunk][index] & LIST_MASK);
            isList = (fAttributeDeclType[chunk][index] & LIST_FLAG) != 0;
        }
        attributeDecl.simpleType.setValues(attributeType,fAttributeDeclName[chunk][index].localpart,
                                           fAttributeDeclEnumeration[chunk][index],
                                           isList, fAttributeDeclDefaultType[chunk][index],
                                           fAttributeDeclDefaultValue[chunk][index],
                                           fAttributeDeclNonNormalizedDefaultValue[chunk][index],
                                           fAttributeDeclDatatypeValidator[chunk][index]);
        return true;

    } public boolean isCDATAAttribute(QName elName, QName atName) {
        int elDeclIdx = getElementDeclIndex(elName);
        if (getAttributeDecl(elDeclIdx, fAttributeDecl)
            && fAttributeDecl.simpleType.type != XMLSimpleType.TYPE_CDATA){
            return false;
        }
        return true;
    }


    public int getEntityDeclIndex(String entityDeclName) {
        if (entityDeclName == null || fEntityIndexMap.get(entityDeclName) == null) {
            return -1;
        }

        return fEntityIndexMap.get(entityDeclName);
    } public boolean getEntityDecl(int entityDeclIndex, XMLEntityDecl entityDecl) {
        if (entityDeclIndex < 0 || entityDeclIndex >= fEntityCount) {
            return false;
        }
        int chunk = entityDeclIndex >> CHUNK_SHIFT;
        int index = entityDeclIndex & CHUNK_MASK;

        entityDecl.setValues(fEntityName[chunk][index],
                             fEntityPublicId[chunk][index],
                             fEntitySystemId[chunk][index],
                             fEntityBaseSystemId[chunk][index],
                             fEntityNotation[chunk][index],
                             fEntityValue[chunk][index],
                             fEntityIsPE[chunk][index] == 0 ? false : true ,
                             fEntityInExternal[chunk][index] == 0 ? false : true );

        return true;
    } public int getNotationDeclIndex(String notationDeclName) {
        if (notationDeclName == null || fNotationIndexMap.get(notationDeclName) == null) {
            return -1;
        }

        return fNotationIndexMap.get(notationDeclName);
    } public boolean getNotationDecl(int notationDeclIndex, XMLNotationDecl notationDecl) {
        if (notationDeclIndex < 0 || notationDeclIndex >= fNotationCount) {
            return false;
        }
        int chunk = notationDeclIndex >> CHUNK_SHIFT;
        int index = notationDeclIndex & CHUNK_MASK;

        notationDecl.setValues(fNotationName[chunk][index],
                               fNotationPublicId[chunk][index],
                               fNotationSystemId[chunk][index],
                               fNotationBaseSystemId[chunk][index]);

        return true;

    } public boolean getContentSpec(int contentSpecIndex, XMLContentSpec contentSpec) {
        if (contentSpecIndex < 0 || contentSpecIndex >= fContentSpecCount )
            return false;

        int chunk = contentSpecIndex >> CHUNK_SHIFT;
        int index = contentSpecIndex & CHUNK_MASK;

        contentSpec.type       = fContentSpecType[chunk][index];
        contentSpec.value      = fContentSpecValue[chunk][index];
        contentSpec.otherValue = fContentSpecOtherValue[chunk][index];
        return true;
    }


    public int getContentSpecIndex(int elementDeclIndex) {
        if (elementDeclIndex < 0 || elementDeclIndex >= fElementDeclCount) {
            return -1;
        }
        final int chunk = elementDeclIndex >> CHUNK_SHIFT;
        final int index = elementDeclIndex & CHUNK_MASK;
        return fElementDeclContentSpecIndex[chunk][index];
    }


    public String getContentSpecAsString(int elementDeclIndex){

        if (elementDeclIndex < 0 || elementDeclIndex >= fElementDeclCount) {
            return null;
        }

        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex &  CHUNK_MASK;

        int contentSpecIndex = fElementDeclContentSpecIndex[chunk][index];

        XMLContentSpec contentSpec = new XMLContentSpec();

        if (getContentSpec(contentSpecIndex, contentSpec)) {

            StringBuffer str = new StringBuffer();
            int    parentContentSpecType = contentSpec.type & 0x0f;
            int    nextContentSpec;
            switch (parentContentSpecType) {
                case XMLContentSpec.CONTENTSPECNODE_LEAF: {
                    str.append('(');
                    if (contentSpec.value == null && contentSpec.otherValue == null) {
                        str.append("#PCDATA");
                    }
                    else {
                        str.append(contentSpec.value);
                    }
                    str.append(')');
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE: {
                    getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    nextContentSpec = contentSpec.type;

                    if (nextContentSpec == XMLContentSpec.CONTENTSPECNODE_LEAF) {
                        str.append('(');
                        str.append(contentSpec.value);
                        str.append(')');
                    } else if( nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE  ||
                        nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE  ||
                        nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ) {
                        str.append('(' );
                        appendContentSpec(contentSpec, str,
                                          true, parentContentSpecType );
                        str.append(')');
                    } else {
                        appendContentSpec(contentSpec, str,
                                          true, parentContentSpecType );
                    }
                    str.append('?');
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE: {
                    getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    nextContentSpec = contentSpec.type;

                    if ( nextContentSpec == XMLContentSpec.CONTENTSPECNODE_LEAF) {
                        str.append('(');
                        if (contentSpec.value == null && contentSpec.otherValue == null) {
                            str.append("#PCDATA");
                        }
                        else if (contentSpec.otherValue != null) {
                            str.append("##any:uri=").append(contentSpec.otherValue);
                        }
                        else if (contentSpec.value == null) {
                            str.append("##any");
                        }
                        else {
                            appendContentSpec(contentSpec, str,
                                              true, parentContentSpecType );
                        }
                        str.append(')');
                    } else if( nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE  ||
                        nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE  ||
                        nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ) {
                        str.append('(' );
                        appendContentSpec(contentSpec, str,
                                          true, parentContentSpecType );
                        str.append(')');
                    } else {
                        appendContentSpec(contentSpec, str,
                                          true, parentContentSpecType );
                    }
                    str.append('*');
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE: {
                    getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    nextContentSpec = contentSpec.type;

                    if ( nextContentSpec == XMLContentSpec.CONTENTSPECNODE_LEAF) {
                        str.append('(');
                        if (contentSpec.value == null && contentSpec.otherValue == null) {
                            str.append("#PCDATA");
                        }
                        else if (contentSpec.otherValue != null) {
                            str.append("##any:uri=").append(contentSpec.otherValue);
                        }
                        else if (contentSpec.value == null) {
                            str.append("##any");
                        }
                        else {
                            str.append(contentSpec.value);
                        }
                        str.append(')');
                    } else if( nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE  ||
                        nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE  ||
                        nextContentSpec == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ) {
                        str.append('(' );
                        appendContentSpec(contentSpec, str,
                                          true, parentContentSpecType );
                        str.append(')');
                    } else {
                        appendContentSpec(contentSpec, str,
                                          true, parentContentSpecType);
                    }
                    str.append('+');
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_CHOICE:
                case XMLContentSpec.CONTENTSPECNODE_SEQ: {
                    appendContentSpec(contentSpec, str,
                                      true, parentContentSpecType );
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ANY: {
                    str.append("##any");
                    if (contentSpec.otherValue != null) {
                        str.append(":uri=");
                        str.append(contentSpec.otherValue);
                    }
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ANY_OTHER: {
                    str.append("##other:uri=");
                    str.append(contentSpec.otherValue);
                    break;
                }
                case XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL: {
                    str.append("##local");
                    break;
                }
                default: {
                    str.append("???");
                }

            } return str.toString();
        }

        return null;

    } public void printElements(  ) {
        int elementDeclIndex = 0;
        XMLElementDecl elementDecl = new XMLElementDecl();
        while (getElementDecl(elementDeclIndex++, elementDecl)) {

            System.out.println("element decl: "+elementDecl.name+
                               ", "+ elementDecl.name.rawname  );

            }
    }

    public void printAttributes(int elementDeclIndex) {
        int attributeDeclIndex = getFirstAttributeDeclIndex(elementDeclIndex);
        System.out.print(elementDeclIndex);
        System.out.print(" [");
        while (attributeDeclIndex != -1) {
            System.out.print(' ');
            System.out.print(attributeDeclIndex);
            printAttribute(attributeDeclIndex);
            attributeDeclIndex = getNextAttributeDeclIndex(attributeDeclIndex);
            if (attributeDeclIndex != -1) {
                System.out.print(",");
            }
        }
        System.out.println(" ]");
    }

    protected void addContentSpecToElement(XMLElementDecl elementDecl) {
        if ((fDepth == 0 || (fDepth == 1 && elementDecl.type == XMLElementDecl.TYPE_MIXED)) &&
                fNodeIndexStack != null) {
            if (elementDecl.type == XMLElementDecl.TYPE_MIXED) {
                int pcdata = addUniqueLeafNode(null);
                if (fNodeIndexStack[0] == -1) {
                    fNodeIndexStack[0] = pcdata;
                }
                else {
                    fNodeIndexStack[0] = addContentSpecNode(XMLContentSpec.CONTENTSPECNODE_CHOICE,
                            pcdata, fNodeIndexStack[0]);
                }
            }
            setContentSpecIndex(fCurrentElementIndex, fNodeIndexStack[fDepth]);
        }
    }


    protected ContentModelValidator getElementContentModelValidator(int elementDeclIndex) {

        int chunk = elementDeclIndex >> CHUNK_SHIFT;
        int index = elementDeclIndex & CHUNK_MASK;

        ContentModelValidator contentModel    =  fElementDeclContentModelValidator[chunk][index];

        if (contentModel != null) {
            return contentModel;
        }

        int contentType = fElementDeclType[chunk][index];
        if (contentType == XMLElementDecl.TYPE_SIMPLE) {
            return null;
        }

        int contentSpecIndex = fElementDeclContentSpecIndex[chunk][index];



        XMLContentSpec  contentSpec = new XMLContentSpec();
        getContentSpec( contentSpecIndex, contentSpec );

        if ( contentType == XMLElementDecl.TYPE_MIXED ) {
            ChildrenList children = new ChildrenList();
            contentSpecTree(contentSpecIndex, contentSpec, children);
            contentModel = new MixedContentModel(children.qname,
                                                 children.type,
                                                 0, children.length,
                                                 false);
        } else if (contentType == XMLElementDecl.TYPE_CHILDREN) {
            contentModel = createChildModel(contentSpecIndex);
        } else {
            throw new RuntimeException("Unknown content type for a element decl "
                                     + "in getElementContentModelValidator() in AbstractDTDGrammar class");
        }

        fElementDeclContentModelValidator[chunk][index] = contentModel;

        return contentModel;

    } protected int createElementDecl() {
      int chunk = fElementDeclCount >> CHUNK_SHIFT;
      int index = fElementDeclCount & CHUNK_MASK;
      ensureElementDeclCapacity(chunk);
      fElementDeclName[chunk][index]                    = new QName();
      fElementDeclType[chunk][index]                    = -1;
      fElementDeclContentModelValidator[chunk][index]   = null;
      fElementDeclFirstAttributeDeclIndex[chunk][index] = -1;
      fElementDeclLastAttributeDeclIndex[chunk][index]  = -1;
      return fElementDeclCount++;
   }

   protected void setElementDecl(int elementDeclIndex, XMLElementDecl elementDecl) {
      if (elementDeclIndex < 0 || elementDeclIndex >= fElementDeclCount) {
         return;
      }
      int     chunk       = elementDeclIndex >> CHUNK_SHIFT;
      int     index       = elementDeclIndex &  CHUNK_MASK;

      fElementDeclName[chunk][index].setValues(elementDecl.name);
      fElementDeclType[chunk][index]                  = elementDecl.type;

      fElementDeclContentModelValidator[chunk][index] = elementDecl.contentModelValidator;

      if (elementDecl.simpleType.list  == true ) {
         fElementDeclType[chunk][index] |= LIST_FLAG;
      }

      fElementIndexMap.put(elementDecl.name.rawname, elementDeclIndex);
   }




   protected void putElementNameMapping(QName name, int scope,
                                        int elementDeclIndex) {
   }

   protected void setFirstAttributeDeclIndex(int elementDeclIndex, int newFirstAttrIndex){

      if (elementDeclIndex < 0 || elementDeclIndex >= fElementDeclCount) {
         return;
      }

      int chunk = elementDeclIndex >> CHUNK_SHIFT;
      int index = elementDeclIndex &  CHUNK_MASK;

      fElementDeclFirstAttributeDeclIndex[chunk][index] = newFirstAttrIndex;
   }

   protected void setContentSpecIndex(int elementDeclIndex, int contentSpecIndex){

      if (elementDeclIndex < 0 || elementDeclIndex >= fElementDeclCount) {
         return;
      }

      int chunk = elementDeclIndex >> CHUNK_SHIFT;
      int index = elementDeclIndex &  CHUNK_MASK;

      fElementDeclContentSpecIndex[chunk][index] = contentSpecIndex;
   }


   protected int createAttributeDecl() {
      int chunk = fAttributeDeclCount >> CHUNK_SHIFT;
      int index = fAttributeDeclCount & CHUNK_MASK;

      ensureAttributeDeclCapacity(chunk);
      fAttributeDeclName[chunk][index]                    = new QName();
      fAttributeDeclType[chunk][index]                    = -1;
      fAttributeDeclDatatypeValidator[chunk][index]       = null;
      fAttributeDeclEnumeration[chunk][index]             = null;
      fAttributeDeclDefaultType[chunk][index]             = XMLSimpleType.DEFAULT_TYPE_IMPLIED;
      fAttributeDeclDefaultValue[chunk][index]            = null;
      fAttributeDeclNonNormalizedDefaultValue[chunk][index]            = null;
      fAttributeDeclNextAttributeDeclIndex[chunk][index]  = -1;
      return fAttributeDeclCount++;
   }


   protected void setAttributeDecl(int elementDeclIndex, int attributeDeclIndex,
                                   XMLAttributeDecl attributeDecl) {
      int attrChunk = attributeDeclIndex >> CHUNK_SHIFT;
      int attrIndex = attributeDeclIndex &  CHUNK_MASK;
      fAttributeDeclName[attrChunk][attrIndex].setValues(attributeDecl.name);
      fAttributeDeclType[attrChunk][attrIndex]  =  attributeDecl.simpleType.type;

      if (attributeDecl.simpleType.list) {
         fAttributeDeclType[attrChunk][attrIndex] |= LIST_FLAG;
      }
      fAttributeDeclEnumeration[attrChunk][attrIndex]  =  attributeDecl.simpleType.enumeration;
      fAttributeDeclDefaultType[attrChunk][attrIndex]  =  attributeDecl.simpleType.defaultType;
      fAttributeDeclDatatypeValidator[attrChunk][attrIndex] =  attributeDecl.simpleType.datatypeValidator;

      fAttributeDeclDefaultValue[attrChunk][attrIndex] = attributeDecl.simpleType.defaultValue;
      fAttributeDeclNonNormalizedDefaultValue[attrChunk][attrIndex] = attributeDecl.simpleType.nonNormalizedDefaultValue;

      int elemChunk     = elementDeclIndex >> CHUNK_SHIFT;
      int elemIndex     = elementDeclIndex &  CHUNK_MASK;
      int index         = fElementDeclFirstAttributeDeclIndex[elemChunk][elemIndex];
      while (index != -1) {
         if (index == attributeDeclIndex) {
            break;
         }
         attrChunk = index >> CHUNK_SHIFT;
         attrIndex = index & CHUNK_MASK;
         index = fAttributeDeclNextAttributeDeclIndex[attrChunk][attrIndex];
      }
      if (index == -1) {
         if (fElementDeclFirstAttributeDeclIndex[elemChunk][elemIndex] == -1) {
            fElementDeclFirstAttributeDeclIndex[elemChunk][elemIndex] = attributeDeclIndex;
         } else {
            index = fElementDeclLastAttributeDeclIndex[elemChunk][elemIndex];
            attrChunk = index >> CHUNK_SHIFT;
            attrIndex = index & CHUNK_MASK;
            fAttributeDeclNextAttributeDeclIndex[attrChunk][attrIndex] = attributeDeclIndex;
         }
         fElementDeclLastAttributeDeclIndex[elemChunk][elemIndex] = attributeDeclIndex;
      }
   }

   protected int createContentSpec() {
      int chunk = fContentSpecCount >> CHUNK_SHIFT;
      int index = fContentSpecCount & CHUNK_MASK;

      ensureContentSpecCapacity(chunk);
      fContentSpecType[chunk][index]       = -1;
      fContentSpecValue[chunk][index]      = null;
      fContentSpecOtherValue[chunk][index] = null;

      return fContentSpecCount++;
   }

   protected void setContentSpec(int contentSpecIndex, XMLContentSpec contentSpec) {
      int   chunk = contentSpecIndex >> CHUNK_SHIFT;
      int   index = contentSpecIndex & CHUNK_MASK;

      fContentSpecType[chunk][index]       = contentSpec.type;
      fContentSpecValue[chunk][index]      = contentSpec.value;
      fContentSpecOtherValue[chunk][index] = contentSpec.otherValue;
   }


   protected int createEntityDecl() {
       int chunk = fEntityCount >> CHUNK_SHIFT;
       int index = fEntityCount & CHUNK_MASK;

      ensureEntityDeclCapacity(chunk);
      fEntityIsPE[chunk][index] = 0;
      fEntityInExternal[chunk][index] = 0;

      return fEntityCount++;
   }

   protected void setEntityDecl(int entityDeclIndex, XMLEntityDecl entityDecl) {
       int chunk = entityDeclIndex >> CHUNK_SHIFT;
       int index = entityDeclIndex & CHUNK_MASK;

       fEntityName[chunk][index] = entityDecl.name;
       fEntityValue[chunk][index] = entityDecl.value;
       fEntityPublicId[chunk][index] = entityDecl.publicId;
       fEntitySystemId[chunk][index] = entityDecl.systemId;
       fEntityBaseSystemId[chunk][index] = entityDecl.baseSystemId;
       fEntityNotation[chunk][index] = entityDecl.notation;
       fEntityIsPE[chunk][index] = entityDecl.isPE ? (byte)1 : (byte)0;
       fEntityInExternal[chunk][index] = entityDecl.inExternal ? (byte)1 : (byte)0;

       fEntityIndexMap.put(entityDecl.name, entityDeclIndex);
   }

   protected int createNotationDecl() {
       int chunk = fNotationCount >> CHUNK_SHIFT;
       ensureNotationDeclCapacity(chunk);
       return fNotationCount++;
   }

   protected void setNotationDecl(int notationDeclIndex, XMLNotationDecl notationDecl) {
       int chunk = notationDeclIndex >> CHUNK_SHIFT;
       int index = notationDeclIndex & CHUNK_MASK;

       fNotationName[chunk][index] = notationDecl.name;
       fNotationPublicId[chunk][index] = notationDecl.publicId;
       fNotationSystemId[chunk][index] = notationDecl.systemId;
       fNotationBaseSystemId[chunk][index] = notationDecl.baseSystemId;

       fNotationIndexMap.put(notationDecl.name, notationDeclIndex);
   }


    protected int addContentSpecNode(short nodeType, String nodeValue) {

        int contentSpecIndex = createContentSpec();

        fContentSpec.setValues(nodeType, nodeValue, null);
        setContentSpec(contentSpecIndex, fContentSpec);

        return contentSpecIndex;

    } protected int addUniqueLeafNode(String elementName) {

        int contentSpecIndex = createContentSpec();

        fContentSpec.setValues( XMLContentSpec.CONTENTSPECNODE_LEAF,
                                elementName, null);
        setContentSpec(contentSpecIndex, fContentSpec);

        return contentSpecIndex;

    } protected int addContentSpecNode(short nodeType,
                                     int leftNodeIndex, int rightNodeIndex) {

        int contentSpecIndex = createContentSpec();

        int[] leftIntArray  = new int[1];
        int[] rightIntArray = new int[1];

        leftIntArray[0]      = leftNodeIndex;
        rightIntArray[0]    = rightNodeIndex;
        fContentSpec.setValues(nodeType, leftIntArray, rightIntArray);
        setContentSpec(contentSpecIndex, fContentSpec);

        return contentSpecIndex;

    } protected void initializeContentModelStack() {

        if (fOpStack == null) {
            fOpStack = new short[8];
            fNodeIndexStack = new int[8];
            fPrevNodeIndexStack = new int[8];
        } else if (fDepth == fOpStack.length) {
            short[] newStack = new short[fDepth * 2];
            System.arraycopy(fOpStack, 0, newStack, 0, fDepth);
            fOpStack = newStack;
            int[]   newIntStack = new int[fDepth * 2];
            System.arraycopy(fNodeIndexStack, 0, newIntStack, 0, fDepth);
            fNodeIndexStack = newIntStack;
            newIntStack = new int[fDepth * 2];
            System.arraycopy(fPrevNodeIndexStack, 0, newIntStack, 0, fDepth);
            fPrevNodeIndexStack = newIntStack;
        }
        fOpStack[fDepth] = -1;
        fNodeIndexStack[fDepth] = -1;
        fPrevNodeIndexStack[fDepth] = -1;

    } boolean isImmutable() {
        return fIsImmutable;
    }

    private void appendContentSpec(XMLContentSpec contentSpec,
                                   StringBuffer str, boolean parens,
                                   int parentContentSpecType ) {

        int thisContentSpec = contentSpec.type & 0x0f;
        switch (thisContentSpec) {
            case XMLContentSpec.CONTENTSPECNODE_LEAF: {
                if (contentSpec.value == null && contentSpec.otherValue == null) {
                    str.append("#PCDATA");
                }
                else if (contentSpec.value == null && contentSpec.otherValue != null) {
                    str.append("##any:uri=").append(contentSpec.otherValue);
                }
                else if (contentSpec.value == null) {
                    str.append("##any");
                }
                else {
                    str.append(contentSpec.value);
                }
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE: {
                if (parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE  ||
                    parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE ||
                    parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ) {
                    getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    str.append('(');
                    appendContentSpec(contentSpec, str, true, thisContentSpec );
                    str.append(')');
                }
                else {
                    getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    appendContentSpec( contentSpec, str, true, thisContentSpec );
                }
                str.append('?');
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE: {
                if (parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE ||
                    parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE ||
                    parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ) {
                    getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    str.append('(');
                    appendContentSpec(contentSpec, str, true, thisContentSpec);
                    str.append(')' );
                }
                else {
                    getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    appendContentSpec(contentSpec, str, true, thisContentSpec);
                }
                str.append('*');
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE: {
                if (parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE   ||
                    parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE  ||
                    parentContentSpecType == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ) {

                    str.append('(');
                    getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    appendContentSpec(contentSpec, str, true, thisContentSpec);
                    str.append(')' );
                }
                else {
                    getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                    appendContentSpec(contentSpec, str, true, thisContentSpec);
                }
                str.append('+');
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_CHOICE:
            case XMLContentSpec.CONTENTSPECNODE_SEQ: {
                if (parens) {
                    str.append('(');
                }
                int type = contentSpec.type;
                int otherValue = ((int[])contentSpec.otherValue)[0];
                getContentSpec(((int[])contentSpec.value)[0], contentSpec);
                appendContentSpec(contentSpec, str, contentSpec.type != type, thisContentSpec);
                if (type == XMLContentSpec.CONTENTSPECNODE_CHOICE) {
                    str.append('|');
                }
                else {
                    str.append(',');
                }
                getContentSpec(otherValue, contentSpec);
                appendContentSpec(contentSpec, str, true, thisContentSpec);
                if (parens) {
                    str.append(')');
                }
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_ANY: {
                str.append("##any");
                if (contentSpec.otherValue != null) {
                    str.append(":uri=");
                    str.append(contentSpec.otherValue);
                }
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_ANY_OTHER: {
                str.append("##other:uri=");
                str.append(contentSpec.otherValue);
                break;
            }
            case XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL: {
                str.append("##local");
                break;
            }
            default: {
                str.append("???");
                break;
            }

        } } private void printAttribute(int attributeDeclIndex) {

        XMLAttributeDecl attributeDecl = new XMLAttributeDecl();
        if (getAttributeDecl(attributeDeclIndex, attributeDecl)) {
            System.out.print(" { ");
            System.out.print(attributeDecl.name.localpart);
            System.out.print(" }");
        }

    } private synchronized ContentModelValidator createChildModel(int contentSpecIndex) {

        XMLContentSpec contentSpec = new XMLContentSpec();
        getContentSpec(contentSpecIndex, contentSpec);

        if ((contentSpec.type & 0x0f ) == XMLContentSpec.CONTENTSPECNODE_ANY ||
            (contentSpec.type & 0x0f ) == XMLContentSpec.CONTENTSPECNODE_ANY_OTHER ||
            (contentSpec.type & 0x0f ) == XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL) {
            }

        else if (contentSpec.type == XMLContentSpec.CONTENTSPECNODE_LEAF) {
            if (contentSpec.value == null && contentSpec.otherValue == null)
                throw new RuntimeException("ImplementationMessages.VAL_NPCD");

            fQName.setValues(null, (String)contentSpec.value,
                              (String)contentSpec.value, (String)contentSpec.otherValue);
            return new SimpleContentModel(contentSpec.type, fQName, null);
        } else if ((contentSpec.type == XMLContentSpec.CONTENTSPECNODE_CHOICE)
                    ||  (contentSpec.type == XMLContentSpec.CONTENTSPECNODE_SEQ)) {
            XMLContentSpec contentSpecLeft  = new XMLContentSpec();
            XMLContentSpec contentSpecRight = new XMLContentSpec();

            getContentSpec( ((int[])contentSpec.value)[0], contentSpecLeft);
            getContentSpec( ((int[])contentSpec.otherValue)[0], contentSpecRight);

            if ((contentSpecLeft.type == XMLContentSpec.CONTENTSPECNODE_LEAF)
                 &&  (contentSpecRight.type == XMLContentSpec.CONTENTSPECNODE_LEAF)) {
                fQName.setValues(null, (String)contentSpecLeft.value,
                                  (String)contentSpecLeft.value, (String)contentSpecLeft.otherValue);
                fQName2.setValues(null, (String)contentSpecRight.value,
                                  (String)contentSpecRight.value, (String)contentSpecRight.otherValue);
                return new SimpleContentModel(contentSpec.type, fQName, fQName2);
            }
        } else if ((contentSpec.type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE)
                    ||  (contentSpec.type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE)
                    ||  (contentSpec.type == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE)) {
            XMLContentSpec contentSpecLeft = new XMLContentSpec();
            getContentSpec(((int[])contentSpec.value)[0], contentSpecLeft);

            if (contentSpecLeft.type == XMLContentSpec.CONTENTSPECNODE_LEAF) {
                fQName.setValues(null, (String)contentSpecLeft.value,
                                  (String)contentSpecLeft.value, (String)contentSpecLeft.otherValue);
                return new SimpleContentModel(contentSpec.type, fQName, null);
            }
        } else {
            throw new RuntimeException("ImplementationMessages.VAL_CST");
        }

        fLeafCount = 0;
        fLeafCount = 0;
        CMNode cmn    = buildSyntaxTree(contentSpecIndex, contentSpec);

        return new DFAContentModel(  cmn, fLeafCount, false);

    } private final CMNode buildSyntaxTree(int startNode,
                                         XMLContentSpec contentSpec) {

        CMNode nodeRet = null;
        getContentSpec(startNode, contentSpec);
        if ((contentSpec.type & 0x0f) == XMLContentSpec.CONTENTSPECNODE_ANY) {
            nodeRet = new CMAny(contentSpec.type, (String)contentSpec.otherValue, fLeafCount++);
        }
        else if ((contentSpec.type & 0x0f) == XMLContentSpec.CONTENTSPECNODE_ANY_OTHER) {
            nodeRet = new CMAny(contentSpec.type, (String)contentSpec.otherValue, fLeafCount++);
        }
        else if ((contentSpec.type & 0x0f) == XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL) {
            nodeRet = new CMAny(contentSpec.type, null, fLeafCount++);
        }
        else if (contentSpec.type == XMLContentSpec.CONTENTSPECNODE_LEAF) {
            fQName.setValues(null, (String)contentSpec.value,
                              (String)contentSpec.value, (String)contentSpec.otherValue);
            nodeRet = new CMLeaf(fQName, fLeafCount++);
        }
        else {
            final int leftNode = ((int[])contentSpec.value)[0];
            final int rightNode = ((int[])contentSpec.otherValue)[0];

            if ((contentSpec.type == XMLContentSpec.CONTENTSPECNODE_CHOICE)
                ||  (contentSpec.type == XMLContentSpec.CONTENTSPECNODE_SEQ)) {
                nodeRet = new CMBinOp( contentSpec.type, buildSyntaxTree(leftNode, contentSpec)
                                       , buildSyntaxTree(rightNode, contentSpec));
            }
            else if (contentSpec.type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE) {
                nodeRet = new CMUniOp( contentSpec.type, buildSyntaxTree(leftNode, contentSpec));
            }
            else if (contentSpec.type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE
                  || contentSpec.type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE
                  || contentSpec.type == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE) {
                nodeRet = new CMUniOp(contentSpec.type, buildSyntaxTree(leftNode, contentSpec));
            }
            else {
                throw new RuntimeException("ImplementationMessages.VAL_CST");
            }
        }
        return nodeRet;
    }


    private void contentSpecTree(int contentSpecIndex,
                                 XMLContentSpec contentSpec,
                                 ChildrenList children) {

        getContentSpec( contentSpecIndex, contentSpec);
        if ( contentSpec.type == XMLContentSpec.CONTENTSPECNODE_LEAF ||
            (contentSpec.type & 0x0f) == XMLContentSpec.CONTENTSPECNODE_ANY ||
            (contentSpec.type & 0x0f) == XMLContentSpec.CONTENTSPECNODE_ANY_LOCAL ||
            (contentSpec.type & 0x0f) == XMLContentSpec.CONTENTSPECNODE_ANY_OTHER) {

            if (children.length == children.qname.length) {
                QName[] newQName = new QName[children.length * 2];
                System.arraycopy(children.qname, 0, newQName, 0, children.length);
                children.qname = newQName;
                int[] newType = new int[children.length * 2];
                System.arraycopy(children.type, 0, newType, 0, children.length);
                children.type = newType;
            }

            children.qname[children.length] = new QName(null, (String)contentSpec.value,
                                                     (String) contentSpec.value,
                                                     (String) contentSpec.otherValue);
            children.type[children.length] = contentSpec.type;
            children.length++;
            return;
        }

        final int leftNode = contentSpec.value != null
                           ? ((int[])(contentSpec.value))[0] : -1;
        int rightNode = -1 ;
        if (contentSpec.otherValue != null )
            rightNode = ((int[])(contentSpec.otherValue))[0];
        else
            return;

        if (contentSpec.type == XMLContentSpec.CONTENTSPECNODE_CHOICE ||
            contentSpec.type == XMLContentSpec.CONTENTSPECNODE_SEQ) {
            contentSpecTree(leftNode, contentSpec, children);
            contentSpecTree(rightNode, contentSpec, children);
            return;
        }

        if (contentSpec.type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_ONE ||
            contentSpec.type == XMLContentSpec.CONTENTSPECNODE_ZERO_OR_MORE ||
            contentSpec.type == XMLContentSpec.CONTENTSPECNODE_ONE_OR_MORE) {
            contentSpecTree(leftNode, contentSpec, children);
            return;
        }

        throw new RuntimeException("Invalid content spec type seen in contentSpecTree() method of AbstractDTDGrammar class : "+contentSpec.type);

    } private void ensureElementDeclCapacity(int chunk) {
        if (chunk >= fElementDeclName.length) {
            fElementDeclIsExternal = resize(fElementDeclIsExternal,
                                     fElementDeclIsExternal.length * 2);

            fElementDeclName = resize(fElementDeclName, fElementDeclName.length * 2);
            fElementDeclType = resize(fElementDeclType, fElementDeclType.length * 2);
            fElementDeclContentModelValidator = resize(fElementDeclContentModelValidator, fElementDeclContentModelValidator.length * 2);
            fElementDeclContentSpecIndex = resize(fElementDeclContentSpecIndex,fElementDeclContentSpecIndex.length * 2);
            fElementDeclFirstAttributeDeclIndex = resize(fElementDeclFirstAttributeDeclIndex, fElementDeclFirstAttributeDeclIndex.length * 2);
            fElementDeclLastAttributeDeclIndex = resize(fElementDeclLastAttributeDeclIndex, fElementDeclLastAttributeDeclIndex.length * 2);
        }
        else if (fElementDeclName[chunk] != null) {
            return;
        }

        fElementDeclIsExternal[chunk] = new int[CHUNK_SIZE];
        fElementDeclName[chunk] = new QName[CHUNK_SIZE];
        fElementDeclType[chunk] = new short[CHUNK_SIZE];
        fElementDeclContentModelValidator[chunk] = new ContentModelValidator[CHUNK_SIZE];
        fElementDeclContentSpecIndex[chunk] = new int[CHUNK_SIZE];
        fElementDeclFirstAttributeDeclIndex[chunk] = new int[CHUNK_SIZE];
        fElementDeclLastAttributeDeclIndex[chunk] = new int[CHUNK_SIZE];
        return;
    }

    private void ensureAttributeDeclCapacity(int chunk) {

        if (chunk >= fAttributeDeclName.length) {
            fAttributeDeclIsExternal = resize(fAttributeDeclIsExternal,
                                       fAttributeDeclIsExternal.length * 2);
            fAttributeDeclName = resize(fAttributeDeclName, fAttributeDeclName.length * 2);
            fAttributeDeclType = resize(fAttributeDeclType, fAttributeDeclType.length * 2);
            fAttributeDeclEnumeration = resize(fAttributeDeclEnumeration, fAttributeDeclEnumeration.length * 2);
            fAttributeDeclDefaultType = resize(fAttributeDeclDefaultType, fAttributeDeclDefaultType.length * 2);
            fAttributeDeclDatatypeValidator = resize(fAttributeDeclDatatypeValidator, fAttributeDeclDatatypeValidator.length * 2);
            fAttributeDeclDefaultValue = resize(fAttributeDeclDefaultValue, fAttributeDeclDefaultValue.length * 2);
            fAttributeDeclNonNormalizedDefaultValue = resize(fAttributeDeclNonNormalizedDefaultValue, fAttributeDeclNonNormalizedDefaultValue.length * 2);
            fAttributeDeclNextAttributeDeclIndex = resize(fAttributeDeclNextAttributeDeclIndex, fAttributeDeclNextAttributeDeclIndex.length * 2);
        }
        else if (fAttributeDeclName[chunk] != null) {
            return;
        }

        fAttributeDeclIsExternal[chunk] = new int[CHUNK_SIZE];
        fAttributeDeclName[chunk] = new QName[CHUNK_SIZE];
        fAttributeDeclType[chunk] = new short[CHUNK_SIZE];
        fAttributeDeclEnumeration[chunk] = new String[CHUNK_SIZE][];
        fAttributeDeclDefaultType[chunk] = new short[CHUNK_SIZE];
        fAttributeDeclDatatypeValidator[chunk] = new DatatypeValidator[CHUNK_SIZE];
        fAttributeDeclDefaultValue[chunk] = new String[CHUNK_SIZE];
        fAttributeDeclNonNormalizedDefaultValue[chunk] = new String[CHUNK_SIZE];
        fAttributeDeclNextAttributeDeclIndex[chunk] = new int[CHUNK_SIZE];
        return;
    }

    private void ensureEntityDeclCapacity(int chunk) {
        if (chunk >= fEntityName.length) {
            fEntityName = resize(fEntityName, fEntityName.length * 2);
            fEntityValue = resize(fEntityValue, fEntityValue.length * 2);
            fEntityPublicId = resize(fEntityPublicId, fEntityPublicId.length * 2);
            fEntitySystemId = resize(fEntitySystemId, fEntitySystemId.length * 2);
            fEntityBaseSystemId = resize(fEntityBaseSystemId, fEntityBaseSystemId.length * 2);
            fEntityNotation = resize(fEntityNotation, fEntityNotation.length * 2);
            fEntityIsPE = resize(fEntityIsPE, fEntityIsPE.length * 2);
            fEntityInExternal = resize(fEntityInExternal, fEntityInExternal.length * 2);
        }
        else if (fEntityName[chunk] != null) {
            return;
        }

        fEntityName[chunk] = new String[CHUNK_SIZE];
        fEntityValue[chunk] = new String[CHUNK_SIZE];
        fEntityPublicId[chunk] = new String[CHUNK_SIZE];
        fEntitySystemId[chunk] = new String[CHUNK_SIZE];
        fEntityBaseSystemId[chunk] = new String[CHUNK_SIZE];
        fEntityNotation[chunk] = new String[CHUNK_SIZE];
        fEntityIsPE[chunk] = new byte[CHUNK_SIZE];
        fEntityInExternal[chunk] = new byte[CHUNK_SIZE];
        return;
    }

    private void ensureNotationDeclCapacity(int chunk) {
        if (chunk >= fNotationName.length) {
            fNotationName = resize(fNotationName, fNotationName.length * 2);
            fNotationPublicId = resize(fNotationPublicId, fNotationPublicId.length * 2);
            fNotationSystemId = resize(fNotationSystemId, fNotationSystemId.length * 2);
            fNotationBaseSystemId = resize(fNotationBaseSystemId, fNotationBaseSystemId.length * 2);
        }
        else if (fNotationName[chunk] != null) {
            return;
        }

        fNotationName[chunk] = new String[CHUNK_SIZE];
        fNotationPublicId[chunk] = new String[CHUNK_SIZE];
        fNotationSystemId[chunk] = new String[CHUNK_SIZE];
        fNotationBaseSystemId[chunk] = new String[CHUNK_SIZE];
        return;
    }

    private void ensureContentSpecCapacity(int chunk) {
        if (chunk >= fContentSpecType.length) {
            fContentSpecType = resize(fContentSpecType, fContentSpecType.length * 2);
            fContentSpecValue = resize(fContentSpecValue, fContentSpecValue.length * 2);
            fContentSpecOtherValue = resize(fContentSpecOtherValue, fContentSpecOtherValue.length * 2);
        }
        else if (fContentSpecType[chunk] != null) {
            return;
        }

        fContentSpecType[chunk] = new short[CHUNK_SIZE];
        fContentSpecValue[chunk] = new Object[CHUNK_SIZE];
        fContentSpecOtherValue[chunk] = new Object[CHUNK_SIZE];
        return;
    }

    private static byte[][] resize(byte array[][], int newsize) {
        byte newarray[][] = new byte[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }

    private static short[][] resize(short array[][], int newsize) {
        short newarray[][] = new short[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }

    private static int[][] resize(int array[][], int newsize) {
        int newarray[][] = new int[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }

    private static DatatypeValidator[][] resize(DatatypeValidator array[][], int newsize) {
        DatatypeValidator newarray[][] = new DatatypeValidator[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }

    private static ContentModelValidator[][] resize(ContentModelValidator array[][], int newsize) {
        ContentModelValidator newarray[][] = new ContentModelValidator[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }

    private static Object[][] resize(Object array[][], int newsize) {
        Object newarray[][] = new Object[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }

    private static QName[][] resize(QName array[][], int newsize) {
        QName newarray[][] = new QName[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }

    private static String[][] resize(String array[][], int newsize) {
        String newarray[][] = new String[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }

    private static String[][][] resize(String array[][][], int newsize) {
        String newarray[][][] = new String[newsize] [][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }

    private static class ChildrenList {

        public int length = 0;

        public QName[] qname = new QName[2];


        public int[] type = new int[2];

        public ChildrenList () {}

    } public boolean isEntityDeclared (String name){
        return (getEntityDeclIndex(name)!=-1)?true:false;
    }

    public boolean isEntityUnparsed (String name){
        int entityIndex = getEntityDeclIndex(name);
        if (entityIndex >-1) {
            int chunk = entityIndex >> CHUNK_SHIFT;
            int index = entityIndex & CHUNK_MASK;
            return (fEntityNotation[chunk][index]!=null)?true:false;
        }
        return false;
    }
}