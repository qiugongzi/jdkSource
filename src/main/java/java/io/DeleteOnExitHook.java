,
                new Runnable() {
                    public void run() {
                       runHooks();
                    }
                }
        );
    }

    private DeleteOnExitHook() {}

    static synchronized void add(String file) {
        if(files == null) {

            throw new IllegalStateException("Shutdown in progress");
        }

        files.add(file);
    }

    static void runHooks() {
        LinkedHashSet<String> theFiles;

        synchronized (DeleteOnExitHook.class) {
            theFiles = files;
            files = null;
        }

        ArrayList<String> toBeDeleted = new ArrayList<>(theFiles);



        Collections.reverse(toBeDeleted);
        for (String filename : toBeDeleted) {
            (new File(filename)).delete();
        }
    }
}
