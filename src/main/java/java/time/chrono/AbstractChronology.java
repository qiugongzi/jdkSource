


package java.time.chrono;

import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.PROLEPTIC_MONTH;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.YEAR_OF_ERA;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import sun.util.logging.PlatformLogger;


public abstract class AbstractChronology implements Chronology {


    static final Comparator<ChronoLocalDate> DATE_ORDER =
        (Comparator<ChronoLocalDate> & Serializable) (date1, date2) -> {
            return Long.compare(date1.toEpochDay(), date2.toEpochDay());
        };

    static final Comparator<ChronoLocalDateTime<? extends ChronoLocalDate>> DATE_TIME_ORDER =
        (Comparator<ChronoLocalDateTime<? extends ChronoLocalDate>> & Serializable) (dateTime1, dateTime2) -> {
            int cmp = Long.compare(dateTime1.toLocalDate().toEpochDay(), dateTime2.toLocalDate().toEpochDay());
            if (cmp == 0) {
                cmp = Long.compare(dateTime1.toLocalTime().toNanoOfDay(), dateTime2.toLocalTime().toNanoOfDay());
            }
            return cmp;
        };

    static final Comparator<ChronoZonedDateTime<?>> INSTANT_ORDER =
            (Comparator<ChronoZonedDateTime<?>> & Serializable) (dateTime1, dateTime2) -> {
                int cmp = Long.compare(dateTime1.toEpochSecond(), dateTime2.toEpochSecond());
                if (cmp == 0) {
                    cmp = Long.compare(dateTime1.toLocalTime().getNano(), dateTime2.toLocalTime().getNano());
                }
                return cmp;
            };


    private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_ID = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_TYPE = new ConcurrentHashMap<>();


    static Chronology registerChrono(Chronology chrono) {
        return registerChrono(chrono, chrono.getId());
    }


    static Chronology registerChrono(Chronology chrono, String id) {
        Chronology prev = CHRONOS_BY_ID.putIfAbsent(id, chrono);
        if (prev == null) {
            String type = chrono.getCalendarType();
            if (type != null) {
                CHRONOS_BY_TYPE.putIfAbsent(type, chrono);
            }
        }
        return prev;
    }


    private static boolean initCache() {
        if (CHRONOS_BY_ID.get("ISO") == null) {
            registerChrono(HijrahChronology.INSTANCE);
            registerChrono(JapaneseChronology.INSTANCE);
            registerChrono(MinguoChronology.INSTANCE);
            registerChrono(ThaiBuddhistChronology.INSTANCE);

            @SuppressWarnings("rawtypes")
            ServiceLoader<AbstractChronology> loader =  ServiceLoader.load(AbstractChronology.class, null);
            for (AbstractChronology chrono : loader) {
                String id = chrono.getId();
                if (id.equals("ISO") || registerChrono(chrono) != null) {
                    PlatformLogger logger = PlatformLogger.getLogger("java.time.chrono");
                    logger.warning("Ignoring duplicate Chronology, from ServiceLoader configuration "  + id);
                }
            }

            registerChrono(IsoChronology.INSTANCE);
            return true;
        }
        return false;
    }

    static Chronology ofLocale(Locale locale) {
        Objects.requireNonNull(locale, "locale");
        String type = locale.getUnicodeLocaleType("ca");
        if (type == null || "iso".equals(type) || "iso8601".equals(type)) {
            return IsoChronology.INSTANCE;
        }
        do {
            Chronology chrono = CHRONOS_BY_TYPE.get(type);
            if (chrono != null) {
                return chrono;
            }
            } while (initCache());

        @SuppressWarnings("rawtypes")
        ServiceLoader<Chronology> loader = ServiceLoader.load(Chronology.class);
        for (Chronology chrono : loader) {
            if (type.equals(chrono.getCalendarType())) {
                return chrono;
            }
        }
        throw new DateTimeException("Unknown calendar system: " + type);
    }

    static Chronology of(String id) {
        Objects.requireNonNull(id, "id");
        do {
            Chronology chrono = of0(id);
            if (chrono != null) {
                return chrono;
            }
            } while (initCache());

        @SuppressWarnings("rawtypes")
        ServiceLoader<Chronology> loader = ServiceLoader.load(Chronology.class);
        for (Chronology chrono : loader) {
            if (id.equals(chrono.getId()) || id.equals(chrono.getCalendarType())) {
                return chrono;
            }
        }
        throw new DateTimeException("Unknown chronology: " + id);
    }


