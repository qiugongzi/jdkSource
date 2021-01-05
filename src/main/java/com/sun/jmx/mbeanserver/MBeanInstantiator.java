

package com.sun.jmx.mbeanserver;


import static com.sun.jmx.defaults.JmxProperties.MBEANSERVER_LOGGER;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.logging.Level;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanPermission;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import sun.reflect.misc.ConstructorUtil;
import sun.reflect.misc.ReflectUtil;


public class MBeanInstantiator {
    private final ModifiableClassLoaderRepository clr;
    MBeanInstantiator(ModifiableClassLoaderRepository clr) {
        this.clr = clr;
    }



    public void testCreation(Class<?> c) throws NotCompliantMBeanException {
        Introspector.testCreation(c);
    }


    public Class<?> findClassWithDefaultLoaderRepository(String className)
        throws ReflectionException {

        Class<?> theClass;
        if (className == null) {
            throw new RuntimeOperationsException(new
                IllegalArgumentException("The class name cannot be null"),
                             "Exception occurred during object instantiation");
        }

        ReflectUtil.checkPackageAccess(className);
        try {
            if (clr == null) throw new ClassNotFoundException(className);
            theClass = clr.loadClass(className);
        }
        catch (ClassNotFoundException ee) {
            throw new ReflectionException(ee,
       "The MBean class could not be loaded by the default loader repository");
        }

        return theClass;
    }



    public Class<?> findClass(String className, ClassLoader loader)
        throws ReflectionException {

        return loadClass(className,loader);
    }


    public Class<?> findClass(String className, ObjectName aLoader)
        throws ReflectionException, InstanceNotFoundException  {

        if (aLoader == null)
            throw new RuntimeOperationsException(new
                IllegalArgumentException(), "Null loader passed in parameter");

        ClassLoader loader = null;
        synchronized (this) {
            loader = getClassLoader(aLoader);
        }
        if (loader == null) {
            throw new InstanceNotFoundException("The loader named " +
                       aLoader + " is not registered in the MBeanServer");
        }
        return findClass(className,loader);
    }



    public Class<?>[] findSignatureClasses(String signature[],
                                           ClassLoader loader)
        throws ReflectionException {

        if (signature == null) return null;
        final ClassLoader aLoader = loader;
        final int length= signature.length;
        final Class<?> tab[]=new Class<?>[length];

        if (length == 0) return tab;
        try {
            for (int i= 0; i < length; i++) {
                final Class<?> primCla = primitiveClasses.get(signature[i]);
                if (primCla != null) {
                    tab[i] = primCla;
                    continue;
                }

                ReflectUtil.checkPackageAccess(signature[i]);
                if (aLoader != null) {
                    tab[i] = Class.forName(signature[i], false, aLoader);
                } else {
                    tab[i] = findClass(signature[i],
                                       this.getClass().getClassLoader());
                }
            }
        } catch (ClassNotFoundException e) {
            if (MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                MBEANSERVER_LOGGER.logp(Level.FINEST,
                        MBeanInstantiator.class.getName(),
                        "findSignatureClasses",
                        "The parameter class could not be found", e);
            }
            throw new ReflectionException(e,
                      "The parameter class could not be found");
        } catch (RuntimeException e) {
            if (MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                MBEANSERVER_LOGGER.logp(Level.FINEST,
                        MBeanInstantiator.class.getName(),
                        "findSignatureClasses",
                        "Unexpected exception", e);
            }
            throw e;
        }
        return tab;
    }



