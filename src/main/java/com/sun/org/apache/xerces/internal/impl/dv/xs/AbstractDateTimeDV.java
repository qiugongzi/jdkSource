


package com.sun.org.apache.xerces.internal.impl.dv.xs;

import java.math.BigDecimal;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSDateTime;


public abstract class AbstractDateTimeDV extends TypeValidator {

    private static final boolean DEBUG = false;
    protected final static int YEAR = 2000;
    protected final static int MONTH = 01;
    protected final static int DAY = 01;
    protected static final DatatypeFactory datatypeFactory = new DatatypeFactoryImpl();

    @Override
    public short getAllowedFacets() {
        return (XSSimpleTypeDecl.FACET_PATTERN | XSSimpleTypeDecl.FACET_WHITESPACE | XSSimpleTypeDecl.FACET_ENUMERATION | XSSimpleTypeDecl.FACET_MAXINCLUSIVE | XSSimpleTypeDecl.FACET_MININCLUSIVE | XSSimpleTypeDecl.FACET_MAXEXCLUSIVE | XSSimpleTypeDecl.FACET_MINEXCLUSIVE);
    }@Override
    public boolean isIdentical(Object value1, Object value2) {
        if (!(value1 instanceof DateTimeData) || !(value2 instanceof DateTimeData)) {
            return false;
        }

        DateTimeData v1 = (DateTimeData) value1;
        DateTimeData v2 = (DateTimeData) value2;

        if ((v1.timezoneHr == v2.timezoneHr) && (v1.timezoneMin == v2.timezoneMin)) {
            return v1.equals(v2);
        }

        return false;
    }@Override
    public int compare(Object value1, Object value2) {
        return compareDates(((DateTimeData) value1),
                ((DateTimeData) value2), true);
    }protected short compareDates(DateTimeData date1, DateTimeData date2, boolean strict) {
        if (date1.utc == date2.utc) {
            return compareOrder(date1, date2);
        }
        short c1, c2;

        DateTimeData tempDate = new DateTimeData(null, this);

        if (date1.utc == 'Z') {

            cloneDate(date2, tempDate); tempDate.timezoneHr = 14;
            tempDate.timezoneMin = 0;
            tempDate.utc = '+';
            normalize(tempDate);
            c1 = compareOrder(date1, tempDate);
            if (c1 == LESS_THAN) {
                return c1;
            }

            cloneDate(date2, tempDate); tempDate.timezoneHr = -14;
            tempDate.timezoneMin = 0;
            tempDate.utc = '-';
            normalize(tempDate);
            c2 = compareOrder(date1, tempDate);
            if (c2 == GREATER_THAN) {
                return c2;
            }

            return INDETERMINATE;
        } else if (date2.utc == 'Z') {

            cloneDate(date1, tempDate); tempDate.timezoneHr = -14;
            tempDate.timezoneMin = 0;
            tempDate.utc = '-';
            if (DEBUG) {
                System.out.println("tempDate=" + dateToString(tempDate));
            }
            normalize(tempDate);
            c1 = compareOrder(tempDate, date2);
            if (DEBUG) {
                System.out.println("date=" + dateToString(date2));
                System.out.println("tempDate=" + dateToString(tempDate));
            }
            if (c1 == LESS_THAN) {
                return c1;
            }

            cloneDate(date1, tempDate); tempDate.timezoneHr = 14;
            tempDate.timezoneMin = 0;
            tempDate.utc = '+';
            normalize(tempDate);
            c2 = compareOrder(tempDate, date2);
            if (DEBUG) {
                System.out.println("tempDate=" + dateToString(tempDate));
            }
            if (c2 == GREATER_THAN) {
                return c2;
            }

            return INDETERMINATE;
        }
        return INDETERMINATE;

    }


    protected short compareOrder(DateTimeData date1, DateTimeData date2) {
        if (date1.position < 1) {
            if (date1.year < date2.year) {
                return -1;
            }
            if (date1.year > date2.year) {
                return 1;
            }
        }
        if (date1.position < 2) {
            if (date1.month < date2.month) {
                return -1;
            }
            if (date1.month > date2.month) {
                return 1;
            }
        }
        if (date1.day < date2.day) {
            return -1;
        }
        if (date1.day > date2.day) {
            return 1;
        }
        if (date1.hour < date2.hour) {
            return -1;
        }
        if (date1.hour > date2.hour) {
            return 1;
        }
        if (date1.minute < date2.minute) {
            return -1;
        }
        if (date1.minute > date2.minute) {
            return 1;
        }
        if (date1.second < date2.second) {
            return -1;
        }
        if (date1.second > date2.second) {
            return 1;
        }
        if (date1.utc < date2.utc) {
            return -1;
        }
        if (date1.utc > date2.utc) {
            return 1;
        }
        return 0;
    }


