



package com.sun.org.apache.xml.internal.dtm.ref;

import javax.xml.transform.SourceLocator;


public class NodeLocator implements SourceLocator
{
  protected String m_publicId;
  protected String m_systemId;
  protected int m_lineNumber;
  protected int m_columnNumber;


  public NodeLocator(String publicId, String systemId,
                     int lineNumber, int columnNumber)
  {
    this.m_publicId = publicId;
    this.m_systemId = systemId;
    this.m_lineNumber = lineNumber;
    this.m_columnNumber = columnNumber;
  }


  public String getPublicId()
  {
    return m_publicId;
  }


  public String getSystemId()
  {
    return m_systemId;
  }


  public int getLineNumber()
  {
    return m_lineNumber;
  }


  public int getColumnNumber()
  {
    return m_columnNumber;
  }


  public String toString()
  {
    return "file '" + m_systemId
      + "', line #" + m_lineNumber
      + ", column #" + m_columnNumber;
  }
}