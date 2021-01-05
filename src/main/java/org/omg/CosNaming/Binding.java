package org.omg.CosNaming;




public final class Binding implements org.omg.CORBA.portable.IDLEntity
{
  public org.omg.CosNaming.NameComponent binding_name[] = null;


  public org.omg.CosNaming.BindingType binding_type = null;

  public Binding ()
  {
  }

  public Binding (org.omg.CosNaming.NameComponent[] _binding_name, org.omg.CosNaming.BindingType _binding_type)
  {
    binding_name = _binding_name;
    binding_type = _binding_type;
  }

}
