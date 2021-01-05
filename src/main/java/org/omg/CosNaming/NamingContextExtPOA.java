package org.omg.CosNaming;






public abstract class NamingContextExtPOA extends org.omg.PortableServer.Servant
 implements org.omg.CosNaming.NamingContextExtOperations, org.omg.CORBA.portable.InvokeHandler
{

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("to_string", new java.lang.Integer (0));
    _methods.put ("to_name", new java.lang.Integer (1));
    _methods.put ("to_url", new java.lang.Integer (2));
    _methods.put ("resolve_str", new java.lang.Integer (3));
    _methods.put ("bind", new java.lang.Integer (4));
    _methods.put ("bind_context", new java.lang.Integer (5));
    _methods.put ("rebind", new java.lang.Integer (6));
    _methods.put ("rebind_context", new java.lang.Integer (7));
    _methods.put ("resolve", new java.lang.Integer (8));
    _methods.put ("unbind", new java.lang.Integer (9));
    _methods.put ("list", new java.lang.Integer (10));
    _methods.put ("new_context", new java.lang.Integer (11));
    _methods.put ("bind_new_context", new java.lang.Integer (12));
    _methods.put ("destroy", new java.lang.Integer (13));
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
         try {
           org.omg.CosNaming.NameComponent n[] = org.omg.CosNaming.NameHelper.read (in);
           String $result = null;
           $result = this.to_string (n);
           out = $rh.createReply();
           out.write_string ($result);
         } catch (org.omg.CosNaming.NamingContextPackage.InvalidName $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write (out, $ex);
         }
         break;
       }



       case 1:  {
         try {
           String sn = org.omg.CosNaming.NamingContextExtPackage.StringNameHelper.read (in);
           org.omg.CosNaming.NameComponent $result[] = null;
           $result = this.to_name (sn);
           out = $rh.createReply();
           org.omg.CosNaming.NameHelper.write (out, $result);
         } catch (org.omg.CosNaming.NamingContextPackage.InvalidName $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write (out, $ex);
         }
         break;
       }



       case 2:  {
         try {
           String addr = org.omg.CosNaming.NamingContextExtPackage.AddressHelper.read (in);
           String sn = org.omg.CosNaming.NamingContextExtPackage.StringNameHelper.read (in);
           String $result = null;
           $result = this.to_url (addr, sn);
           out = $rh.createReply();
           out.write_string ($result);
         } catch (org.omg.CosNaming.NamingContextExtPackage.InvalidAddress $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextExtPackage.InvalidAddressHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.InvalidName $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write (out, $ex);
         }
         break;
       }



       case 3:  {
         try {
           String sn = org.omg.CosNaming.NamingContextExtPackage.StringNameHelper.read (in);
           org.omg.CORBA.Object $result = null;
           $result = this.resolve_str (sn);
           out = $rh.createReply();
           org.omg.CORBA.ObjectHelper.write (out, $result);
         } catch (org.omg.CosNaming.NamingContextPackage.NotFound $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.InvalidName $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write (out, $ex);
         }
         break;
       }



       case 4:  {
         try {
           org.omg.CosNaming.NameComponent n[] = org.omg.CosNaming.NameHelper.read (in);
           org.omg.CORBA.Object obj = org.omg.CORBA.ObjectHelper.read (in);
           this.bind (n, obj);
           out = $rh.createReply();
         } catch (org.omg.CosNaming.NamingContextPackage.NotFound $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.InvalidName $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.write (out, $ex);
         }
         break;
       }



       case 5:  {
         try {
           org.omg.CosNaming.NameComponent n[] = org.omg.CosNaming.NameHelper.read (in);
           org.omg.CosNaming.NamingContext nc = org.omg.CosNaming.NamingContextHelper.read (in);
           this.bind_context (n, nc);
           out = $rh.createReply();
         } catch (org.omg.CosNaming.NamingContextPackage.NotFound $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.InvalidName $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.write (out, $ex);
         }
         break;
       }



       case 6:  {
         try {
           org.omg.CosNaming.NameComponent n[] = org.omg.CosNaming.NameHelper.read (in);
           org.omg.CORBA.Object obj = org.omg.CORBA.ObjectHelper.read (in);
           this.rebind (n, obj);
           out = $rh.createReply();
         } catch (org.omg.CosNaming.NamingContextPackage.NotFound $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.InvalidName $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write (out, $ex);
         }
         break;
       }



       case 7:  {
         try {
           org.omg.CosNaming.NameComponent n[] = org.omg.CosNaming.NameHelper.read (in);
           org.omg.CosNaming.NamingContext nc = org.omg.CosNaming.NamingContextHelper.read (in);
           this.rebind_context (n, nc);
           out = $rh.createReply();
         } catch (org.omg.CosNaming.NamingContextPackage.NotFound $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.InvalidName $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write (out, $ex);
         }
         break;
       }



       case 8:  {
         try {
           org.omg.CosNaming.NameComponent n[] = org.omg.CosNaming.NameHelper.read (in);
           org.omg.CORBA.Object $result = null;
           $result = this.resolve (n);
           out = $rh.createReply();
           org.omg.CORBA.ObjectHelper.write (out, $result);
         } catch (org.omg.CosNaming.NamingContextPackage.NotFound $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.InvalidName $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write (out, $ex);
         }
         break;
       }



       case 9:  {
         try {
           org.omg.CosNaming.NameComponent n[] = org.omg.CosNaming.NameHelper.read (in);
           this.unbind (n);
           out = $rh.createReply();
         } catch (org.omg.CosNaming.NamingContextPackage.NotFound $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.InvalidName $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write (out, $ex);
         }
         break;
       }



       case 10:  {
         int how_many = in.read_ulong ();
         org.omg.CosNaming.BindingListHolder bl = new org.omg.CosNaming.BindingListHolder ();
         org.omg.CosNaming.BindingIteratorHolder bi = new org.omg.CosNaming.BindingIteratorHolder ();
         this.list (how_many, bl, bi);
         out = $rh.createReply();
         org.omg.CosNaming.BindingListHelper.write (out, bl.value);
         org.omg.CosNaming.BindingIteratorHelper.write (out, bi.value);
         break;
       }



       case 11:  {
         org.omg.CosNaming.NamingContext $result = null;
         $result = this.new_context ();
         out = $rh.createReply();
         org.omg.CosNaming.NamingContextHelper.write (out, $result);
         break;
       }



       case 12:  {
         try {
           org.omg.CosNaming.NameComponent n[] = org.omg.CosNaming.NameHelper.read (in);
           org.omg.CosNaming.NamingContext $result = null;
           $result = this.bind_new_context (n);
           out = $rh.createReply();
           org.omg.CosNaming.NamingContextHelper.write (out, $result);
         } catch (org.omg.CosNaming.NamingContextPackage.NotFound $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write (out, $ex);
         } catch (org.omg.CosNaming.NamingContextPackage.InvalidName $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write (out, $ex);
         }
         break;
       }



       case 13:  {
         try {
           this.destroy ();
           out = $rh.createReply();
         } catch (org.omg.CosNaming.NamingContextPackage.NotEmpty $ex) {
           out = $rh.createExceptionReply ();
           org.omg.CosNaming.NamingContextPackage.NotEmptyHelper.write (out, $ex);
         }
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } private static String[] __ids = {
    "IDL:omg.org/CosNaming/NamingContextExt:1.0", 
    "IDL:omg.org/CosNaming/NamingContext:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public NamingContextExt _this() 
  {
    return NamingContextExtHelper.narrow(
    super._this_object());
  }

  public NamingContextExt _this(org.omg.CORBA.ORB orb) 
  {
    return NamingContextExtHelper.narrow(
    super._this_object(orb));
  }


}