    private static Chronology of0(String id) {
        Chronology chrono = CHRONOS_BY_ID.get(id);
        if (chrono == null) {
            chrono = CHRONOS_BY_TYPE.get(id);
        }
        return chrono;
    }


    static Set<Chronology> getAvailableChronologies() {
        initCache();       HashSet<Chronology> chronos = new HashSet<>(CHRONOS_BY_ID.values());

        @SuppressWarnings("rawtypes")
        ServiceLoader<Chronology> loader = ServiceLoader.load(Chronology.class);
        for (Chronology chrono : loader) {
            chronos.add(chrono);
        }
        return chronos;
    }

    protected AbstractChronology() {
    }

    @Override
    public ChronoLocalDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        if (fieldValues.containsKey(EPOCH_DAY)) {
            return dateEpochDay(fieldValues.remove(EPOCH_DAY));
        }

        resolveProlepticMonth(fieldValues, resolverStyle);

        ChronoLocalDate resolved = resolveYearOfEra(fieldValues, resolverStyle);
        if (resolved != null) {
            return resolved;
        }

        if (fieldValues.containsKey(YEAR)) {
            if (fieldValues.containsKey(MONTH_OF_YEAR)) {
                if (fieldValues.containsKey(DAY_OF_MONTH)) {
                    return resolveYMD(fieldValues, resolverStyle);
                }
                if (fieldValues.containsKey(ALIGNED_WEEK_OF_MONTH)) {
                    if (fieldValues.containsKey(ALIGNED_DAY_OF_WEEK_IN_MONTH)) {
                        return resolveYMAA(fieldValues, resolverStyle);
                    }
                    if (fieldValues.containsKey(DAY_OF_WEEK)) {
                        return resolveYMAD(fieldValues, resolverStyle);
                    }
                }
            }
            if (fieldValues.containsKey(DAY_OF_YEAR)) {
                return resolveYD(fieldValues, resolverStyle);
            }
            if (fieldValues.containsKey(ALIGNED_WEEK_OF_YEAR)) {
                if (fieldValues.containsKey(ALIGNED_DAY_OF_WEEK_IN_YEAR)) {
                    return resolveYAA(fieldValues, resolverStyle);
                }
                if (fieldValues.containsKey(DAY_OF_WEEK)) {
                    return resolveYAD(fieldValues, resolverStyle);
                }
            }
        }
        return null;
    }

    void resolveProlepticMonth(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        Long pMonth = fieldValues.remove(PROLEPTIC_MONTH);
        if (pMonth != null) {
            if (resolverStyle != ResolverStyle.LENIENT) {
                PROLEPTIC_MONTH.checkValidValue(pMonth);
            }
            ChronoLocalDate chronoDate = dateNow()
                    .with(DAY_OF_MONTH, 1).with(PROLEPTIC_MONTH, pMonth);
            addFieldValue(fieldValues, MONTH_OF_YEAR, chronoDate.get(MONTH_OF_YEAR));
            addFieldValue(fieldValues, YEAR, chronoDate.get(YEAR));
        }
    }

    ChronoLocalDate resolveYearOfEra(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        Long yoeLong = fieldValues.remove(YEAR_OF_ERA);
        if (yoeLong != null) {
            Long eraLong = fieldValues.remove(ERA);
            int yoe;
            if (resolverStyle != ResolverStyle.LENIENT) {
                yoe = range(YEAR_OF_ERA).checkValidIntValue(yoeLong, YEAR_OF_ERA);
            } else {
                yoe = Math.toIntExact(yoeLong);
            }
            if (eraLong != null) {
                Era eraObj = eraOf(range(ERA).checkValidIntValue(eraLong, ERA));
                addFieldValue(fieldValues, YEAR, prolepticYear(eraObj, yoe));
            } else {
                if (fieldValues.containsKey(YEAR)) {
                    int year = range(YEAR).checkValidIntValue(fieldValues.get(YEAR), YEAR);
                    ChronoLocalDate chronoDate = dateYearDay(year, 1);
                    addFieldValue(fieldValues, YEAR, prolepticYear(chronoDate.getEra(), yoe));
                } else if (resolverStyle == ResolverStyle.STRICT) {
                    fieldValues.put(YEAR_OF_ERA, yoeLong);
                } else {
                    List<Era> eras = eras();
                    if (eras.isEmpty()) {
                        addFieldValue(fieldValues, YEAR, yoe);
                    } else {
                        Era eraObj = eras.get(eras.size() - 1);
                        addFieldValue(fieldValues, YEAR, prolepticYear(eraObj, yoe));
                    }
                }
            }
        } else if (fieldValues.containsKey(ERA)) {
            range(ERA).checkValidValue(fieldValues.get(ERA), ERA);  }
        return null;
    }

    ChronoLocalDate resolveYMD(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int y = range(YEAR).checkValidIntValue(fieldValues.remove(YEAR), YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long months = Math.subtractExact(fieldValues.remove(MONTH_OF_YEAR), 1);
            long days = Math.subtractExact(fieldValues.remove(DAY_OF_MONTH), 1);
            return date(y, 1, 1).plus(months, MONTHS).plus(days, DAYS);
        }
        int moy = range(MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(MONTH_OF_YEAR), MONTH_OF_YEAR);
        ValueRange domRange = range(DAY_OF_MONTH);
        int dom = domRange.checkValidIntValue(fieldValues.remove(DAY_OF_MONTH), DAY_OF_MONTH);
        if (resolverStyle == ResolverStyle.SMART) {  try {
                return date(y, moy, dom);
            } catch (DateTimeException ex) {
                return date(y, moy, 1).with(TemporalAdjusters.lastDayOfMonth());
            }
        }
        return date(y, moy, dom);
    }

    ChronoLocalDate resolveYD(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int y = range(YEAR).checkValidIntValue(fieldValues.remove(YEAR), YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long days = Math.subtractExact(fieldValues.remove(DAY_OF_YEAR), 1);
            return dateYearDay(y, 1).plus(days, DAYS);
        }
        int doy = range(DAY_OF_YEAR).checkValidIntValue(fieldValues.remove(DAY_OF_YEAR), DAY_OF_YEAR);
        return dateYearDay(y, doy);  }

    ChronoLocalDate resolveYMAA(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int y = range(YEAR).checkValidIntValue(fieldValues.remove(YEAR), YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long months = Math.subtractExact(fieldValues.remove(MONTH_OF_YEAR), 1);
            long weeks = Math.subtractExact(fieldValues.remove(ALIGNED_WEEK_OF_MONTH), 1);
            long days = Math.subtractExact(fieldValues.remove(ALIGNED_DAY_OF_WEEK_IN_MONTH), 1);
            return date(y, 1, 1).plus(months, MONTHS).plus(weeks, WEEKS).plus(days, DAYS);
        }
        int moy = range(MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(MONTH_OF_YEAR), MONTH_OF_YEAR);
        int aw = range(ALIGNED_WEEK_OF_MONTH).checkValidIntValue(fieldValues.remove(ALIGNED_WEEK_OF_MONTH), ALIGNED_WEEK_OF_MONTH);
        int ad = range(ALIGNED_DAY_OF_WEEK_IN_MONTH).checkValidIntValue(fieldValues.remove(ALIGNED_DAY_OF_WEEK_IN_MONTH), ALIGNED_DAY_OF_WEEK_IN_MONTH);
        ChronoLocalDate date = date(y, moy, 1).plus((aw - 1) * 7 + (ad - 1), DAYS);
        if (resolverStyle == ResolverStyle.STRICT && date.get(MONTH_OF_YEAR) != moy) {
            throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
        }
        return date;
    }

    ChronoLocalDate resolveYMAD(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int y = range(YEAR).checkValidIntValue(fieldValues.remove(YEAR), YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long months = Math.subtractExact(fieldValues.remove(MONTH_OF_YEAR), 1);
            long weeks = Math.subtractExact(fieldValues.remove(ALIGNED_WEEK_OF_MONTH), 1);
            long dow = Math.subtractExact(fieldValues.remove(DAY_OF_WEEK), 1);
            return resolveAligned(date(y, 1, 1), months, weeks, dow);
        }
        int moy = range(MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(MONTH_OF_YEAR), MONTH_OF_YEAR);
        int aw = range(ALIGNED_WEEK_OF_MONTH).checkValidIntValue(fieldValues.remove(ALIGNED_WEEK_OF_MONTH), ALIGNED_WEEK_OF_MONTH);
        int dow = range(DAY_OF_WEEK).checkValidIntValue(fieldValues.remove(DAY_OF_WEEK), DAY_OF_WEEK);
        ChronoLocalDate date = date(y, moy, 1).plus((aw - 1) * 7, DAYS).with(nextOrSame(DayOfWeek.of(dow)));
        if (resolverStyle == ResolverStyle.STRICT && date.get(MONTH_OF_YEAR) != moy) {
            throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
        }
        return date;
    }

    ChronoLocalDate resolveYAA(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int y = range(YEAR).checkValidIntValue(fieldValues.remove(YEAR), YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long weeks = Math.subtractExact(fieldValues.remove(ALIGNED_WEEK_OF_YEAR), 1);
            long days = Math.subtractExact(fieldValues.remove(ALIGNED_DAY_OF_WEEK_IN_YEAR), 1);
            return dateYearDay(y, 1).plus(weeks, WEEKS).plus(days, DAYS);
        }
        int aw = range(ALIGNED_WEEK_OF_YEAR).checkValidIntValue(fieldValues.remove(ALIGNED_WEEK_OF_YEAR), ALIGNED_WEEK_OF_YEAR);
        int ad = range(ALIGNED_DAY_OF_WEEK_IN_YEAR).checkValidIntValue(fieldValues.remove(ALIGNED_DAY_OF_WEEK_IN_YEAR), ALIGNED_DAY_OF_WEEK_IN_YEAR);
        ChronoLocalDate date = dateYearDay(y, 1).plus((aw - 1) * 7 + (ad - 1), DAYS);
        if (resolverStyle == ResolverStyle.STRICT && date.get(YEAR) != y) {
            throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
        }
        return date;
    }

    ChronoLocalDate resolveYAD(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int y = range(YEAR).checkValidIntValue(fieldValues.remove(YEAR), YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long weeks = Math.subtractExact(fieldValues.remove(ALIGNED_WEEK_OF_YEAR), 1);
            long dow = Math.subtractExact(fieldValues.remove(DAY_OF_WEEK), 1);
            return resolveAligned(dateYearDay(y, 1), 0, weeks, dow);
        }
        int aw = range(ALIGNED_WEEK_OF_YEAR).checkValidIntValue(fieldValues.remove(ALIGNED_WEEK_OF_YEAR), ALIGNED_WEEK_OF_YEAR);
        int dow = range(DAY_OF_WEEK).checkValidIntValue(fieldValues.remove(DAY_OF_WEEK), DAY_OF_WEEK);
        ChronoLocalDate date = dateYearDay(y, 1).plus((aw - 1) * 7, DAYS).with(nextOrSame(DayOfWeek.of(dow)));
        if (resolverStyle == ResolverStyle.STRICT && date.get(YEAR) != y) {
            throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
        }
        return date;
    }

    ChronoLocalDate resolveAligned(ChronoLocalDate base, long months, long weeks, long dow) {
        ChronoLocalDate date = base.plus(months, MONTHS).plus(weeks, WEEKS);
        if (dow > 7) {
            date = date.plus((dow - 1) / 7, WEEKS);
            dow = ((dow - 1) % 7) + 1;
        } else if (dow < 1) {
            date = date.plus(Math.subtractExact(dow,  7) / 7, WEEKS);
            dow = ((dow + 6) % 7) + 1;
        }
        return date.with(nextOrSame(DayOfWeek.of((int) dow)));
    }


    void addFieldValue(Map<TemporalField, Long> fieldValues, ChronoField field, long value) {
        Long old = fieldValues.get(field);  if (old != null && old.longValue() != value) {
            throw new DateTimeException("Conflict found: " + field + " " + old + " differs from " + field + " " + value);
        }
        fieldValues.put(field, value);
    }

    @Override
    public int compareTo(Chronology other) {
        return getId().compareTo(other.getId());
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
           return true;
        }
        if (obj instanceof AbstractChronology) {
            return compareTo((AbstractChronology) obj) == 0;
        }
        return false;
    }


    @Override
    public int hashCode() {
        return getClass().hashCode() ^ getId().hashCode();
    }

    @Override
    public String toString() {
        return getId();
    }

    Object writeReplace() {
        return new Ser(Ser.CHRONO_TYPE, this);
    }


    private void readObject(ObjectInputStream s) throws ObjectStreamException {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    void writeExternal(DataOutput out) throws IOException {
        out.writeUTF(getId());
    }

    static Chronology readExternal(DataInput in) throws IOException {
        String id = in.readUTF();
        return Chronology.of(id);
    }

}
