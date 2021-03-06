package org.omg.PortableServer;






public interface POAOperations 
{


  org.omg.PortableServer.POA create_POA (String adapter_name, org.omg.PortableServer.POAManager a_POAManager, org.omg.CORBA.Policy[] policies) throws org.omg.PortableServer.POAPackage.AdapterAlreadyExists, org.omg.PortableServer.POAPackage.InvalidPolicy;


  org.omg.PortableServer.POA find_POA (String adapter_name, boolean activate_it) throws org.omg.PortableServer.POAPackage.AdapterNonExistent;


  void destroy (boolean etherealize_objects, boolean wait_for_completion);


  org.omg.PortableServer.ThreadPolicy create_thread_policy (org.omg.PortableServer.ThreadPolicyValue value);


  org.omg.PortableServer.LifespanPolicy create_lifespan_policy (org.omg.PortableServer.LifespanPolicyValue value);


  org.omg.PortableServer.IdUniquenessPolicy create_id_uniqueness_policy (org.omg.PortableServer.IdUniquenessPolicyValue value);


  org.omg.PortableServer.IdAssignmentPolicy create_id_assignment_policy (org.omg.PortableServer.IdAssignmentPolicyValue value);


  org.omg.PortableServer.ImplicitActivationPolicy create_implicit_activation_policy (org.omg.PortableServer.ImplicitActivationPolicyValue value);


  org.omg.PortableServer.ServantRetentionPolicy create_servant_retention_policy (org.omg.PortableServer.ServantRetentionPolicyValue value);


  org.omg.PortableServer.RequestProcessingPolicy create_request_processing_policy (org.omg.PortableServer.RequestProcessingPolicyValue value);


  String the_name ();


  org.omg.PortableServer.POA the_parent ();


  org.omg.PortableServer.POA[] the_children ();


  org.omg.PortableServer.POAManager the_POAManager ();


  org.omg.PortableServer.AdapterActivator the_activator ();


  void the_activator (org.omg.PortableServer.AdapterActivator newThe_activator);


  org.omg.PortableServer.ServantManager get_servant_manager () throws org.omg.PortableServer.POAPackage.WrongPolicy;


  void set_servant_manager (org.omg.PortableServer.ServantManager imgr) throws org.omg.PortableServer.POAPackage.WrongPolicy;


  org.omg.PortableServer.Servant get_servant () throws org.omg.PortableServer.POAPackage.NoServant, org.omg.PortableServer.POAPackage.WrongPolicy;


  void set_servant (org.omg.PortableServer.Servant p_servant) throws org.omg.PortableServer.POAPackage.WrongPolicy;


  byte[] activate_object (org.omg.PortableServer.Servant p_servant) throws org.omg.PortableServer.POAPackage.ServantAlreadyActive, org.omg.PortableServer.POAPackage.WrongPolicy;


  void activate_object_with_id (byte[] id, org.omg.PortableServer.Servant p_servant) throws org.omg.PortableServer.POAPackage.ServantAlreadyActive, org.omg.PortableServer.POAPackage.ObjectAlreadyActive, org.omg.PortableServer.POAPackage.WrongPolicy;


  void deactivate_object (byte[] oid) throws org.omg.PortableServer.POAPackage.ObjectNotActive, org.omg.PortableServer.POAPackage.WrongPolicy;


  org.omg.CORBA.Object create_reference (String intf) throws org.omg.PortableServer.POAPackage.WrongPolicy;


  org.omg.CORBA.Object create_reference_with_id (byte[] oid, String intf);


  byte[] servant_to_id (org.omg.PortableServer.Servant p_servant) throws org.omg.PortableServer.POAPackage.ServantNotActive, org.omg.PortableServer.POAPackage.WrongPolicy;


  org.omg.CORBA.Object servant_to_reference (org.omg.PortableServer.Servant p_servant) throws org.omg.PortableServer.POAPackage.ServantNotActive, org.omg.PortableServer.POAPackage.WrongPolicy;


  org.omg.PortableServer.Servant reference_to_servant (org.omg.CORBA.Object reference) throws org.omg.PortableServer.POAPackage.ObjectNotActive, org.omg.PortableServer.POAPackage.WrongPolicy, org.omg.PortableServer.POAPackage.WrongAdapter;


  byte[] reference_to_id (org.omg.CORBA.Object reference) throws org.omg.PortableServer.POAPackage.WrongAdapter, org.omg.PortableServer.POAPackage.WrongPolicy;


  org.omg.PortableServer.Servant id_to_servant (byte[] oid) throws org.omg.PortableServer.POAPackage.ObjectNotActive, org.omg.PortableServer.POAPackage.WrongPolicy;


  org.omg.CORBA.Object id_to_reference (byte[] oid) throws org.omg.PortableServer.POAPackage.ObjectNotActive, org.omg.PortableServer.POAPackage.WrongPolicy;


  byte[] id ();
}