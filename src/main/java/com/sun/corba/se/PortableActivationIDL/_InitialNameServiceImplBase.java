package com.sun.corba.se.PortableActivationIDL;



       case 0:
       {
         try {
           String name = in.read_string ();
           org.omg.CORBA.Object obj = org.omg.CORBA.ObjectHelper.read (in);
           boolean isPersistant = in.read_boolean ();
           this.bind (name, obj, isPersistant);
           out = $rh.createReply();
         } catch (com.sun.corba.se.PortableActivationIDL.InitialNameServicePackage.NameAlreadyBound $ex) {
           out = $rh.createExceptionReply ();
           com.sun.corba.se.PortableActivationIDL.InitialNameServicePackage.NameAlreadyBoundHelper.write (out, $ex);
         }
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  }


  private static String[] __ids = {
    "IDL:PortableActivationIDL/InitialNameService:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }


}
