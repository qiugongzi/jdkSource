package com.sun.corba.se.spi.activation;



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
    "IDL:activation/Server:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }


}
