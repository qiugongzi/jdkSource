
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {












        DTMIterator nl = m_arg0.asIterator(xctxt, xctxt.getCurrentNode());
        int i = nl.getLength();
        nl.detach();

    return new XNumber((double) i);
  }
}
