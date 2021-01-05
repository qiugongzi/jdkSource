package com.sun.corba.se.PortableActivationIDL;



       case 2:
       {
         this.uninstall ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  }


  private static String[] __ids = {
    "IDL:PortableActivationIDL/ServerProxy:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }


}
