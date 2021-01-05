

package java.security;

import sun.security.util.Debug;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;



public final class AccessController {


    private AccessController() { }



    @CallerSensitive
    public static native <T> T doPrivileged(PrivilegedAction<T> action);


    @CallerSensitive
    public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> action) {
        AccessControlContext acc = getStackAccessControlContext();
        if (acc == null) {
            return AccessController.doPrivileged(action);
        }
        DomainCombiner dc = acc.getAssignedCombiner();
        return AccessController.doPrivileged(action,
                                             preserveCombiner(dc, Reflection.getCallerClass()));
    }



    @CallerSensitive
    public static native <T> T doPrivileged(PrivilegedAction<T> action,
                                            AccessControlContext context);



    @CallerSensitive
    public static <T> T doPrivileged(PrivilegedAction<T> action,
        AccessControlContext context, Permission... perms) {

        AccessControlContext parent = getContext();
        if (perms == null) {
            throw new NullPointerException("null permissions parameter");
        }
        Class <?> caller = Reflection.getCallerClass();
        return AccessController.doPrivileged(action, createWrapper(null,
            caller, parent, context, perms));
    }



    @CallerSensitive
    public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> action,
        AccessControlContext context, Permission... perms) {

        AccessControlContext parent = getContext();
        DomainCombiner dc = parent.getCombiner();
        if (dc == null && context != null) {
            dc = context.getCombiner();
        }
        if (perms == null) {
            throw new NullPointerException("null permissions parameter");
        }
        Class <?> caller = Reflection.getCallerClass();
        return AccessController.doPrivileged(action, createWrapper(dc, caller,
            parent, context, perms));
    }


    @CallerSensitive
    public static native <T> T
        doPrivileged(PrivilegedExceptionAction<T> action)
        throws PrivilegedActionException;



    @CallerSensitive
    public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> action)
        throws PrivilegedActionException
    {
        AccessControlContext acc = getStackAccessControlContext();
        if (acc == null) {
            return AccessController.doPrivileged(action);
        }
        DomainCombiner dc = acc.getAssignedCombiner();
        return AccessController.doPrivileged(action,
                                             preserveCombiner(dc, Reflection.getCallerClass()));
    }


    private static AccessControlContext preserveCombiner(DomainCombiner combiner,
                                                         Class<?> caller)
    {
        return createWrapper(combiner, caller, null, null, null);
    }


    private static AccessControlContext
        createWrapper(DomainCombiner combiner, Class<?> caller,
                      AccessControlContext parent, AccessControlContext context,
                      Permission[] perms)
    {
        ProtectionDomain callerPD = getCallerPD(caller);
        if (context != null && !context.isAuthorized() &&
            System.getSecurityManager() != null &&
            !callerPD.impliesCreateAccessControlContext())
        {
            ProtectionDomain nullPD = new ProtectionDomain(null, null);
            return new AccessControlContext(new ProtectionDomain[] { nullPD });
        } else {
            return new AccessControlContext(callerPD, combiner, parent,
                                            context, perms);
        }
    }

    private static ProtectionDomain getCallerPD(final Class <?> caller) {
        ProtectionDomain callerPd = doPrivileged
            (new PrivilegedAction<ProtectionDomain>() {
            public ProtectionDomain run() {
                return caller.getProtectionDomain();
            }
        });

        return callerPd;
    }


    @CallerSensitive
    public static native <T> T
        doPrivileged(PrivilegedExceptionAction<T> action,
                     AccessControlContext context)
        throws PrivilegedActionException;



    @CallerSensitive
    public static <T> T doPrivileged(PrivilegedExceptionAction<T> action,
                                     AccessControlContext context, Permission... perms)
        throws PrivilegedActionException
    {
        AccessControlContext parent = getContext();
        if (perms == null) {
            throw new NullPointerException("null permissions parameter");
        }
        Class <?> caller = Reflection.getCallerClass();
        return AccessController.doPrivileged(action, createWrapper(null, caller, parent, context, perms));
    }



    @CallerSensitive
    public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> action,
                                                 AccessControlContext context,
                                                 Permission... perms)
        throws PrivilegedActionException
    {
        AccessControlContext parent = getContext();
        DomainCombiner dc = parent.getCombiner();
        if (dc == null && context != null) {
            dc = context.getCombiner();
        }
        if (perms == null) {
            throw new NullPointerException("null permissions parameter");
        }
        Class <?> caller = Reflection.getCallerClass();
        return AccessController.doPrivileged(action, createWrapper(dc, caller,
            parent, context, perms));
    }



    private static native AccessControlContext getStackAccessControlContext();




    static native AccessControlContext getInheritedAccessControlContext();



    public static AccessControlContext getContext()
    {
        AccessControlContext acc = getStackAccessControlContext();
        if (acc == null) {
            return new AccessControlContext(null, true);
        } else {
            return acc.optimize();
        }
    }



    public static void checkPermission(Permission perm)
        throws AccessControlException
    {
        if (perm == null) {
            throw new NullPointerException("permission can't be null");
        }

        AccessControlContext stack = getStackAccessControlContext();
        if (stack == null) {
            Debug debug = AccessControlContext.getDebug();
            boolean dumpDebug = false;
            if (debug != null) {
                dumpDebug = !Debug.isOn("codebase=");
                dumpDebug &= !Debug.isOn("permission=") ||
                    Debug.isOn("permission=" + perm.getClass().getCanonicalName());
            }

            if (dumpDebug && Debug.isOn("stack")) {
                Thread.dumpStack();
            }

            if (dumpDebug && Debug.isOn("domain")) {
                debug.println("domain (context is null)");
            }

            if (dumpDebug) {
                debug.println("access allowed "+perm);
            }
            return;
        }

        AccessControlContext acc = stack.optimize();
        acc.checkPermission(perm);
    }
}
