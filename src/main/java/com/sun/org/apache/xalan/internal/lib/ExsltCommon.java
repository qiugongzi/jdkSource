


package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import com.sun.org.apache.xpath.internal.NodeSet;


public class ExsltCommon
{

  public static String objectType (Object obj)
  {
    if (obj instanceof String)
      return "string";
    else if (obj instanceof Boolean)
      return "boolean";
    else if (obj instanceof Number)
      return "number";
    else if (obj instanceof DTMNodeIterator)
    {
      DTMIterator dtmI = ((DTMNodeIterator)obj).getDTMIterator();
      if (dtmI instanceof com.sun.org.apache.xpath.internal.axes.RTFIterator)
        return "RTF";
      else
        return "node-set";
    }
    else
      return "unknown";
  }


  public static NodeSet nodeSet(ExpressionContext myProcessor, Object rtf)
  {
    return Extensions.nodeset(myProcessor, rtf);
  }

}
