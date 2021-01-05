


package com.sun.org.apache.xerces.internal.xni;

import java.util.Enumeration;
import java.util.Vector;


public interface NamespaceContext {

    public final static String XML_URI = "http:public final static String XMLNS_URI = "http:public void pushContext();


    public void popContext();


    public boolean declarePrefix(String prefix, String uri);



    public String getURI(String prefix);


    public String getPrefix(String uri);



    public int getDeclaredPrefixCount();


    public String getDeclaredPrefixAt(int index);


    public Enumeration getAllPrefixes();


    public void reset();


}