


package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;


public interface XMLSchemaDescription extends XMLGrammarDescription {

    public final static short CONTEXT_INCLUDE   = 0;

    public final static short CONTEXT_REDEFINE  = 1;

    public final static short CONTEXT_IMPORT    = 2;

    public final static short CONTEXT_PREPARSE  = 3;

    public final static short CONTEXT_INSTANCE  = 4;

    public final static short CONTEXT_ELEMENT   = 5;

    public final static short CONTEXT_ATTRIBUTE = 6;

    public final static short CONTEXT_XSITYPE   = 7;


    public short getContextType();


    public String getTargetNamespace();


    public String[] getLocationHints();


    public QName getTriggeringComponent();


    public QName getEnclosingElementName();


    public XMLAttributes getAttributes();

}