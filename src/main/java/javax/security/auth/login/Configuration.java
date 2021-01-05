

package javax.security.auth.login;

import javax.security.auth.AuthPermission;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.Objects;

import sun.security.jca.GetInstance;


public abstract class Configuration {

    private static Configuration configuration;

    private final java.security.AccessControlContext acc =
            java.security.AccessController.getContext();

    private static void checkPermission(String type) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AuthPermission
                                ("createLoginConfiguration." + type));
        }
    }


    protected Configuration() { }


    public static Configuration getConfiguration() {

        SecurityManager sm = System.getSecurityManager();
        if (sm != null)
            sm.checkPermission(new AuthPermission("getLoginConfiguration"));

        synchronized (Configuration.class) {
            if (configuration == null) {
                String config_class = null;
                config_class = AccessController.doPrivileged
                    (new PrivilegedAction<String>() {
                    public String run() {
                        return java.security.Security.getProperty
                                    ("login.configuration.provider");
                    }
                });
                if (config_class == null) {
                    config_class = "sun.security.provider.ConfigFile";
                }

                try {
                    final String finalClass = config_class;
                    Configuration untrustedImpl = AccessController.doPrivileged(
                            new PrivilegedExceptionAction<Configuration>() {
                                public Configuration run() throws ClassNotFoundException,
                                        InstantiationException,
                                        IllegalAccessException {
                                    Class<? extends Configuration> implClass = Class.forName(
                                            finalClass, false,
                                            Thread.currentThread().getContextClassLoader()
                                    ).asSubclass(Configuration.class);
                                    return implClass.newInstance();
                                }
                            });
                    AccessController.doPrivileged(
                            new PrivilegedExceptionAction<Void>() {
                                public Void run() {
                                    setConfiguration(untrustedImpl);
                                    return null;
                                }
                            }, Objects.requireNonNull(untrustedImpl.acc)
                    );
                } catch (PrivilegedActionException e) {
                    Exception ee = e.getException();
                    if (ee instanceof InstantiationException) {
                        throw (SecurityException) new
                            SecurityException
                                    ("Configuration error:" +
                                     ee.getCause().getMessage() +
                                     "\n").initCause(ee.getCause());
                    } else {
                        throw (SecurityException) new
                            SecurityException
                                    ("Configuration error: " +
                                     ee.toString() +
                                     "\n").initCause(ee);
                    }
                }
            }
            return configuration;
        }
    }


    public static void setConfiguration(Configuration configuration) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null)
            sm.checkPermission(new AuthPermission("setLoginConfiguration"));
        Configuration.configuration = configuration;
    }


    public static Configuration getInstance(String type,
                                Configuration.Parameters params)
                throws NoSuchAlgorithmException {

        checkPermission(type);
        try {
            GetInstance.Instance instance = GetInstance.getInstance
                                                        ("Configuration",
                                                        ConfigurationSpi.class,
                                                        type,
                                                        params);
            return new ConfigDelegate((ConfigurationSpi)instance.impl,
                                                        instance.provider,
                                                        type,
                                                        params);
        } catch (NoSuchAlgorithmException nsae) {
            return handleException (nsae);
        }
    }


    public static Configuration getInstance(String type,
                                Configuration.Parameters params,
                                String provider)
                throws NoSuchProviderException, NoSuchAlgorithmException {

        if (provider == null || provider.length() == 0) {
            throw new IllegalArgumentException("missing provider");
        }

        checkPermission(type);
        try {
            GetInstance.Instance instance = GetInstance.getInstance
                                                        ("Configuration",
                                                        ConfigurationSpi.class,
                                                        type,
                                                        params,
                                                        provider);
            return new ConfigDelegate((ConfigurationSpi)instance.impl,
                                                        instance.provider,
                                                        type,
                                                        params);
        } catch (NoSuchAlgorithmException nsae) {
            return handleException (nsae);
        }
    }


    public static Configuration getInstance(String type,
                                Configuration.Parameters params,
                                Provider provider)
                throws NoSuchAlgorithmException {

        if (provider == null) {
            throw new IllegalArgumentException("missing provider");
        }

        checkPermission(type);
        try {
            GetInstance.Instance instance = GetInstance.getInstance
                                                        ("Configuration",
                                                        ConfigurationSpi.class,
                                                        type,
                                                        params,
                                                        provider);
            return new ConfigDelegate((ConfigurationSpi)instance.impl,
                                                        instance.provider,
                                                        type,
                                                        params);
        } catch (NoSuchAlgorithmException nsae) {
            return handleException (nsae);
        }
    }

    private static Configuration handleException(NoSuchAlgorithmException nsae)
                throws NoSuchAlgorithmException {
        Throwable cause = nsae.getCause();
        if (cause instanceof IllegalArgumentException) {
            throw (IllegalArgumentException)cause;
        }
        throw nsae;
    }


    public Provider getProvider() {
        return null;
    }


    public String getType() {
        return null;
    }


    public Configuration.Parameters getParameters() {
        return null;
    }


    public abstract AppConfigurationEntry[] getAppConfigurationEntry
                                                        (String name);


    public void refresh() { }


    private static class ConfigDelegate extends Configuration {

        private ConfigurationSpi spi;
        private Provider p;
        private String type;
        private Configuration.Parameters params;

        private ConfigDelegate(ConfigurationSpi spi, Provider p,
                        String type, Configuration.Parameters params) {
            this.spi = spi;
            this.p = p;
            this.type = type;
            this.params = params;
        }

        public String getType() { return type; }

        public Configuration.Parameters getParameters() { return params; }

        public Provider getProvider() { return p; }

        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            return spi.engineGetAppConfigurationEntry(name);
        }

        public void refresh() {
            spi.engineRefresh();
        }
    }


    public static interface Parameters { }
}
