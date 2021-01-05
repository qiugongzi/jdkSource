


package com.sun.org.apache.xalan.internal.lib;

import java.util.StringTokenizer;
import com.sun.org.apache.xpath.internal.NodeSet;
import jdk.xml.internal.JdkXmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


public class ExsltStrings extends ExsltBase
{


  public static String align(String targetStr, String paddingStr, String type)
  {
    if (targetStr.length() >= paddingStr.length())
      return targetStr.substring(0, paddingStr.length());

    if (type.equals("right"))
    {
      return paddingStr.substring(0, paddingStr.length() - targetStr.length()) + targetStr;
    }
    else if (type.equals("center"))
    {
      int startIndex = (paddingStr.length() - targetStr.length()) / 2;
      return paddingStr.substring(0, startIndex) + targetStr + paddingStr.substring(startIndex + targetStr.length());
    }
    else
    {
      return targetStr + paddingStr.substring(targetStr.length());
    }
  }


  public static String align(String targetStr, String paddingStr)
  {
    return align(targetStr, paddingStr, "left");
  }


  public static String concat(NodeList nl)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < nl.getLength(); i++)
    {
      Node node = nl.item(i);
      String value = toString(node);

      if (value != null && value.length() > 0)
        sb.append(value);
    }

    return sb.toString();
  }


  public static String padding(double length, String pattern)
  {
    if (pattern == null || pattern.length() == 0)
      return "";

    StringBuffer sb = new StringBuffer();
    int len = (int)length;
    int numAdded = 0;
    int index = 0;
    while (numAdded < len)
    {
      if (index == pattern.length())
        index = 0;

      sb.append(pattern.charAt(index));
      index++;
      numAdded++;
    }

    return sb.toString();
  }


  public static String padding(double length)
  {
    return padding(length, " ");
  }


  public static NodeList split(String str, String pattern)
  {


    NodeSet resultSet = new NodeSet();
    resultSet.setShouldCacheNodes(true);

    boolean done = false;
    int fromIndex = 0;
    int matchIndex = 0;
    String token = null;

    while (!done && fromIndex < str.length())
    {
      matchIndex = str.indexOf(pattern, fromIndex);
      if (matchIndex >= 0)
      {
        token = str.substring(fromIndex, matchIndex);
        fromIndex = matchIndex + pattern.length();
      }
      else
      {
        done = true;
        token = str.substring(fromIndex);
      }

      Document doc = JdkXmlUtils.getDOMDocument();
      synchronized (doc)
      {
        Element element = doc.createElement("token");
        Text text = doc.createTextNode(token);
        element.appendChild(text);
        resultSet.addNode(element);
      }
    }

    return resultSet;
  }


  public static NodeList split(String str)
  {
    return split(str, " ");
  }


  public static NodeList tokenize(String toTokenize, String delims)
  {


    NodeSet resultSet = new NodeSet();

    if (delims != null && delims.length() > 0)
    {
      StringTokenizer lTokenizer = new StringTokenizer(toTokenize, delims);

      Document doc = JdkXmlUtils.getDOMDocument();
      synchronized (doc)
      {
        while (lTokenizer.hasMoreTokens())
        {
          Element element = doc.createElement("token");
          element.appendChild(doc.createTextNode(lTokenizer.nextToken()));
          resultSet.addNode(element);
        }
      }
    }
    else
    {

      Document doc = JdkXmlUtils.getDOMDocument();
      synchronized (doc)
      {
        for (int i = 0; i < toTokenize.length(); i++)
        {
          Element element = doc.createElement("token");
          element.appendChild(doc.createTextNode(toTokenize.substring(i, i+1)));
          resultSet.addNode(element);
        }
      }
    }

    return resultSet;
  }


  public static NodeList tokenize(String toTokenize)
  {
    return tokenize(toTokenize, " \t\n\r");
  }
}
