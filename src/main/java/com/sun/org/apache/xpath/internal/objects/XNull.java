


package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;


public class XNull extends XNodeSet
{
    static final long serialVersionUID = -6841683711458983005L;


  public XNull()
  {
    super();
  }


  public int getType()
  {
    return CLASS_NULL;
  }


  public String getTypeString()
  {
    return "#CLASS_NULL";
  }



  public double num()
  {
    return 0.0;
  }


  public boolean bool()
  {
    return false;
  }


  public String str()
  {
    return "";
  }


  public int rtf(XPathContext support)
  {
    return DTM.NULL;
  }

public boolean equals(XObject obj2)
  {
    return obj2.getType() == CLASS_NULL;
  }
}
