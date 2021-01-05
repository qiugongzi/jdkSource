


package java.time.format;

import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;


final class DateTimeParseContext {


    private DateTimeFormatter formatter;

    private boolean caseSensitive = true;

    private boolean strict = true;

    private final ArrayList<Parsed> parsed = new ArrayList<>();

    private ArrayList<Consumer<Chronology>> chronoListeners = null;


    DateTimeParseContext(DateTimeFormatter formatter) {
        super();
        this.formatter = formatter;
        parsed.add(new Parsed());
    }


    DateTimeParseContext copy() {
        DateTimeParseContext newContext = new DateTimeParseContext(formatter);
        newContext.caseSensitive = caseSensitive;
        newContext.strict = strict;
        return newContext;
    }

    Locale getLocale() {
        return formatter.getLocale();
    }


    DecimalStyle getDecimalStyle() {
        return formatter.getDecimalStyle();
    }


    Chronology getEffectiveChronology() {
        Chronology chrono = currentParsed().chrono;
        if (chrono == null) {
            chrono = formatter.getChronology();
            if (chrono == null) {
                chrono = IsoChronology.INSTANCE;
            }
        }
        return chrono;
    }

    boolean isCaseSensitive() {
        return caseSensitive;
    }


    void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    boolean subSequenceEquals(CharSequence cs1, int offset1, CharSequence cs2, int offset2, int length) {
        if (offset1 + length > cs1.length() || offset2 + length > cs2.length()) {
            return false;
        }
        if (isCaseSensitive()) {
            for (int i = 0; i < length; i++) {
                char ch1 = cs1.charAt(offset1 + i);
                char ch2 = cs2.charAt(offset2 + i);
                if (ch1 != ch2) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < length; i++) {
                char ch1 = cs1.charAt(offset1 + i);
                char ch2 = cs2.charAt(offset2 + i);
                if (ch1 != ch2 && Character.toUpperCase(ch1) != Character.toUpperCase(ch2) &&
                        Character.toLowerCase(ch1) != Character.toLowerCase(ch2)) {
                    return false;
                }
            }
        }
        return true;
    }


    boolean charEquals(char ch1, char ch2) {
        if (isCaseSensitive()) {
            return ch1 == ch2;
        }
        return charEqualsIgnoreCase(ch1, ch2);
    }


    static boolean charEqualsIgnoreCase(char c1, char c2) {
        return c1 == c2 ||
                Character.toUpperCase(c1) == Character.toUpperCase(c2) ||
                Character.toLowerCase(c1) == Character.toLowerCase(c2);
    }

    boolean isStrict() {
        return strict;
    }


    void setStrict(boolean strict) {
        this.strict = strict;
    }

    void startOptional() {
        parsed.add(currentParsed().copy());
    }


    void endOptional(boolean successful) {
        if (successful) {
            parsed.remove(parsed.size() - 2);
        } else {
            parsed.remove(parsed.size() - 1);
        }
    }

    private Parsed currentParsed() {
        return parsed.get(parsed.size() - 1);
    }


    Parsed toUnresolved() {
        return currentParsed();
    }


    TemporalAccessor toResolved(ResolverStyle resolverStyle, Set<TemporalField> resolverFields) {
        Parsed parsed = currentParsed();
        parsed.chrono = getEffectiveChronology();
        parsed.zone = (parsed.zone != null ? parsed.zone : formatter.getZone());
        return parsed.resolve(resolverStyle, resolverFields);
    }


    Long getParsed(TemporalField field) {
        return currentParsed().fieldValues.get(field);
    }


    int setParsedField(TemporalField field, long value, int errorPos, int successPos) {
        Objects.requireNonNull(field, "field");
        Long old = currentParsed().fieldValues.put(field, value);
        return (old != null && old.longValue() != value) ? ~errorPos : successPos;
    }


    void setParsed(Chronology chrono) {
        Objects.requireNonNull(chrono, "chrono");
        currentParsed().chrono = chrono;
        if (chronoListeners != null && !chronoListeners.isEmpty()) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Consumer<Chronology>[] tmp = new Consumer[1];
            Consumer<Chronology>[] listeners = chronoListeners.toArray(tmp);
            chronoListeners.clear();
            for (Consumer<Chronology> l : listeners) {
                l.accept(chrono);
            }
        }
    }


    void addChronoChangedListener(Consumer<Chronology> listener) {
        if (chronoListeners == null) {
            chronoListeners = new ArrayList<Consumer<Chronology>>();
        }
        chronoListeners.add(listener);
    }


    void setParsed(ZoneId zone) {
        Objects.requireNonNull(zone, "zone");
        currentParsed().zone = zone;
    }


    void setParsedLeapSecond() {
        currentParsed().leapSecond = true;
    }

    @Override
    public String toString() {
        return currentParsed().toString();
    }

}
