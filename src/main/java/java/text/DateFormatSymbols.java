



package java.text;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.text.spi.DateFormatSymbolsProvider;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.locale.provider.ResourceBundleBasedAdapter;
import sun.util.locale.provider.TimeZoneNameUtility;


public class DateFormatSymbols implements Serializable, Cloneable {


    public DateFormatSymbols()
    {
        initializeData(Locale.getDefault(Locale.Category.FORMAT));
    }


    public DateFormatSymbols(Locale locale)
    {
        initializeData(locale);
    }


    private DateFormatSymbols(boolean flag) {
    }


    String eras[] = null;


    String months[] = null;


    String shortMonths[] = null;


    String weekdays[] = null;


    String shortWeekdays[] = null;


    String ampms[] = null;


    String zoneStrings[][] = null;


    transient boolean isZoneStringsSet = false;


    static final String  patternChars = "GyMdkHmsSEDFwWahKzZYuXL";

    static final int PATTERN_ERA                  =  0; static final int PATTERN_YEAR                 =  1; static final int PATTERN_MONTH                =  2; static final int PATTERN_DAY_OF_MONTH         =  3; static final int PATTERN_HOUR_OF_DAY1         =  4; static final int PATTERN_HOUR_OF_DAY0         =  5; static final int PATTERN_MINUTE               =  6; static final int PATTERN_SECOND               =  7; static final int PATTERN_MILLISECOND          =  8; static final int PATTERN_DAY_OF_WEEK          =  9; static final int PATTERN_DAY_OF_YEAR          = 10; static final int PATTERN_DAY_OF_WEEK_IN_MONTH = 11; static final int PATTERN_WEEK_OF_YEAR         = 12; static final int PATTERN_WEEK_OF_MONTH        = 13; static final int PATTERN_AM_PM                = 14; static final int PATTERN_HOUR1                = 15; static final int PATTERN_HOUR0                = 16; static final int PATTERN_ZONE_NAME            = 17; static final int PATTERN_ZONE_VALUE           = 18; static final int PATTERN_WEEK_YEAR            = 19; static final int PATTERN_ISO_DAY_OF_WEEK      = 20; static final int PATTERN_ISO_ZONE             = 21; static final int PATTERN_MONTH_STANDALONE     = 22; String  localPatternChars = null;


    Locale locale = null;


    static final long serialVersionUID = -5987973545549424702L;


    public static Locale[] getAvailableLocales() {
        LocaleServiceProviderPool pool=
            LocaleServiceProviderPool.getPool(DateFormatSymbolsProvider.class);
        return pool.getAvailableLocales();
    }


    public static final DateFormatSymbols getInstance() {
        return getInstance(Locale.getDefault(Locale.Category.FORMAT));
    }


    public static final DateFormatSymbols getInstance(Locale locale) {
        DateFormatSymbols dfs = getProviderInstance(locale);
        if (dfs != null) {
            return dfs;
        }
        throw new RuntimeException("DateFormatSymbols instance creation failed.");
    }


    static final DateFormatSymbols getInstanceRef(Locale locale) {
        DateFormatSymbols dfs = getProviderInstance(locale);
        if (dfs != null) {
            return dfs;
        }
        throw new RuntimeException("DateFormatSymbols instance creation failed.");
    }

    private static DateFormatSymbols getProviderInstance(Locale locale) {
        LocaleProviderAdapter adapter = LocaleProviderAdapter.getAdapter(DateFormatSymbolsProvider.class, locale);
        DateFormatSymbolsProvider provider = adapter.getDateFormatSymbolsProvider();
        DateFormatSymbols dfsyms = provider.getInstance(locale);
        if (dfsyms == null) {
            provider = LocaleProviderAdapter.forJRE().getDateFormatSymbolsProvider();
            dfsyms = provider.getInstance(locale);
        }
        return dfsyms;
    }


    public String[] getEras() {
        return Arrays.copyOf(eras, eras.length);
    }


    public void setEras(String[] newEras) {
        eras = Arrays.copyOf(newEras, newEras.length);
        cachedHashCode = 0;
    }


    public String[] getMonths() {
        return Arrays.copyOf(months, months.length);
    }


    public void setMonths(String[] newMonths) {
        months = Arrays.copyOf(newMonths, newMonths.length);
        cachedHashCode = 0;
    }


    public String[] getShortMonths() {
        return Arrays.copyOf(shortMonths, shortMonths.length);
    }


