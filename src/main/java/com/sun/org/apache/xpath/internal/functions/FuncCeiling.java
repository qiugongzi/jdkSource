
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
    return new XNumber(Math.ceil(m_arg0.execute(xctxt).num()));
  }
}
