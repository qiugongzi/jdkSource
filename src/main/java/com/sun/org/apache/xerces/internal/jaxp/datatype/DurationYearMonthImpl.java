

package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.math.BigInteger;
import javax.xml.datatype.DatatypeConstants;




class DurationYearMonthImpl
        extends DurationImpl {



    public DurationYearMonthImpl(
        boolean isPositive,
        BigInteger years,
        BigInteger months) {

        super(isPositive, years, months, null, null, null, null);
        convertToCanonicalYearMonth();
    }

    protected DurationYearMonthImpl(
        final boolean isPositive,
        final int years,
        final int months) {

        this(isPositive,
            wrap(years),
            wrap(months));


    }



    protected DurationYearMonthImpl(long durationInMilliseconds) {

        super(durationInMilliseconds);
        convertToCanonicalYearMonth();
        days = null;
        hours = null;
        minutes = null;
        seconds = null;
        signum = calcSignum((signum<0)?false:true);
    }



    protected DurationYearMonthImpl(String lexicalRepresentation) {
        super(lexicalRepresentation);
        if (getDays() > 0 || getHours() > 0
                || getMinutes() > 0 || getSeconds() > 0) {
            throw new IllegalArgumentException(
                    "Trying to create an xdt:yearMonthDuration with an invalid"
                    + " lexical representation of \"" + lexicalRepresentation
                    + "\", data model requires PnYnM.");
        }
        convertToCanonicalYearMonth();
    }


    public int getValue() {
        return getYears() * 12 + getMonths();
    }

    private void convertToCanonicalYearMonth() {
        while (getMonths() >= 12)
        {
            months = months.subtract(BigInteger.valueOf(12));
            years = BigInteger.valueOf((long) getYears()).add(BigInteger.ONE);
        }
    }
}
