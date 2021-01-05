
package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;



class DefaultDatagramSocketImplFactory
{
    private final static Class<?> prefixImplClass;


    private static float version;


    private static boolean preferIPv4Stack = false;


    private final static boolean useDualStackImpl;


    private static String exclBindProp;


    private final static boolean exclusiveBind;

    static {
        Class<?> prefixImplClassLocal = null;
        boolean useDualStackImplLocal = false;
        boolean exclusiveBindLocal = true;

        java.security.AccessController.doPrivileged(
                new PrivilegedAction<Object>() {
                    public Object run() {
                        version = 0;
                        try {
                            version = Float.parseFloat(System.getProperties()
                                    .getProperty("os.version"));
                            preferIPv4Stack = Boolean.parseBoolean(
                                              System.getProperties()
                                              .getProperty(
                                                   "java.net.preferIPv4Stack"));
                            exclBindProp = System.getProperty(
                                    "sun.net.useExclusiveBind");
                        } catch (NumberFormatException e) {
                            assert false : e;
                        }
                        return null; }
                });

        if (version >= 6.0 && !preferIPv4Stack) {
            useDualStackImplLocal = true;
        }
        if (exclBindProp != null) {
            exclusiveBindLocal = exclBindProp.length() == 0 ? true
                    : Boolean.parseBoolean(exclBindProp);
        } else if (version < 6.0) {
            exclusiveBindLocal = false;
        }

        String prefix = null;
        try {
            prefix = AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction("impl.prefix", null));
            if (prefix != null)
                prefixImplClassLocal = Class.forName("java.net."+prefix+"DatagramSocketImpl");
        } catch (Exception e) {
            System.err.println("Can't find class: java.net." +
                                prefix +
                                "DatagramSocketImpl: check impl.prefix property");
        }

        prefixImplClass = prefixImplClassLocal;
        useDualStackImpl = useDualStackImplLocal;
        exclusiveBind = exclusiveBindLocal;
    }


    static DatagramSocketImpl createDatagramSocketImpl(boolean isMulticast)
        throws SocketException {
        if (prefixImplClass != null) {
            try {
                return (DatagramSocketImpl) prefixImplClass.newInstance();
            } catch (Exception e) {
                throw new SocketException("can't instantiate DatagramSocketImpl");
            }
        } else {
            if (useDualStackImpl && !isMulticast)
                return new DualStackPlainDatagramSocketImpl(exclusiveBind);
            else
                return new TwoStacksPlainDatagramSocketImpl(exclusiveBind && !isMulticast);
        }
    }
}
