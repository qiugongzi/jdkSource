
    public Object run() {
        String value = System.getProperty(theProp);
        return (value == null) ? defaultVal : value;
    }
}
