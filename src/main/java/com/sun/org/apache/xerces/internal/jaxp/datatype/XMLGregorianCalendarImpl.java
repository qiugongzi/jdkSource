

package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import com.sun.org.apache.xerces.internal.util.DatatypeMessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;



public class XMLGregorianCalendarImpl
        extends XMLGregorianCalendar
        implements Serializable, Cloneable {


    private BigInteger eon = null;


    private int year = DatatypeConstants.FIELD_UNDEFINED;


    private int month = DatatypeConstants.FIELD_UNDEFINED;


    private int day = DatatypeConstants.FIELD_UNDEFINED;


    private int timezone = DatatypeConstants.FIELD_UNDEFINED;


    private int hour = DatatypeConstants.FIELD_UNDEFINED;


    private int minute = DatatypeConstants.FIELD_UNDEFINED;


    private int second = DatatypeConstants.FIELD_UNDEFINED ;


    private BigDecimal fractionalSecond = null;


    private static final BigInteger BILLION = new BigInteger("1000000000");


    private static final Date PURE_GREGORIAN_CHANGE =
        new Date(Long.MIN_VALUE);


    private static final int YEAR   = 0;


    private static final int MONTH  = 1;


    private static final int DAY    = 2;


    private static final int HOUR   = 3;


    private static final int MINUTE = 4;


    private static final int SECOND = 5;


    private static final int MILLISECOND = 6;


    private static final int TIMEZONE = 7;



    private static final String FIELD_NAME[] = {
        "Year",
        "Month",
        "Day",
        "Hour",
        "Minute",
        "Second",
        "Millisecond",
        "Timezone"
    };


    private static final long serialVersionUID = 1L;


    public static final XMLGregorianCalendar LEAP_YEAR_DEFAULT =
                createDateTime(
                        400,  DatatypeConstants.JANUARY,  1,  0,  0,  0,  DatatypeConstants.FIELD_UNDEFINED,  DatatypeConstants.FIELD_UNDEFINED );

    protected XMLGregorianCalendarImpl(String lexicalRepresentation)
            throws IllegalArgumentException {

        String format = null;
        String lexRep = lexicalRepresentation;
        final int NOT_FOUND = -1;
        int lexRepLength = lexRep.length();

        if (lexRep.indexOf('T') != NOT_FOUND) {
            format = "%Y-%M-%DT%h:%m:%s" + "%z";
        } else if (lexRepLength >= 3 && lexRep.charAt(2) == ':') {
            format = "%h:%m:%s" + "%z";
        } else if (lexRep.startsWith("--")) {
            if (lexRepLength >= 3 && lexRep.charAt(2) == '-') {
                format = "---%D" + "%z";
            } else if (lexRepLength == 4     || lexRepLength == 5     || lexRepLength == 10) { format = "--%M" + "%z";
            } else {
                format = "--%M-%D" + "%z";
            }
        } else {
            int countSeparator = 0;

            int timezoneOffset = lexRep.indexOf(':');
            if (timezoneOffset != NOT_FOUND) {

                lexRepLength -= 6;
            }

            for (int i = 1; i < lexRepLength; i++) {
                if (lexRep.charAt(i) == '-') {
                    countSeparator++;
                }
            }
            if (countSeparator == 0) {
                format = "%Y" + "%z";
            } else if (countSeparator == 1) {
                format = "%Y-%M" + "%z";
            } else {
                format = "%Y-%M-%D" + "%z";
            }
        }
        Parser p = new Parser(format, lexRep);
        p.parse();

        if (!isValid()) {
            throw new IllegalArgumentException(
                    DatatypeMessageFormatter.formatMessage(null, "InvalidXGCRepresentation", new Object[]{lexicalRepresentation})
                    );
        }
    }


    public XMLGregorianCalendarImpl() {

        }


    protected XMLGregorianCalendarImpl(
        BigInteger year,
        int month,
        int day,
        int hour,
        int minute,
        int second,
        BigDecimal fractionalSecond,
        int timezone) {

                setYear(year);
        setMonth(month);
        setDay(day);
        setTime(hour, minute, second, fractionalSecond);
                setTimezone(timezone);

                if (!isValid()) {

            throw new IllegalArgumentException(
                DatatypeMessageFormatter.formatMessage(null,
                    "InvalidXGCValue-fractional",
                    new Object[] { year, new Integer(month), new Integer(day),
                    new Integer(hour), new Integer(minute), new Integer(second),
                    fractionalSecond, new Integer(timezone)})
                        );



                }

    }


    private XMLGregorianCalendarImpl(
        int year,
        int month,
        int day,
        int hour,
        int minute,
        int second,
                int millisecond,
        int timezone) {

                setYear(year);
        setMonth(month);
        setDay(day);
        setTime(hour, minute, second);
                setTimezone(timezone);
                setMillisecond(millisecond);

                if (!isValid()) {

            throw new IllegalArgumentException(
                DatatypeMessageFormatter.formatMessage(null,
                "InvalidXGCValue-milli",
                new Object[] { new Integer(year), new Integer(month), new Integer(day),
                new Integer(hour), new Integer(minute), new Integer(second),
                new Integer(millisecond), new Integer(timezone)})
                        );


                }
    }


    public XMLGregorianCalendarImpl(GregorianCalendar cal) {

        int year = cal.get(Calendar.YEAR);
        if (cal.get(Calendar.ERA) == GregorianCalendar.BC) {
            year = -year;
        }
        this.setYear(year);

        this.setMonth(cal.get(Calendar.MONTH) + 1);
        this.setDay(cal.get(Calendar.DAY_OF_MONTH));
        this.setTime(
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                cal.get(Calendar.MILLISECOND));

        int offsetInMinutes = (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)) / (60 * 1000);
        this.setTimezone(offsetInMinutes);
    }

    public static XMLGregorianCalendar createDateTime(
        BigInteger year,
        int month,
        int day,
        int hours,
        int minutes,
        int seconds,
        BigDecimal fractionalSecond,
        int timezone) {

        return new XMLGregorianCalendarImpl(
            year,
            month,
            day,
            hours,
            minutes,
            seconds,
            fractionalSecond,
            timezone);
    }


    public static XMLGregorianCalendar createDateTime(
        int year,
        int month,
        int day,
        int hour,
        int minute,
        int second) {

        return new XMLGregorianCalendarImpl(
            year,
            month,
            day,
            hour,
            minute,
            second,
            DatatypeConstants.FIELD_UNDEFINED,  DatatypeConstants.FIELD_UNDEFINED );
    }


    public static XMLGregorianCalendar createDateTime(
        int year,
        int month,
        int day,
        int hours,
        int minutes,
        int seconds,
        int milliseconds,
        int timezone) {

        return new XMLGregorianCalendarImpl(
            year,
            month,
            day,
            hours,
            minutes,
            seconds,
            milliseconds,
            timezone);
    }


    public static XMLGregorianCalendar createDate(
        int year,
        int month,
        int day,
        int timezone) {

        return new XMLGregorianCalendarImpl(
            year,
            month,
            day,
            DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, timezone);
    }


    public static XMLGregorianCalendar createTime(
        int hours,
        int minutes,
        int seconds,
                int timezone) {

                return new XMLGregorianCalendarImpl(
                        DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, hours,
                        minutes,
                        seconds,
                        DatatypeConstants.FIELD_UNDEFINED, timezone);
    }


    public static XMLGregorianCalendar createTime(
        int hours,
        int minutes,
        int seconds,
        BigDecimal fractionalSecond,
        int timezone) {

        return new XMLGregorianCalendarImpl(
            null,            DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, hours,
            minutes,
            seconds,
            fractionalSecond,
            timezone);
    }


    public static XMLGregorianCalendar createTime(
        int hours,
        int minutes,
        int seconds,
        int milliseconds,
        int timezone) {

        return new XMLGregorianCalendarImpl(
                DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, hours,
                minutes,
                seconds,
                milliseconds,
                timezone);
    }

    public BigInteger getEon() {
           return eon;
    }


    public int getYear() {
           return year;
    }


    public BigInteger getEonAndYear() {

                if (year != DatatypeConstants.FIELD_UNDEFINED
                        && eon != null) {

                        return eon.add(BigInteger.valueOf((long) year));
                }

                if (year != DatatypeConstants.FIELD_UNDEFINED
                        && eon == null) {

                        return BigInteger.valueOf((long) year);
                }

        return null;
    }


    public int getMonth() {
        return month;
    }


    public int getDay() {
        return day;
    }


    public int getTimezone() {
        return timezone;
    }


    public int getHour() {
        return hour;
    }


    public int getMinute() {
        return minute;
    }


    public int getSecond() {
           return second;
    }


    private BigDecimal getSeconds() {
        if (second == DatatypeConstants.FIELD_UNDEFINED) {
            return DECIMAL_ZERO;
        }
        BigDecimal result = BigDecimal.valueOf((long) second);
        if (fractionalSecond != null) {
            return result.add(fractionalSecond);
        } else {
            return result;
        }
    }



    public int getMillisecond() {
        if (fractionalSecond == null) {
            return DatatypeConstants.FIELD_UNDEFINED;
        } else {
            return fractionalSecond.movePointRight(3).intValue();
        }
    }


    public BigDecimal getFractionalSecond() {
           return fractionalSecond;
    }

    public void setYear(BigInteger year) {
        if (year == null) {
            this.eon = null;
            this.year = DatatypeConstants.FIELD_UNDEFINED;
        } else {
            BigInteger temp = year.remainder(BILLION);
            this.year = temp.intValue();
            setEon(year.subtract(temp));
        }
    }


    public void setYear(int year) {
        if (year == DatatypeConstants.FIELD_UNDEFINED) {
            this.year = DatatypeConstants.FIELD_UNDEFINED;
            this.eon = null;
        } else if (Math.abs(year) < BILLION.intValue()) {
            this.year = year;
            this.eon = null;
        } else {
            BigInteger theYear = BigInteger.valueOf((long) year);
            BigInteger remainder = theYear.remainder(BILLION);
            this.year = remainder.intValue();
            setEon(theYear.subtract(remainder));
        }
    }


    private void setEon(BigInteger eon) {
        if (eon != null && eon.compareTo(BigInteger.ZERO) == 0) {
            this.eon = null;
        } else {
            this.eon = eon;
        }
    }


    public void setMonth(int month) {
        if(month<DatatypeConstants.JANUARY || DatatypeConstants.DECEMBER<month)
            if(month!=DatatypeConstants.FIELD_UNDEFINED)
                invalidFieldValue(MONTH, month);
        this.month = month;
    }


    public void setDay(int day) {
        if(day<1 || 31<day)
            if(day!=DatatypeConstants.FIELD_UNDEFINED)
                invalidFieldValue(DAY,day);
        this.day = day;
    }


    public void setTimezone(int offset) {
            if(offset<-14*60 || 14*60<offset)
            if(offset!=DatatypeConstants.FIELD_UNDEFINED)
                invalidFieldValue(TIMEZONE,offset);
        this.timezone = offset;
    }


    public void setTime(int hour, int minute, int second) {
        setTime(hour, minute, second, null);
    }

    private void invalidFieldValue(int field, int value) {
        throw new IllegalArgumentException(
            DatatypeMessageFormatter.formatMessage(null, "InvalidFieldValue",
                new Object[]{ new Integer(value), FIELD_NAME[field]})
        );
    }

    private void testHour() {

        if (getHour() == 24) {
            if (getMinute() != 0
                    || getSecond() != 0) {
                invalidFieldValue(HOUR, getHour());
            }
            setHour(0, false);
            add(new DurationImpl(true, 0, 0, 1, 0, 0, 0));
        }
    }

    public void setHour(int hour) {

        setHour(hour, true);
    }

    private void setHour(int hour, boolean validate) {

        if (hour < 0 || hour > 24) {
            if (hour != DatatypeConstants.FIELD_UNDEFINED) {
                invalidFieldValue(HOUR, hour);
            }
        }

        this.hour = hour;

        if (validate) {
            testHour();
        }
    }

    public void setMinute(int minute) {
        if(minute<0 || 59<minute)
            if(minute!=DatatypeConstants.FIELD_UNDEFINED)
                invalidFieldValue(MINUTE, minute);
        this.minute = minute;
    }

    public void setSecond(int second) {
        if(second<0 || 60<second)   if(second!=DatatypeConstants.FIELD_UNDEFINED)
                invalidFieldValue(SECOND, second);
        this.second  = second;
    }


    public void setTime(
            int hour,
            int minute,
            int second,
            BigDecimal fractional) {

        setHour(hour, false);

        setMinute(minute);
        if (second != 60) {
            setSecond(second);
        } else if ((hour == 23 && minute == 59) || (hour == 0 && minute == 0)) {
            setSecond(second);
        } else {
            invalidFieldValue(SECOND, second);
        }

        setFractionalSecond(fractional);

        testHour();
    }



    public void setTime(int hour, int minute, int second, int millisecond) {

        setHour(hour, false);

        setMinute(minute);
        if (second != 60) {
            setSecond(second);
        } else if ((hour == 23 && minute == 59) || (hour == 0 && minute == 0)) {
            setSecond(second);
        } else {
            invalidFieldValue(SECOND, second);
        }
        setMillisecond(millisecond);

        testHour();
    }

    public int compare(XMLGregorianCalendar rhs) {

        XMLGregorianCalendar lhs = this;

        int result = DatatypeConstants.INDETERMINATE;
        XMLGregorianCalendarImpl P = (XMLGregorianCalendarImpl) lhs;
        XMLGregorianCalendarImpl Q = (XMLGregorianCalendarImpl) rhs;

        if (P.getTimezone() == Q.getTimezone()) {
            return internalCompare(P, Q);

        } else if (P.getTimezone() != DatatypeConstants.FIELD_UNDEFINED &&
                Q.getTimezone() != DatatypeConstants.FIELD_UNDEFINED) {

            P = (XMLGregorianCalendarImpl) P.normalize();
            Q = (XMLGregorianCalendarImpl) Q.normalize();
            return internalCompare(P, Q);
        } else if (P.getTimezone() != DatatypeConstants.FIELD_UNDEFINED) {

            if (P.getTimezone() != 0) {
                P = (XMLGregorianCalendarImpl) P.normalize();
            }

            XMLGregorianCalendar MinQ = Q.normalizeToTimezone(DatatypeConstants.MIN_TIMEZONE_OFFSET);
            result = internalCompare(P, MinQ);
            if (result == DatatypeConstants.LESSER) {
                return result;
            }

            XMLGregorianCalendar MaxQ = Q.normalizeToTimezone(DatatypeConstants.MAX_TIMEZONE_OFFSET);
            result = internalCompare(P, MaxQ);
            if (result == DatatypeConstants.GREATER) {
                return result;
            } else {
                return DatatypeConstants.INDETERMINATE;
            }
        } else { if (Q.getTimezone() != 0) {
                Q = (XMLGregorianCalendarImpl) Q.normalizeToTimezone(Q.getTimezone());
            }

            XMLGregorianCalendar MaxP = P.normalizeToTimezone(DatatypeConstants.MAX_TIMEZONE_OFFSET);
            result = internalCompare(MaxP, Q);
            if (result == DatatypeConstants.LESSER) {
                return result;
            }

            XMLGregorianCalendar MinP = P.normalizeToTimezone(DatatypeConstants.MIN_TIMEZONE_OFFSET);
            result = internalCompare(MinP, Q);
            if (result == DatatypeConstants.GREATER) {
                return result;
            } else {
                return DatatypeConstants.INDETERMINATE;
            }
        }
    }


    public XMLGregorianCalendar normalize() {

        XMLGregorianCalendar normalized = normalizeToTimezone(timezone);

        if (getTimezone() == DatatypeConstants.FIELD_UNDEFINED) {
            normalized.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        }

        if (getMillisecond() == DatatypeConstants.FIELD_UNDEFINED) {
            normalized.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        }

        return normalized;
    }


    private XMLGregorianCalendar normalizeToTimezone(int timezone) {

        int minutes = timezone;
        XMLGregorianCalendar result = (XMLGregorianCalendar) this.clone();

        minutes = -minutes;
        Duration d = new DurationImpl(minutes >= 0, 0, 0, 0, 0, minutes < 0 ? -minutes : minutes, 0  );
        result.add(d);

        result.setTimezone(0);
        return result;
    }


    private static int internalCompare(XMLGregorianCalendar P,
                                       XMLGregorianCalendar Q) {

        int result;

        if (P.getEon() == Q.getEon()) {

            result = compareField(P.getYear(), Q.getYear());
            if (result != DatatypeConstants.EQUAL) {
                return result;
            }
        } else {
            result = compareField(P.getEonAndYear(), Q.getEonAndYear());
            if (result != DatatypeConstants.EQUAL) {
                return result;
            }
        }

        result = compareField(P.getMonth(), Q.getMonth());
        if (result != DatatypeConstants.EQUAL) {
            return result;
        }

        result = compareField(P.getDay(), Q.getDay());
        if (result != DatatypeConstants.EQUAL) {
            return result;
        }

        result = compareField(P.getHour(), Q.getHour());
        if (result != DatatypeConstants.EQUAL) {
            return result;
        }

        result = compareField(P.getMinute(), Q.getMinute());
        if (result != DatatypeConstants.EQUAL) {
            return result;
        }
        result = compareField(P.getSecond(), Q.getSecond());
        if (result != DatatypeConstants.EQUAL) {
            return result;
        }

        result = compareField(P.getFractionalSecond(), Q.getFractionalSecond());
        return result;
    }


    private static int compareField(int Pfield, int Qfield) {
        if (Pfield == Qfield) {

            return DatatypeConstants.EQUAL;
        } else {
            if (Pfield == DatatypeConstants.FIELD_UNDEFINED || Qfield == DatatypeConstants.FIELD_UNDEFINED) {
                return DatatypeConstants.INDETERMINATE;
            } else {
                return (Pfield < Qfield ? DatatypeConstants.LESSER : DatatypeConstants.GREATER);
            }
        }
    }

    private static int compareField(BigInteger Pfield, BigInteger Qfield) {
        if (Pfield == null) {
            return (Qfield == null ? DatatypeConstants.EQUAL : DatatypeConstants.INDETERMINATE);
        }
        if (Qfield == null) {
            return DatatypeConstants.INDETERMINATE;
        }
        return Pfield.compareTo(Qfield);
    }

    private static int compareField(BigDecimal Pfield, BigDecimal Qfield) {
        if (Pfield == Qfield) {
            return DatatypeConstants.EQUAL;
        }

        if (Pfield == null) {
            Pfield = DECIMAL_ZERO;
        }

        if (Qfield == null) {
            Qfield = DECIMAL_ZERO;
        }

        return Pfield.compareTo(Qfield);
    }


    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof XMLGregorianCalendar)) {
            return false;
        }
        return compare((XMLGregorianCalendar) obj) == DatatypeConstants.EQUAL;
    }


    public int hashCode() {

        int timezone = getTimezone();
        if (timezone == DatatypeConstants.FIELD_UNDEFINED) {
            timezone = 0;
        }
        XMLGregorianCalendar gc = this;
        if (timezone != 0) {
            gc = this.normalizeToTimezone(getTimezone());
        }
        return gc.getYear() + gc.getMonth() + gc.getDay() +
                gc.getHour() + gc.getMinute() + gc.getSecond();
    }



    public static XMLGregorianCalendar parse(String lexicalRepresentation) {

                return new XMLGregorianCalendarImpl(lexicalRepresentation);
    }


    public String toXMLFormat() {

        QName typekind = getXMLSchemaType();

        String formatString = null;
        if (typekind == DatatypeConstants.DATETIME) {
            formatString = "%Y-%M-%DT%h:%m:%s" + "%z";
        } else if (typekind == DatatypeConstants.DATE) {
            formatString = "%Y-%M-%D" + "%z";
        } else if (typekind == DatatypeConstants.TIME) {
            formatString = "%h:%m:%s" + "%z";
        } else if (typekind == DatatypeConstants.GMONTH) {
            formatString = "--%M" + "%z";
        } else if (typekind == DatatypeConstants.GDAY) {
            formatString = "---%D" + "%z";
        } else if (typekind == DatatypeConstants.GYEAR) {
            formatString = "%Y" + "%z";
        } else if (typekind == DatatypeConstants.GYEARMONTH) {
            formatString = "%Y-%M" + "%z";
        } else if (typekind == DatatypeConstants.GMONTHDAY) {
            formatString = "--%M-%D" + "%z";
        }
        return format(formatString);
    }


    public QName getXMLSchemaType() {

        int mask =
            (year != DatatypeConstants.FIELD_UNDEFINED ?   0x20 : 0 )|
            (month != DatatypeConstants.FIELD_UNDEFINED ?  0x10 : 0 )|
            (day != DatatypeConstants.FIELD_UNDEFINED ?    0x08 : 0 )|
            (hour != DatatypeConstants.FIELD_UNDEFINED ?   0x04 : 0 )|
            (minute != DatatypeConstants.FIELD_UNDEFINED ? 0x02 : 0 )|
            (second != DatatypeConstants.FIELD_UNDEFINED ? 0x01 : 0 );

        switch(mask) {
        case 0x3F:
                return DatatypeConstants.DATETIME;
        case 0x38:
                return DatatypeConstants.DATE;
        case 0x07:
                return DatatypeConstants.TIME;
        case 0x30:
                return DatatypeConstants.GYEARMONTH;
        case 0x18:
                return DatatypeConstants.GMONTHDAY;
        case 0x20:
                return DatatypeConstants.GYEAR;
        case 0x10:
                return DatatypeConstants.GMONTH;
        case 0x08:
                return DatatypeConstants.GDAY;
        default:
            throw new IllegalStateException(
                this.getClass().getName()
                + "#getXMLSchemaType() :"
                + DatatypeMessageFormatter.formatMessage(null, "InvalidXGCFields", null)
            );
        }
    }



    public boolean isValid() {
        if (getMonth() == DatatypeConstants.FEBRUARY) {
            int maxDays = 29;

            if (eon == null) {
                if(year!=DatatypeConstants.FIELD_UNDEFINED)
                    maxDays = maximumDayInMonthFor(year,getMonth());
            } else {
                BigInteger years = getEonAndYear();
                if (years != null) {
                    maxDays = maximumDayInMonthFor(getEonAndYear(), DatatypeConstants.FEBRUARY);
                }
            }
            if (getDay() > maxDays) {
                return false;
            }
        }

        if (getHour() == 24) {
            if(getMinute() != 0) {
                return false;
            } else if (getSecond() != 0) {
                return false;
            }
        }

        if (eon == null) {
            if (year == 0) {
                return false;
            }
        } else {
            BigInteger yearField = getEonAndYear();
            if (yearField != null) {
                int result = compareField(yearField, BigInteger.ZERO);
                if (result == DatatypeConstants.EQUAL) {
                    return false;
                }
            }
        }
        return true;
    }


    public void add(Duration duration) {



        boolean fieldUndefined[] = {
                false,
                false,
                false,
                false,
                false,
                false
        };

        int signum = duration.getSign();

        int startMonth = getMonth();
        if (startMonth == DatatypeConstants.FIELD_UNDEFINED) {
            startMonth = DatatypeConstants.JANUARY;
            fieldUndefined[MONTH] = true;
        }

        BigInteger dMonths = sanitize(duration.getField(DatatypeConstants.MONTHS), signum);
        BigInteger temp = BigInteger.valueOf((long) startMonth).add(dMonths);
        setMonth(temp.subtract(BigInteger.ONE).mod(TWELVE).intValue() + 1);
        BigInteger carry =
                new BigDecimal(temp.subtract(BigInteger.ONE)).divide(new BigDecimal(TWELVE), BigDecimal.ROUND_FLOOR).toBigInteger();


        BigInteger startYear = getEonAndYear();
        if (startYear == null) {
            fieldUndefined[YEAR] = true;
            startYear = BigInteger.ZERO;
        }
        BigInteger dYears = sanitize(duration.getField(DatatypeConstants.YEARS), signum);
        BigInteger endYear = startYear.add(dYears).add(carry);
        setYear(endYear);




        BigDecimal startSeconds;
        if (getSecond() == DatatypeConstants.FIELD_UNDEFINED) {
            fieldUndefined[SECOND] = true;
            startSeconds = DECIMAL_ZERO;
        } else {
            startSeconds = getSeconds();
        }

        BigDecimal dSeconds = DurationImpl.sanitize((BigDecimal) duration.getField(DatatypeConstants.SECONDS), signum);
        BigDecimal tempBD = startSeconds.add(dSeconds);
        BigDecimal fQuotient =
                new BigDecimal(new BigDecimal(tempBD.toBigInteger()).divide(DECIMAL_SIXTY, BigDecimal.ROUND_FLOOR).toBigInteger());
        BigDecimal endSeconds = tempBD.subtract(fQuotient.multiply(DECIMAL_SIXTY));

        carry = fQuotient.toBigInteger();
        setSecond(endSeconds.intValue());
        BigDecimal tempFracSeconds = endSeconds.subtract(new BigDecimal(BigInteger.valueOf((long) getSecond())));
        if (tempFracSeconds.compareTo(DECIMAL_ZERO) < 0) {
            setFractionalSecond(DECIMAL_ONE.add(tempFracSeconds));
            if (getSecond() == 0) {
                setSecond(59);
                carry = carry.subtract(BigInteger.ONE);
            } else {
                setSecond(getSecond() - 1);
            }
        } else {
            setFractionalSecond(tempFracSeconds);
        }


        int startMinutes = getMinute();
        if (startMinutes == DatatypeConstants.FIELD_UNDEFINED) {
            fieldUndefined[MINUTE] = true;
            startMinutes = 0;
        }
        BigInteger dMinutes = sanitize(duration.getField(DatatypeConstants.MINUTES), signum);

        temp = BigInteger.valueOf(startMinutes).add(dMinutes).add(carry);
        setMinute(temp.mod(SIXTY).intValue());
        carry = new BigDecimal(temp).divide(DECIMAL_SIXTY, BigDecimal.ROUND_FLOOR).toBigInteger();


        int startHours = getHour();
        if (startHours == DatatypeConstants.FIELD_UNDEFINED) {
            fieldUndefined[HOUR] = true;
            startHours = 0;
        }
        BigInteger dHours = sanitize(duration.getField(DatatypeConstants.HOURS), signum);

        temp = BigInteger.valueOf(startHours).add(dHours).add(carry);
        setHour(temp.mod(TWENTY_FOUR).intValue(), false);
        carry = new BigDecimal(temp).divide(new BigDecimal(TWENTY_FOUR), BigDecimal.ROUND_FLOOR).toBigInteger();


        BigInteger tempDays;
        int startDay = getDay();
        if (startDay == DatatypeConstants.FIELD_UNDEFINED) {
            fieldUndefined[DAY] = true;
            startDay = 1;
        }
        BigInteger dDays = sanitize(duration.getField(DatatypeConstants.DAYS), signum);
        int maxDayInMonth = maximumDayInMonthFor(getEonAndYear(), getMonth());
        if (startDay > maxDayInMonth) {
            tempDays = BigInteger.valueOf(maxDayInMonth);
        } else if (startDay < 1) {
            tempDays = BigInteger.ONE;
        } else {
            tempDays = BigInteger.valueOf(startDay);
        }
        BigInteger endDays = tempDays.add(dDays).add(carry);
        int monthCarry;
        int intTemp;
        while (true) {
            if (endDays.compareTo(BigInteger.ONE) < 0) {
                BigInteger mdimf = null;
                if (month >= 2) {
                    mdimf = BigInteger.valueOf(maximumDayInMonthFor(getEonAndYear(), getMonth() - 1));
                } else {
                    mdimf = BigInteger.valueOf(maximumDayInMonthFor(getEonAndYear().subtract(BigInteger.valueOf((long) 1)), 12));
                }
                endDays = endDays.add(mdimf);
                monthCarry = -1;
            } else if (endDays.compareTo(BigInteger.valueOf(maximumDayInMonthFor(getEonAndYear(), getMonth()))) > 0) {
                endDays = endDays.add(BigInteger.valueOf(-maximumDayInMonthFor(getEonAndYear(), getMonth())));
                monthCarry = 1;
            } else {
                break;
            }

            intTemp = getMonth() + monthCarry;
            int endMonth = (intTemp - 1) % (13 - 1);
            int quotient;
            if (endMonth < 0) {
                endMonth = (13 - 1) + endMonth + 1;
                quotient = new BigDecimal(intTemp - 1).divide(new BigDecimal(TWELVE), BigDecimal.ROUND_UP).intValue();
            } else {
                quotient = (intTemp - 1) / (13 - 1);
                endMonth += 1;
            }
            setMonth(endMonth);
            if (quotient != 0) {
                setYear(getEonAndYear().add(BigInteger.valueOf(quotient)));
            }
        }
        setDay(endDays.intValue());

        for (int i = YEAR; i <= SECOND; i++) {
            if (fieldUndefined[i]) {
                switch (i) {
                case YEAR:
                    setYear(DatatypeConstants.FIELD_UNDEFINED);
                    break;
                case MONTH:
                    setMonth(DatatypeConstants.FIELD_UNDEFINED);
                    break;
                case DAY:
                    setDay(DatatypeConstants.FIELD_UNDEFINED);
                    break;
                case HOUR:
                    setHour(DatatypeConstants.FIELD_UNDEFINED, false);
                    break;
                case MINUTE:
                    setMinute(DatatypeConstants.FIELD_UNDEFINED);
                    break;
                case SECOND:
                    setSecond(DatatypeConstants.FIELD_UNDEFINED);
                    setFractionalSecond(null);
                    break;
                }
            }
        }
    }

    private static final BigInteger FOUR = BigInteger.valueOf(4);
    private static final BigInteger HUNDRED = BigInteger.valueOf(100);
    private static final BigInteger FOUR_HUNDRED = BigInteger.valueOf(400);
    private static final BigInteger SIXTY = BigInteger.valueOf(60);
    private static final BigInteger TWENTY_FOUR = BigInteger.valueOf(24);
    private static final BigInteger TWELVE = BigInteger.valueOf(12);
    private static final BigDecimal DECIMAL_ZERO = new BigDecimal("0");
    private static final BigDecimal DECIMAL_ONE = new BigDecimal("1");
    private static final BigDecimal DECIMAL_SIXTY = new BigDecimal("60");


    private static int daysInMonth[] = { 0,  31, 28, 31, 30, 31, 30,
                                       31, 31, 30, 31, 30, 31};

    private static int maximumDayInMonthFor(BigInteger year, int month) {
        if (month != DatatypeConstants.FEBRUARY) {
            return daysInMonth[month];
        } else {
            if (year.mod(FOUR_HUNDRED).equals(BigInteger.ZERO) ||
                    (!year.mod(HUNDRED).equals(BigInteger.ZERO) &&
                            year.mod(FOUR).equals(BigInteger.ZERO))) {
                return 29;
            } else {
                return daysInMonth[month];
            }
        }
    }

    private static int maximumDayInMonthFor(int year, int month) {
        if (month != DatatypeConstants.FEBRUARY) {
            return daysInMonth[month];
        } else {
            if (((year % 400) == 0) ||
                    (((year % 100) != 0) && ((year % 4) == 0))) {
                return 29;
            } else {
                return daysInMonth[DatatypeConstants.FEBRUARY];
            }
        }
    }


    public java.util.GregorianCalendar toGregorianCalendar() {

        GregorianCalendar result = null;
        final int DEFAULT_TIMEZONE_OFFSET = DatatypeConstants.FIELD_UNDEFINED;
        TimeZone tz = getTimeZone(DEFAULT_TIMEZONE_OFFSET);

        Locale locale = getDefaultLocale();

        result = new GregorianCalendar(tz, locale);
        result.clear();
        result.setGregorianChange(PURE_GREGORIAN_CHANGE);

        BigInteger year = getEonAndYear();
        if (year != null) {
            result.set(Calendar.ERA, year.signum() == -1 ? GregorianCalendar.BC : GregorianCalendar.AD);
            result.set(Calendar.YEAR, year.abs().intValue());
        }

        if (month != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.MONTH, month - 1);
        }

        if (day != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.DAY_OF_MONTH, day);
        }

        if (hour != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.HOUR_OF_DAY, hour);
        }

        if (minute != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.MINUTE, minute);
        }

        if (second != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.SECOND, second);
        }

        if (fractionalSecond != null) {
            result.set(Calendar.MILLISECOND, getMillisecond());
        }

        return result;
    }


    private Locale getDefaultLocale() {

        String lang = SecuritySupport.getSystemProperty("user.language.format");
        String country = SecuritySupport.getSystemProperty("user.country.format");
        String variant = SecuritySupport.getSystemProperty("user.variant.format");
        Locale locale = null;
        if (lang != null) {
            if (country != null) {
                if (variant != null) {
                    locale = new Locale(lang, country, variant);
                } else {
                    locale = new Locale(lang, country);
                }
            } else {
                locale = new Locale(lang);
            }
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }


    public GregorianCalendar toGregorianCalendar(TimeZone timezone,
                                                 Locale aLocale,
                                                 XMLGregorianCalendar defaults) {
        GregorianCalendar result = null;
        TimeZone tz = timezone;
        if (tz == null) {
            int defaultZoneoffset = DatatypeConstants.FIELD_UNDEFINED;
            if (defaults != null) {
                defaultZoneoffset = defaults.getTimezone();
            }
            tz = getTimeZone(defaultZoneoffset);
        }
        if (aLocale == null) {
            aLocale = Locale.getDefault();
        }
        result = new GregorianCalendar(tz, aLocale);
        result.clear();
        result.setGregorianChange(PURE_GREGORIAN_CHANGE);

        BigInteger year = getEonAndYear();
        if (year != null) {
            result.set(Calendar.ERA, year.signum() == -1 ? GregorianCalendar.BC : GregorianCalendar.AD);
            result.set(Calendar.YEAR, year.abs().intValue());
        } else {
            BigInteger defaultYear = (defaults != null) ? defaults.getEonAndYear() : null;
            if (defaultYear != null) {
                result.set(Calendar.ERA, defaultYear.signum() == -1 ? GregorianCalendar.BC : GregorianCalendar.AD);
                result.set(Calendar.YEAR, defaultYear.abs().intValue());
            }
        }

        if (month != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.MONTH, month - 1);
        } else {
            int defaultMonth = (defaults != null) ? defaults.getMonth() : DatatypeConstants.FIELD_UNDEFINED;
            if (defaultMonth != DatatypeConstants.FIELD_UNDEFINED) {
                result.set(Calendar.MONTH, defaultMonth - 1);
            }
        }

        if (day != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.DAY_OF_MONTH, day);
        } else {
            int defaultDay = (defaults != null) ? defaults.getDay() : DatatypeConstants.FIELD_UNDEFINED;
            if (defaultDay != DatatypeConstants.FIELD_UNDEFINED) {
                result.set(Calendar.DAY_OF_MONTH, defaultDay);
            }
        }

        if (hour != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.HOUR_OF_DAY, hour);
        } else {
            int defaultHour = (defaults != null) ? defaults.getHour() : DatatypeConstants.FIELD_UNDEFINED;
            if (defaultHour != DatatypeConstants.FIELD_UNDEFINED) {
                result.set(Calendar.HOUR_OF_DAY, defaultHour);
            }
        }

        if (minute != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.MINUTE, minute);
        } else {
            int defaultMinute = (defaults != null) ? defaults.getMinute() : DatatypeConstants.FIELD_UNDEFINED;
            if (defaultMinute != DatatypeConstants.FIELD_UNDEFINED) {
                result.set(Calendar.MINUTE, defaultMinute);
            }
        }

        if (second != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.SECOND, second);
        } else {
            int defaultSecond = (defaults != null) ? defaults.getSecond() : DatatypeConstants.FIELD_UNDEFINED;
            if (defaultSecond != DatatypeConstants.FIELD_UNDEFINED) {
                result.set(Calendar.SECOND, defaultSecond);
            }
        }

        if (fractionalSecond != null) {
            result.set(Calendar.MILLISECOND, getMillisecond());
        } else {
            BigDecimal defaultFractionalSecond = (defaults != null) ? defaults.getFractionalSecond() : null;
            if (defaultFractionalSecond != null) {
                result.set(Calendar.MILLISECOND, defaults.getMillisecond());
            }
        }

        return result;
    }


    public TimeZone getTimeZone(int defaultZoneoffset) {
        TimeZone result = null;
        int zoneoffset = getTimezone();

        if (zoneoffset == DatatypeConstants.FIELD_UNDEFINED) {
            zoneoffset = defaultZoneoffset;
        }
        if (zoneoffset == DatatypeConstants.FIELD_UNDEFINED) {
            result = TimeZone.getDefault();
        } else {
            char sign = zoneoffset < 0 ? '-' : '+';
            if (sign == '-') {
                zoneoffset = -zoneoffset;
            }
            int hour = zoneoffset / 60;
            int minutes = zoneoffset - (hour * 60);

            StringBuffer customTimezoneId = new StringBuffer(8);
            customTimezoneId.append("GMT");
            customTimezoneId.append(sign);
            customTimezoneId.append(hour);
            if (minutes != 0) {
                if (minutes < 10) {
                    customTimezoneId.append('0');
                }
                customTimezoneId.append(minutes);
            }
            result = TimeZone.getTimeZone(customTimezoneId.toString());
        }
        return result;
    }


   public Object clone() {
        return new XMLGregorianCalendarImpl(getEonAndYear(),
                        this.month, this.day,
                        this.hour, this.minute, this.second,
                        this.fractionalSecond,
                        this.timezone);
    }


    public void clear() {
        eon = null;
        year = DatatypeConstants.FIELD_UNDEFINED;
        month = DatatypeConstants.FIELD_UNDEFINED;
        day = DatatypeConstants.FIELD_UNDEFINED;
        timezone = DatatypeConstants.FIELD_UNDEFINED;  hour = DatatypeConstants.FIELD_UNDEFINED;
        minute = DatatypeConstants.FIELD_UNDEFINED;
        second = DatatypeConstants.FIELD_UNDEFINED;
        fractionalSecond = null;
    }

    public void setMillisecond(int millisecond) {
        if (millisecond == DatatypeConstants.FIELD_UNDEFINED) {
            fractionalSecond = null;
        } else {
            if(millisecond<0 || 999<millisecond)
                if(millisecond!=DatatypeConstants.FIELD_UNDEFINED)
                    invalidFieldValue(MILLISECOND, millisecond);
            fractionalSecond = new BigDecimal((long) millisecond).movePointLeft(3);
        }
    }

    public void setFractionalSecond(BigDecimal fractional) {
        if (fractional != null) {
            if ((fractional.compareTo(DECIMAL_ZERO) < 0) ||
                    (fractional.compareTo(DECIMAL_ONE) > 0)) {
                throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null,
                        "InvalidFractional", new Object[]{fractional}));
            }
        }
        this.fractionalSecond = fractional;
    }

    private final class Parser {
        private final String format;
        private final String value;

        private final int flen;
        private final int vlen;

        private int fidx;
        private int vidx;

        private Parser(String format, String value) {
            this.format = format;
            this.value = value;
            this.flen = format.length();
            this.vlen = value.length();
        }


        public void parse() throws IllegalArgumentException {
            while (fidx < flen) {
                char fch = format.charAt(fidx++);

                if (fch != '%') { skip(fch);
                    continue;
                }

                switch (format.charAt(fidx++)) {
                    case 'Y' : parseAndSetYear(4);
                        break;

                    case 'M' : setMonth(parseInt(2, 2));
                        break;

                    case 'D' : setDay(parseInt(2, 2));
                        break;

                    case 'h' : setHour(parseInt(2, 2), false);
                        break;

                    case 'm' : setMinute(parseInt(2, 2));
                        break;

                    case 's' : setSecond(parseInt(2, 2));

                        if (peek() == '.') {
                            setFractionalSecond(parseBigDecimal());
                        }
                        break;

                    case 'z' : char vch = peek();
                        if (vch == 'Z') {
                            vidx++;
                            setTimezone(0);
                        } else if (vch == '+' || vch == '-') {
                            vidx++;
                            int h = parseInt(2, 2);
                            skip(':');
                            int m = parseInt(2, 2);
                            setTimezone((h * 60 + m) * (vch == '+' ? 1 : -1));
                        }

                        break;

                    default :
                        throw new InternalError();
                }
            }

            if (vidx != vlen) {
                throw new IllegalArgumentException(value); }
            testHour();
        }

        private char peek() throws IllegalArgumentException {
            if (vidx == vlen) {
                return (char) -1;
            }
            return value.charAt(vidx);
        }

        private char read() throws IllegalArgumentException {
            if (vidx == vlen) {
                throw new IllegalArgumentException(value); }
            return value.charAt(vidx++);
        }

        private void skip(char ch) throws IllegalArgumentException {
            if (read() != ch) {
                throw new IllegalArgumentException(value); }
        }

        private int parseInt(int minDigits, int maxDigits)
            throws IllegalArgumentException {

            int n = 0;
            char ch;
            int vstart = vidx;
            while (isDigit(ch=peek()) && (vidx - vstart) <= maxDigits) {
                vidx++;
                n = n*10 + ch-'0';
            }
            if ((vidx - vstart) < minDigits) {
                throw new IllegalArgumentException(value); }

            return n;
        }

        private void parseAndSetYear(int minDigits)
                throws IllegalArgumentException {
            int vstart = vidx;
            int n = 0;
            boolean neg = false;

            if (peek() == '-') {
                vidx++;
                neg = true;
            }
            while(true) {
                char ch = peek();
                if(!isDigit(ch))
                    break;
                vidx++;
                n = n*10 + ch-'0';
            }

            if ((vidx - vstart) < minDigits) {
                throw new IllegalArgumentException(value); }

            if(vidx-vstart<7) {
                if(neg)     n = -n;
                year = n;
                eon = null;
            } else {
                setYear(new BigInteger(value.substring(vstart, vidx)));
            }
        }

        private BigDecimal parseBigDecimal()
                throws IllegalArgumentException {
            int vstart = vidx;

            if (peek() == '.') {
                vidx++;
            } else {
                throw new IllegalArgumentException(value);
            }
            while (isDigit(peek())) {
                vidx++;
            }
            return new BigDecimal(value.substring(vstart, vidx));
        }
    }

    private static boolean isDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }


    private String format( String format ) {
        char[] buf = new char[32];
        int bufPtr = 0;

        int fidx=0,flen=format.length();

        while(fidx<flen) {
            char fch = format.charAt(fidx++);
            if(fch!='%') {buf[bufPtr++] = fch;
                continue;
            }

            switch(format.charAt(fidx++)) {
            case 'Y':
                if(eon==null) {
                    int y = getYear();
                    if(y<0) {
                        buf[bufPtr++] = '-';
                        y = -y;
                    }
                    bufPtr = print4Number(buf,bufPtr,y);
                } else {
                    String s = getEonAndYear().toString();
                    char[] n = new char[buf.length+s.length()];
                    System.arraycopy(buf,0,n,0,bufPtr);
                    buf = n;
                    for(int i=s.length();i<4;i++)
                        buf[bufPtr++] = '0';
                    s.getChars(0,s.length(),buf,bufPtr);
                    bufPtr += s.length();
                }
                break;
            case 'M':
                bufPtr = print2Number(buf,bufPtr,getMonth());
                break;
            case 'D':
                bufPtr = print2Number(buf,bufPtr,getDay());
                break;
            case 'h':
                bufPtr = print2Number(buf,bufPtr,getHour());
                break;
            case 'm':
                bufPtr = print2Number(buf,bufPtr,getMinute());
                break;
            case 's':
                bufPtr = print2Number(buf,bufPtr,getSecond());
                if (getFractionalSecond() != null) {
                    String frac = getFractionalSecond().toString();

                    int pos = frac.indexOf("E-");
                    if (pos >= 0) {
                        String zeros = frac.substring(pos+2);
                        frac = frac.substring(0,pos);
                        pos = frac.indexOf(".");
                        if (pos >= 0) {
                            frac = frac.substring(0,pos) + frac.substring(pos+1);
                        }
                        int count = Integer.parseInt(zeros);
                        if (count < 40) {
                            frac = "00000000000000000000000000000000000000000".substring(0,count-1) + frac;
                        } else {
                            while (count > 1) {
                                frac = "0" + frac;
                                count--;
                            }
                        }
                        frac = "0." + frac;
                    }

                    char[] n = new char[buf.length+frac.length()];
                    System.arraycopy(buf,0,n,0,bufPtr);
                    buf = n;
                    frac.getChars(1, frac.length(), buf, bufPtr);
                    bufPtr += frac.length()-1;
                }
                break;
            case 'z':
                int offset = getTimezone();
                if (offset == 0) {
                    buf[bufPtr++] = 'Z';
                } else
                if (offset != DatatypeConstants.FIELD_UNDEFINED) {
                    if (offset < 0) {
                        buf[bufPtr++] = '-';
                        offset *= -1;
                    } else {
                        buf[bufPtr++] = '+';
                    }
                    bufPtr = print2Number(buf, bufPtr, offset / 60);
                    buf[bufPtr++] = ':';
                    bufPtr = print2Number(buf, bufPtr, offset % 60);
                }
                break;
            default:
                throw new InternalError();  }
        }

        return new String(buf,0,bufPtr);
    }


    private int print2Number( char[] out, int bufptr, int number ) {
        out[bufptr++] = (char) ('0'+(number/10));
        out[bufptr++] = (char) ('0'+(number%10));
        return bufptr;
    }


    private int print4Number( char[] out, int bufptr, int number ) {
        out[bufptr+3] = (char) ('0'+(number%10));
        number /= 10;
        out[bufptr+2] = (char) ('0'+(number%10));
        number /= 10;
        out[bufptr+1] = (char) ('0'+(number%10));
        number /= 10;
        out[bufptr  ] = (char) ('0'+(number%10));
        return bufptr+4;
    }


    static BigInteger sanitize(Number value, int signum) {
        if (signum == 0 || value == null) {
            return BigInteger.ZERO;
        }
        return (signum <  0)? ((BigInteger)value).negate() : (BigInteger)value;
    }


    public void reset() {
        }
}