    public void setShortMonths(String[] newShortMonths) {
        shortMonths = Arrays.copyOf(newShortMonths, newShortMonths.length);
        cachedHashCode = 0;
    }


    public String[] getWeekdays() {
        return Arrays.copyOf(weekdays, weekdays.length);
    }


    public void setWeekdays(String[] newWeekdays) {
        weekdays = Arrays.copyOf(newWeekdays, newWeekdays.length);
        cachedHashCode = 0;
    }


    public String[] getShortWeekdays() {
        return Arrays.copyOf(shortWeekdays, shortWeekdays.length);
    }


    public void setShortWeekdays(String[] newShortWeekdays) {
        shortWeekdays = Arrays.copyOf(newShortWeekdays, newShortWeekdays.length);
        cachedHashCode = 0;
    }


    public String[] getAmPmStrings() {
        return Arrays.copyOf(ampms, ampms.length);
    }


    public void setAmPmStrings(String[] newAmpms) {
        ampms = Arrays.copyOf(newAmpms, newAmpms.length);
        cachedHashCode = 0;
    }


    public String[][] getZoneStrings() {
        return getZoneStringsImpl(true);
    }


    public void setZoneStrings(String[][] newZoneStrings) {
        String[][] aCopy = new String[newZoneStrings.length][];
        for (int i = 0; i < newZoneStrings.length; ++i) {
            int len = newZoneStrings[i].length;
            if (len < 5) {
                throw new IllegalArgumentException();
            }
            aCopy[i] = Arrays.copyOf(newZoneStrings[i], len);
        }
        zoneStrings = aCopy;
        isZoneStringsSet = true;
        cachedHashCode = 0;
    }


    public String getLocalPatternChars() {
        return localPatternChars;
    }


    public void setLocalPatternChars(String newLocalPatternChars) {
        localPatternChars = newLocalPatternChars.toString();
        cachedHashCode = 0;
    }


    public Object clone()
    {
        try
        {
            DateFormatSymbols other = (DateFormatSymbols)super.clone();
            copyMembers(this, other);
            return other;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }


    @Override
    public int hashCode() {
        int hashCode = cachedHashCode;
        if (hashCode == 0) {
            hashCode = 5;
            hashCode = 11 * hashCode + Arrays.hashCode(eras);
            hashCode = 11 * hashCode + Arrays.hashCode(months);
            hashCode = 11 * hashCode + Arrays.hashCode(shortMonths);
            hashCode = 11 * hashCode + Arrays.hashCode(weekdays);
            hashCode = 11 * hashCode + Arrays.hashCode(shortWeekdays);
            hashCode = 11 * hashCode + Arrays.hashCode(ampms);
            hashCode = 11 * hashCode + Arrays.deepHashCode(getZoneStringsWrapper());
            hashCode = 11 * hashCode + Objects.hashCode(localPatternChars);
            cachedHashCode = hashCode;
        }

        return hashCode;
    }


    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DateFormatSymbols that = (DateFormatSymbols) obj;
        return (Arrays.equals(eras, that.eras)
                && Arrays.equals(months, that.months)
                && Arrays.equals(shortMonths, that.shortMonths)
                && Arrays.equals(weekdays, that.weekdays)
                && Arrays.equals(shortWeekdays, that.shortWeekdays)
                && Arrays.equals(ampms, that.ampms)
                && Arrays.deepEquals(getZoneStringsWrapper(), that.getZoneStringsWrapper())
                && ((localPatternChars != null
                  && localPatternChars.equals(that.localPatternChars))
                 || (localPatternChars == null
                  && that.localPatternChars == null)));
    }

    static final int millisPerHour = 60*60*1000;


    private static final ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> cachedInstances
        = new ConcurrentHashMap<>(3);

    private transient int lastZoneIndex = 0;


    transient volatile int cachedHashCode = 0;


