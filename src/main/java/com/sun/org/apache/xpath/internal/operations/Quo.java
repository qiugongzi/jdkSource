
  public XObject operate(XObject left, XObject right)
          throws javax.xml.transform.TransformerException
  {
    return new XNumber((int) (left.num() / right.num()));
  }
}
