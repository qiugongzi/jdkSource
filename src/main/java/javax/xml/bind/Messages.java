
    static String format( String property, Object[] args ) {
        String text = ResourceBundle.getBundle(Messages.class.getName()).getString(property);
        return MessageFormat.format(text,args);
    }






    static final String PROVIDER_NOT_FOUND =
        "ContextFinder.ProviderNotFound";

    static final String COULD_NOT_INSTANTIATE =
        "ContextFinder.CouldNotInstantiate";

    static final String CANT_FIND_PROPERTIES_FILE =
        "ContextFinder.CantFindPropertiesFile";

    static final String CANT_MIX_PROVIDERS =
        "ContextFinder.CantMixProviders";

    static final String MISSING_PROPERTY =
        "ContextFinder.MissingProperty";

    static final String NO_PACKAGE_IN_CONTEXTPATH =
        "ContextFinder.NoPackageInContextPath";

    static final String NAME_VALUE =
        "PropertyException.NameValue";

    static final String CONVERTER_MUST_NOT_BE_NULL =
        "DatatypeConverter.ConverterMustNotBeNull";

    static final String ILLEGAL_CAST =
        "JAXBContext.IllegalCast";
}
