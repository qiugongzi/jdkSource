


package java.time.chrono;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sun.util.calendar.CalendarSystem;
import sun.util.calendar.LocalGregorianCalendar;


public final class JapaneseChronology extends AbstractChronology implements Serializable {

    static final LocalGregorianCalendar JCAL =
        (LocalGregorianCalendar) CalendarSystem.forName("japanese");

    static final Locale LOCALE = Locale.forLanguageTag("ja-JP-u-ca-japanese");


    public static final JapaneseChronology INSTANCE = new JapaneseChronology();


    private static final long serialVersionUID = 459996390165777884L;

    private JapaneseChronology() {
    }

    @Override
    public String getId() {
        return "Japanese";
    }


    @Override
    public String getCalendarType() {
        return "japanese";
    }

    @Override
    public JapaneseDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        if (era instanceof JapaneseEra == false) {
            throw new ClassCastException("Era must be JapaneseEra");
        }
        return JapaneseDate.of((JapaneseEra) era, yearOfEra, month, dayOfMonth);
    }


    @Override
    public JapaneseDate date(int prolepticYear, int month, int dayOfMonth) {
        return new JapaneseDate(LocalDate.of(prolepticYear, month, dayOfMonth));
    }


    @Override
    public JapaneseDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return JapaneseDate.ofYearDay((JapaneseEra) era, yearOfEra, dayOfYear);
    }


    @Override
    public JapaneseDate dateYearDay(int prolepticYear, int dayOfYear) {
        return new JapaneseDate(LocalDate.ofYearDay(prolepticYear, dayOfYear));
    }


    @Override  public JapaneseDate dateEpochDay(long epochDay) {
        return new JapaneseDate(LocalDate.ofEpochDay(epochDay));
    }

    @Override
    public JapaneseDate dateNow() {
        return dateNow(Clock.systemDefaultZone());
    }

    @Override
    public JapaneseDate dateNow(ZoneId zone) {
        return dateNow(Clock.system(zone));
    }

    @Override
    public JapaneseDate dateNow(Clock clock) {
        return date(LocalDate.now(clock));
    }

    @Override
    public JapaneseDate date(TemporalAccessor temporal) {
        if (temporal instanceof JapaneseDate) {
            return (JapaneseDate) temporal;
        }
        return new JapaneseDate(LocalDate.from(temporal));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<JapaneseDate> localDateTime(TemporalAccessor temporal) {
        return (ChronoLocalDateTime<JapaneseDate>)super.localDateTime(temporal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<JapaneseDate> zonedDateTime(TemporalAccessor temporal) {
        return (ChronoZonedDateTime<JapaneseDate>)super.zonedDateTime(temporal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<JapaneseDate> zonedDateTime(Instant instant, ZoneId zone) {
        return (ChronoZonedDateTime<JapaneseDate>)super.zonedDateTime(instant, zone);
    }

    @Override
    public boolean isLeapYear(long prolepticYear) {
        return IsoChronology.INSTANCE.isLeapYear(prolepticYear);
    }

    @Override
    public int prolepticYear(Era era, int yearOfEra) {
        if (era instanceof JapaneseEra == false) {
            throw new ClassCastException("Era must be JapaneseEra");
        }

        JapaneseEra jera = (JapaneseEra) era;
        int gregorianYear = jera.getPrivateEra().getSinceDate().getYear() + yearOfEra - 1;
        if (yearOfEra == 1) {
            return gregorianYear;
        }
        if (gregorianYear >= Year.MIN_VALUE && gregorianYear <= Year.MAX_VALUE) {
            LocalGregorianCalendar.Date jdate = JCAL.newCalendarDate(null);
            jdate.setEra(jera.getPrivateEra()).setDate(yearOfEra, 1, 1);
            if (JapaneseChronology.JCAL.validate(jdate)) {
                return gregorianYear;
            }
        }
        throw new DateTimeException("Invalid yearOfEra value");
    }


    @Override
    public JapaneseEra eraOf(int eraValue) {
        return JapaneseEra.of(eraValue);
    }

    @Override
    public List<Era> eras() {
        return Arrays.<Era>asList(JapaneseEra.values());
    }

    JapaneseEra getCurrentEra() {
        JapaneseEra[] eras = JapaneseEra.values();
        return eras[eras.length - 1];
    }

    @Override
    public ValueRange range(ChronoField field) {
        switch (field) {
            case ALIGNED_DAY_OF_WEEK_IN_MONTH:
            case ALIGNED_DAY_OF_WEEK_IN_YEAR:
            case ALIGNED_WEEK_OF_MONTH:
            case ALIGNED_WEEK_OF_YEAR:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            case YEAR_OF_ERA: {
                Calendar jcal = Calendar.getInstance(LOCALE);
                int startYear = getCurrentEra().getPrivateEra().getSinceDate().getYear();
                return ValueRange.of(1, jcal.getGreatestMinimum(Calendar.YEAR),
                        jcal.getLeastMaximum(Calendar.YEAR) + 1, Year.MAX_VALUE - startYear);
            }
            case DAY_OF_YEAR: {
                Calendar jcal = Calendar.getInstance(LOCALE);
                int fieldIndex = Calendar.DAY_OF_YEAR;
                return ValueRange.of(jcal.getMinimum(fieldIndex), jcal.getGreatestMinimum(fieldIndex),
                        jcal.getLeastMaximum(fieldIndex), jcal.getMaximum(fieldIndex));
            }
            case YEAR:
                return ValueRange.of(JapaneseDate.MEIJI_6_ISODATE.getYear(), Year.MAX_VALUE);
            case ERA:
                return ValueRange.of(JapaneseEra.MEIJI.getValue(), getCurrentEra().getValue());
            default:
                return field.range();
        }
    }

    @Override  public JapaneseDate resolveDate(Map <TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        return (JapaneseDate) super.resolveDate(fieldValues, resolverStyle);
    }

    @Override  ChronoLocalDate resolveYearOfEra(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        Long eraLong = fieldValues.get(ERA);
        JapaneseEra era = null;
        if (eraLong != null) {
            era = eraOf(range(ERA).checkValidIntValue(eraLong, ERA));  }
        Long yoeLong = fieldValues.get(YEAR_OF_ERA);
        int yoe = 0;
        if (yoeLong != null) {
            yoe = range(YEAR_OF_ERA).checkValidIntValue(yoeLong, YEAR_OF_ERA);  }
        if (era == null && yoeLong != null && fieldValues.containsKey(YEAR) == false && resolverStyle != ResolverStyle.STRICT) {
            era = JapaneseEra.values()[JapaneseEra.values().length - 1];
        }
        if (yoeLong != null && era != null) {
            if (fieldValues.containsKey(MONTH_OF_YEAR)) {
                if (fieldValues.containsKey(DAY_OF_MONTH)) {
                    return resolveYMD(era, yoe, fieldValues, resolverStyle);
                }
            }
            if (fieldValues.containsKey(DAY_OF_YEAR)) {
                return resolveYD(era, yoe, fieldValues, resolverStyle);
            }
        }
        return null;
    }

    private int prolepticYearLenient(JapaneseEra era, int yearOfEra) {
        return era.getPrivateEra().getSinceDate().getYear() + yearOfEra - 1;
    }

     private ChronoLocalDate resolveYMD(JapaneseEra era, int yoe, Map<TemporalField,Long> fieldValues, ResolverStyle resolverStyle) {
         fieldValues.remove(ERA);
         fieldValues.remove(YEAR_OF_ERA);
         if (resolverStyle == ResolverStyle.LENIENT) {
             int y = prolepticYearLenient(era, yoe);
             long months = Math.subtractExact(fieldValues.remove(MONTH_OF_YEAR), 1);
             long days = Math.subtractExact(fieldValues.remove(DAY_OF_MONTH), 1);
             return date(y, 1, 1).plus(months, MONTHS).plus(days, DAYS);
         }
         int moy = range(MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(MONTH_OF_YEAR), MONTH_OF_YEAR);
         int dom = range(DAY_OF_MONTH).checkValidIntValue(fieldValues.remove(DAY_OF_MONTH), DAY_OF_MONTH);
         if (resolverStyle == ResolverStyle.SMART) {  if (yoe < 1) {
                 throw new DateTimeException("Invalid YearOfEra: " + yoe);
             }
             int y = prolepticYearLenient(era, yoe);
             JapaneseDate result;
             try {
                 result = date(y, moy, dom);
             } catch (DateTimeException ex) {
                 result = date(y, moy, 1).with(TemporalAdjusters.lastDayOfMonth());
             }
             if (result.getEra() != era && result.get(YEAR_OF_ERA) > 1 && yoe > 1) {
                 throw new DateTimeException("Invalid YearOfEra for Era: " + era + " " + yoe);
             }
             return result;
         }
         return date(era, yoe, moy, dom);
     }

    private ChronoLocalDate resolveYD(JapaneseEra era, int yoe, Map <TemporalField,Long> fieldValues, ResolverStyle resolverStyle) {
        fieldValues.remove(ERA);
        fieldValues.remove(YEAR_OF_ERA);
        if (resolverStyle == ResolverStyle.LENIENT) {
            int y = prolepticYearLenient(era, yoe);
            long days = Math.subtractExact(fieldValues.remove(DAY_OF_YEAR), 1);
            return dateYearDay(y, 1).plus(days, DAYS);
        }
        int doy = range(DAY_OF_YEAR).checkValidIntValue(fieldValues.remove(DAY_OF_YEAR), DAY_OF_YEAR);
        return dateYearDay(era, yoe, doy);  }

    @Override
    Object writeReplace() {
        return super.writeReplace();
    }


    private void readObject(ObjectInputStream s) throws InvalidObjectException {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }
}
