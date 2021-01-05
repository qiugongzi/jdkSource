


package com.sun.jmx.snmp;

import java.util.Stack;
import java.util.EmptyStackException;


public class ThreadContext implements Cloneable {




    private  ThreadContext previous;
    private  String key;
    private  Object value;

    private ThreadContext(ThreadContext previous, String key, Object value) {
        this.previous = previous;
        this.key = key;
        this.value = value;
    }


    public static Object get(String key) throws IllegalArgumentException {
        ThreadContext context = contextContaining(key);
        if (context == null)
            return null;
        else
            return context.value;
    }


    public static boolean contains(String key)
            throws IllegalArgumentException {
        return (contextContaining(key) != null);
    }


    private static ThreadContext contextContaining(String key)
            throws IllegalArgumentException {
        if (key == null)
            throw new IllegalArgumentException("null key");
        for (ThreadContext context = getContext();
             context != null;
             context = context.previous) {
            if (key.equals(context.key))
                return context;

        }
        return null;
    }

public static ThreadContext push(String key, Object value)
            throws IllegalArgumentException {
        if (key == null)
            throw new IllegalArgumentException("null key");

        ThreadContext oldContext = getContext();
        if (oldContext == null)
            oldContext = new ThreadContext(null, null, null);  ThreadContext newContext = new ThreadContext(oldContext, key, value);
        setContext(newContext);
        return oldContext;
    }


    public static ThreadContext getThreadContext() {
        return getContext();
    }


    public static void restore(ThreadContext oldContext)
            throws NullPointerException, IllegalArgumentException {

        if (oldContext == null)
            throw new NullPointerException();


        for (ThreadContext context = getContext();
             context != oldContext;
             context = context.previous) {
            if (context == null) {
                throw new IllegalArgumentException("Restored context is not " +
                                                   "contained in current " +
                                                   "context");
            }
        }


        if (oldContext.key == null)
            oldContext = null;

        setContext(oldContext);
    }



    public void setInitialContext(ThreadContext context)
            throws IllegalArgumentException {

        if (getContext() != null)
            throw new IllegalArgumentException("previous context not empty");
        setContext(context);
    }

    private static ThreadContext getContext() {
        return localContext.get();
    }

    private static void setContext(ThreadContext context) {
        localContext.set(context);
    }

    private static ThreadLocal<ThreadContext> localContext =
            new ThreadLocal<ThreadContext>();
}
