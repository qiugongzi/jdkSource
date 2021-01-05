
    private void check()
    {
        if (file == null)
            return;

        long lastMod = file.lastModified();
        if (lastMod > fileModified) {
            try {
                FileInputStream fileIS = new FileInputStream(file);
                savedProps.clear();
                savedProps.load(fileIS);
                fileIS.close();
                fileModified = lastMod;
            } catch (java.io.FileNotFoundException e) {
                System.err.println( CorbaResourceUtil.getText(
                    "bootstrap.filenotfound", file.getAbsolutePath()));
            } catch (java.io.IOException e) {
                System.err.println( CorbaResourceUtil.getText(
                    "bootstrap.exception",
                    file.getAbsolutePath(), e.toString()));
            }
        }
    }
}
