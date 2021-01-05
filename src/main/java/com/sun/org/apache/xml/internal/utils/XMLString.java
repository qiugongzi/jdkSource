


package com.sun.org.apache.xml.internal.utils;

import java.util.Locale;


public interface XMLString
{


  public abstract void dispatchCharactersEvents(org.xml.sax.ContentHandler ch)
    throws org.xml.sax.SAXException;


  public abstract void dispatchAsComment(org.xml.sax.ext.LexicalHandler lh)
    throws org.xml.sax.SAXException;


  public XMLString fixWhiteSpace(boolean trimHead,
                                 boolean trimTail,
                                 boolean doublePunctuationSpaces);


  public abstract int length();


  public abstract char charAt(int index);


  public abstract void getChars(int srcBegin, int srcEnd, char dst[],
                                int dstBegin);


  public abstract boolean equals(XMLString anObject);


  public abstract boolean equals(String anotherString);


  public abstract boolean equals(Object anObject);


  public abstract boolean equalsIgnoreCase(String anotherString);


  public abstract int compareTo(XMLString anotherString);


  public abstract int compareToIgnoreCase(XMLString str);


  public abstract boolean startsWith(String prefix, int toffset);


  public abstract boolean startsWith(XMLString prefix, int toffset);


  public abstract boolean startsWith(String prefix);


  public abstract boolean startsWith(XMLString prefix);


  public abstract boolean endsWith(String suffix);


  public abstract int hashCode();


  public abstract int indexOf(int ch);


  public abstract int indexOf(int ch, int fromIndex);


  public abstract int lastIndexOf(int ch);


  public abstract int lastIndexOf(int ch, int fromIndex);


  public abstract int indexOf(String str);


  public abstract int indexOf(XMLString str);


  public abstract int indexOf(String str, int fromIndex);


  public abstract int lastIndexOf(String str);


  public abstract int lastIndexOf(String str, int fromIndex);


  public abstract XMLString substring(int beginIndex);


  public abstract XMLString substring(int beginIndex, int endIndex);


  public abstract XMLString concat(String str);


  public abstract XMLString toLowerCase(Locale locale);


  public abstract XMLString toLowerCase();


  public abstract XMLString toUpperCase(Locale locale);


  public abstract XMLString toUpperCase();


  public abstract XMLString trim();


  public abstract String toString();


  public abstract boolean hasString();


  public double toDouble();
}
