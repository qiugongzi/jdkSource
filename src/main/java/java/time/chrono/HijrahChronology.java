



package java.time.chrono;

import static java.time.temporal.ChronoField.EPOCH_DAY;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import sun.util.logging.PlatformLogger;


public final class HijrahChronology extends AbstractChronology implements Serializable {


    private final transient String typeId;

    private final transient String calendarType;

    private static final long serialVersionUID = 3127340209035924785L;

    public static final HijrahChronology INSTANCE;

    private transient volatile boolean initComplete;

    private transient int[] hijrahEpochMonthStartDays;

    private transient int minEpochDay;

    private transient int maxEpochDay;

    private transient int hijrahStartEpochMonth;

    private transient int minMonthLength;

    private transient int maxMonthLength;

    private transient int minYearLength;

    private transient int maxYearLength;

    private final transient static Properties calendarProperties;


    private static final String PROP_PREFIX = "calendar.hijrah.";

    private static final String PROP_TYPE_SUFFIX = ".type";


    static {
        try {
            calendarProperties = sun.util.calendar.BaseCalendar.getCalendarProperties();
        } catch (IOException ioe) {
            throw new InternalError("Can't initialize lib/calendars.properties", ioe);
        }

        try {
            INSTANCE = new HijrahChronology("Hijrah-umalqura");
            AbstractChronology.registerChrono(INSTANCE, "Hijrah");
            AbstractChronology.registerChrono(INSTANCE, "islamic");
        } catch (DateTimeException ex) {
            PlatformLogger logger = PlatformLogger.getLogger("java.time.chrono");
            logger.severe("Unable to initialize Hijrah calendar: Hijrah-umalqura", ex);
            throw new RuntimeException("Unable to initialize Hijrah-umalqura calendar", ex.getCause());
        }
        registerVariants();
    }


    private static void registerVariants() {
        for (String name : calendarProperties.stringPropertyNames()) {
            if (name.startsWith(PROP_PREFIX)) {
                String id = name.substring(PROP_PREFIX.length());
                if (id.indexOf('.') >= 0) {
                    continue;   }
                if (id.equals(INSTANCE.getId())) {
                    continue;           }
                try {
                    HijrahChronology chrono = new HijrahChronology(id);
                    AbstractChronology.registerChrono(chrono);
                } catch (DateTimeException ex) {
                    PlatformLogger logger = PlatformLogger.getLogger("java.time.chrono");
                    logger.severe("Unable to initialize Hijrah calendar: " + id, ex);
                }
            }
        }
    }


    private HijrahChronology(String id) throws DateTimeException {
        if (id.isEmpty()) {
            throw new IllegalArgumentException("calendar id is empty");
        }
        String propName = PROP_PREFIX + id + PROP_TYPE_SUFFIX;
        String calType = calendarProperties.getProperty(propName);
        if (calType == null || calType.isEmpty()) {
            throw new DateTimeException("calendarType is missing or empty for: " + propName);
        }
        this.typeId = id;
        this.calendarType = calType;
    }


    private void checkCalendarInit() {
        if (initComplete == false) {
            loadCalendarData();
            initComplete = true;
        }
    }

    @Override
    public String getId() {
        return typeId;
    }


    @Override
    public String getCalendarType() {
        return calendarType;
    }

