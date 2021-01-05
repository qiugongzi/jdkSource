

package java.sql;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.CopyOnWriteArrayList;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;



public class DriverManager {


    private final static CopyOnWriteArrayList<DriverInfo> registeredDrivers = new CopyOnWriteArrayList<>();
    private static volatile int loginTimeout = 0;
    private static volatile java.io.PrintWriter logWriter = null;
    private static volatile java.io.PrintStream logStream = null;
    private final static  Object logSync = new Object();


    private DriverManager(){}



    static {
        loadInitialDrivers();
        println("JDBC DriverManager initialized");
    }


    final static SQLPermission SET_LOG_PERMISSION =
        new SQLPermission("setLog");


    final static SQLPermission DEREGISTER_DRIVER_PERMISSION =
        new SQLPermission("deregisterDriver");

    public static java.io.PrintWriter getLogWriter() {
            return logWriter;
    }


    public static void setLogWriter(java.io.PrintWriter out) {

        SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            sec.checkPermission(SET_LOG_PERMISSION);
        }
            logStream = null;
            logWriter = out;
    }


    @CallerSensitive
    public static Connection getConnection(String url,
        java.util.Properties info) throws SQLException {

        return (getConnection(url, info, Reflection.getCallerClass()));
    }


    @CallerSensitive
    public static Connection getConnection(String url,
        String user, String password) throws SQLException {
        java.util.Properties info = new java.util.Properties();

        if (user != null) {
            info.put("user", user);
        }
        if (password != null) {
            info.put("password", password);
        }

        return (getConnection(url, info, Reflection.getCallerClass()));
    }


    @CallerSensitive
    public static Connection getConnection(String url)
        throws SQLException {

        java.util.Properties info = new java.util.Properties();
        return (getConnection(url, info, Reflection.getCallerClass()));
    }


    @CallerSensitive
    public static Driver getDriver(String url)
        throws SQLException {

        println("DriverManager.getDriver(\"" + url + "\")");

        Class<?> callerClass = Reflection.getCallerClass();

        for (DriverInfo aDriver : registeredDrivers) {
            if(isDriverAllowed(aDriver.driver, callerClass)) {
                try {
                    if(aDriver.driver.acceptsURL(url)) {
                        println("getDriver returning " + aDriver.driver.getClass().getName());
                    return (aDriver.driver);
                    }

                } catch(SQLException sqe) {
                    }
            } else {
                println("    skipping: " + aDriver.driver.getClass().getName());
            }

        }

        println("getDriver: no suitable driver");
        throw new SQLException("No suitable driver", "08001");
    }



    public static synchronized void registerDriver(java.sql.Driver driver)
        throws SQLException {

        registerDriver(driver, null);
    }


    public static synchronized void registerDriver(java.sql.Driver driver,
            DriverAction da)
        throws SQLException {


        if(driver != null) {
            registeredDrivers.addIfAbsent(new DriverInfo(driver, da));
        } else {
            throw new NullPointerException();
        }

        println("registerDriver: " + driver);

    }


    @CallerSensitive
    public static synchronized void deregisterDriver(Driver driver)
        throws SQLException {
        if (driver == null) {
            return;
        }

        SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            sec.checkPermission(DEREGISTER_DRIVER_PERMISSION);
        }

        println("DriverManager.deregisterDriver: " + driver);

        DriverInfo aDriver = new DriverInfo(driver, null);
        if(registeredDrivers.contains(aDriver)) {
            if (isDriverAllowed(driver, Reflection.getCallerClass())) {
                DriverInfo di = registeredDrivers.get(registeredDrivers.indexOf(aDriver));
                 if(di.action() != null) {
                     di.action().deregister();
                 }
                 registeredDrivers.remove(aDriver);
            } else {
                throw new SecurityException();
            }
        } else {
            println("    couldn't find driver to unload");
        }
    }


    @CallerSensitive
    public static java.util.Enumeration<Driver> getDrivers() {
        java.util.Vector<Driver> result = new java.util.Vector<>();

        Class<?> callerClass = Reflection.getCallerClass();

        for(DriverInfo aDriver : registeredDrivers) {
            if(isDriverAllowed(aDriver.driver, callerClass)) {
                result.addElement(aDriver.driver);
            } else {
                println("    skipping: " + aDriver.getClass().getName());
            }
        }
        return (result.elements());
    }



    public static void setLoginTimeout(int seconds) {
        loginTimeout = seconds;
    }


    public static int getLoginTimeout() {
        return (loginTimeout);
    }


    @Deprecated
    public static void setLogStream(java.io.PrintStream out) {

        SecurityManager sec = System.getSecurityManager();
        if (sec != null) {
            sec.checkPermission(SET_LOG_PERMISSION);
        }

        logStream = out;
        if ( out != null )
            logWriter = new java.io.PrintWriter(out);
        else
            logWriter = null;
    }


    @Deprecated
    public static java.io.PrintStream getLogStream() {
        return logStream;
    }


    public static void println(String message) {
        synchronized (logSync) {
            if (logWriter != null) {
                logWriter.println(message);

                logWriter.flush();
            }
        }
    }

    private static boolean isDriverAllowed(Driver driver, Class<?> caller) {
        ClassLoader callerCL = caller != null ? caller.getClassLoader() : null;
        return isDriverAllowed(driver, callerCL);
    }

    private static boolean isDriverAllowed(Driver driver, ClassLoader classLoader) {
        boolean result = false;
        if(driver != null) {
            Class<?> aClass = null;
            try {
                aClass =  Class.forName(driver.getClass().getName(), true, classLoader);
            } catch (Exception ex) {
                result = false;
            }

             result = ( aClass == driver.getClass() ) ? true : false;
        }

        return result;
    }

    private static void loadInitialDrivers() {
        String drivers;
        try {
            drivers = AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty("jdbc.drivers");
                }
            });
        } catch (Exception ex) {
            drivers = null;
        }
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {

                ServiceLoader<Driver> loadedDrivers = ServiceLoader.load(Driver.class);
                Iterator<Driver> driversIterator = loadedDrivers.iterator();


                try{
                    while(driversIterator.hasNext()) {
                        driversIterator.next();
                    }
                } catch(Throwable t) {
                }
                return null;
            }
        });

        println("DriverManager.initialize: jdbc.drivers = " + drivers);

        if (drivers == null || drivers.equals("")) {
            return;
        }
        String[] driversList = drivers.split(":");
        println("number of Drivers:" + driversList.length);
        for (String aDriver : driversList) {
            try {
                println("DriverManager.Initialize: loading " + aDriver);
                Class.forName(aDriver, true,
                        ClassLoader.getSystemClassLoader());
            } catch (Exception ex) {
                println("DriverManager.Initialize: load failed: " + ex);
            }
        }
    }


    private static Connection getConnection(
        String url, java.util.Properties info, Class<?> caller) throws SQLException {

        ClassLoader callerCL = caller != null ? caller.getClassLoader() : null;
        synchronized(DriverManager.class) {
            if (callerCL == null) {
                callerCL = Thread.currentThread().getContextClassLoader();
            }
        }

        if(url == null) {
            throw new SQLException("The url cannot be null", "08001");
        }

        println("DriverManager.getConnection(\"" + url + "\")");

        SQLException reason = null;

        for(DriverInfo aDriver : registeredDrivers) {
            if(isDriverAllowed(aDriver.driver, callerCL)) {
                try {
                    println("    trying " + aDriver.driver.getClass().getName());
                    Connection con = aDriver.driver.connect(url, info);
                    if (con != null) {
                        println("getConnection returning " + aDriver.driver.getClass().getName());
                        return (con);
                    }
                } catch (SQLException ex) {
                    if (reason == null) {
                        reason = ex;
                    }
                }

            } else {
                println("    skipping: " + aDriver.getClass().getName());
            }

        }

        if (reason != null)    {
            println("getConnection failed: " + reason);
            throw reason;
        }

        println("getConnection: no suitable driver found for "+ url);
        throw new SQLException("No suitable driver found for "+ url, "08001");
    }


}


class DriverInfo {

    final Driver driver;
    DriverAction da;
    DriverInfo(Driver driver, DriverAction action) {
        this.driver = driver;
        da = action;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof DriverInfo)
                && this.driver == ((DriverInfo) other).driver;
    }

    @Override
    public int hashCode() {
        return driver.hashCode();
    }

    @Override
    public String toString() {
        return ("driver[className="  + driver + "]");
    }

    DriverAction action() {
        return da;
    }
}
