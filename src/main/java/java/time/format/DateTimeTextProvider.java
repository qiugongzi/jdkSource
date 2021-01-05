


package java.time.format;

import static java.time.temporal.ChronoField.AMPM_OF_DAY;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;

import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalField;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import sun.util.locale.provider.CalendarDataUtility;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;


class DateTimeTextProvider {


    private static final ConcurrentMap<Entry<TemporalField, Locale>, Object> CACHE = new ConcurrentHashMap<>(16, 0.75f, 2);

    private static final Comparator<Entry<String, Long>> COMPARATOR = new Comparator<Entry<String, Long>>() {
        @Override
        public int compare(Entry<String, Long> obj1, Entry<String, Long> obj2) {
            return obj2.getKey().length() - obj1.getKey().length();  }
    };

    DateTimeTextProvider() {}


    static DateTimeTextProvider getInstance() {
        return new DateTimeTextProvider();
    }


    public String getText(TemporalField field, long value, TextStyle style, Locale locale) {
        Object store = findStore(field, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore) store).getText(value, style);
        }
        return null;
    }


    public String getText(Chronology chrono, TemporalField field, long value,
                                    TextStyle style, Locale locale) {
        if (chrono == IsoChronology.INSTANCE
                || !(field instanceof ChronoField)) {
            return getText(field, value, style, locale);
        }

        int fieldIndex;
        int fieldValue;
        if (field == ERA) {
            fieldIndex = Calendar.ERA;
            if (chrono == JapaneseChronology.INSTANCE) {
                if (value == -999) {
                    fieldValue = 0;
                } else {
                    fieldValue = (int) value + 2;
                }
            } else {
                fieldValue = (int) value;
            }
        } else if (field == MONTH_OF_YEAR) {
            fieldIndex = Calendar.MONTH;
            fieldValue = (int) value - 1;
        } else if (field == DAY_OF_WEEK) {
            fieldIndex = Calendar.DAY_OF_WEEK;
            fieldValue = (int) value + 1;
            if (fieldValue > 7) {
                fieldValue = Calendar.SUNDAY;
            }
        } else if (field == AMPM_OF_DAY) {
            fieldIndex = Calendar.AM_PM;
            fieldValue = (int) value;
        } else {
            return null;
        }
        return CalendarDataUtility.retrieveJavaTimeFieldValueName(
                chrono.getCalendarType(), fieldIndex, fieldValue, style.toCalendarStyle(), locale);
    }


    public Iterator<Entry<String, Long>> getTextIterator(TemporalField field, TextStyle style, Locale locale) {
        Object store = findStore(field, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore) store).getTextIterator(style);
        }
        return null;
    }


    public Iterator<Entry<String, Long>> getTextIterator(Chronology chrono, TemporalField field,
                                                         TextStyle style, Locale locale) {
        if (chrono == IsoChronology.INSTANCE
                || !(field instanceof ChronoField)) {
            return getTextIterator(field, style, locale);
        }

        int fieldIndex;
        switch ((ChronoField)field) {
        case ERA:
            fieldIndex = Calendar.ERA;
            break;
        case MONTH_OF_YEAR:
            fieldIndex = Calendar.MONTH;
            break;
        case DAY_OF_WEEK:
            fieldIndex = Calendar.DAY_OF_WEEK;
            break;
        case AMPM_OF_DAY:
            fieldIndex = Calendar.AM_PM;
            break;
        default:
            return null;
        }

        int calendarStyle = (style == null) ? Calendar.ALL_STYLES : style.toCalendarStyle();
        Map<String, Integer> map = CalendarDataUtility.retrieveJavaTimeFieldValueNames(
                chrono.getCalendarType(), fieldIndex, calendarStyle, locale);
        if (map == null) {
            return null;
        }
        List<Entry<String, Long>> list = new ArrayList<>(map.size());
        switch (fieldIndex) {
        case Calendar.ERA:
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                int era = entry.getValue();
                if (chrono == JapaneseChronology.INSTANCE) {
                    if (era == 0) {
                        era = -999;
                    } else {
                        era -= 2;
                    }
                }
                list.add(createEntry(entry.getKey(), (long)era));
            }
            break;
        case Calendar.MONTH:
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                list.add(createEntry(entry.getKey(), (long)(entry.getValue() + 1)));
            }
            break;
        case Calendar.DAY_OF_WEEK:
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                list.add(createEntry(entry.getKey(), (long)toWeekDay(entry.getValue())));
            }
            break;
        default:
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                list.add(createEntry(entry.getKey(), (long)entry.getValue()));
            }
            break;
        }
        return list.iterator();
    }

    private Object findStore(TemporalField field, Locale locale) {
        Entry<TemporalField, Locale> key = createEntry(field, locale);
        Object store = CACHE.get(key);
        if (store == null) {
            store = createStore(field, locale);
            CACHE.putIfAbsent(key, store);
            store = CACHE.get(key);
        }
        return store;
    }

    private static int toWeekDay(int calWeekDay) {
        if (calWeekDay == Calendar.SUNDAY) {
            return 7;
        } else {
            return calWeekDay - 1;
        }
    }

    private Object createStore(TemporalField field, Locale locale) {
        Map<TextStyle, Map<Long, String>> styleMap = new HashMap<>();
        if (field == ERA) {
            for (TextStyle textStyle : TextStyle.values()) {
                if (textStyle.isStandalone()) {
                    continue;
                }
                Map<String, Integer> displayNames = CalendarDataUtility.retrieveJavaTimeFieldValueNames(
                        "gregory", Calendar.ERA, textStyle.toCalendarStyle(), locale);
                if (displayNames != null) {
                    Map<Long, String> map = new HashMap<>();
                    for (Entry<String, Integer> entry : displayNames.entrySet()) {
                        map.put((long) entry.getValue(), entry.getKey());
                    }
                    if (!map.isEmpty()) {
                        styleMap.put(textStyle, map);
                    }
                }
            }
            return new LocaleStore(styleMap);
        }

        if (field == MONTH_OF_YEAR) {
            for (TextStyle textStyle : TextStyle.values()) {
                Map<String, Integer> displayNames = CalendarDataUtility.retrieveJavaTimeFieldValueNames(
                        "gregory", Calendar.MONTH, textStyle.toCalendarStyle(), locale);
                Map<Long, String> map = new HashMap<>();
                if (displayNames != null) {
                    for (Entry<String, Integer> entry : displayNames.entrySet()) {
                        map.put((long) (entry.getValue() + 1), entry.getKey());
                    }

                } else {
                    for (int month = Calendar.JANUARY; month <= Calendar.DECEMBER; month++) {
                        String name;
                        name = CalendarDataUtility.retrieveJavaTimeFieldValueName(
                                "gregory", Calendar.MONTH, month, textStyle.toCalendarStyle(), locale);
                        if (name == null) {
                            break;
                        }
                        map.put((long) (month + 1), name);
                    }
                }
                if (!map.isEmpty()) {
                    styleMap.put(textStyle, map);
                }
            }
            return new LocaleStore(styleMap);
        }

        if (field == DAY_OF_WEEK) {
            for (TextStyle textStyle : TextStyle.values()) {
                Map<String, Integer> displayNames = CalendarDataUtility.retrieveJavaTimeFieldValueNames(
                        "gregory", Calendar.DAY_OF_WEEK, textStyle.toCalendarStyle(), locale);
                Map<Long, String> map = new HashMap<>();
                if (displayNames != null) {
                    for (Entry<String, Integer> entry : displayNames.entrySet()) {
                        map.put((long)toWeekDay(entry.getValue()), entry.getKey());
                    }

                } else {
                    for (int wday = Calendar.SUNDAY; wday <= Calendar.SATURDAY; wday++) {
                        String name;
                        name = CalendarDataUtility.retrieveJavaTimeFieldValueName(
                            "gregory", Calendar.DAY_OF_WEEK, wday, textStyle.toCalendarStyle(), locale);
                        if (name == null) {
                            break;
                        }
                        map.put((long)toWeekDay(wday), name);
                    }
                }
                if (!map.isEmpty()) {
                    styleMap.put(textStyle, map);
                }
            }
            return new LocaleStore(styleMap);
        }

        if (field == AMPM_OF_DAY) {
            for (TextStyle textStyle : TextStyle.values()) {
                if (textStyle.isStandalone()) {
                    continue;
                }
                Map<String, Integer> displayNames = CalendarDataUtility.retrieveJavaTimeFieldValueNames(
                        "gregory", Calendar.AM_PM, textStyle.toCalendarStyle(), locale);
                if (displayNames != null) {
                    Map<Long, String> map = new HashMap<>();
                    for (Entry<String, Integer> entry : displayNames.entrySet()) {
                        map.put((long) entry.getValue(), entry.getKey());
                    }
                    if (!map.isEmpty()) {
                        styleMap.put(textStyle, map);
                    }
                }
            }
            return new LocaleStore(styleMap);
        }

        if (field == IsoFields.QUARTER_OF_YEAR) {
            final String[] keys = {
                "QuarterNames",
                "standalone.QuarterNames",
                "QuarterAbbreviations",
                "standalone.QuarterAbbreviations",
                "QuarterNarrows",
                "standalone.QuarterNarrows",
            };
            for (int i = 0; i < keys.length; i++) {
                String[] names = getLocalizedResource(keys[i], locale);
                if (names != null) {
                    Map<Long, String> map = new HashMap<>();
                    for (int q = 0; q < names.length; q++) {
                        map.put((long) (q + 1), names[q]);
                    }
                    styleMap.put(TextStyle.values()[i], map);
                }
            }
            return new LocaleStore(styleMap);
        }

        return "";  }


    private static <A, B> Entry<A, B> createEntry(A text, B field) {
        return new SimpleImmutableEntry<>(text, field);
    }


    @SuppressWarnings("unchecked")
    static <T> T getLocalizedResource(String key, Locale locale) {
        LocaleResources lr = LocaleProviderAdapter.getResourceBundleBased()
                                    .getLocaleResources(locale);
        ResourceBundle rb = lr.getJavaTimeFormatData();
        return rb.containsKey(key) ? (T) rb.getObject(key) : null;
    }


    static final class LocaleStore {

        private final Map<TextStyle, Map<Long, String>> valueTextMap;

        private final Map<TextStyle, List<Entry<String, Long>>> parsable;


        LocaleStore(Map<TextStyle, Map<Long, String>> valueTextMap) {
            this.valueTextMap = valueTextMap;
            Map<TextStyle, List<Entry<String, Long>>> map = new HashMap<>();
            List<Entry<String, Long>> allList = new ArrayList<>();
            for (Map.Entry<TextStyle, Map<Long, String>> vtmEntry : valueTextMap.entrySet()) {
                Map<String, Entry<String, Long>> reverse = new HashMap<>();
                for (Map.Entry<Long, String> entry : vtmEntry.getValue().entrySet()) {
                    if (reverse.put(entry.getValue(), createEntry(entry.getValue(), entry.getKey())) != null) {
                        continue;  }
                }
                List<Entry<String, Long>> list = new ArrayList<>(reverse.values());
                Collections.sort(list, COMPARATOR);
                map.put(vtmEntry.getKey(), list);
                allList.addAll(list);
                map.put(null, allList);
            }
            Collections.sort(allList, COMPARATOR);
            this.parsable = map;
        }


        String getText(long value, TextStyle style) {
            Map<Long, String> map = valueTextMap.get(style);
            return map != null ? map.get(value) : null;
        }


        Iterator<Entry<String, Long>> getTextIterator(TextStyle style) {
            List<Entry<String, Long>> list = parsable.get(style);
            return list != null ? list.iterator() : null;
        }
    }
}