    private void initializeData(Locale locale) {
        SoftReference<DateFormatSymbols> ref = cachedInstances.get(locale);
        DateFormatSymbols dfs;
        if (ref == null || (dfs = ref.get()) == null) {
            if (ref != null) {
                cachedInstances.remove(locale, ref);
            }
            dfs = new DateFormatSymbols(false);

            LocaleProviderAdapter adapter
                = LocaleProviderAdapter.getAdapter(DateFormatSymbolsProvider.class, locale);
            if (!(adapter instanceof ResourceBundleBasedAdapter)) {
                adapter = LocaleProviderAdapter.getResourceBundleBased();
            }
            ResourceBundle resource
                = ((ResourceBundleBasedAdapter)adapter).getLocaleData().getDateFormatData(locale);

            dfs.locale = locale;
            if (resource.containsKey("Eras")) {
                dfs.eras = resource.getStringArray("Eras");
            } else if (resource.containsKey("long.Eras")) {
                dfs.eras = resource.getStringArray("long.Eras");
            } else if (resource.containsKey("short.Eras")) {
                dfs.eras = resource.getStringArray("short.Eras");
            }
            dfs.months = resource.getStringArray("MonthNames");
            dfs.shortMonths = resource.getStringArray("MonthAbbreviations");
            dfs.ampms = resource.getStringArray("AmPmMarkers");
            dfs.localPatternChars = resource.getString("DateTimePatternChars");

            dfs.weekdays = toOneBasedArray(resource.getStringArray("DayNames"));
            dfs.shortWeekdays = toOneBasedArray(resource.getStringArray("DayAbbreviations"));

            ref = new SoftReference<>(dfs);
            SoftReference<DateFormatSymbols> x = cachedInstances.putIfAbsent(locale, ref);
            if (x != null) {
                DateFormatSymbols y = x.get();
                if (y == null) {
                    cachedInstances.replace(locale, x, ref);
                } else {
                    ref = x;
                    dfs = y;
                }
            }
            Locale bundleLocale = resource.getLocale();
            if (!bundleLocale.equals(locale)) {
                SoftReference<DateFormatSymbols> z
                    = cachedInstances.putIfAbsent(bundleLocale, ref);
                if (z != null && z.get() == null) {
                    cachedInstances.replace(bundleLocale, z, ref);
                }
            }
        }

        copyMembers(dfs, this);
    }

    private static String[] toOneBasedArray(String[] src) {
        int len = src.length;
        String[] dst = new String[len + 1];
        dst[0] = "";
        for (int i = 0; i < len; i++) {
            dst[i + 1] = src[i];
        }
        return dst;
    }


    final int getZoneIndex(String ID) {
        String[][] zoneStrings = getZoneStringsWrapper();


        if (lastZoneIndex < zoneStrings.length && ID.equals(zoneStrings[lastZoneIndex][0])) {
            return lastZoneIndex;
        }


        for (int index = 0; index < zoneStrings.length; index++) {
            if (ID.equals(zoneStrings[index][0])) {
                lastZoneIndex = index;
                return index;
            }
        }

        return -1;
    }


    final String[][] getZoneStringsWrapper() {
        if (isSubclassObject()) {
            return getZoneStrings();
        } else {
            return getZoneStringsImpl(false);
        }
    }

    private String[][] getZoneStringsImpl(boolean needsCopy) {
        if (zoneStrings == null) {
            zoneStrings = TimeZoneNameUtility.getZoneStrings(locale);
        }

        if (!needsCopy) {
            return zoneStrings;
        }

        int len = zoneStrings.length;
        String[][] aCopy = new String[len][];
        for (int i = 0; i < len; i++) {
            aCopy[i] = Arrays.copyOf(zoneStrings[i], zoneStrings[i].length);
        }
        return aCopy;
    }

    private boolean isSubclassObject() {
        return !getClass().getName().equals("java.text.DateFormatSymbols");
    }


    private void copyMembers(DateFormatSymbols src, DateFormatSymbols dst)
    {
        dst.locale = src.locale;
        dst.eras = Arrays.copyOf(src.eras, src.eras.length);
        dst.months = Arrays.copyOf(src.months, src.months.length);
        dst.shortMonths = Arrays.copyOf(src.shortMonths, src.shortMonths.length);
        dst.weekdays = Arrays.copyOf(src.weekdays, src.weekdays.length);
        dst.shortWeekdays = Arrays.copyOf(src.shortWeekdays, src.shortWeekdays.length);
        dst.ampms = Arrays.copyOf(src.ampms, src.ampms.length);
        if (src.zoneStrings != null) {
            dst.zoneStrings = src.getZoneStringsImpl(true);
        } else {
            dst.zoneStrings = null;
        }
        dst.localPatternChars = src.localPatternChars;
        dst.cachedHashCode = 0;
    }


    private void writeObject(ObjectOutputStream stream) throws IOException {
        if (zoneStrings == null) {
            zoneStrings = TimeZoneNameUtility.getZoneStrings(locale);
        }
        stream.defaultWriteObject();
    }
}
