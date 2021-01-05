


package com.sun.org.apache.xalan.internal;


public class Version
{


  public static String getVersion()
  {
     return getProduct()+" "+getImplementationLanguage()+" "
           +getMajorVersionNum()+"."+getReleaseVersionNum()+"."
           +( (getDevelopmentVersionNum() > 0) ?
               ("D"+getDevelopmentVersionNum()) : (""+getMaintenanceVersionNum()));
  }


  public static void _main(String argv[])
  {
    System.out.println(getVersion());
  }


  public static String getProduct()
  {
    return "Xalan";
  }


  public static String getImplementationLanguage()
  {
    return "Java";
  }



  public static int getMajorVersionNum()
  {
    return 2;

  }


  public static int getReleaseVersionNum()
  {
    return 7;
  }


  public static int getMaintenanceVersionNum()
  {
    return 0;
  }


  public static int getDevelopmentVersionNum()
  {
    try {
        if ((new String("")).length() == 0)
          return 0;
        else
          return Integer.parseInt("");
    } catch (NumberFormatException nfe) {
           return 0;
    }
  }
}
