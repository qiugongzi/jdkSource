


package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.org.apache.xerces.internal.util.DatatypeMessageFormatter;


class DurationImpl
        extends Duration
        implements Serializable {


    private static final int FIELD_NUM = 6;


        private static final DatatypeConstants.Field[] FIELDS = new DatatypeConstants.Field[]{
                        DatatypeConstants.YEARS,
                        DatatypeConstants.MONTHS,
                        DatatypeConstants.DAYS,
                        DatatypeConstants.HOURS,
                        DatatypeConstants.MINUTES,
                        DatatypeConstants.SECONDS
                };


                private static final int[] FIELD_IDS = {
                                DatatypeConstants.YEARS.getId(),
                                DatatypeConstants.MONTHS.getId(),
                                DatatypeConstants.DAYS.getId(),
                                DatatypeConstants.HOURS.getId(),
                                DatatypeConstants.MINUTES.getId(),
                                DatatypeConstants.SECONDS.getId()
                        };


    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");


        private static final BigDecimal ZERO = BigDecimal.valueOf((long) 0);


    protected int signum;



    protected BigInteger years;


    protected BigInteger months;


    protected BigInteger days;


    protected BigInteger hours;


    protected BigInteger minutes;


    protected BigDecimal seconds;


        public int getSign() {

                return signum;
        }


    protected int calcSignum(boolean isPositive) {
        if ((years == null || years.signum() == 0)
            && (months == null || months.signum() == 0)
            && (days == null || days.signum() == 0)
            && (hours == null || hours.signum() == 0)
            && (minutes == null || minutes.signum() == 0)
            && (seconds == null || seconds.signum() == 0)) {
            return 0;
            }

            if (isPositive) {
                return 1;
            } else {
                return -1;
            }

    }


    protected DurationImpl(
        boolean isPositive,
        BigInteger years,
        BigInteger months,
        BigInteger days,
        BigInteger hours,
        BigInteger minutes,
        BigDecimal seconds) {

        this.years = years;
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;

        this.signum = calcSignum(isPositive);

        if (years == null
            && months == null
            && days == null
            && hours == null
            && minutes == null
            && seconds == null) {
            throw new IllegalArgumentException(
            DatatypeMessageFormatter.formatMessage(null, "AllFieldsNull", null)
            );
        }
        testNonNegative(years, DatatypeConstants.YEARS);
        testNonNegative(months, DatatypeConstants.MONTHS);
        testNonNegative(days, DatatypeConstants.DAYS);
        testNonNegative(hours, DatatypeConstants.HOURS);
        testNonNegative(minutes, DatatypeConstants.MINUTES);
        testNonNegative(seconds, DatatypeConstants.SECONDS);
    }


    protected static void testNonNegative(BigInteger n, DatatypeConstants.Field f) {
        if (n != null && n.signum() < 0) {
            throw new IllegalArgumentException(
                DatatypeMessageFormatter.formatMessage(null, "NegativeField", new Object[]{f.toString()})
            );
        }
    }


    protected static void testNonNegative(BigDecimal n, DatatypeConstants.Field f) {
        if (n != null && n.signum() < 0) {

            throw new IllegalArgumentException(
                DatatypeMessageFormatter.formatMessage(null, "NegativeField", new Object[]{f.toString()})
            );
        }
    }


    protected DurationImpl(
        final boolean isPositive,
        final int years,
        final int months,
        final int days,
        final int hours,
        final int minutes,
        final int seconds) {
        this(
            isPositive,
            wrap(years),
            wrap(months),
            wrap(days),
            wrap(hours),
            wrap(minutes),
            seconds != DatatypeConstants.FIELD_UNDEFINED ? new BigDecimal(String.valueOf(seconds)) : null);
    }


    protected static BigInteger wrap(final int i) {

        if (i == DatatypeConstants.FIELD_UNDEFINED) {
                return null;
        }

        return new BigInteger(String.valueOf(i));
    }


    protected DurationImpl(final long durationInMilliSeconds) {

        long l = durationInMilliSeconds;

        if (l > 0) {
            signum = 1;
        } else if (l < 0) {
            signum = -1;
            if (l == 0x8000000000000000L) {
                l++;
            }
            l *= -1;
        } else {
            signum = 0;
        }

        GregorianCalendar gregorianCalendar = new GregorianCalendar(GMT);

        gregorianCalendar.setTimeInMillis(l);

        long int2long = 0L;

        int2long = gregorianCalendar.get(Calendar.YEAR) - 1970;
        this.years = BigInteger.valueOf(int2long);

        int2long = gregorianCalendar.get(Calendar.MONTH);
        this.months = BigInteger.valueOf(int2long);

        int2long = gregorianCalendar.get(Calendar.DAY_OF_MONTH) - 1;
        this.days = BigInteger.valueOf(int2long);

        int2long = gregorianCalendar.get(Calendar.HOUR_OF_DAY);
        this.hours = BigInteger.valueOf(int2long);

        int2long = gregorianCalendar.get(Calendar.MINUTE);
        this.minutes = BigInteger.valueOf(int2long);

        int2long = (gregorianCalendar.get(Calendar.SECOND) * 1000)
                    + gregorianCalendar.get(Calendar.MILLISECOND);
        this.seconds = BigDecimal.valueOf(int2long, 3);
    }


    protected DurationImpl(String lexicalRepresentation)
        throws IllegalArgumentException {
        final String s = lexicalRepresentation;
        boolean positive;
        int[] idx = new int[1];
        int length = s.length();
        boolean timeRequired = false;

        if (lexicalRepresentation == null) {
            throw new NullPointerException();
        }

        idx[0] = 0;
        if (length != idx[0] && s.charAt(idx[0]) == '-') {
            idx[0]++;
            positive = false;
        } else {
            positive = true;
        }

        if (length != idx[0] && s.charAt(idx[0]++) != 'P') {
            throw new IllegalArgumentException(s); }


        int dateLen = 0;
        String[] dateParts = new String[3];
        int[] datePartsIndex = new int[3];
        while (length != idx[0]
            && isDigit(s.charAt(idx[0]))
            && dateLen < 3) {
            datePartsIndex[dateLen] = idx[0];
            dateParts[dateLen++] = parsePiece(s, idx);
        }

        if (length != idx[0]) {
            if (s.charAt(idx[0]++) == 'T') {
                timeRequired = true;
            } else {
                throw new IllegalArgumentException(s); }
        }

        int timeLen = 0;
        String[] timeParts = new String[3];
        int[] timePartsIndex = new int[3];
        while (length != idx[0]
            && isDigitOrPeriod(s.charAt(idx[0]))
            && timeLen < 3) {
            timePartsIndex[timeLen] = idx[0];
            timeParts[timeLen++] = parsePiece(s, idx);
        }

        if (timeRequired && timeLen == 0) {
            throw new IllegalArgumentException(s); }

        if (length != idx[0]) {
            throw new IllegalArgumentException(s); }
        if (dateLen == 0 && timeLen == 0) {
            throw new IllegalArgumentException(s); }

        organizeParts(s, dateParts, datePartsIndex, dateLen, "YMD");
        organizeParts(s, timeParts, timePartsIndex, timeLen, "HMS");

        years = parseBigInteger(s, dateParts[0], datePartsIndex[0]);
        months = parseBigInteger(s, dateParts[1], datePartsIndex[1]);
        days = parseBigInteger(s, dateParts[2], datePartsIndex[2]);
        hours = parseBigInteger(s, timeParts[0], timePartsIndex[0]);
        minutes = parseBigInteger(s, timeParts[1], timePartsIndex[1]);
        seconds = parseBigDecimal(s, timeParts[2], timePartsIndex[2]);
        signum = calcSignum(positive);
    }



    private static boolean isDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }


    private static boolean isDigitOrPeriod(char ch) {
        return isDigit(ch) || ch == '.';
    }


    private static String parsePiece(String whole, int[] idx)
        throws IllegalArgumentException {
        int start = idx[0];
        while (idx[0] < whole.length()
            && isDigitOrPeriod(whole.charAt(idx[0]))) {
            idx[0]++;
            }
        if (idx[0] == whole.length()) {
            throw new IllegalArgumentException(whole); }

        idx[0]++;

        return whole.substring(start, idx[0]);
    }


    private static void organizeParts(
        String whole,
        String[] parts,
        int[] partsIndex,
        int len,
        String tokens)
        throws IllegalArgumentException {

        int idx = tokens.length();
        for (int i = len - 1; i >= 0; i--) {
            int nidx =
                tokens.lastIndexOf(
                    parts[i].charAt(parts[i].length() - 1),
                    idx - 1);
            if (nidx == -1) {
                throw new IllegalArgumentException(whole);
                }

            for (int j = nidx + 1; j < idx; j++) {
                parts[j] = null;
            }
            idx = nidx;
            parts[idx] = parts[i];
            partsIndex[idx] = partsIndex[i];
        }
        for (idx--; idx >= 0; idx--) {
            parts[idx] = null;
        }
    }


    private static BigInteger parseBigInteger(
        String whole,
        String part,
        int index)
        throws IllegalArgumentException {
        if (part == null) {
            return null;
        }
        part = part.substring(0, part.length() - 1);
        return new BigInteger(part);
        }


    private static BigDecimal parseBigDecimal(
        String whole,
        String part,
        int index)
        throws IllegalArgumentException {
        if (part == null) {
            return null;
        }
        part = part.substring(0, part.length() - 1);
        return new BigDecimal(part);
        }


    private static final XMLGregorianCalendar[] TEST_POINTS = new XMLGregorianCalendar[] {
        XMLGregorianCalendarImpl.parse("1696-09-01T00:00:00Z"),
        XMLGregorianCalendarImpl.parse("1697-02-01T00:00:00Z"),
        XMLGregorianCalendarImpl.parse("1903-03-01T00:00:00Z"),
        XMLGregorianCalendarImpl.parse("1903-07-01T00:00:00Z")
    };


    public int compare(Duration rhs) {

        BigInteger maxintAsBigInteger = BigInteger.valueOf((long) Integer.MAX_VALUE);
        BigInteger minintAsBigInteger = BigInteger.valueOf((long) Integer.MIN_VALUE);

        if (years != null && years.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(
                        DatatypeMessageFormatter.formatMessage(null, "TooLarge",
                            new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.YEARS.toString(), years.toString()})
                                        );
        }
        if (months != null && months.compareTo(maxintAsBigInteger) == 1) {
                throw new UnsupportedOperationException(
                        DatatypeMessageFormatter.formatMessage(null, "TooLarge",
                            new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MONTHS.toString(), months.toString()})

                                        );
        }
        if (days != null && days.compareTo(maxintAsBigInteger) == 1) {
                throw new UnsupportedOperationException(
                        DatatypeMessageFormatter.formatMessage(null, "TooLarge",
                            new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.DAYS.toString(), days.toString()})

                                        );
        }
        if (hours != null && hours.compareTo(maxintAsBigInteger) == 1) {
                throw new UnsupportedOperationException(
                        DatatypeMessageFormatter.formatMessage(null, "TooLarge",
                            new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.HOURS.toString(), hours.toString()})

                                        );
        }
        if (minutes != null && minutes.compareTo(maxintAsBigInteger) == 1) {
                throw new UnsupportedOperationException(
                        DatatypeMessageFormatter.formatMessage(null, "TooLarge",
                            new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MINUTES.toString(), minutes.toString()})

                                        );
        }
        if (seconds != null && seconds.toBigInteger().compareTo(maxintAsBigInteger) == 1) {
                throw new UnsupportedOperationException(
                        DatatypeMessageFormatter.formatMessage(null, "TooLarge",
                            new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.SECONDS.toString(), seconds.toString()})

                                        );
        }

        BigInteger rhsYears = (BigInteger) rhs.getField(DatatypeConstants.YEARS);
        if (rhsYears != null && rhsYears.compareTo(maxintAsBigInteger) == 1) {
                throw new UnsupportedOperationException(
                        DatatypeMessageFormatter.formatMessage(null, "TooLarge",
                            new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.YEARS.toString(), rhsYears.toString()})

                                        );
        }
        BigInteger rhsMonths = (BigInteger) rhs.getField(DatatypeConstants.MONTHS);
        if (rhsMonths != null && rhsMonths.compareTo(maxintAsBigInteger) == 1) {
                throw new UnsupportedOperationException(
                        DatatypeMessageFormatter.formatMessage(null, "TooLarge",
                            new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MONTHS.toString(), rhsMonths.toString()})

                                        );
        }
        BigInteger rhsDays = (BigInteger) rhs.getField(DatatypeConstants.DAYS);
        if (rhsDays != null && rhsDays.compareTo(maxintAsBigInteger) == 1) {
                throw new UnsupportedOperationException(
                        DatatypeMessageFormatter.formatMessage(null, "TooLarge",
                            new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.DAYS.toString(), rhsDays.toString()})

                                        );
        }
        BigInteger rhsHours = (BigInteger) rhs.getField(DatatypeConstants.HOURS);
        if (rhsHours != null && rhsHours.compareTo(maxintAsBigInteger) == 1) {
                throw new UnsupportedOperationException(
                        DatatypeMessageFormatter.formatMessage(null, "TooLarge",
                            new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.HOURS.toString(), rhsHours.toString()})

                                        );
        }
        BigInteger rhsMinutes = (BigInteger) rhs.getField(DatatypeConstants.MINUTES);
        if (rhsMinutes != null && rhsMinutes.compareTo(maxintAsBigInteger) == 1) {
                throw new UnsupportedOperationException(
                        DatatypeMessageFormatter.formatMessage(null, "TooLarge",
                            new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MINUTES.toString(), rhsMinutes.toString()})

                                        );
        }
        BigDecimal rhsSecondsAsBigDecimal = (BigDecimal) rhs.getField(DatatypeConstants.SECONDS);
        BigInteger rhsSeconds = null;
        if ( rhsSecondsAsBigDecimal != null ) {
                rhsSeconds =  rhsSecondsAsBigDecimal.toBigInteger();
        }
        if (rhsSeconds != null && rhsSeconds.compareTo(maxintAsBigInteger) == 1) {
                throw new UnsupportedOperationException(
                        DatatypeMessageFormatter.formatMessage(null, "TooLarge",
                            new Object[]{this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.SECONDS.toString(), rhsSeconds.toString()})

                                        );
        }

        GregorianCalendar lhsCalendar = new GregorianCalendar(
                        1970,
                                1,
                                1,
                                0,
                                0,
                                0);
                lhsCalendar.add(GregorianCalendar.YEAR, getYears() * getSign());
                lhsCalendar.add(GregorianCalendar.MONTH, getMonths() * getSign());
                lhsCalendar.add(GregorianCalendar.DAY_OF_YEAR, getDays() * getSign());
                lhsCalendar.add(GregorianCalendar.HOUR_OF_DAY, getHours() * getSign());
                lhsCalendar.add(GregorianCalendar.MINUTE, getMinutes() * getSign());
                lhsCalendar.add(GregorianCalendar.SECOND, getSeconds() * getSign());

                GregorianCalendar rhsCalendar = new GregorianCalendar(
                                1970,
                                1,
                                1,
                                0,
                                0,
                                0);
                rhsCalendar.add(GregorianCalendar.YEAR, rhs.getYears() * rhs.getSign());
                rhsCalendar.add(GregorianCalendar.MONTH, rhs.getMonths() * rhs.getSign());
                rhsCalendar.add(GregorianCalendar.DAY_OF_YEAR, rhs.getDays() * rhs.getSign());
                rhsCalendar.add(GregorianCalendar.HOUR_OF_DAY, rhs.getHours() * rhs.getSign());
                rhsCalendar.add(GregorianCalendar.MINUTE, rhs.getMinutes() * rhs.getSign());
                rhsCalendar.add(GregorianCalendar.SECOND, rhs.getSeconds() * rhs.getSign());


                if (lhsCalendar.equals(rhsCalendar)) {
                        return DatatypeConstants.EQUAL;
                }

                return compareDates(this, rhs);
    }


    private int compareDates(Duration duration1, Duration duration2) {

        int resultA = DatatypeConstants.INDETERMINATE;
        int resultB = DatatypeConstants.INDETERMINATE;

        XMLGregorianCalendar tempA = (XMLGregorianCalendar)TEST_POINTS[0].clone();
        XMLGregorianCalendar tempB = (XMLGregorianCalendar)TEST_POINTS[0].clone();

        tempA.add(duration1);
        tempB.add(duration2);
        resultA =  tempA.compare(tempB);
        if ( resultA == DatatypeConstants.INDETERMINATE ) {
            return DatatypeConstants.INDETERMINATE;
        }

        tempA = (XMLGregorianCalendar)TEST_POINTS[1].clone();
        tempB = (XMLGregorianCalendar)TEST_POINTS[1].clone();

        tempA.add(duration1);
        tempB.add(duration2);
        resultB = tempA.compare(tempB);
        resultA = compareResults(resultA, resultB);
        if (resultA == DatatypeConstants.INDETERMINATE) {
            return DatatypeConstants.INDETERMINATE;
        }

        tempA = (XMLGregorianCalendar)TEST_POINTS[2].clone();
        tempB = (XMLGregorianCalendar)TEST_POINTS[2].clone();

        tempA.add(duration1);
        tempB.add(duration2);
        resultB = tempA.compare(tempB);
        resultA = compareResults(resultA, resultB);
        if (resultA == DatatypeConstants.INDETERMINATE) {
            return DatatypeConstants.INDETERMINATE;
        }

        tempA = (XMLGregorianCalendar)TEST_POINTS[3].clone();
        tempB = (XMLGregorianCalendar)TEST_POINTS[3].clone();

        tempA.add(duration1);
        tempB.add(duration2);
        resultB = tempA.compare(tempB);
        resultA = compareResults(resultA, resultB);

        return resultA;
    }

    private int compareResults(int resultA, int resultB){

      if ( resultB == DatatypeConstants.INDETERMINATE ) {
            return DatatypeConstants.INDETERMINATE;
        }
        else if ( resultA!=resultB) {
            return DatatypeConstants.INDETERMINATE;
        }
        return resultA;
    }


    public int hashCode() {
        Calendar cal = TEST_POINTS[0].toGregorianCalendar();
        this.addTo(cal);
        return (int) getCalendarTimeInMillis(cal);
    }


    public String toString() {
        StringBuffer buf = new StringBuffer();
        if (signum < 0) {
            buf.append('-');
        }
        buf.append('P');

        if (years != null) {
            buf.append(years + "Y");
        }
        if (months != null) {
            buf.append(months + "M");
        }
        if (days != null) {
            buf.append(days + "D");
        }

        if (hours != null || minutes != null || seconds != null) {
            buf.append('T');
            if (hours != null) {
                buf.append(hours + "H");
            }
            if (minutes != null) {
                buf.append(minutes + "M");
            }
            if (seconds != null) {
                buf.append(toString(seconds) + "S");
            }
        }

        return buf.toString();
    }


    private String toString(BigDecimal bd) {
        String intString = bd.unscaledValue().toString();
        int scale = bd.scale();

        if (scale == 0) {
            return intString;
        }


        StringBuffer buf;
        int insertionPoint = intString.length() - scale;
        if (insertionPoint == 0) {
            return "0." + intString;
        } else if (insertionPoint > 0) {
            buf = new StringBuffer(intString);
            buf.insert(insertionPoint, '.');
        } else {
            buf = new StringBuffer(3 - insertionPoint + intString.length());
            buf.append("0.");
            for (int i = 0; i < -insertionPoint; i++) {
                buf.append('0');
            }
            buf.append(intString);
        }
        return buf.toString();
    }


    public boolean isSet(DatatypeConstants.Field field) {

        if (field == null) {
            String methodName = "javax.xml.datatype.Duration" + "#isSet(DatatypeConstants.Field field)" ;
                throw new NullPointerException(
                DatatypeMessageFormatter.formatMessage(null, "FieldCannotBeNull", new Object[]{methodName})
                );
        }

        if (field == DatatypeConstants.YEARS) {
                        return years != null;
        }

                if (field == DatatypeConstants.MONTHS) {
                        return months != null;
                }

                if (field == DatatypeConstants.DAYS) {
                        return days != null;
                }

                if (field == DatatypeConstants.HOURS) {
                        return hours != null;
                }

                if (field == DatatypeConstants.MINUTES) {
                        return minutes != null;
                }

                if (field == DatatypeConstants.SECONDS) {
                        return seconds != null;
                }
        String methodName = "javax.xml.datatype.Duration" + "#isSet(DatatypeConstants.Field field)";

        throw new IllegalArgumentException(
            DatatypeMessageFormatter.formatMessage(null,"UnknownField", new Object[]{methodName, field.toString()})
                );

    }


    public Number getField(DatatypeConstants.Field field) {

                if (field == null) {
            String methodName = "javax.xml.datatype.Duration" + "#isSet(DatatypeConstants.Field field) " ;

                        throw new NullPointerException(
                DatatypeMessageFormatter.formatMessage(null,"FieldCannotBeNull", new Object[]{methodName})
                );
                }

                if (field == DatatypeConstants.YEARS) {
                        return years;
                }

                if (field == DatatypeConstants.MONTHS) {
                        return months;
                }

                if (field == DatatypeConstants.DAYS) {
                        return days;
                }

                if (field == DatatypeConstants.HOURS) {
                        return hours;
                }

                if (field == DatatypeConstants.MINUTES) {
                        return minutes;
                }

                if (field == DatatypeConstants.SECONDS) {
                        return seconds;
                }

        String methodName = "javax.xml.datatype.Duration" + "#(getSet(DatatypeConstants.Field field)";

        throw new IllegalArgumentException(
            DatatypeMessageFormatter.formatMessage(null,"UnknownField", new Object[]{methodName, field.toString()})
                );

    }


    public int getYears() {
        return getInt(DatatypeConstants.YEARS);
    }


    public int getMonths() {
        return getInt(DatatypeConstants.MONTHS);
    }


    public int getDays() {
        return getInt(DatatypeConstants.DAYS);
    }


    public int getHours() {
        return getInt(DatatypeConstants.HOURS);
    }


    public int getMinutes() {
        return getInt(DatatypeConstants.MINUTES);
    }


    public int getSeconds() {
        return getInt(DatatypeConstants.SECONDS);
    }


    private int getInt(DatatypeConstants.Field field) {
        Number n = getField(field);
        if (n == null) {
            return 0;
        } else {
            return n.intValue();
        }
    }


    public long getTimeInMillis(final Calendar startInstant) {
        Calendar cal = (Calendar) startInstant.clone();
        addTo(cal);
        return getCalendarTimeInMillis(cal)
                    - getCalendarTimeInMillis(startInstant);
    }


    public long getTimeInMillis(final Date startInstant) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(startInstant);
        this.addTo(cal);
        return getCalendarTimeInMillis(cal) - startInstant.getTime();
    }

