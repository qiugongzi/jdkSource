
    static String format( String property, Object[] args ) {
        String text = ResourceBundle.getBundle(Messages.class.getName()).getString(property);
        return MessageFormat.format(text,args);
    }






    static final String INPUTSTREAM_NOT_NULL =
        "AbstractUnmarshallerImpl.ISNotNull";

    static final String MUST_BE_BOOLEAN =
        "AbstractMarshallerImpl.MustBeBoolean";

    static final String MUST_BE_STRING =
        "AbstractMarshallerImpl.MustBeString";

    static final String SEVERITY_MESSAGE =
        "DefaultValidationEventHandler.SeverityMessage";

    static final String LOCATION_UNAVAILABLE =
        "DefaultValidationEventHandler.LocationUnavailable";

    static final String UNRECOGNIZED_SEVERITY =
        "DefaultValidationEventHandler.UnrecognizedSeverity";

    static final String WARNING =
        "DefaultValidationEventHandler.Warning";

    static final String ERROR =
        "DefaultValidationEventHandler.Error";

    static final String FATAL_ERROR =
        "DefaultValidationEventHandler.FatalError";

    static final String ILLEGAL_SEVERITY =
        "ValidationEventImpl.IllegalSeverity";

    static final String MUST_NOT_BE_NULL =
        "Shared.MustNotBeNull";
}
