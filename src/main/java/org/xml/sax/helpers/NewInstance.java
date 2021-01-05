
    static Object newInstance (ClassLoader classLoader, String className)
        throws ClassNotFoundException, IllegalAccessException,
            InstantiationException
    {

        boolean internal = false;
        if (System.getSecurityManager() != null) {
            if (className != null && className.startsWith(DEFAULT_PACKAGE)) {
                internal = true;
            }
        }

        Class driverClass;
        if (classLoader == null || internal) {
            driverClass = Class.forName(className);
        } else {
            driverClass = classLoader.loadClass(className);
        }
        return driverClass.newInstance();
    }

}
