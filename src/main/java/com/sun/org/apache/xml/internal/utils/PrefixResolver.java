


package com.sun.org.apache.xml.internal.utils;


public interface PrefixResolver
{


  String getNamespaceForPrefix(String prefix);


  String getNamespaceForPrefix(String prefix, org.w3c.dom.Node context);


  public String getBaseIdentifier();

  public boolean handlesNullPrefixes();
}
