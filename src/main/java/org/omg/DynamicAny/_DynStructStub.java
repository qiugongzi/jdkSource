package org.omg.DynamicAny;






public class _DynStructStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.DynamicAny.DynStruct
{
  final public static java.lang.Class _opsClass = DynStructOperations.class;




  public String current_member_name () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("current_member_name", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.current_member_name ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public org.omg.CORBA.TCKind current_member_kind () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("current_member_kind", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.current_member_kind ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public org.omg.DynamicAny.NameValuePair[] get_members ()
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_members", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_members ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public void set_members (org.omg.DynamicAny.NameValuePair[] value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("set_members", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.set_members (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public org.omg.DynamicAny.NameDynAnyPair[] get_members_as_dyn_any ()
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_members_as_dyn_any", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_members_as_dyn_any ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public void set_members_as_dyn_any (org.omg.DynamicAny.NameDynAnyPair[] value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("set_members_as_dyn_any", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.set_members_as_dyn_any (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public org.omg.CORBA.TypeCode type ()
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("type", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.type ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public void assign (org.omg.DynamicAny.DynAny dyn_any) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("assign", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.assign (dyn_any);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void from_any (org.omg.CORBA.Any value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("from_any", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.from_any (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public org.omg.CORBA.Any to_any ()
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("to_any", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.to_any ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public boolean equal (org.omg.DynamicAny.DynAny dyn_any)
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("equal", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.equal (dyn_any);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void destroy ()
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("destroy", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.destroy ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public org.omg.DynamicAny.DynAny copy ()
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("copy", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.copy ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_boolean (boolean value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_boolean", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_boolean (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_octet (byte value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_octet", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_octet (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_char (char value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_char", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_char (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_short (short value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_short", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_short (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_ushort (short value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_ushort", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_ushort (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_long (int value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_long", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_long (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_ulong (int value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_ulong", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_ulong (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_float (float value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_float", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_float (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_double (double value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_double", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_double (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_string (String value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_string", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_string (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_reference (org.omg.CORBA.Object value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_reference", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_reference (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_typecode (org.omg.CORBA.TypeCode value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_typecode", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_typecode (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_longlong (long value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_longlong", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_longlong (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_ulonglong (long value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_ulonglong", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_ulonglong (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_wchar (char value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_wchar", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_wchar (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_wstring (String value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_wstring", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_wstring (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_any (org.omg.CORBA.Any value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_any", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_any (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_dyn_any (org.omg.DynamicAny.DynAny value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_dyn_any", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_dyn_any (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void insert_val (java.io.Serializable value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("insert_val", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.insert_val (value);
      } finally {
          _servant_postinvoke ($so);
      }
  } public boolean get_boolean () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_boolean", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_boolean ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public byte get_octet () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_octet", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_octet ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public char get_char () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_char", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_char ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public short get_short () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_short", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_short ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public short get_ushort () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_ushort", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_ushort ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public int get_long () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_long", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_long ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public int get_ulong () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_ulong", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_ulong ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public float get_float () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_float", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_float ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public double get_double () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_double", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_double ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public String get_string () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_string", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_string ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public org.omg.CORBA.Object get_reference () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_reference", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_reference ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public org.omg.CORBA.TypeCode get_typecode () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_typecode", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_typecode ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public long get_longlong () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_longlong", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_longlong ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public long get_ulonglong () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_ulonglong", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_ulonglong ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public char get_wchar () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_wchar", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_wchar ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public String get_wstring () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_wstring", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_wstring ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public org.omg.CORBA.Any get_any () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_any", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_any ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public org.omg.DynamicAny.DynAny get_dyn_any () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_dyn_any", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_dyn_any ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public java.io.Serializable get_val () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("get_val", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.get_val ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public boolean seek (int index)
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("seek", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.seek (index);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void rewind ()
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("rewind", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         $self.rewind ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public boolean next ()
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("next", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.next ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public int component_count ()
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("component_count", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.component_count ();
      } finally {
          _servant_postinvoke ($so);
      }
  } public org.omg.DynamicAny.DynAny current_component () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("current_component", _opsClass);
      DynStructOperations  $self = (DynStructOperations) $so.servant;

      try {
         return $self.current_component ();
      } finally {
          _servant_postinvoke ($so);
      }
  } private static String[] __ids = {
    "IDL:omg.org/DynamicAny/DynStruct:1.0", 
    "IDL:omg.org/DynamicAny/DynAny:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
}