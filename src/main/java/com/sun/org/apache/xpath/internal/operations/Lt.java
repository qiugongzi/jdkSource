
  public XObject operate(XObject left, XObject right)
          throws javax.xml.transform.TransformerException
  {
    return left.lessThan(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}
