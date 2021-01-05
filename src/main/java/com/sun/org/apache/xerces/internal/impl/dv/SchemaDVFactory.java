


package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;


public abstract class SchemaDVFactory {

    private static final String DEFAULT_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl";


    public static synchronized final SchemaDVFactory getInstance() throws DVFactoryException {
        return getInstance(DEFAULT_FACTORY_CLASS);
    } public static synchronized final SchemaDVFactory getInstance(String factoryClass) throws DVFactoryException {

        try {
            return (SchemaDVFactory)(ObjectFactory.newInstance(factoryClass, true));
        } catch (ClassCastException e4) {
            throw new DVFactoryException("Schema factory class " + factoryClass + " does not extend from SchemaDVFactory.");
        }

    }

    protected SchemaDVFactory(){}


    public abstract XSSimpleType getBuiltInType(String name);


    public abstract SymbolHash getBuiltInTypes();


    public abstract XSSimpleType createTypeRestriction(String name, String targetNamespace,
                                                       short finalSet, XSSimpleType base,
                                                       XSObjectList annotations);


    public abstract XSSimpleType createTypeList(String name, String targetNamespace,
                                                short finalSet, XSSimpleType itemType,
                                                XSObjectList annotations);


    public abstract XSSimpleType createTypeUnion(String name, String targetNamespace,
                                                 short finalSet, XSSimpleType[] memberTypes,
                                                 XSObjectList annotations);

}
