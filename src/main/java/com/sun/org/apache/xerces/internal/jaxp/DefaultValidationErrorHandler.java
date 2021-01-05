

class DefaultValidationErrorHandler extends DefaultHandler {
    static private int ERROR_COUNT_LIMIT = 10;
    private int errorCount = 0;
    private Locale locale = Locale.getDefault();

    public DefaultValidationErrorHandler(Locale locale) {
        this.locale = locale;
    }


    public void error(SAXParseException e) throws SAXException {
        if (errorCount >= ERROR_COUNT_LIMIT) {

            return;
        } else if (errorCount == 0) {

            System.err.println(SAXMessageFormatter.formatMessage(locale,
                        "errorHandlerNotSet", new Object [] {errorCount}));
        }

        String systemId = e.getSystemId();
        if (systemId == null) {
            systemId = "null";
        }
        String message = "Error: URI=" + systemId +
            " Line=" + e.getLineNumber() +
            ": " + e.getMessage();
        System.err.println(message);
        errorCount++;
    }
}