    protected void getTime(String buffer, int start, int end, DateTimeData data) throws RuntimeException {

        int stop = start + 2;

        data.hour = parseInt(buffer, start, stop);

        if (buffer.charAt(stop++) != ':') {
            throw new RuntimeException("Error in parsing time zone");
        }
        start = stop;
        stop = stop + 2;
        data.minute = parseInt(buffer, start, stop);

        if (buffer.charAt(stop++) != ':') {
            throw new RuntimeException("Error in parsing time zone");
        }

        int sign = findUTCSign(buffer, start, end);

        start = stop;
        stop = sign < 0 ? end : sign;
        data.second = parseSecond(buffer, start, stop);

        if (sign > 0) {
            getTimeZone(buffer, data, sign, end);
        }
    }


    protected int getDate(String buffer, int start, int end, DateTimeData date) throws RuntimeException {

        start = getYearMonth(buffer, start, end, date);

        if (buffer.charAt(start++) != '-') {
            throw new RuntimeException("CCYY-MM must be followed by '-' sign");
        }
        int stop = start + 2;
        date.day = parseInt(buffer, start, stop);
        return stop;
    }


    protected int getYearMonth(String buffer, int start, int end, DateTimeData date) throws RuntimeException {

        if (buffer.charAt(0) == '-') {
            start++;
        }
        int i = indexOf(buffer, start, end, '-');
        if (i == -1) {
            throw new RuntimeException("Year separator is missing or misplaced");
        }
        int length = i - start;
        if (length < 4) {
            throw new RuntimeException("Year must have 'CCYY' format");
        } else if (length > 4 && buffer.charAt(start) == '0') {
            throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden");
        }
        date.year = parseIntYear(buffer, i);
        if (buffer.charAt(i) != '-') {
            throw new RuntimeException("CCYY must be followed by '-' sign");
        }
        start = ++i;
        i = start + 2;
        date.month = parseInt(buffer, start, i);
        return i; }


    protected void parseTimeZone(String buffer, int start, int end, DateTimeData date) throws RuntimeException {

        if (start < end) {
            if (!isNextCharUTCSign(buffer, start, end)) {
                throw new RuntimeException("Error in month parsing");
            } else {
                getTimeZone(buffer, date, start, end);
            }
        }
    }


    protected void getTimeZone(String buffer, DateTimeData data, int sign, int end) throws RuntimeException {
        data.utc = buffer.charAt(sign);

        if (buffer.charAt(sign) == 'Z') {
            if (end > (++sign)) {
                throw new RuntimeException("Error in parsing time zone");
            }
            return;
        }
        if (sign <= (end - 6)) {

            int negate = buffer.charAt(sign) == '-' ? -1 : 1;
            int stop = ++sign + 2;
            data.timezoneHr = negate * parseInt(buffer, sign, stop);
            if (buffer.charAt(stop++) != ':') {
                throw new RuntimeException("Error in parsing time zone");
            }

            data.timezoneMin = negate * parseInt(buffer, stop, stop + 2);

            if (stop + 2 != end) {
                throw new RuntimeException("Error in parsing time zone");
            }
            if (data.timezoneHr != 0 || data.timezoneMin != 0) {
                data.normalized = false;
            }
        } else {
            throw new RuntimeException("Error in parsing time zone");
        }
        if (DEBUG) {
            System.out.println("time[hh]=" + data.timezoneHr + " time[mm]=" + data.timezoneMin);
        }
    }


    protected int indexOf(String buffer, int start, int end, char ch) {
        for (int i = start; i < end; i++) {
            if (buffer.charAt(i) == ch) {
                return i;
            }
        }
        return -1;
    }


