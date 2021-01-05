package org.omg.CosNaming;






public abstract class BindingIteratorPOA extends org.omg.PortableServer.Servant
 implements org.omg.CosNaming.BindingIteratorOperations, org.omg.CORBA.portable.InvokeHandler
{

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("next_one", new java.lang.Integer (0));
    _methods.put ("next_n", new java.lang.Integer (1));
    _methods.put ("destroy", new java.lang.Integer (2));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {


       case 0:  {
         org.omg.CosNaming.BindingHolder b = new org.omg.CosNaming.BindingHolder ();
         boolean $result = false;
         $result = this.next_one (b);
         out = $rh.createReply();
         out.write_boolean ($result);
         org.omg.CosNaming.BindingHelper.write (out, b.value);
         break;
       }



       case 1:  {
         int how_many = in.read_ulong ();
         org.omg.CosNaming.BindingListHolder bl = new org.omg.CosNaming.BindingListHolder ();
         boolean $result = false;
         $result = this.next_n (how_many, bl);
         out = $rh.createReply();
         out.write_boolean ($result);
         org.omg.CosNaming.BindingListHelper.write (out, bl.value);
         break;
       }



       case 2:  {
         this.destroy ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } private static String[] __ids = {
    "IDL:omg.org/CosNaming/BindingIterator:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public BindingIterator _this() 
  {
    return BindingIteratorHelper.narrow(
    super._this_object());
  }

  public BindingIterator _this(org.omg.CORBA.ORB orb) 
  {
    return BindingIteratorHelper.narrow(
    super._this_object(orb));
  }


}