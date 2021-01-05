
  protected static double toNumber(Node n)
  {
    double d = 0.0;
    String str = toString(n);
    try
    {
      d = Double.valueOf(str).doubleValue();
    }
    catch (NumberFormatException e)
    {
      d= Double.NaN;
    }
    return d;
  }
}
