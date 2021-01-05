

package org.omg.CORBA;


public interface DataInputStream extends org.omg.CORBA.portable.ValueBase
{

    org.omg.CORBA.Any read_any ();


    boolean read_boolean ();


    char read_char ();


    char read_wchar ();


    byte read_octet ();


    short read_short ();


    short read_ushort ();


    int read_long ();


    int read_ulong ();


    long read_longlong ();


    long read_ulonglong ();


    float read_float ();


    double read_double ();
    String read_string ();


    String read_wstring ();


    org.omg.CORBA.Object read_Object ();


    java.lang.Object read_Abstract ();


    java.io.Serializable read_Value ();


    org.omg.CORBA.TypeCode read_TypeCode ();


    void read_any_array (org.omg.CORBA.AnySeqHolder seq, int offset, int length);


    void read_boolean_array (org.omg.CORBA.BooleanSeqHolder seq, int offset, int length);


    void read_char_array (org.omg.CORBA.CharSeqHolder seq, int offset, int length);


    void read_wchar_array (org.omg.CORBA.WCharSeqHolder seq, int offset, int length);


    void read_octet_array (org.omg.CORBA.OctetSeqHolder seq, int offset, int length);


    void read_short_array (org.omg.CORBA.ShortSeqHolder seq, int offset, int length);


    void read_ushort_array (org.omg.CORBA.UShortSeqHolder seq, int offset, int length);


    void read_long_array (org.omg.CORBA.LongSeqHolder seq, int offset, int length);


    void read_ulong_array (org.omg.CORBA.ULongSeqHolder seq, int offset, int length);


    void read_ulonglong_array (org.omg.CORBA.ULongLongSeqHolder seq, int offset, int length);


    void read_longlong_array (org.omg.CORBA.LongLongSeqHolder seq, int offset, int length);


    void read_float_array (org.omg.CORBA.FloatSeqHolder seq, int offset, int length);


    void read_double_array (org.omg.CORBA.DoubleSeqHolder seq, int offset, int length);
}