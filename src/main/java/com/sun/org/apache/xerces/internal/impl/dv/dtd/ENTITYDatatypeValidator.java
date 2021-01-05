
    public void validate(String content, ValidationContext context) throws InvalidDatatypeValueException {

        if (!context.isEntityUnparsed(content))
            throw new InvalidDatatypeValueException("ENTITYNotUnparsed", new Object[]{content});

    }

}