public Duration normalizeWith(Calendar startTimeInstant) {

        Calendar c = (Calendar) startTimeInstant.clone();

        c.add(Calendar.YEAR, getYears() * signum);
        c.add(Calendar.MONTH, getMonths() * signum);
        c.add(Calendar.DAY_OF_MONTH, getDays() * signum);

        long diff = getCalendarTimeInMillis(c) - getCalendarTimeInMillis(startTimeInstant);
        int days = (int) (diff / (1000L * 60L * 60L * 24L));

        return new DurationImpl(
            days >= 0,
            null,
            null,
            wrap(Math.abs(days)),
            (BigInteger) getField(DatatypeConstants.HOURS),
            (BigInteger) getField(DatatypeConstants.MINUTES),
            (BigDecimal) getField(DatatypeConstants.SECONDS));
    }


    public Duration multiply(int factor) {
        return multiply(BigDecimal.valueOf(factor));
    }


    public Duration multiply(BigDecimal factor) {
        BigDecimal carry = ZERO;
        int factorSign = factor.signum();
        factor = factor.abs();

        BigDecimal[] buf = new BigDecimal[6];

        for (int i = 0; i < 5; i++) {
            BigDecimal bd = getFieldAsBigDecimal(FIELDS[i]);
            bd = bd.multiply(factor).add(carry);

            buf[i] = bd.setScale(0, BigDecimal.ROUND_DOWN);

            bd = bd.subtract(buf[i]);
            if (i == 1) {
                if (bd.signum() != 0) {
                    throw new IllegalStateException(); } else {
                    carry = ZERO;
                }
            } else {
                carry = bd.multiply(FACTORS[i]);
            }
        }

        if (seconds != null) {
            buf[5] = seconds.multiply(factor).add(carry);
        } else {
            buf[5] = carry;
        }

        return new DurationImpl(
            this.signum * factorSign >= 0,
            toBigInteger(buf[0], null == years),
            toBigInteger(buf[1], null == months),
            toBigInteger(buf[2], null == days),
            toBigInteger(buf[3], null == hours),
            toBigInteger(buf[4], null == minutes),
            (buf[5].signum() == 0 && seconds == null) ? null : buf[5]);
    }


    private BigDecimal getFieldAsBigDecimal(DatatypeConstants.Field f) {
        if (f == DatatypeConstants.SECONDS) {
            if (seconds != null) {
                return seconds;
            } else {
                return ZERO;
            }
        } else {
            BigInteger bi = (BigInteger) getField(f);
            if (bi == null) {
                return ZERO;
            } else {
                return new BigDecimal(bi);
            }
        }
    }


    private static BigInteger toBigInteger(
        BigDecimal value,
        boolean canBeNull) {
        if (canBeNull && value.signum() == 0) {
            return null;
        } else {
            return value.unscaledValue();
        }
    }


    private static final BigDecimal[] FACTORS = new BigDecimal[]{
        BigDecimal.valueOf(12),
        null,
        BigDecimal.valueOf(24),
        BigDecimal.valueOf(60),
        BigDecimal.valueOf(60)
    };


    public Duration add(final Duration rhs) {
        Duration lhs = this;
        BigDecimal[] buf = new BigDecimal[6];

        buf[0] = sanitize((BigInteger) lhs.getField(DatatypeConstants.YEARS),
                lhs.getSign()).add(sanitize((BigInteger) rhs.getField(DatatypeConstants.YEARS),  rhs.getSign()));
        buf[1] = sanitize((BigInteger) lhs.getField(DatatypeConstants.MONTHS),
                lhs.getSign()).add(sanitize((BigInteger) rhs.getField(DatatypeConstants.MONTHS), rhs.getSign()));
        buf[2] = sanitize((BigInteger) lhs.getField(DatatypeConstants.DAYS),
                lhs.getSign()).add(sanitize((BigInteger) rhs.getField(DatatypeConstants.DAYS),   rhs.getSign()));
        buf[3] = sanitize((BigInteger) lhs.getField(DatatypeConstants.HOURS),
                lhs.getSign()).add(sanitize((BigInteger) rhs.getField(DatatypeConstants.HOURS),  rhs.getSign()));
        buf[4] = sanitize((BigInteger) lhs.getField(DatatypeConstants.MINUTES),
                lhs.getSign()).add(sanitize((BigInteger) rhs.getField(DatatypeConstants.MINUTES), rhs.getSign()));
        buf[5] = sanitize((BigDecimal) lhs.getField(DatatypeConstants.SECONDS),
                lhs.getSign()).add(sanitize((BigDecimal) rhs.getField(DatatypeConstants.SECONDS), rhs.getSign()));

        alignSigns(buf, 0, 2); alignSigns(buf, 2, 6); int s = 0;
        for (int i = 0; i < 6; i++) {
            if (s * buf[i].signum() < 0) {
                throw new IllegalStateException();
            }
            if (s == 0) {
                s = buf[i].signum();
            }
        }

        return new DurationImpl(
            s >= 0,
            toBigInteger(sanitize(buf[0], s),
                lhs.getField(DatatypeConstants.YEARS) == null && rhs.getField(DatatypeConstants.YEARS) == null),
            toBigInteger(sanitize(buf[1], s),
                lhs.getField(DatatypeConstants.MONTHS) == null && rhs.getField(DatatypeConstants.MONTHS) == null),
            toBigInteger(sanitize(buf[2], s),
                lhs.getField(DatatypeConstants.DAYS) == null && rhs.getField(DatatypeConstants.DAYS) == null),
            toBigInteger(sanitize(buf[3], s),
                lhs.getField(DatatypeConstants.HOURS) == null && rhs.getField(DatatypeConstants.HOURS) == null),
            toBigInteger(sanitize(buf[4], s),
                lhs.getField(DatatypeConstants.MINUTES) == null && rhs.getField(DatatypeConstants.MINUTES) == null),
             (buf[5].signum() == 0
             && lhs.getField(DatatypeConstants.SECONDS) == null
             && rhs.getField(DatatypeConstants.SECONDS) == null) ? null : sanitize(buf[5], s));
    }

    private static void alignSigns(BigDecimal[] buf, int start, int end) {
        boolean touched;

        do { touched = false;
            int s = 0; for (int i = start; i < end; i++) {
                if (s * buf[i].signum() < 0) {
                    touched = true;

                    BigDecimal borrow =
                        buf[i].abs().divide(
                            FACTORS[i - 1],
                            BigDecimal.ROUND_UP);
                    if (buf[i].signum() > 0) {
                        borrow = borrow.negate();
                    }

                    buf[i - 1] = buf[i - 1].subtract(borrow);
                    buf[i] = buf[i].add(borrow.multiply(FACTORS[i - 1]));
                }
                if (buf[i].signum() != 0) {
                    s = buf[i].signum();
                }
            }
        } while (touched);
    }


    private static BigDecimal sanitize(BigInteger value, int signum) {
        if (signum == 0 || value == null) {
            return ZERO;
        }
        if (signum > 0) {
            return new BigDecimal(value);
        }
        return new BigDecimal(value.negate());
    }


    static BigDecimal sanitize(BigDecimal value, int signum) {
        if (signum == 0 || value == null) {
            return ZERO;
        }
        if (signum > 0) {
            return value;
        }
        return value.negate();
    }


    public Duration subtract(final Duration rhs) {
        return add(rhs.negate());
    }


    public Duration negate() {
        return new DurationImpl(
            signum <= 0,
            years,
            months,
            days,
            hours,
            minutes,
            seconds);
    }


    public int signum() {
        return signum;
    }



    public void addTo(Calendar calendar) {
        calendar.add(Calendar.YEAR, getYears() * signum);
        calendar.add(Calendar.MONTH, getMonths() * signum);
        calendar.add(Calendar.DAY_OF_MONTH, getDays() * signum);
        calendar.add(Calendar.HOUR, getHours() * signum);
        calendar.add(Calendar.MINUTE, getMinutes() * signum);
        calendar.add(Calendar.SECOND, getSeconds() * signum);

        if (seconds != null) {
            BigDecimal fraction =
                seconds.subtract(seconds.setScale(0, BigDecimal.ROUND_DOWN));
            int millisec = fraction.movePointRight(3).intValue();
            calendar.add(Calendar.MILLISECOND, millisec * signum);
        }
    }


    public void addTo(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date); this.addTo(cal);
        date.setTime(getCalendarTimeInMillis(cal));
    }


    private static final long serialVersionUID = 1L;


    private Object writeReplace() throws IOException {
        return new DurationStream(this.toString());
    }


    private static class DurationStream implements Serializable {
        private final String lexical;

        private DurationStream(String _lexical) {
            this.lexical = _lexical;
        }

        private Object readResolve() throws ObjectStreamException {
            return new DurationImpl(lexical);
            }

        private static final long serialVersionUID = 1L;
    }


    private static long getCalendarTimeInMillis(Calendar cal) {
        return cal.getTime().getTime();
    }
}
