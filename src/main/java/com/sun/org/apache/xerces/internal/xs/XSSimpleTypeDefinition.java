


package com.sun.org.apache.xerces.internal.xs;


public interface XSSimpleTypeDefinition extends XSTypeDefinition {
    public static final short VARIETY_ABSENT            = 0;

    public static final short VARIETY_ATOMIC            = 1;

    public static final short VARIETY_LIST              = 2;

    public static final short VARIETY_UNION             = 3;

    public static final short FACET_NONE                = 0;

    public static final short FACET_LENGTH              = 1;

    public static final short FACET_MINLENGTH           = 2;

    public static final short FACET_MAXLENGTH           = 4;

    public static final short FACET_PATTERN             = 8;

    public static final short FACET_WHITESPACE          = 16;

    public static final short FACET_MAXINCLUSIVE        = 32;

    public static final short FACET_MAXEXCLUSIVE        = 64;

    public static final short FACET_MINEXCLUSIVE        = 128;

    public static final short FACET_MININCLUSIVE        = 256;

    public static final short FACET_TOTALDIGITS         = 512;

    public static final short FACET_FRACTIONDIGITS      = 1024;

    public static final short FACET_ENUMERATION         = 2048;


    public static final short ORDERED_FALSE             = 0;

    public static final short ORDERED_PARTIAL           = 1;

    public static final short ORDERED_TOTAL             = 2;

    public short getVariety();


    public XSSimpleTypeDefinition getPrimitiveType();


    public short getBuiltInKind();


    public XSSimpleTypeDefinition getItemType();


    public XSObjectList getMemberTypes();


    public short getDefinedFacets();


    public boolean isDefinedFacet(short facetName);


    public short getFixedFacets();


    public boolean isFixedFacet(short facetName);


    public String getLexicalFacetValue(short facetName);


    public StringList getLexicalEnumeration();


    public StringList getLexicalPattern();


    public short getOrdered();


    public boolean getFinite();


    public boolean getBounded();


    public boolean getNumeric();


    public XSObjectList getFacets();


    public XSObjectList getMultiValueFacets();


    public XSObjectList getAnnotations();

}
