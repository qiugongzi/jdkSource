


package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;


public class XMLStringFactoryImpl extends XMLStringFactory
{

  private static XMLStringFactory m_xstringfactory =
    new XMLStringFactoryImpl();


  public static XMLStringFactory getFactory()
  {
    return m_xstringfactory;
  }


  public XMLString newstr(String string)
  {
    return new XString(string);
  }


  public XMLString newstr(FastStringBuffer fsb, int start, int length)
  {
    return new XStringForFSB(fsb, start, length);
  }


  public XMLString newstr(char[] string, int start, int length)
  {
    return new XStringForChars(string, start, length);
  }


  public XMLString emptystr()
  {
    return XString.EMPTYSTRING;
  }

}
