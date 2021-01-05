


package org.omg.CORBA;



@Deprecated
public interface DynAny extends org.omg.CORBA.Object
{

    public org.omg.CORBA.TypeCode type() ;


    public void assign(org.omg.CORBA.DynAny dyn_any)
        throws org.omg.CORBA.DynAnyPackage.Invalid;


    public void from_any(org.omg.CORBA.Any value)
        throws org.omg.CORBA.DynAnyPackage.Invalid;


    public org.omg.CORBA.Any to_any()
        throws org.omg.CORBA.DynAnyPackage.Invalid;


    public void destroy() ;


    public org.omg.CORBA.DynAny copy() ;


    public void insert_boolean(boolean value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_octet(byte value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_char(char value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_short(short value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_ushort(short value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_long(int value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_ulong(int value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_float(float value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_double(double value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_string(String value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_reference(org.omg.CORBA.Object value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_typecode(org.omg.CORBA.TypeCode value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_longlong(long value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_ulonglong(long value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_wchar(char value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_wstring(String value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public void insert_any(org.omg.CORBA.Any value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;

    public void insert_val(java.io.Serializable value)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;


    public java.io.Serializable get_val()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;

    public boolean get_boolean()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public byte get_octet()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


    public char get_char()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public short get_short()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public short get_ushort()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public int get_long()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public int get_ulong()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public float get_float()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public double get_double()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public String get_string()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public org.omg.CORBA.Object get_reference()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public org.omg.CORBA.TypeCode get_typecode()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public long get_longlong()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public long get_ulonglong()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public char get_wchar()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public String get_wstring()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;



    public org.omg.CORBA.Any get_any()
        throws org.omg.CORBA.DynAnyPackage.TypeMismatch;


    public org.omg.CORBA.DynAny current_component() ;


    public boolean next() ;


    public boolean seek(int index) ;


    public void rewind() ;
}
