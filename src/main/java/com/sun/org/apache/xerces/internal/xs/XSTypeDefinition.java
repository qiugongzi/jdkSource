


package com.sun.org.apache.xerces.internal.xs;


public interface XSTypeDefinition extends XSObject {

    public static final short COMPLEX_TYPE              = 15;

    public static final short SIMPLE_TYPE               = 16;

    public short getTypeCategory();


    public XSTypeDefinition getBaseType();


    public boolean isFinal(short restriction);


    public short getFinal();


    public boolean getAnonymous();


    public boolean derivedFromType(XSTypeDefinition ancestorType,
                                   short derivationMethod);


    public boolean derivedFrom(String namespace,
                               String name,
                               short derivationMethod);

}
