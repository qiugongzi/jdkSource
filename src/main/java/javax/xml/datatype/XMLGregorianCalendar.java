

package javax.xml.datatype;

import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.TimeZone;
import java.util.GregorianCalendar;



public abstract class XMLGregorianCalendar
        implements Cloneable {


         public XMLGregorianCalendar() {
         }


        public abstract void clear();


        public abstract void reset();


    public abstract void setYear(BigInteger year);


    public abstract void setYear(int year);


    public abstract void setMonth(int month);


    public abstract void setDay(int day);


    public abstract void setTimezone(int offset);


    public void setTime(int hour, int minute, int second) {

                setTime(
                        hour,
                        minute,
                        second,
                        null );
    }


        public abstract void setHour(int hour);


        public abstract void setMinute(int minute);


        public abstract void setSecond(int second);


        public abstract void setMillisecond(int millisecond);


        public abstract void setFractionalSecond(BigDecimal fractional);



    public void setTime(
        int hour,
        int minute,
        int second,
        BigDecimal fractional) {

                setHour(hour);
        setMinute(minute);
        setSecond(second);
        setFractionalSecond(fractional);
    }



    public void setTime(int hour, int minute, int second, int millisecond) {

        setHour(hour);
        setMinute(minute);
        setSecond(second);
        setMillisecond(millisecond);
    }


        public abstract BigInteger getEon();


        public abstract int getYear();


        public abstract BigInteger getEonAndYear();


        public abstract int getMonth();


        public abstract int getDay();


        public abstract int getTimezone();


        public abstract int getHour();


        public abstract int getMinute();


        public abstract int getSecond();


        public int getMillisecond() {

                BigDecimal fractionalSeconds = getFractionalSecond();

                if (fractionalSeconds == null) {
                        return DatatypeConstants.FIELD_UNDEFINED;
                }

                return getFractionalSecond().movePointRight(3).intValue();
        }


        public abstract BigDecimal getFractionalSecond();

    public abstract int compare(XMLGregorianCalendar xmlGregorianCalendar);


    public abstract XMLGregorianCalendar normalize();


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
            gc = this.normalize();
        }
        return gc.getYear()
                + gc.getMonth()
                + gc.getDay()
                + gc.getHour()
                + gc.getMinute()
                + gc.getSecond();
    }


    public abstract String toXMLFormat();


    public abstract QName getXMLSchemaType();


    public String toString() {

        return toXMLFormat();
    }


    public abstract boolean isValid();


    public abstract void add(Duration duration);


    public abstract GregorianCalendar toGregorianCalendar();


    public abstract GregorianCalendar toGregorianCalendar(
        java.util.TimeZone timezone,
                java.util.Locale aLocale,
                XMLGregorianCalendar defaults);


    public abstract TimeZone getTimeZone(int defaultZoneoffset);




   public abstract Object clone();
}
