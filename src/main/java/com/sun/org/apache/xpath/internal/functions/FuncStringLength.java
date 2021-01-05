
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
    return new XNumber(getArg0AsString(xctxt).length());
  }
}
