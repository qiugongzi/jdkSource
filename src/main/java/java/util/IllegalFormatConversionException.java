
    public Class<?> getArgumentClass() {
        return arg;
    }


    public String getMessage() {
        return String.format("%c != %s", c, arg.getName());
    }
}
