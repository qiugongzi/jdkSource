
  public double num(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    return (m_left.num(xctxt) % m_right.num(xctxt));
  }

}
