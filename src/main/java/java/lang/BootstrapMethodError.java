
    public BootstrapMethodError(Throwable cause) {

        super(cause == null ? null : cause.toString());
        initCause(cause);
    }
}
