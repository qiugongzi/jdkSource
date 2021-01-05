

package com.sun.jmx.interceptor;


import java.io.ObjectInputStream;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.ReflectionException;
import javax.management.loading.ClassLoaderRepository;


public interface MBeanServerInterceptor extends MBeanServer {

    public Object instantiate(String className)
            throws ReflectionException, MBeanException;

    public Object instantiate(String className, ObjectName loaderName)
            throws ReflectionException, MBeanException,
            InstanceNotFoundException;

    public Object instantiate(String className, Object[] params,
            String[] signature) throws ReflectionException, MBeanException;


    public Object instantiate(String className, ObjectName loaderName,
            Object[] params, String[] signature)
            throws ReflectionException, MBeanException,
            InstanceNotFoundException;


    @Deprecated
    public ObjectInputStream deserialize(ObjectName name, byte[] data)
            throws InstanceNotFoundException, OperationsException;


    @Deprecated
    public ObjectInputStream deserialize(String className, byte[] data)
            throws OperationsException, ReflectionException;


    @Deprecated
    public ObjectInputStream deserialize(String className,
            ObjectName loaderName, byte[] data)
            throws InstanceNotFoundException, OperationsException,
            ReflectionException;


    public ClassLoaderRepository getClassLoaderRepository();

}