    @Override
    public HijrahDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }


    @Override
    public HijrahDate date(int prolepticYear, int month, int dayOfMonth) {
        return HijrahDate.of(this, prolepticYear, month, dayOfMonth);
    }


    @Override
    public HijrahDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }


    @Override
    public HijrahDate dateYearDay(int prolepticYear, int dayOfYear) {
        HijrahDate date = HijrahDate.of(this, prolepticYear, 1, 1);
        if (dayOfYear > date.lengthOfYear()) {
            throw new DateTimeException("Invalid dayOfYear: " + dayOfYear);
        }
        return date.plusDays(dayOfYear - 1);
    }


    @Override  public HijrahDate dateEpochDay(long epochDay) {
        return HijrahDate.ofEpochDay(this, epochDay);
    }

    @Override
    public HijrahDate dateNow() {
        return dateNow(Clock.systemDefaultZone());
    }

    @Override
    public HijrahDate dateNow(ZoneId zone) {
        return dateNow(Clock.system(zone));
    }

    @Override
    public HijrahDate dateNow(Clock clock) {
        return date(LocalDate.now(clock));
    }

    @Override
    public HijrahDate date(TemporalAccessor temporal) {
        if (temporal instanceof HijrahDate) {
            return (HijrahDate) temporal;
        }
        return HijrahDate.ofEpochDay(this, temporal.getLong(EPOCH_DAY));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<HijrahDate> localDateTime(TemporalAccessor temporal) {
        return (ChronoLocalDateTime<HijrahDate>) super.localDateTime(temporal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<HijrahDate> zonedDateTime(TemporalAccessor temporal) {
        return (ChronoZonedDateTime<HijrahDate>) super.zonedDateTime(temporal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ChronoZonedDateTime<HijrahDate> zonedDateTime(Instant instant, ZoneId zone) {
        return (ChronoZonedDateTime<HijrahDate>) super.zonedDateTime(instant, zone);
    }

    @Override
    public boolean isLeapYear(long prolepticYear) {
        checkCalendarInit();
        if (prolepticYear < getMinimumYear() || prolepticYear > getMaximumYear()) {
            return false;
        }
        int len = getYearLength((int) prolepticYear);
        return (len > 354);
    }

    @Override
    public int prolepticYear(Era era, int yearOfEra) {
        if (era instanceof HijrahEra == false) {
            throw new ClassCastException("Era must be HijrahEra");
        }
        return yearOfEra;
    }

    @Override
    public HijrahEra eraOf(int eraValue) {
        switch (eraValue) {
            case 1:
                return HijrahEra.AH;
            default:
                throw new DateTimeException("invalid Hijrah era");
        }
    }

    @Override
    public List<Era> eras() {
        return Arrays.<Era>asList(HijrahEra.values());
    }

    @Override
    public ValueRange range(ChronoField field) {
        checkCalendarInit();
        if (field instanceof ChronoField) {
            ChronoField f = field;
            switch (f) {
                case DAY_OF_MONTH:
                    return ValueRange.of(1, 1, getMinimumMonthLength(), getMaximumMonthLength());
                case DAY_OF_YEAR:
                    return ValueRange.of(1, getMaximumDayOfYear());
                case ALIGNED_WEEK_OF_MONTH:
                    return ValueRange.of(1, 5);
                case YEAR:
                case YEAR_OF_ERA:
                    return ValueRange.of(getMinimumYear(), getMaximumYear());
                case ERA:
                    return ValueRange.of(1, 1);
                default:
                    return field.range();
            }
        }
        return field.range();
    }

    @Override  public HijrahDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        return (HijrahDate) super.resolveDate(fieldValues, resolverStyle);
    }

    int checkValidYear(long prolepticYear) {
        if (prolepticYear < getMinimumYear() || prolepticYear > getMaximumYear()) {
            throw new DateTimeException("Invalid Hijrah year: " + prolepticYear);
        }
        return (int) prolepticYear;
    }

    void checkValidDayOfYear(int dayOfYear) {
        if (dayOfYear < 1 || dayOfYear > getMaximumDayOfYear()) {
            throw new DateTimeException("Invalid Hijrah day of year: " + dayOfYear);
        }
    }

    void checkValidMonth(int month) {
        if (month < 1 || month > 12) {
            throw new DateTimeException("Invalid Hijrah month: " + month);
        }
    }

    int[] getHijrahDateInfo(int epochDay) {
        checkCalendarInit();    if (epochDay < minEpochDay || epochDay >= maxEpochDay) {
            throw new DateTimeException("Hijrah date out of range");
        }

        int epochMonth = epochDayToEpochMonth(epochDay);
        int year = epochMonthToYear(epochMonth);
        int month = epochMonthToMonth(epochMonth);
        int day1 = epochMonthToEpochDay(epochMonth);
        int date = epochDay - day1; int dateInfo[] = new int[3];
        dateInfo[0] = year;
        dateInfo[1] = month + 1; dateInfo[2] = date + 1; return dateInfo;
    }


    long getEpochDay(int prolepticYear, int monthOfYear, int dayOfMonth) {
        checkCalendarInit();    checkValidMonth(monthOfYear);
        int epochMonth = yearToEpochMonth(prolepticYear) + (monthOfYear - 1);
        if (epochMonth < 0 || epochMonth >= hijrahEpochMonthStartDays.length) {
            throw new DateTimeException("Invalid Hijrah date, year: " +
                    prolepticYear +  ", month: " + monthOfYear);
        }
        if (dayOfMonth < 1 || dayOfMonth > getMonthLength(prolepticYear, monthOfYear)) {
            throw new DateTimeException("Invalid Hijrah day of month: " + dayOfMonth);
        }
        return epochMonthToEpochDay(epochMonth) + (dayOfMonth - 1);
    }


    int getDayOfYear(int prolepticYear, int month) {
        return yearMonthToDayOfYear(prolepticYear, (month - 1));
    }


    int getMonthLength(int prolepticYear, int monthOfYear) {
        int epochMonth = yearToEpochMonth(prolepticYear) + (monthOfYear - 1);
        if (epochMonth < 0 || epochMonth >= hijrahEpochMonthStartDays.length) {
            throw new DateTimeException("Invalid Hijrah date, year: " +
                    prolepticYear +  ", month: " + monthOfYear);
        }
        return epochMonthLength(epochMonth);
    }


    int getYearLength(int prolepticYear) {
        return yearMonthToDayOfYear(prolepticYear, 12);
    }


    int getMinimumYear() {
        return epochMonthToYear(0);
    }


    int getMaximumYear() {
        return epochMonthToYear(hijrahEpochMonthStartDays.length - 1) - 1;
    }


    int getMaximumMonthLength() {
        return maxMonthLength;
    }


    int getMinimumMonthLength() {
        return minMonthLength;
    }


    int getMaximumDayOfYear() {
        return maxYearLength;
    }


    int getSmallestMaximumDayOfYear() {
        return minYearLength;
    }


    private int epochDayToEpochMonth(int epochDay) {
        int ndx = Arrays.binarySearch(hijrahEpochMonthStartDays, epochDay);
        if (ndx < 0) {
            ndx = -ndx - 2;
        }
        return ndx;
    }


    private int epochMonthToYear(int epochMonth) {
        return (epochMonth + hijrahStartEpochMonth) / 12;
    }


    private int yearToEpochMonth(int year) {
        return (year * 12) - hijrahStartEpochMonth;
    }


    private int epochMonthToMonth(int epochMonth) {
        return (epochMonth + hijrahStartEpochMonth) % 12;
    }


    private int epochMonthToEpochDay(int epochMonth) {
        return hijrahEpochMonthStartDays[epochMonth];

    }


    private int yearMonthToDayOfYear(int prolepticYear, int month) {
        int epochMonthFirst = yearToEpochMonth(prolepticYear);
        return epochMonthToEpochDay(epochMonthFirst + month)
                - epochMonthToEpochDay(epochMonthFirst);
    }


    private int epochMonthLength(int epochMonth) {
        return hijrahEpochMonthStartDays[epochMonth + 1]
                - hijrahEpochMonthStartDays[epochMonth];
    }

    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_VERSION = "version";
    private static final String KEY_ISO_START = "iso-start";


    private static Properties readConfigProperties(final String resource) throws Exception {
        try {
            return AccessController
                    .doPrivileged((java.security.PrivilegedExceptionAction<Properties>)
                        () -> {
                        String libDir = System.getProperty("java.home") + File.separator + "lib";
                        File file = new File(libDir, resource);
                        Properties props = new Properties();
                        try (InputStream is = new FileInputStream(file)) {
                            props.load(is);
                        }
                        return props;
                    });
        } catch (PrivilegedActionException pax) {
            throw pax.getException();
        }
    }


    private void loadCalendarData() {
        try {
            String resourceName = calendarProperties.getProperty(PROP_PREFIX + typeId);
            Objects.requireNonNull(resourceName, "Resource missing for calendar: " + PROP_PREFIX + typeId);
            Properties props = readConfigProperties(resourceName);

            Map<Integer, int[]> years = new HashMap<>();
            int minYear = Integer.MAX_VALUE;
            int maxYear = Integer.MIN_VALUE;
            String id = null;
            String type = null;
            String version = null;
            int isoStart = 0;
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                String key = (String) entry.getKey();
                switch (key) {
                    case KEY_ID:
                        id = (String)entry.getValue();
                        break;
                    case KEY_TYPE:
                        type = (String)entry.getValue();
                        break;
                    case KEY_VERSION:
                        version = (String)entry.getValue();
                        break;
                    case KEY_ISO_START: {
                        int[] ymd = parseYMD((String) entry.getValue());
                        isoStart = (int) LocalDate.of(ymd[0], ymd[1], ymd[2]).toEpochDay();
                        break;
                    }
                    default:
                        try {
                            int year = Integer.valueOf(key);
                            int[] months = parseMonths((String) entry.getValue());
                            years.put(year, months);
                            maxYear = Math.max(maxYear, year);
                            minYear = Math.min(minYear, year);
                        } catch (NumberFormatException nfe) {
                            throw new IllegalArgumentException("bad key: " + key);
                        }
                }
            }

            if (!getId().equals(id)) {
                throw new IllegalArgumentException("Configuration is for a different calendar: " + id);
            }
            if (!getCalendarType().equals(type)) {
                throw new IllegalArgumentException("Configuration is for a different calendar type: " + type);
            }
            if (version == null || version.isEmpty()) {
                throw new IllegalArgumentException("Configuration does not contain a version");
            }
            if (isoStart == 0) {
                throw new IllegalArgumentException("Configuration does not contain a ISO start date");
            }

            hijrahStartEpochMonth = minYear * 12;
            minEpochDay = isoStart;
            hijrahEpochMonthStartDays = createEpochMonths(minEpochDay, minYear, maxYear, years);
            maxEpochDay = hijrahEpochMonthStartDays[hijrahEpochMonthStartDays.length - 1];

            for (int year = minYear; year < maxYear; year++) {
                int length = getYearLength(year);
                minYearLength = Math.min(minYearLength, length);
                maxYearLength = Math.max(maxYearLength, length);
            }
        } catch (Exception ex) {
            PlatformLogger logger = PlatformLogger.getLogger("java.time.chrono");
            logger.severe("Unable to initialize Hijrah calendar proxy: " + typeId, ex);
            throw new DateTimeException("Unable to initialize HijrahCalendar: " + typeId, ex);
        }
    }


    private int[] createEpochMonths(int epochDay, int minYear, int maxYear, Map<Integer, int[]> years) {
        int numMonths = (maxYear - minYear + 1) * 12 + 1;

        int epochMonth = 0; int[] epochMonths = new int[numMonths];
        minMonthLength = Integer.MAX_VALUE;
        maxMonthLength = Integer.MIN_VALUE;

        for (int year = minYear; year <= maxYear; year++) {
            int[] months = years.get(year);for (int month = 0; month < 12; month++) {
                int length = months[month];
                epochMonths[epochMonth++] = epochDay;

                if (length < 29 || length > 32) {
                    throw new IllegalArgumentException("Invalid month length in year: " + minYear);
                }
                epochDay += length;
                minMonthLength = Math.min(minMonthLength, length);
                maxMonthLength = Math.max(maxMonthLength, length);
            }
        }

        epochMonths[epochMonth++] = epochDay;

        if (epochMonth != epochMonths.length) {
            throw new IllegalStateException("Did not fill epochMonths exactly: ndx = " + epochMonth
                    + " should be " + epochMonths.length);
        }

        return epochMonths;
    }


    private int[] parseMonths(String line) {
        int[] months = new int[12];
        String[] numbers = line.split("\\s");
        if (numbers.length != 12) {
            throw new IllegalArgumentException("wrong number of months on line: " + Arrays.toString(numbers) + "; count: " + numbers.length);
        }
        for (int i = 0; i < 12; i++) {
            try {
                months[i] = Integer.valueOf(numbers[i]);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("bad key: " + numbers[i]);
            }
        }
        return months;
    }


    private int[] parseYMD(String string) {
        string = string.trim();
        try {
            if (string.charAt(4) != '-' || string.charAt(7) != '-') {
                throw new IllegalArgumentException("date must be yyyy-MM-dd");
            }
            int[] ymd = new int[3];
            ymd[0] = Integer.valueOf(string.substring(0, 4));
            ymd[1] = Integer.valueOf(string.substring(5, 7));
            ymd[2] = Integer.valueOf(string.substring(8, 10));
            return ymd;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("date must be yyyy-MM-dd", ex);
        }
    }

    @Override
    Object writeReplace() {
        return super.writeReplace();
    }


    private void readObject(ObjectInputStream s) throws InvalidObjectException {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }
}
