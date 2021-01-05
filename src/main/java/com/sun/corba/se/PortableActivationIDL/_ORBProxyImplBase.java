package com.sun.corba.se.PortableActivationIDL;



       case 0:
       {
         String name[] = org.omg.PortableInterceptor.AdapterNameHelper.read (in);
         boolean $result = false;
         $result = this.activate_adapter (name);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  }


  private static String[] __ids = {
    "IDL:PortableActivationIDL/ORBProxy:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }


}
