package org.omg.CosNaming;






public abstract class NamingContextPOA extends org.omg.PortableServer.Servant
 implements org.omg.CosNaming.NamingContextOperations, org.omg.CORBA.portable.InvokeHandler
{

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("bind", new java.lang.Integer (0));
    _methods.put ("bind_context", new java.lang.Integer (1));
    _methods.put ("rebind", new java.lang.Integer (2));
    _methods.put ("rebind_context", new java.lang.Integer (3));
    _methods.put ("resolve", new java.lang.Integer (4));
    _methods.put ("unbind", new java.lang.Integer (5));
    _methods.put ("list", new java.lang.Integer (6));
    _methods.put ("new_context", new java.lang.Integer (7));
    _methods.put ("bind_new_context", new java.lang.Integer (8));
    _methods.put ("destroy", new java.lang.Integer (9));
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



       case 1:  {
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



       case 2:  {
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



       case 3:  {
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



       case 4:  {
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



       case 5:  {
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



       case 6:  {
         int how_many = in.read_ulong ();
         org.omg.CosNaming.BindingListHolder bl = new org.omg.CosNaming.BindingListHolder ();
         org.omg.CosNaming.BindingIteratorHolder bi = new org.omg.CosNaming.BindingIteratorHolder ();
         this.list (how_many, bl, bi);
         out = $rh.createReply();
         org.omg.CosNaming.BindingListHelper.write (out, bl.value);
         org.omg.CosNaming.BindingIteratorHelper.write (out, bi.value);
         break;
       }



       case 7:  {
         org.omg.CosNaming.NamingContext $result = null;
         $result = this.new_context ();
         out = $rh.createReply();
         org.omg.CosNaming.NamingContextHelper.write (out, $result);
         break;
       }



       case 8:  {
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



       case 9:  {
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
    "IDL:omg.org/CosNaming/NamingContext:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public NamingContext _this() 
  {
    return NamingContextHelper.narrow(
    super._this_object());
  }

  public NamingContext _this(org.omg.CORBA.ORB orb) 
  {
    return NamingContextHelper.narrow(
    super._this_object(orb));
  }


}