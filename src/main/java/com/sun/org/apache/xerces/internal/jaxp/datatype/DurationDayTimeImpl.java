



package com.sun.org.apache.xerces.internal.jaxp.datatype;


import java.math.BigInteger;
import java.math.BigDecimal;
import javax.xml.datatype.DatatypeConstants;



class DurationDayTimeImpl
        extends DurationImpl {

    public DurationDayTimeImpl(
        boolean isPositive,
        BigInteger days,
        BigInteger hours,
        BigInteger minutes,
        BigDecimal seconds) {

        super(isPositive, null, null, days, hours, minutes, seconds);
        convertToCanonicalDayTime();
    }

    public DurationDayTimeImpl(
        boolean isPositive,
        int days,
        int hours,
        int minutes,
        int seconds) {

        this(
            isPositive,
            wrap(days),
            wrap(hours),
            wrap(minutes),
            (seconds != DatatypeConstants.FIELD_UNDEFINED ? new BigDecimal(String.valueOf(seconds)) : null));
    }


    protected DurationDayTimeImpl(String lexicalRepresentation) {
        super(lexicalRepresentation);

        if (getYears() > 0 || getMonths() > 0) {
            throw new IllegalArgumentException(
                    "Trying to create an xdt:dayTimeDuration with an invalid"
                    + " lexical representation of \"" + lexicalRepresentation
                    + "\", data model requires a format PnDTnHnMnS.");
        }

        convertToCanonicalDayTime();
    }

    protected DurationDayTimeImpl(final long durationInMilliseconds) {
            super(durationInMilliseconds);
            convertToCanonicalDayTime();
            years = null;
            months = null;
    }



    public float getValue() {
        float sec = (seconds==null)?0:seconds.floatValue();
        return (((((getDays() * 24) +
                    getHours()) * 60) +
                    getMinutes())*60) +
                    sec;
    }

    private void convertToCanonicalDayTime() {

        while (getSeconds() >= 60)
        {
            seconds = seconds.subtract(BigDecimal.valueOf(60));
            minutes = BigInteger.valueOf((long) getMinutes()).add(BigInteger.ONE);
        }

        while (getMinutes() >= 60)
        {
            minutes = minutes.subtract(BigInteger.valueOf(60));
            hours = BigInteger.valueOf((long) getHours()).add(BigInteger.ONE);
        }

        while (getHours() >= 24)
        {
            hours = hours.subtract(BigInteger.valueOf(24));
            days = BigInteger.valueOf((long) getDays()).add(BigInteger.ONE);
        }
    }

}
