




package org.w3c.dom.bootstrap;

import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.DOMImplementationSource;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMImplementation;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;


public final class DOMImplementationRegistry {

    public static final String PROPERTY =
        "org.w3c.dom.DOMImplementationSourceList";


    private static final int DEFAULT_LINE_LENGTH = 80;


    private Vector sources;


    private static final String FALLBACK_CLASS =
            "com.sun.org.apache.xerces.internal.dom.DOMXSImplementationSourceImpl";
    private static final String DEFAULT_PACKAGE =
            "com.sun.org.apache.xerces.internal.dom";

    private DOMImplementationRegistry(final Vector srcs) {
        sources = srcs;
    }


    public static DOMImplementationRegistry newInstance()
        throws
        ClassNotFoundException,
        InstantiationException,
        IllegalAccessException,
        ClassCastException {
        Vector sources = new Vector();

        ClassLoader classLoader = getClassLoader();
        String p = getSystemProperty(PROPERTY);

        if (p == null) {
            p = getServiceValue(classLoader);
        }
        if (p == null) {
            p = FALLBACK_CLASS;
        }
        if (p != null) {
            StringTokenizer st = new StringTokenizer(p);
            while (st.hasMoreTokens()) {
                String sourceName = st.nextToken();
                boolean internal = false;
                if (System.getSecurityManager() != null) {
                    if (sourceName != null && sourceName.startsWith(DEFAULT_PACKAGE)) {
                        internal = true;
                    }
                }
                Class sourceClass = null;
                if (classLoader != null && !internal) {
                    sourceClass = classLoader.loadClass(sourceName);
                } else {
                    sourceClass = Class.forName(sourceName);
                }
                DOMImplementationSource source =
                    (DOMImplementationSource) sourceClass.newInstance();
                sources.addElement(source);
            }
        }
        return new DOMImplementationRegistry(sources);
    }


    public DOMImplementation getDOMImplementation(final String features) {
        int size = sources.size();
        String name = null;
        for (int i = 0; i < size; i++) {
            DOMImplementationSource source =
                (DOMImplementationSource) sources.elementAt(i);
            DOMImplementation impl = source.getDOMImplementation(features);
            if (impl != null) {
                return impl;
            }
        }
        return null;
    }


    public DOMImplementationList getDOMImplementationList(final String features) {
        final Vector implementations = new Vector();
        int size = sources.size();
        for (int i = 0; i < size; i++) {
            DOMImplementationSource source =
                (DOMImplementationSource) sources.elementAt(i);
            DOMImplementationList impls =
                source.getDOMImplementationList(features);
            for (int j = 0; j < impls.getLength(); j++) {
                DOMImplementation impl = impls.item(j);
                implementations.addElement(impl);
            }
        }
        return new DOMImplementationList() {
                public DOMImplementation item(final int index) {
                    if (index >= 0 && index < implementations.size()) {
                        try {
                            return (DOMImplementation)
                                implementations.elementAt(index);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            return null;
                        }
                    }
                    return null;
                }

                public int getLength() {
                    return implementations.size();
                }
            };
    }


    public void addSource(final DOMImplementationSource s) {
        if (s == null) {
            throw new NullPointerException();
        }
        if (!sources.contains(s)) {
            sources.addElement(s);
        }
    }


    private static ClassLoader getClassLoader() {
        try {
            ClassLoader contextClassLoader = getContextClassLoader();

            if (contextClassLoader != null) {
                return contextClassLoader;
            }
        } catch (Exception e) {
            return DOMImplementationRegistry.class.getClassLoader();
        }
        return DOMImplementationRegistry.class.getClassLoader();
    }


    private static String getServiceValue(final ClassLoader classLoader) {
        String serviceId = "META-INF/services/" + PROPERTY;
        try {
            InputStream is = getResourceAsStream(classLoader, serviceId);

            if (is != null) {
                BufferedReader rd;
                try {
                    rd =
                        new BufferedReader(new InputStreamReader(is, "UTF-8"),
                                           DEFAULT_LINE_LENGTH);
                } catch (java.io.UnsupportedEncodingException e) {
                    rd =
                        new BufferedReader(new InputStreamReader(is),
                                           DEFAULT_LINE_LENGTH);
                }
                String serviceValue = rd.readLine();
                rd.close();
                if (serviceValue != null && serviceValue.length() > 0) {
                    return serviceValue;
                }
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }


    private static boolean isJRE11() {
        try {
            Class c = Class.forName("java.security.AccessController");
            return false;
        } catch (Exception ex) {
            }
        return true;
    }


    private static ClassLoader getContextClassLoader() {
        return isJRE11()
            ? null
            : (ClassLoader)
              AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        ClassLoader classLoader = null;
                        try {
                            classLoader =
                                Thread.currentThread().getContextClassLoader();
                        } catch (SecurityException ex) {
                        }
                        return classLoader;
                    }
                });
    }


    private static String getSystemProperty(final String name) {
        return isJRE11()
            ? (String) System.getProperty(name)
            : (String) AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        return System.getProperty(name);
                    }
                });
    }


    private static InputStream getResourceAsStream(final ClassLoader classLoader,
                                                   final String name) {
        if (isJRE11()) {
            InputStream ris;
            if (classLoader == null) {
                ris = ClassLoader.getSystemResourceAsStream(name);
            } else {
                ris = classLoader.getResourceAsStream(name);
            }
            return ris;
        } else {
            return (InputStream)
                AccessController.doPrivileged(new PrivilegedAction() {
                        public Object run() {
                            InputStream ris;
                            if (classLoader == null) {
                                ris =
                                    ClassLoader.getSystemResourceAsStream(name);
                            } else {
                                ris = classLoader.getResourceAsStream(name);
                            }
                            return ris;
                        }
                    });
        }
    }
}
