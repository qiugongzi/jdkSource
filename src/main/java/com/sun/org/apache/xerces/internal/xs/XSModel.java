


package com.sun.org.apache.xerces.internal.xs;


public interface XSModel {

    public StringList getNamespaces();


    public XSNamespaceItemList getNamespaceItems();


    public XSNamedMap getComponents(short objectType);


    public XSNamedMap getComponentsByNamespace(short objectType,
                                               String namespace);


    public XSObjectList getAnnotations();


    public XSElementDeclaration getElementDeclaration(String name,
                                                      String namespace);


    public XSAttributeDeclaration getAttributeDeclaration(String name,
                                                          String namespace);


    public XSTypeDefinition getTypeDefinition(String name,
                                              String namespace);


    public XSAttributeGroupDefinition getAttributeGroup(String name,
                                                        String namespace);


    public XSModelGroupDefinition getModelGroupDefinition(String name,
                                                          String namespace);


    public XSNotationDeclaration getNotationDeclaration(String name,
                                                        String namespace);


    public XSObjectList getSubstitutionGroup(XSElementDeclaration head);

}
