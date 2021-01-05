package org.omg.DynamicAny;






public interface DynUnionOperations  extends org.omg.DynamicAny.DynAnyOperations
{


  org.omg.DynamicAny.DynAny get_discriminator ();


  void set_discriminator (org.omg.DynamicAny.DynAny d) throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;


  void set_to_default_member () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;


  void set_to_no_active_member () throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;


  boolean has_no_active_member ();


  org.omg.CORBA.TCKind discriminator_kind ();


  org.omg.CORBA.TCKind member_kind () throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;


  org.omg.DynamicAny.DynAny member () throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;


  String member_name () throws org.omg.DynamicAny.DynAnyPackage.InvalidValue;
}