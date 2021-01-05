
class YearMonthDurationDV extends DurationDV {

    public Object getActualValue(String content, ValidationContext context)
        throws InvalidDatatypeValueException {
        try {
            return parse(content, DurationDV.YEARMONTHDURATION_TYPE);
        }
        catch (Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "yearMonthDuration"});
        }
    }

    protected Duration getDuration(DateTimeData date) {
        int sign = 1;
        if ( date.year<0 || date.month<0) {
            sign = -1;
        }
        return datatypeFactory.newDuration(sign == 1,
                date.year != DatatypeConstants.FIELD_UNDEFINED?BigInteger.valueOf(sign*date.year):null,
                date.month != DatatypeConstants.FIELD_UNDEFINED?BigInteger.valueOf(sign*date.month):null,
                null,
                null,
                null,
                null);
    }
}
