
    private FileNotFoundException(String path, String reason) {
        super(path + ((reason == null)
                      ? ""
                      : " (" + reason + ")"));
    }

}
