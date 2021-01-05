
    public IIOException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
