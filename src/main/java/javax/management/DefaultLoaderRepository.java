
    public static Class<?> loadClassWithout(ClassLoader loader,String className)
        throws ClassNotFoundException {
        return javax.management.loading.DefaultLoaderRepository.loadClassWithout(loader, className);
    }

 }