    public Object instantiate(Class<?> theClass)
        throws ReflectionException, MBeanException {

        checkMBeanPermission(theClass, null, null, "instantiate");

        Object moi;

        Constructor<?> cons = findConstructor(theClass, null);
        if (cons == null) {
            throw new ReflectionException(new
                NoSuchMethodException("No such constructor"));
        }
        try {
            ReflectUtil.checkPackageAccess(theClass);
            ensureClassAccess(theClass);
            moi= cons.newInstance();
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw new RuntimeMBeanException((RuntimeException)t,
                   "RuntimeException thrown in the MBean's empty constructor");
            } else if (t instanceof Error) {
                throw new RuntimeErrorException((Error) t,
                   "Error thrown in the MBean's empty constructor");
            } else {
                throw new MBeanException((Exception) t,
                   "Exception thrown in the MBean's empty constructor");
            }
        } catch (NoSuchMethodError error) {
            throw new ReflectionException(new
                NoSuchMethodException("No constructor"),
                                          "No such constructor");
        } catch (InstantiationException e) {
            throw new ReflectionException(e,
            "Exception thrown trying to invoke the MBean's empty constructor");
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e,
            "Exception thrown trying to invoke the MBean's empty constructor");
        } catch (IllegalArgumentException e) {
            throw new ReflectionException(e,
            "Exception thrown trying to invoke the MBean's empty constructor");
        }
        return moi;

    }




    public Object instantiate(Class<?> theClass, Object params[],
                              String signature[], ClassLoader loader)
        throws ReflectionException, MBeanException {

        checkMBeanPermission(theClass, null, null, "instantiate");

        final Class<?>[] tab;
        Object moi;
        try {
            ClassLoader aLoader= theClass.getClassLoader();
            tab =
                ((signature == null)?null:
                 findSignatureClasses(signature,aLoader));
        }
        catch (IllegalArgumentException e) {
            throw new ReflectionException(e,
                    "The constructor parameter classes could not be loaded");
        }

        Constructor<?> cons = findConstructor(theClass, tab);

        if (cons == null) {
            throw new ReflectionException(new
                NoSuchMethodException("No such constructor"));
        }
        try {
            ReflectUtil.checkPackageAccess(theClass);
            ensureClassAccess(theClass);
            moi = cons.newInstance(params);
        }
        catch (NoSuchMethodError error) {
            throw new ReflectionException(new
                NoSuchMethodException("No such constructor found"),
                                          "No such constructor" );
        }
        catch (InstantiationException e) {
            throw new ReflectionException(e,
                "Exception thrown trying to invoke the MBean's constructor");
        }
        catch (IllegalAccessException e) {
            throw new ReflectionException(e,
                "Exception thrown trying to invoke the MBean's constructor");
        }
        catch (InvocationTargetException e) {
            Throwable th = e.getTargetException();
            if (th instanceof RuntimeException) {
                throw new RuntimeMBeanException((RuntimeException)th,
                      "RuntimeException thrown in the MBean's constructor");
            } else if (th instanceof Error) {
                throw new RuntimeErrorException((Error) th,
                      "Error thrown in the MBean's constructor");
            } else {
                throw new MBeanException((Exception) th,
                      "Exception thrown in the MBean's constructor");
            }
        }
        return moi;
    }


    public ObjectInputStream deserialize(ClassLoader loader, byte[] data)
        throws OperationsException {

        if (data == null) {
            throw new  RuntimeOperationsException(new
                IllegalArgumentException(), "Null data passed in parameter");
        }
        if (data.length == 0) {
            throw new  RuntimeOperationsException(new
                IllegalArgumentException(), "Empty data passed in parameter");
        }

        ByteArrayInputStream bIn;
        ObjectInputStream    objIn;

        bIn   = new ByteArrayInputStream(data);
        try {
            objIn = new ObjectInputStreamWithLoader(bIn,loader);
        } catch (IOException e) {
            throw new OperationsException(
                     "An IOException occurred trying to de-serialize the data");
        }

        return objIn;
    }


    public ObjectInputStream deserialize(String className,
                                         ObjectName loaderName,
                                         byte[] data,
                                         ClassLoader loader)
        throws InstanceNotFoundException,
               OperationsException,
               ReflectionException  {

        if (data == null) {
            throw new  RuntimeOperationsException(new
                IllegalArgumentException(), "Null data passed in parameter");
        }
        if (data.length == 0) {
            throw new  RuntimeOperationsException(new
                IllegalArgumentException(), "Empty data passed in parameter");
        }
        if (className == null) {
            throw new  RuntimeOperationsException(new
             IllegalArgumentException(), "Null className passed in parameter");
        }

        ReflectUtil.checkPackageAccess(className);
        Class<?> theClass;
        if (loaderName == null) {
            theClass = findClass(className, loader);

        } else {
            try {
                ClassLoader instance = null;

                instance = getClassLoader(loaderName);
                if (instance == null)
                    throw new ClassNotFoundException(className);
                theClass = Class.forName(className, false, instance);
            }
            catch (ClassNotFoundException e) {
                throw new ReflectionException(e,
                               "The MBean class could not be loaded by the " +
                               loaderName.toString() + " class loader");
            }
        }

        ByteArrayInputStream bIn;
        ObjectInputStream    objIn;

        bIn   = new ByteArrayInputStream(data);
        try {
            objIn = new ObjectInputStreamWithLoader(bIn,
                                           theClass.getClassLoader());
        } catch (IOException e) {
            throw new OperationsException(
                    "An IOException occurred trying to de-serialize the data");
        }

        return objIn;
    }



    public Object instantiate(String className)
        throws ReflectionException,
        MBeanException {

        return instantiate(className, (Object[]) null, (String[]) null, null);
    }




    public Object instantiate(String className, ObjectName loaderName,
                              ClassLoader loader)
        throws ReflectionException, MBeanException,
               InstanceNotFoundException {

        return instantiate(className, loaderName, (Object[]) null,
                           (String[]) null, loader);
    }



    public Object instantiate(String className,
                              Object params[],
                              String signature[],
                              ClassLoader loader)
        throws ReflectionException,
        MBeanException {

        Class<?> theClass = findClassWithDefaultLoaderRepository(className);
        return instantiate(theClass, params, signature, loader);
    }




    public Object instantiate(String className,
                              ObjectName loaderName,
                              Object params[],
                              String signature[],
                              ClassLoader loader)
        throws ReflectionException,
               MBeanException,
        InstanceNotFoundException {

        Class<?> theClass;

        if (loaderName == null) {
            theClass = findClass(className, loader);
        } else {
            theClass = findClass(className, loaderName);
        }
        return instantiate(theClass, params, signature, loader);
    }



    public ModifiableClassLoaderRepository getClassLoaderRepository() {
        checkMBeanPermission((String)null, null, null, "getClassLoaderRepository");
        return clr;
    }


    static Class<?> loadClass(String className, ClassLoader loader)
        throws ReflectionException {
        Class<?> theClass;
        if (className == null) {
            throw new RuntimeOperationsException(new
                IllegalArgumentException("The class name cannot be null"),
                              "Exception occurred during object instantiation");
        }
        ReflectUtil.checkPackageAccess(className);
        try {
            if (loader == null)
                loader = MBeanInstantiator.class.getClassLoader();
            if (loader != null) {
                theClass = Class.forName(className, false, loader);
            } else {
                theClass = Class.forName(className);
            }
        } catch (ClassNotFoundException e) {
            throw new ReflectionException(e,
            "The MBean class could not be loaded");
        }
        return theClass;
    }




    static Class<?>[] loadSignatureClasses(String signature[],
                                           ClassLoader loader)
        throws  ReflectionException {

        if (signature == null) return null;
        final ClassLoader aLoader =
           (loader==null?MBeanInstantiator.class.getClassLoader():loader);
        final int length= signature.length;
        final Class<?> tab[]=new Class<?>[length];

        if (length == 0) return tab;
        try {
            for (int i= 0; i < length; i++) {
                final Class<?> primCla = primitiveClasses.get(signature[i]);
                if (primCla != null) {
                    tab[i] = primCla;
                    continue;
                }

                ReflectUtil.checkPackageAccess(signature[i]);
                tab[i] = Class.forName(signature[i], false, aLoader);
            }
        } catch (ClassNotFoundException e) {
            if (MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                MBEANSERVER_LOGGER.logp(Level.FINEST,
                        MBeanInstantiator.class.getName(),
                        "findSignatureClasses",
                        "The parameter class could not be found", e);
            }
            throw new ReflectionException(e,
                      "The parameter class could not be found");
        } catch (RuntimeException e) {
            if (MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                MBEANSERVER_LOGGER.logp(Level.FINEST,
                        MBeanInstantiator.class.getName(),
                        "findSignatureClasses",
                        "Unexpected exception", e);
            }
            throw e;
        }
        return tab;
    }

    private Constructor<?> findConstructor(Class<?> c, Class<?>[] params) {
        try {
            return ConstructorUtil.getConstructor(c, params);
        } catch (Exception e) {
            return null;
        }
    }


    private static final Map<String, Class<?>> primitiveClasses = Util.newMap();
    static {
        for (Class<?> c : new Class<?>[] {byte.class, short.class, int.class,
                                          long.class, float.class, double.class,
                                          char.class, boolean.class})
            primitiveClasses.put(c.getName(), c);
    }

    private static void checkMBeanPermission(Class<?> clazz,
                                             String member,
                                             ObjectName objectName,
                                             String actions) {
        if (clazz != null) {
            checkMBeanPermission(clazz.getName(), member, objectName, actions);
        }
    }

    private static void checkMBeanPermission(String classname,
                                             String member,
                                             ObjectName objectName,
                                             String actions)
        throws SecurityException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            Permission perm = new MBeanPermission(classname,
                                                  member,
                                                  objectName,
                                                  actions);
            sm.checkPermission(perm);
        }
    }

    private static void ensureClassAccess(Class clazz)
            throws IllegalAccessException
    {
        int mod = clazz.getModifiers();
        if (!Modifier.isPublic(mod)) {
            throw new IllegalAccessException("Class is not public and can't be instantiated");
        }
    }

    private ClassLoader getClassLoader(final ObjectName name) {
        if(clr == null){
            return null;
        }
        Permissions permissions = new Permissions();
        permissions.add(new MBeanPermission("*", null, name, "getClassLoader"));
        ProtectionDomain protectionDomain = new ProtectionDomain(null, permissions);
        ProtectionDomain[] domains = {protectionDomain};
        AccessControlContext ctx = new AccessControlContext(domains);
        ClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return clr.getClassLoader(name);
            }
        }, ctx);
        return loader;
    }
}
