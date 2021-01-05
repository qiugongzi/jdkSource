


package com.sun.org.apache.xerces.internal.impl.dv.xs;

import java.math.BigInteger;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;


public class DateTimeDV extends AbstractDateTimeDV {

    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
        try{
            return parse(content);
        } catch(Exception ex){
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "dateTime"});
        }
    }


    protected DateTimeData parse(String str) throws SchemaDateTimeException {
        DateTimeData date = new DateTimeData(str, this);
        int len = str.length();

        int end = indexOf (str, 0, len, 'T');

        int dateEnd = getDate(str, 0, end, date);
        getTime(str, end+1, len, date);

        if (dateEnd != end) {
            throw new RuntimeException(str
                    + " is an invalid dateTime dataype value. "
                    + "Invalid character(s) seprating date and time values.");
        }

        validateDateTime(date);

        saveUnnormalized(date);

        if (date.utc!=0 && date.utc!='Z') {
            normalize(date);
        }
        return date;
    }

    protected XMLGregorianCalendar getXMLGregorianCalendar(DateTimeData date) {
        return datatypeFactory.newXMLGregorianCalendar(BigInteger.valueOf(date.unNormYear), date.unNormMonth,
                date.unNormDay, date.unNormHour, date.unNormMinute,
                (int)date.unNormSecond, date.unNormSecond != 0 ? getFractionalSecondsAsBigDecimal(date) : null,
                date.hasTimeZone() ? (date.timezoneHr * 60 + date.timezoneMin) : DatatypeConstants.FIELD_UNDEFINED);
    }
}
