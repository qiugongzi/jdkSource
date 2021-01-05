package org.omg.PortableServer;






public class _ServantLocatorStub extends org.omg.CORBA.portable.ObjectImpl implements org.omg.PortableServer.ServantLocator
{
  final public static java.lang.Class _opsClass = ServantLocatorOperations.class;




  public org.omg.PortableServer.Servant preinvoke (byte[] oid, org.omg.PortableServer.POA adapter, String operation, org.omg.PortableServer.ServantLocatorPackage.CookieHolder the_cookie) throws org.omg.PortableServer.ForwardRequest
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("preinvoke", _opsClass);
      ServantLocatorOperations  $self = (ServantLocatorOperations) $so.servant;

      try {
         return $self.preinvoke (oid, adapter, operation, the_cookie);
      } finally {
          _servant_postinvoke ($so);
      }
  } public void postinvoke (byte[] oid, org.omg.PortableServer.POA adapter, String operation, java.lang.Object the_cookie, org.omg.PortableServer.Servant the_servant)
  {
      org.omg.CORBA.portable.ServantObject $so = _servant_preinvoke ("postinvoke", _opsClass);
      ServantLocatorOperations  $self = (ServantLocatorOperations) $so.servant;

      try {
         $self.postinvoke (oid, adapter, operation, the_cookie, the_servant);
      } finally {
          _servant_postinvoke ($so);
      }
  } private static String[] __ids = {
    "IDL:omg.org/PortableServer/ServantLocator:1.0", 
    "IDL:omg.org/PortableServer/ServantManager:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
}