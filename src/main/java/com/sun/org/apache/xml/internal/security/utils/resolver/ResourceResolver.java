

package com.sun.org.apache.xml.internal.security.utils.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverDirectHTTP;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverFragment;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverLocalFilesystem;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverXPointer;
import org.w3c.dom.Attr;


public class ResourceResolver {


    private static java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(ResourceResolver.class.getName());


    private static List<ResourceResolver> resolverList = new ArrayList<ResourceResolver>();


    private final ResourceResolverSpi resolverSpi;


    public ResourceResolver(ResourceResolverSpi resourceResolver) {
        this.resolverSpi = resourceResolver;
    }


    public static final ResourceResolver getInstance(Attr uri, String baseURI)
        throws ResourceResolverException {
        return getInstance(uri, baseURI, false);
    }


    public static final ResourceResolver getInstance(
        Attr uriAttr, String baseURI, boolean secureValidation
    ) throws ResourceResolverException {
        ResourceResolverContext context = new ResourceResolverContext(uriAttr, baseURI, secureValidation);
        return internalGetInstance(context);
    }

    private static <N> ResourceResolver internalGetInstance(ResourceResolverContext context)
            throws ResourceResolverException {
        synchronized (resolverList) {
            for (ResourceResolver resolver : resolverList) {
                ResourceResolver resolverTmp = resolver;
                if (!resolver.resolverSpi.engineIsThreadSafe()) {
                    try {
                        resolverTmp =
                            new ResourceResolver(resolver.resolverSpi.getClass().newInstance());
                    } catch (InstantiationException e) {
                        throw new ResourceResolverException("", e, context.attr, context.baseUri);
                    } catch (IllegalAccessException e) {
                        throw new ResourceResolverException("", e, context.attr, context.baseUri);
                    }
                }

                if (log.isLoggable(java.util.logging.Level.FINE)) {
                    log.log(java.util.logging.Level.FINE,
                        "check resolvability by class " + resolverTmp.getClass().getName()
                    );
                }

                if ((resolverTmp != null) && resolverTmp.canResolve(context)) {
                    if (context.secureValidation
                        && (resolverTmp.resolverSpi instanceof ResolverLocalFilesystem
                            || resolverTmp.resolverSpi instanceof ResolverDirectHTTP)) {
                        Object exArgs[] = { resolverTmp.resolverSpi.getClass().getName() };
                        throw new ResourceResolverException(
                            "signature.Reference.ForbiddenResolver", exArgs, context.attr, context.baseUri
                        );
                    }
                    return resolverTmp;
                }
            }
        }

        Object exArgs[] = { ((context.uriToResolve != null)
                ? context.uriToResolve : "null"), context.baseUri };

        throw new ResourceResolverException("utils.resolver.noClass", exArgs, context.attr, context.baseUri);
    }


    public static ResourceResolver getInstance(
        Attr uri, String baseURI, List<ResourceResolver> individualResolvers
    ) throws ResourceResolverException {
        return getInstance(uri, baseURI, individualResolvers, false);
    }


    public static ResourceResolver getInstance(
        Attr uri, String baseURI, List<ResourceResolver> individualResolvers, boolean secureValidation
    ) throws ResourceResolverException {
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE,
                "I was asked to create a ResourceResolver and got "
                + (individualResolvers == null ? 0 : individualResolvers.size())
            );
        }

        ResourceResolverContext context = new ResourceResolverContext(uri, baseURI, secureValidation);

        if (individualResolvers != null) {
            for (int i = 0; i < individualResolvers.size(); i++) {
                ResourceResolver resolver = individualResolvers.get(i);

                if (resolver != null) {
                    if (log.isLoggable(java.util.logging.Level.FINE)) {
                        String currentClass = resolver.resolverSpi.getClass().getName();
                        log.log(java.util.logging.Level.FINE, "check resolvability by class " + currentClass);
                    }

                    if (resolver.canResolve(context)) {
                        return resolver;
                    }
                }
            }
        }

        return internalGetInstance(context);
    }


    @SuppressWarnings("unchecked")
    public static void register(String className) {
        JavaUtils.checkRegisterPermission();
        try {
            Class<ResourceResolverSpi> resourceResolverClass =
                (Class<ResourceResolverSpi>) Class.forName(className);
            register(resourceResolverClass, false);
        } catch (ClassNotFoundException e) {
            log.log(java.util.logging.Level.WARNING, "Error loading resolver " + className + " disabling it");
        }
    }


    @SuppressWarnings("unchecked")
    public static void registerAtStart(String className) {
        JavaUtils.checkRegisterPermission();
        try {
            Class<ResourceResolverSpi> resourceResolverClass =
                (Class<ResourceResolverSpi>) Class.forName(className);
            register(resourceResolverClass, true);
        } catch (ClassNotFoundException e) {
            log.log(java.util.logging.Level.WARNING, "Error loading resolver " + className + " disabling it");
        }
    }


    public static void register(Class<? extends ResourceResolverSpi> className, boolean start) {
        JavaUtils.checkRegisterPermission();
        try {
            ResourceResolverSpi resourceResolverSpi = className.newInstance();
            register(resourceResolverSpi, start);
        } catch (IllegalAccessException e) {
            log.log(java.util.logging.Level.WARNING, "Error loading resolver " + className + " disabling it");
        } catch (InstantiationException e) {
            log.log(java.util.logging.Level.WARNING, "Error loading resolver " + className + " disabling it");
        }
    }


    public static void register(ResourceResolverSpi resourceResolverSpi, boolean start) {
        JavaUtils.checkRegisterPermission();
        synchronized(resolverList) {
            if (start) {
                resolverList.add(0, new ResourceResolver(resourceResolverSpi));
            } else {
                resolverList.add(new ResourceResolver(resourceResolverSpi));
            }
        }
        if (log.isLoggable(java.util.logging.Level.FINE)) {
            log.log(java.util.logging.Level.FINE, "Registered resolver: " + resourceResolverSpi.toString());
        }
    }


    public static void registerDefaultResolvers() {
        synchronized(resolverList) {
            resolverList.add(new ResourceResolver(new ResolverFragment()));
            resolverList.add(new ResourceResolver(new ResolverLocalFilesystem()));
            resolverList.add(new ResourceResolver(new ResolverXPointer()));
            resolverList.add(new ResourceResolver(new ResolverDirectHTTP()));
        }
    }


    @Deprecated
    public XMLSignatureInput resolve(Attr uri, String baseURI)
        throws ResourceResolverException {
        return resolve(uri, baseURI, true);
    }


    public XMLSignatureInput resolve(Attr uri, String baseURI, boolean secureValidation)
        throws ResourceResolverException {
        ResourceResolverContext context = new ResourceResolverContext(uri, baseURI, secureValidation);
        return resolverSpi.engineResolveURI(context);
    }


    public void setProperty(String key, String value) {
        resolverSpi.engineSetProperty(key, value);
    }


    public String getProperty(String key) {
        return resolverSpi.engineGetProperty(key);
    }


    public void addProperties(Map<String, String> properties) {
        resolverSpi.engineAddProperies(properties);
    }


    public String[] getPropertyKeys() {
        return resolverSpi.engineGetPropertyKeys();
    }


    public boolean understandsProperty(String propertyToTest) {
        return resolverSpi.understandsProperty(propertyToTest);
    }


    private boolean canResolve(ResourceResolverContext context) {
        return this.resolverSpi.engineCanResolveURI(context);
    }
}