    protected void validateDateTime(DateTimeData data) {

        if (!Constants.SCHEMA_1_1_SUPPORT && data.year == 0) {
            throw new RuntimeException("The year \"0000\" is an illegal year value");

        }

        if (data.month < 1 || data.month > 12) {
            throw new RuntimeException("The month must have values 1 to 12");

        }

        if (data.day > maxDayInMonthFor(data.year, data.month) || data.day < 1) {
            throw new RuntimeException("The day must have values 1 to 31");
        }

        if (data.hour > 23 || data.hour < 0) {
            if (data.hour == 24 && data.minute == 0 && data.second == 0) {
                data.hour = 0;
                if (++data.day > maxDayInMonthFor(data.year, data.month)) {
                    data.day = 1;
                    if (++data.month > 12) {
                        data.month = 1;
                        if (Constants.SCHEMA_1_1_SUPPORT) {
                            ++data.year;
                        } else if (++data.year == 0) {
                            data.year = 1;
                        }
                    }
                }
            } else {
                throw new RuntimeException("Hour must have values 0-23, unless 24:00:00");
            }
        }

        if (data.minute > 59 || data.minute < 0) {
            throw new RuntimeException("Minute must have values 0-59");
        }

        if (data.second >= 60 || data.second < 0) {
            throw new RuntimeException("Second must have values 0-59");

        }

        if (data.timezoneHr > 14 || data.timezoneHr < -14) {
            throw new RuntimeException("Time zone should have range -14:00 to +14:00");
        } else {
            if ((data.timezoneHr == 14 || data.timezoneHr == -14) && data.timezoneMin != 0) {
                throw new RuntimeException("Time zone should have range -14:00 to +14:00");
            } else if (data.timezoneMin > 59 || data.timezoneMin < -59) {
                throw new RuntimeException("Minute must have values 0-59");
            }
        }

    }


    protected int findUTCSign(String buffer, int start, int end) {
        int c;
        for (int i = start; i < end; i++) {
            c = buffer.charAt(i);
            if (c == 'Z' || c == '+' || c == '-') {
                return i;
            }

        }
        return -1;
    }


    protected final boolean isNextCharUTCSign(String buffer, int start, int end) {
        if (start < end) {
            char c = buffer.charAt(start);
            return (c == 'Z' || c == '+' || c == '-');
        }
        return false;
    }


    protected int parseInt(String buffer, int start, int end)
            throws NumberFormatException {
        int radix = 10;
        int result = 0;
        int digit = 0;
        int limit = -Integer.MAX_VALUE;
        int multmin = limit / radix;
        int i = start;
        do {
            digit = getDigit(buffer.charAt(i));
            if (digit < 0) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
            if (result < multmin) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
            result *= radix;
            if (result < limit + digit) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
            result -= digit;

        } while (++i < end);
        return -result;
    }

