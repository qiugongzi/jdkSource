

public final class DoubleSeqHolder implements org.omg.CORBA.portable.Streamable
{
    public double value[] = null;

    public DoubleSeqHolder ()
    {
    }

    public DoubleSeqHolder (double[] initialValue)
    {
        value = initialValue;
    }

    public void _read (org.omg.CORBA.portable.InputStream i)
    {
        value = org.omg.CORBA.DoubleSeqHelper.read (i);
    }

    public void _write (org.omg.CORBA.portable.OutputStream o)
    {
        org.omg.CORBA.DoubleSeqHelper.write (o, value);
    }

    public org.omg.CORBA.TypeCode _type ()
    {
        return org.omg.CORBA.DoubleSeqHelper.type ();
    }

}
