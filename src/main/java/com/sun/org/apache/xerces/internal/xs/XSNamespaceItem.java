


package com.sun.org.apache.xerces.internal.xs;


public interface XSNamespaceItem {

    public String getSchemaNamespace();


    public XSNamedMap getComponents(short objectType);


    public XSObjectList getAnnotations();


    public XSElementDeclaration getElementDeclaration(String name);


    public XSAttributeDeclaration getAttributeDeclaration(String name);


    public XSTypeDefinition getTypeDefinition(String name);


    public XSAttributeGroupDefinition getAttributeGroup(String name);


    public XSModelGroupDefinition getModelGroupDefinition(String name);


    public XSNotationDeclaration getNotationDeclaration(String name);


    public StringList getDocumentLocations();

}