    protected int parseIntYear(String buffer, int end) {
        int radix = 10;
        int result = 0;
        boolean negative = false;
        int i = 0;
        int limit;
        int multmin;
        int digit = 0;

        if (buffer.charAt(0) == '-') {
            negative = true;
            limit = Integer.MIN_VALUE;
            i++;

        } else {
            limit = -Integer.MAX_VALUE;
        }
        multmin = limit / radix;
        while (i < end) {
            digit = getDigit(buffer.charAt(i++));
            if (digit < 0) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
            if (result < multmin) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
            result *= radix;
            if (result < limit + digit) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
            result -= digit;
        }

        if (negative) {
            if (i > 1) {
                return result;
            } else {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
        }
        return -result;

    }


    protected void normalize(DateTimeData date) {

        int negate = -1;

        if (DEBUG) {
            System.out.println("==>date.minute" + date.minute);
            System.out.println("==>date.timezoneMin" + date.timezoneMin);
        }
        int temp = date.minute + negate * date.timezoneMin;
        int carry = fQuotient(temp, 60);
        date.minute = mod(temp, 60, carry);

        if (DEBUG) {
            System.out.println("==>carry: " + carry);
        }
        temp = date.hour + negate * date.timezoneHr + carry;
        carry = fQuotient(temp, 24);
        date.hour = mod(temp, 24, carry);
        if (DEBUG) {
            System.out.println("==>date.hour" + date.hour);
            System.out.println("==>carry: " + carry);
        }

        date.day = date.day + carry;

        while (true) {
            temp = maxDayInMonthFor(date.year, date.month);
            if (date.day < 1) {
                date.day = date.day + maxDayInMonthFor(date.year, date.month - 1);
                carry = -1;
            } else if (date.day > temp) {
                date.day = date.day - temp;
                carry = 1;
            } else {
                break;
            }
            temp = date.month + carry;
            date.month = modulo(temp, 1, 13);
            date.year = date.year + fQuotient(temp, 1, 13);
            if (date.year == 0 && !Constants.SCHEMA_1_1_SUPPORT) {
                date.year = (date.timezoneHr < 0 || date.timezoneMin < 0) ? 1 : -1;
            }
        }
        date.utc = 'Z';
    }


    protected void saveUnnormalized(DateTimeData date) {
        date.unNormYear = date.year;
        date.unNormMonth = date.month;
        date.unNormDay = date.day;
        date.unNormHour = date.hour;
        date.unNormMinute = date.minute;
        date.unNormSecond = date.second;
    }


    protected void resetDateObj(DateTimeData data) {
        data.year = 0;
        data.month = 0;
        data.day = 0;
        data.hour = 0;
        data.minute = 0;
        data.second = 0;
        data.utc = 0;
        data.timezoneHr = 0;
        data.timezoneMin = 0;
    }


    protected int maxDayInMonthFor(int year, int month) {
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else if (month == 2) {
            if (isLeapYear(year)) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return 31;
        }
    }

    private boolean isLeapYear(int year) {

        return ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)));
    }

    protected int mod(int a, int b, int quotient) {
        return (a - quotient * b);
    }

    protected int fQuotient(int a, int b) {

        return (int) Math.floor((float) a / b);
    }

    protected int modulo(int temp, int low, int high) {
        int a = temp - low;
        int b = high - low;
        return (mod(a, b, fQuotient(a, b)) + low);
    }

    protected int fQuotient(int temp, int low, int high) {
        return fQuotient(temp - low, high - low);
    }

    protected String dateToString(DateTimeData date) {
        StringBuffer message = new StringBuffer(25);
        append(message, date.year, 4);
        message.append('-');
        append(message, date.month, 2);
        message.append('-');
        append(message, date.day, 2);
        message.append('T');
        append(message, date.hour, 2);
        message.append(':');
        append(message, date.minute, 2);
        message.append(':');
        append(message, date.second);
        append(message, (char) date.utc, 0);
        return message.toString();
    }

    protected final void append(StringBuffer message, int value, int nch) {
        if (value == Integer.MIN_VALUE) {
            message.append(value);
            return;
        }
        if (value < 0) {
            message.append('-');
            value = -value;
        }
        if (nch == 4) {
            if (value < 10) {
                message.append("000");
            } else if (value < 100) {
                message.append("00");
            } else if (value < 1000) {
                message.append('0');
            }
            message.append(value);
        } else if (nch == 2) {
            if (value < 10) {
                message.append('0');
            }
            message.append(value);
        } else {
            if (value != 0) {
                message.append((char) value);
            }
        }
    }

    protected final void append(StringBuffer message, double value) {
        if (value < 0) {
            message.append('-');
            value = -value;
        }
        if (value < 10) {
            message.append('0');
        }
        append2(message, value);
    }

    protected final void append2(StringBuffer message, double value) {
        final int intValue = (int) value;
        if (value == intValue) {
            message.append(intValue);
        } else {
            append3(message, value);
        }
    }

    private void append3(StringBuffer message, double value) {
        String d = String.valueOf(value);
        int eIndex = d.indexOf('E');
        if (eIndex == -1) {
            message.append(d);
            return;
        }
        int exp;
        if (value < 1) {
            try {
                exp = parseInt(d, eIndex + 2, d.length());
            } catch (Exception e) {
                message.append(d);
                return;
            }
            message.append("0.");
            for (int i = 1; i < exp; ++i) {
                message.append('0');
            }
            int end = eIndex - 1;
            while (end > 0) {
                char c = d.charAt(end);
                if (c != '0') {
                    break;
                }
                --end;
            }
            for (int i = 0; i <= end; ++i) {
                char c = d.charAt(i);
                if (c != '.') {
                    message.append(c);
                }
            }
        } else {
            try {
                exp = parseInt(d, eIndex + 1, d.length());
            } catch (Exception e) {
                message.append(d);
                return;
            }
            final int integerEnd = exp + 2;
            for (int i = 0; i < eIndex; ++i) {
                char c = d.charAt(i);
                if (c != '.') {
                    if (i == integerEnd) {
                        message.append('.');
                    }
                    message.append(c);
                }
            }
            for (int i = integerEnd - eIndex; i > 0; --i) {
                message.append('0');
            }
        }
    }

    protected double parseSecond(String buffer, int start, int end)
            throws NumberFormatException {
        int dot = -1;
        for (int i = start; i < end; i++) {
            char ch = buffer.charAt(i);
            if (ch == '.') {
                dot = i;
            } else if (ch > '9' || ch < '0') {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
        }
        if (dot == -1) {
            if (start + 2 != end) {
                throw new NumberFormatException("'" + buffer + "' has wrong format");
            }
        } else if (start + 2 != dot || dot + 1 == end) {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
        }
        return Double.parseDouble(buffer.substring(start, end));
    }

    private void cloneDate(DateTimeData finalValue, DateTimeData tempDate) {
        tempDate.year = finalValue.year;
        tempDate.month = finalValue.month;
        tempDate.day = finalValue.day;
        tempDate.hour = finalValue.hour;
        tempDate.minute = finalValue.minute;
        tempDate.second = finalValue.second;
        tempDate.utc = finalValue.utc;
        tempDate.timezoneHr = finalValue.timezoneHr;
        tempDate.timezoneMin = finalValue.timezoneMin;
    }


    static final class DateTimeData implements XSDateTime {

        int year, month, day, hour, minute, utc;
        double second;
        int timezoneHr, timezoneMin;
        private String originalValue;
        boolean normalized = true;
        int unNormYear;
        int unNormMonth;
        int unNormDay;
        int unNormHour;
        int unNormMinute;
        double unNormSecond;
        int position;
        final AbstractDateTimeDV type;
        private volatile String canonical;

        public DateTimeData(String originalValue, AbstractDateTimeDV type) {
            this.originalValue = originalValue;
            this.type = type;
        }

        public DateTimeData(int year, int month, int day, int hour, int minute,
                double second, int utc, String originalValue, boolean normalized, AbstractDateTimeDV type) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
            this.utc = utc;
            this.type = type;
            this.originalValue = originalValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DateTimeData)) {
                return false;
            }
            return type.compareDates(this, (DateTimeData) obj, true) == 0;
        }

        @Override
        public int hashCode() {
            final DateTimeData tempDate = new DateTimeData(null, type);
            type.cloneDate(this, tempDate);
            type.normalize(tempDate);
            return type.dateToString(tempDate).hashCode();
        }

        @Override
        public String toString() {
            if (canonical == null) {
                canonical = type.dateToString(this);
            }
            return canonical;
        }


        @Override
        public int getYears() {
            if (type instanceof DurationDV) {
                return 0;
            }
            return normalized ? year : unNormYear;
        }


        @Override
        public int getMonths() {
            if (type instanceof DurationDV) {
                return year * 12 + month;
            }
            return normalized ? month : unNormMonth;
        }


        @Override
        public int getDays() {
            if (type instanceof DurationDV) {
                return 0;
            }
            return normalized ? day : unNormDay;
        }


        @Override
        public int getHours() {
            if (type instanceof DurationDV) {
                return 0;
            }
            return normalized ? hour : unNormHour;
        }


        @Override
        public int getMinutes() {
            if (type instanceof DurationDV) {
                return 0;
            }
            return normalized ? minute : unNormMinute;
        }


        @Override
        public double getSeconds() {
            if (type instanceof DurationDV) {
                return day * 24 * 60 * 60 + hour * 60 * 60 + minute * 60 + second;
            }
            return normalized ? second : unNormSecond;
        }


        @Override
        public boolean hasTimeZone() {
            return utc != 0;
        }


        @Override
        public int getTimeZoneHours() {
            return timezoneHr;
        }


        @Override
        public int getTimeZoneMinutes() {
            return timezoneMin;
        }


        @Override
        public String getLexicalValue() {
            return originalValue;
        }


        @Override
        public XSDateTime normalize() {
            if (!normalized) {
                DateTimeData dt = (DateTimeData) this.clone();
                dt.normalized = true;
                return dt;
            }
            return this;
        }


        @Override
        public boolean isNormalized() {
            return normalized;
        }

        @Override
        public Object clone() {
            DateTimeData dt = new DateTimeData(this.year, this.month, this.day, this.hour,
                    this.minute, this.second, this.utc, this.originalValue, this.normalized, this.type);
            dt.canonical = this.canonical;
            dt.position = position;
            dt.timezoneHr = this.timezoneHr;
            dt.timezoneMin = this.timezoneMin;
            dt.unNormYear = this.unNormYear;
            dt.unNormMonth = this.unNormMonth;
            dt.unNormDay = this.unNormDay;
            dt.unNormHour = this.unNormHour;
            dt.unNormMinute = this.unNormMinute;
            dt.unNormSecond = this.unNormSecond;
            return dt;
        }


        @Override
        public XMLGregorianCalendar getXMLGregorianCalendar() {
            return type.getXMLGregorianCalendar(this);
        }


        @Override
        public Duration getDuration() {
            return type.getDuration(this);
        }
    }

    protected XMLGregorianCalendar getXMLGregorianCalendar(DateTimeData data) {
        return null;
    }

    protected Duration getDuration(DateTimeData data) {
        return null;
    }

    protected final BigDecimal getFractionalSecondsAsBigDecimal(DateTimeData data) {
        final StringBuffer buf = new StringBuffer();
        append3(buf, data.unNormSecond);
        String value = buf.toString();
        final int index = value.indexOf('.');
        if (index == -1) {
            return null;
        }
        value = value.substring(index);
        final BigDecimal _val = new BigDecimal(value);
        if (_val.compareTo(BigDecimal.valueOf(0)) == 0) {
            return null;
        }
        return _val;
    }
}
