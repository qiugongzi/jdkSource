package org.omg.DynamicAny;






public interface DynValueOperations  extends org.omg.DynamicAny.DynValueCommonOperations
{


  String current_member_name () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue;


  org.omg.CORBA.TCKind current_member_kind () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue;


  org.omg.DynamicAny.NameValuePair[] get_members () throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;


  void set_members (org.omg.DynamicAny.NameValuePair[] value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue;


  org.omg.DynamicAny.NameDynAnyPair[] get_members_as_dyn_any () throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;


  void set_members_as_dyn_any (org.omg.DynamicAny.NameDynAnyPair[] value) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch, org.omg.DynamicAny.DynAnyPackage.InvalidValue;
}