
public final class TransletException extends SAXException {
    static final long serialVersionUID = -878916829521217293L;

    public TransletException() {
        super("Translet error");
    }

    public TransletException(Exception e) {
        super(e.toString());
        initCause(e);
    }

    public TransletException(String message) {
        super(message);
    }
}
