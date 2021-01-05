


package java.time.temporal;

import java.time.DateTimeException;
import java.util.Objects;


public interface TemporalAccessor {


    boolean isSupported(TemporalField field);


    default ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            if (isSupported(field)) {
                return field.range();
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        Objects.requireNonNull(field, "field");
        return field.rangeRefinedBy(this);
    }


    default int get(TemporalField field) {
        ValueRange range = range(field);
        if (range.isIntValue() == false) {
            throw new UnsupportedTemporalTypeException("Invalid field " + field + " for get() method, use getLong() instead");
        }
        long value = getLong(field);
        if (range.isValidValue(value) == false) {
            throw new DateTimeException("Invalid value for " + field + " (valid values " + range + "): " + value);
        }
        return (int) value;
    }


    long getLong(TemporalField field);


    default <R> R query(TemporalQuery<R> query) {
        if (query == TemporalQueries.zoneId()
                || query == TemporalQueries.chronology()
                || query == TemporalQueries.precision()) {
            return null;
        }
        return query.queryFrom(this);
    }

}
