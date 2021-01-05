


package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;


public class DatatypeFactoryImpl
        extends DatatypeFactory {


        public DatatypeFactoryImpl() {
        }


        public Duration newDuration(final String lexicalRepresentation) {

                return new DurationImpl(lexicalRepresentation);
        }


        public Duration newDuration(final long durationInMilliseconds) {

                return new DurationImpl(durationInMilliseconds);
        }


        public Duration newDuration(
                final boolean isPositive,
                final BigInteger years,
                final BigInteger months,
                final BigInteger days,
                final BigInteger hours,
                final BigInteger minutes,
                final BigDecimal seconds) {

                return new DurationImpl(
                                isPositive,
                                years,
                                months,
                                days,
                                hours,
                                minutes,
                                seconds
                        );
                }

        public Duration newDurationYearMonth(
                final boolean isPositive,
                final BigInteger year,
                final BigInteger month) {

                return new DurationYearMonthImpl(
                         isPositive,
                         year,
                         month
                 );

        }

    @Override
        public Duration newDurationYearMonth(
                final boolean isPositive,
                final int year,
                final int month) {

                return new DurationYearMonthImpl(
                        isPositive,
                        year,
                        month);
                }


        public Duration newDurationYearMonth(
                final String lexicalRepresentation) {

                return new DurationYearMonthImpl(lexicalRepresentation);

        }

    public Duration newDurationYearMonth(
            final long durationInMilliseconds) {

        return new DurationYearMonthImpl(durationInMilliseconds);
    }


        public Duration newDurationDayTime(final String lexicalRepresentation) {
            if (lexicalRepresentation == null) {
                throw new NullPointerException(
                    "Trying to create an xdt:dayTimeDuration with an invalid"
                    + " lexical representation of \"null\"");
            }

            return new DurationDayTimeImpl(lexicalRepresentation);
        }


        public Duration newDurationDayTime(final long durationInMilliseconds) {

                return new DurationDayTimeImpl(durationInMilliseconds);
        }


        public Duration newDurationDayTime(
                final boolean isPositive,
                final BigInteger day,
                final BigInteger hour,
                final BigInteger minute,
                final BigInteger second) {

                return new DurationDayTimeImpl(
                        isPositive,
                        day,
                        hour,
                        minute,
                        (second != null)? new BigDecimal(second):null
                );
        }


        public Duration newDurationDayTime(
                final boolean isPositive,
                final int day,
                final int hour,
                final int minute,
                final int second) {

                        return new DurationDayTimeImpl(
                                isPositive,
                                day,
                                hour,
                                minute,
                                second
                                );
                }


        public XMLGregorianCalendar newXMLGregorianCalendar() {

                return new XMLGregorianCalendarImpl();
        }


        public XMLGregorianCalendar newXMLGregorianCalendar(final String lexicalRepresentation) {

                return new XMLGregorianCalendarImpl(lexicalRepresentation);
        }


        public XMLGregorianCalendar newXMLGregorianCalendar(final GregorianCalendar cal) {

                return new XMLGregorianCalendarImpl(cal);
        }


        public XMLGregorianCalendar newXMLGregorianCalendar(
                final BigInteger year,
                final int month,
                final int day,
                final int hour,
                final int minute,
                final int second,
                final BigDecimal fractionalSecond,
                final int timezone) {

                return new XMLGregorianCalendarImpl(
                        year,
                        month,
                        day,
                        hour,
                        minute,
                        second,
                        fractionalSecond,
                        timezone
                );
        }
}
