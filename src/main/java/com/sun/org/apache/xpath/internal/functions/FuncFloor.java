
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
    return new XNumber(java.lang.Math.floor(m_arg0.execute(xctxt).num()));
  }
}
