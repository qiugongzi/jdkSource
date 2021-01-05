
    public void validate(String content, ValidationContext context) throws InvalidDatatypeValueException {
        if (!XMLChar.isValidNmtoken(content)) {
           throw new InvalidDatatypeValueException("NMTOKENInvalid", new Object[]{content});
       }
   }

}
