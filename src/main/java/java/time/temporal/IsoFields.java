


package java.time.temporal;

import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.FOREVER;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;


public final class IsoFields {


    public static final TemporalField DAY_OF_QUARTER = Field.DAY_OF_QUARTER;

    public static final TemporalField QUARTER_OF_YEAR = Field.QUARTER_OF_YEAR;

    public static final TemporalField WEEK_OF_WEEK_BASED_YEAR = Field.WEEK_OF_WEEK_BASED_YEAR;

    public static final TemporalField WEEK_BASED_YEAR = Field.WEEK_BASED_YEAR;

    public static final TemporalUnit WEEK_BASED_YEARS = Unit.WEEK_BASED_YEARS;

    public static final TemporalUnit QUARTER_YEARS = Unit.QUARTER_YEARS;


    private IsoFields() {
        throw new AssertionError("Not instantiable");
    }

    private static enum Field implements TemporalField {
        DAY_OF_QUARTER {
            @Override
            public TemporalUnit getBaseUnit() {
                return DAYS;
            }
            @Override
            public TemporalUnit getRangeUnit() {
                return QUARTER_YEARS;
            }
            @Override
            public ValueRange range() {
                return ValueRange.of(1, 90, 92);
            }
            @Override
            public boolean isSupportedBy(TemporalAccessor temporal) {
                return temporal.isSupported(DAY_OF_YEAR) && temporal.isSupported(MONTH_OF_YEAR) &&
                        temporal.isSupported(YEAR) && isIso(temporal);
            }
            @Override
            public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
                if (isSupportedBy(temporal) == false) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: DayOfQuarter");
                }
                long qoy = temporal.getLong(QUARTER_OF_YEAR);
                if (qoy == 1) {
                    long year = temporal.getLong(YEAR);
                    return (IsoChronology.INSTANCE.isLeapYear(year) ? ValueRange.of(1, 91) : ValueRange.of(1, 90));
                } else if (qoy == 2) {
                    return ValueRange.of(1, 91);
                } else if (qoy == 3 || qoy == 4) {
                    return ValueRange.of(1, 92);
                } return range();
            }
            @Override
            public long getFrom(TemporalAccessor temporal) {
                if (isSupportedBy(temporal) == false) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: DayOfQuarter");
                }
                int doy = temporal.get(DAY_OF_YEAR);
                int moy = temporal.get(MONTH_OF_YEAR);
                long year = temporal.getLong(YEAR);
                return doy - QUARTER_DAYS[((moy - 1) / 3) + (IsoChronology.INSTANCE.isLeapYear(year) ? 4 : 0)];
            }
            @SuppressWarnings("unchecked")
            @Override
            public <R extends Temporal> R adjustInto(R temporal, long newValue) {
                long curValue = getFrom(temporal);
                range().checkValidValue(newValue, this);  return (R) temporal.with(DAY_OF_YEAR, temporal.getLong(DAY_OF_YEAR) + (newValue - curValue));
            }
            @Override
            public ChronoLocalDate resolve(
                    Map<TemporalField, Long> fieldValues, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
                Long yearLong = fieldValues.get(YEAR);
                Long qoyLong = fieldValues.get(QUARTER_OF_YEAR);
                if (yearLong == null || qoyLong == null) {
                    return null;
                }
                int y = YEAR.checkValidIntValue(yearLong);  long doq = fieldValues.get(DAY_OF_QUARTER);
                ensureIso(partialTemporal);
                LocalDate date;
                if (resolverStyle == ResolverStyle.LENIENT) {
                    date = LocalDate.of(y, 1, 1).plusMonths(Math.multiplyExact(Math.subtractExact(qoyLong, 1), 3));
                    doq = Math.subtractExact(doq, 1);
                } else {
                    int qoy = QUARTER_OF_YEAR.range().checkValidIntValue(qoyLong, QUARTER_OF_YEAR);  date = LocalDate.of(y, ((qoy - 1) * 3) + 1, 1);
                    if (doq < 1 || doq > 90) {
                        if (resolverStyle == ResolverStyle.STRICT) {
                            rangeRefinedBy(date).checkValidValue(doq, this);  } else {  range().checkValidValue(doq, this);  }
                    }
                    doq--;
                }
                fieldValues.remove(this);
                fieldValues.remove(YEAR);
                fieldValues.remove(QUARTER_OF_YEAR);
                return date.plusDays(doq);
            }
            @Override
            public String toString() {
                return "DayOfQuarter";
            }
        },
        QUARTER_OF_YEAR {
            @Override
            public TemporalUnit getBaseUnit() {
                return QUARTER_YEARS;
            }
            @Override
            public TemporalUnit getRangeUnit() {
                return YEARS;
            }
            @Override
            public ValueRange range() {
                return ValueRange.of(1, 4);
            }
            @Override
            public boolean isSupportedBy(TemporalAccessor temporal) {
                return temporal.isSupported(MONTH_OF_YEAR) && isIso(temporal);
            }
            @Override
            public long getFrom(TemporalAccessor temporal) {
                if (isSupportedBy(temporal) == false) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: QuarterOfYear");
                }
                long moy = temporal.getLong(MONTH_OF_YEAR);
                return ((moy + 2) / 3);
            }
            @SuppressWarnings("unchecked")
            @Override
            public <R extends Temporal> R adjustInto(R temporal, long newValue) {
                long curValue = getFrom(temporal);
                range().checkValidValue(newValue, this);  return (R) temporal.with(MONTH_OF_YEAR, temporal.getLong(MONTH_OF_YEAR) + (newValue - curValue) * 3);
            }
            @Override
            public String toString() {
                return "QuarterOfYear";
            }
        },
        WEEK_OF_WEEK_BASED_YEAR {
            @Override
            public String getDisplayName(Locale locale) {
                Objects.requireNonNull(locale, "locale");
                LocaleResources lr = LocaleProviderAdapter.getResourceBundleBased()
                                            .getLocaleResources(locale);
                ResourceBundle rb = lr.getJavaTimeFormatData();
                return rb.containsKey("field.week") ? rb.getString("field.week") : toString();
            }

            @Override
            public TemporalUnit getBaseUnit() {
                return WEEKS;
            }
            @Override
            public TemporalUnit getRangeUnit() {
                return WEEK_BASED_YEARS;
            }
            @Override
            public ValueRange range() {
                return ValueRange.of(1, 52, 53);
            }
            @Override
            public boolean isSupportedBy(TemporalAccessor temporal) {
                return temporal.isSupported(EPOCH_DAY) && isIso(temporal);
            }
            @Override
            public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
                if (isSupportedBy(temporal) == false) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: WeekOfWeekBasedYear");
                }
                return getWeekRange(LocalDate.from(temporal));
            }
            @Override
            public long getFrom(TemporalAccessor temporal) {
                if (isSupportedBy(temporal) == false) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: WeekOfWeekBasedYear");
                }
                return getWeek(LocalDate.from(temporal));
            }
            @SuppressWarnings("unchecked")
            @Override
            public <R extends Temporal> R adjustInto(R temporal, long newValue) {
                range().checkValidValue(newValue, this);  return (R) temporal.plus(Math.subtractExact(newValue, getFrom(temporal)), WEEKS);
            }
            @Override
            public ChronoLocalDate resolve(
                    Map<TemporalField, Long> fieldValues, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
                Long wbyLong = fieldValues.get(WEEK_BASED_YEAR);
                Long dowLong = fieldValues.get(DAY_OF_WEEK);
                if (wbyLong == null || dowLong == null) {
                    return null;
                }
                int wby = WEEK_BASED_YEAR.range().checkValidIntValue(wbyLong, WEEK_BASED_YEAR);  long wowby = fieldValues.get(WEEK_OF_WEEK_BASED_YEAR);
                ensureIso(partialTemporal);
                LocalDate date = LocalDate.of(wby, 1, 4);
                if (resolverStyle == ResolverStyle.LENIENT) {
                    long dow = dowLong;  if (dow > 7) {
                        date = date.plusWeeks((dow - 1) / 7);
                        dow = ((dow - 1) % 7) + 1;
                    } else if (dow < 1) {
                        date = date.plusWeeks(Math.subtractExact(dow,  7) / 7);
                        dow = ((dow + 6) % 7) + 1;
                    }
                    date = date.plusWeeks(Math.subtractExact(wowby, 1)).with(DAY_OF_WEEK, dow);
                } else {
                    int dow = DAY_OF_WEEK.checkValidIntValue(dowLong);  if (wowby < 1 || wowby > 52) {
                        if (resolverStyle == ResolverStyle.STRICT) {
                            getWeekRange(date).checkValidValue(wowby, this);  } else {  range().checkValidValue(wowby, this);  }
                    }
                    date = date.plusWeeks(wowby - 1).with(DAY_OF_WEEK, dow);
                }
                fieldValues.remove(this);
                fieldValues.remove(WEEK_BASED_YEAR);
                fieldValues.remove(DAY_OF_WEEK);
                return date;
            }
            @Override
            public String toString() {
                return "WeekOfWeekBasedYear";
            }
        },
        WEEK_BASED_YEAR {
            @Override
            public TemporalUnit getBaseUnit() {
                return WEEK_BASED_YEARS;
            }
            @Override
            public TemporalUnit getRangeUnit() {
                return FOREVER;
            }
            @Override
            public ValueRange range() {
                return YEAR.range();
            }
            @Override
            public boolean isSupportedBy(TemporalAccessor temporal) {
                return temporal.isSupported(EPOCH_DAY) && isIso(temporal);
            }
            @Override
            public long getFrom(TemporalAccessor temporal) {
                if (isSupportedBy(temporal) == false) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: WeekBasedYear");
                }
                return getWeekBasedYear(LocalDate.from(temporal));
            }
            @SuppressWarnings("unchecked")
            @Override
            public <R extends Temporal> R adjustInto(R temporal, long newValue) {
                if (isSupportedBy(temporal) == false) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: WeekBasedYear");
                }
                int newWby = range().checkValidIntValue(newValue, WEEK_BASED_YEAR);  LocalDate date = LocalDate.from(temporal);
                int dow = date.get(DAY_OF_WEEK);
                int week = getWeek(date);
                if (week == 53 && getWeekRange(newWby) == 52) {
                    week = 52;
                }
                LocalDate resolved = LocalDate.of(newWby, 1, 4);  int days = (dow - resolved.get(DAY_OF_WEEK)) + ((week - 1) * 7);
                resolved = resolved.plusDays(days);
                return (R) temporal.with(resolved);
            }
            @Override
            public String toString() {
                return "WeekBasedYear";
            }
        };

        @Override
        public boolean isDateBased() {
            return true;
        }

        @Override
        public boolean isTimeBased() {
            return false;
        }

        @Override
        public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
            return range();
        }

        private static final int[] QUARTER_DAYS = {0, 90, 181, 273, 0, 91, 182, 274};

        private static boolean isIso(TemporalAccessor temporal) {
            return Chronology.from(temporal).equals(IsoChronology.INSTANCE);
        }

        private static void ensureIso(TemporalAccessor temporal) {
            if (isIso(temporal) == false) {
                throw new DateTimeException("Resolve requires IsoChronology");
            }
        }

        private static ValueRange getWeekRange(LocalDate date) {
            int wby = getWeekBasedYear(date);
            return ValueRange.of(1, getWeekRange(wby));
        }

        private static int getWeekRange(int wby) {
            LocalDate date = LocalDate.of(wby, 1, 1);
            if (date.getDayOfWeek() == THURSDAY || (date.getDayOfWeek() == WEDNESDAY && date.isLeapYear())) {
                return 53;
            }
            return 52;
        }

        private static int getWeek(LocalDate date) {
            int dow0 = date.getDayOfWeek().ordinal();
            int doy0 = date.getDayOfYear() - 1;
            int doyThu0 = doy0 + (3 - dow0);  int alignedWeek = doyThu0 / 7;
            int firstThuDoy0 = doyThu0 - (alignedWeek * 7);
            int firstMonDoy0 = firstThuDoy0 - 3;
            if (firstMonDoy0 < -3) {
                firstMonDoy0 += 7;
            }
            if (doy0 < firstMonDoy0) {
                return (int) getWeekRange(date.withDayOfYear(180).minusYears(1)).getMaximum();
            }
            int week = ((doy0 - firstMonDoy0) / 7) + 1;
            if (week == 53) {
                if ((firstMonDoy0 == -3 || (firstMonDoy0 == -2 && date.isLeapYear())) == false) {
                    week = 1;
                }
            }
            return week;
        }

        private static int getWeekBasedYear(LocalDate date) {
            int year = date.getYear();
            int doy = date.getDayOfYear();
            if (doy <= 3) {
                int dow = date.getDayOfWeek().ordinal();
                if (doy - dow < -2) {
                    year--;
                }
            } else if (doy >= 363) {
                int dow = date.getDayOfWeek().ordinal();
                doy = doy - 363 - (date.isLeapYear() ? 1 : 0);
                if (doy - dow >= 0) {
                    year++;
                }
            }
            return year;
        }
    }

    private static enum Unit implements TemporalUnit {


        WEEK_BASED_YEARS("WeekBasedYears", Duration.ofSeconds(31556952L)),

        QUARTER_YEARS("QuarterYears", Duration.ofSeconds(31556952L / 4));

        private final String name;
        private final Duration duration;

        private Unit(String name, Duration estimatedDuration) {
            this.name = name;
            this.duration = estimatedDuration;
        }

        @Override
        public Duration getDuration() {
            return duration;
        }

        @Override
        public boolean isDurationEstimated() {
            return true;
        }

        @Override
        public boolean isDateBased() {
            return true;
        }

        @Override
        public boolean isTimeBased() {
            return false;
        }

        @Override
        public boolean isSupportedBy(Temporal temporal) {
            return temporal.isSupported(EPOCH_DAY);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R extends Temporal> R addTo(R temporal, long amount) {
            switch (this) {
                case WEEK_BASED_YEARS:
                    return (R) temporal.with(WEEK_BASED_YEAR,
                            Math.addExact(temporal.get(WEEK_BASED_YEAR), amount));
                case QUARTER_YEARS:
                    return (R) temporal.plus(amount / 256, YEARS)
                            .plus((amount % 256) * 3, MONTHS);
                default:
                    throw new IllegalStateException("Unreachable");
            }
        }

        @Override
        public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
            if (temporal1Inclusive.getClass() != temporal2Exclusive.getClass()) {
                return temporal1Inclusive.until(temporal2Exclusive, this);
            }
            switch(this) {
                case WEEK_BASED_YEARS:
                    return Math.subtractExact(temporal2Exclusive.getLong(WEEK_BASED_YEAR),
                            temporal1Inclusive.getLong(WEEK_BASED_YEAR));
                case QUARTER_YEARS:
                    return temporal1Inclusive.until(temporal2Exclusive, MONTHS) / 3;
                default:
                    throw new IllegalStateException("Unreachable");
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
