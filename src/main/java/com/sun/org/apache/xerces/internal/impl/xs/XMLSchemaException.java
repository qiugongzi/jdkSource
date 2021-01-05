
    static final long serialVersionUID = -9096984648537046218L;


    String key;
    Object[] args;


    public XMLSchemaException(String key, Object[] args) {
        this.key = key;
        this.args = args;
    }

    public String getKey() {
        return key;
    }

    public Object[] getArgs() {
        return args;
    }

}
