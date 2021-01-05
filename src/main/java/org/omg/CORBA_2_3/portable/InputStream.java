


package org.omg.CORBA_2_3.portable;

import java.io.SerializablePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;



public abstract class InputStream extends org.omg.CORBA.portable.InputStream {


    private static final String ALLOW_SUBCLASS_PROP = "jdk.corba.allowInputStreamSubclass";

    private static final boolean allowSubclass = AccessController.doPrivileged(
        new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
            String prop = System.getProperty(ALLOW_SUBCLASS_PROP);
                return prop == null ? false :
                           (prop.equalsIgnoreCase("false") ? false : true);
            }
        });

    private static Void checkPermission() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            if (!allowSubclass)
                sm.checkPermission(new
                    SerializablePermission("enableSubclassImplementation"));
        }
        return null;
    }

    private InputStream(Void ignore) { }


    public InputStream() {
        this(checkPermission());
    }


    public java.io.Serializable read_value() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public java.io.Serializable read_value(java.lang.Class clz) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public java.io.Serializable read_value(org.omg.CORBA.portable.BoxedValueHelper factory) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public java.io.Serializable read_value(java.lang.String rep_id) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public java.io.Serializable read_value(java.io.Serializable value) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public java.lang.Object read_abstract_interface() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    public java.lang.Object read_abstract_interface(java.lang.Class clz) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

}
