


package java.time.format;

import static java.time.temporal.ChronoField.AMPM_OF_DAY;
import static java.time.temporal.ChronoField.CLOCK_HOUR_OF_AMPM;
import static java.time.temporal.ChronoField.CLOCK_HOUR_OF_DAY;
import static java.time.temporal.ChronoField.HOUR_OF_AMPM;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.INSTANT_SECONDS;
import static java.time.temporal.ChronoField.MICRO_OF_DAY;
import static java.time.temporal.ChronoField.MICRO_OF_SECOND;
import static java.time.temporal.ChronoField.MILLI_OF_DAY;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.MINUTE_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_DAY;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.OFFSET_SECONDS;
import static java.time.temporal.ChronoField.SECOND_OF_DAY;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;


final class Parsed implements TemporalAccessor {
    final Map<TemporalField, Long> fieldValues = new HashMap<>();

    ZoneId zone;

    Chronology chrono;

    boolean leapSecond;

    private ResolverStyle resolverStyle;

    private ChronoLocalDate date;

    private LocalTime time;

    Period excessDays = Period.ZERO;


    Parsed() {
    }


    Parsed copy() {
        Parsed cloned = new Parsed();
        cloned.fieldValues.putAll(this.fieldValues);
        cloned.zone = this.zone;
        cloned.chrono = this.chrono;
        cloned.leapSecond = this.leapSecond;
        return cloned;
    }

    @Override
    public boolean isSupported(TemporalField field) {
        if (fieldValues.containsKey(field) ||
                (date != null && date.isSupported(field)) ||
                (time != null && time.isSupported(field))) {
            return true;
        }
        return field != null && (field instanceof ChronoField == false) && field.isSupportedBy(this);
    }

    @Override
    public long getLong(TemporalField field) {
        Objects.requireNonNull(field, "field");
        Long value = fieldValues.get(field);
        if (value != null) {
            return value;
        }
        if (date != null && date.isSupported(field)) {
            return date.getLong(field);
        }
        if (time != null && time.isSupported(field)) {
            return time.getLong(field);
        }
        if (field instanceof ChronoField) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.getFrom(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R query(TemporalQuery<R> query) {
        if (query == TemporalQueries.zoneId()) {
            return (R) zone;
        } else if (query == TemporalQueries.chronology()) {
            return (R) chrono;
        } else if (query == TemporalQueries.localDate()) {
            return (R) (date != null ? LocalDate.from(date) : null);
        } else if (query == TemporalQueries.localTime()) {
            return (R) time;
        } else if (query == TemporalQueries.zone() || query == TemporalQueries.offset()) {
            return query.queryFrom(this);
        } else if (query == TemporalQueries.precision()) {
            return null;  }
        return query.queryFrom(this);
    }

    TemporalAccessor resolve(ResolverStyle resolverStyle, Set<TemporalField> resolverFields) {
        if (resolverFields != null) {
            fieldValues.keySet().retainAll(resolverFields);
        }
        this.resolverStyle = resolverStyle;
        resolveFields();
        resolveTimeLenient();
        crossCheck();
        resolvePeriod();
        resolveFractional();
        resolveInstant();
        return this;
    }

    private void resolveFields() {
        resolveInstantFields();
        resolveDateFields();
        resolveTimeFields();

        if (fieldValues.size() > 0) {
            int changedCount = 0;
            outer:
            while (changedCount < 50) {
                for (Map.Entry<TemporalField, Long> entry : fieldValues.entrySet()) {
                    TemporalField targetField = entry.getKey();
                    TemporalAccessor resolvedObject = targetField.resolve(fieldValues, this, resolverStyle);
                    if (resolvedObject != null) {
                        if (resolvedObject instanceof ChronoZonedDateTime) {
                            ChronoZonedDateTime<?> czdt = (ChronoZonedDateTime<?>) resolvedObject;
                            if (zone == null) {
                                zone = czdt.getZone();
                            } else if (zone.equals(czdt.getZone()) == false) {
                                throw new DateTimeException("ChronoZonedDateTime must use the effective parsed zone: " + zone);
                            }
                            resolvedObject = czdt.toLocalDateTime();
                        }
                        if (resolvedObject instanceof ChronoLocalDateTime) {
                            ChronoLocalDateTime<?> cldt = (ChronoLocalDateTime<?>) resolvedObject;
                            updateCheckConflict(cldt.toLocalTime(), Period.ZERO);
                            updateCheckConflict(cldt.toLocalDate());
                            changedCount++;
                            continue outer;  }
                        if (resolvedObject instanceof ChronoLocalDate) {
                            updateCheckConflict((ChronoLocalDate) resolvedObject);
                            changedCount++;
                            continue outer;  }
                        if (resolvedObject instanceof LocalTime) {
                            updateCheckConflict((LocalTime) resolvedObject, Period.ZERO);
                            changedCount++;
                            continue outer;  }
                        throw new DateTimeException("Method resolve() can only return ChronoZonedDateTime, " +
                                "ChronoLocalDateTime, ChronoLocalDate or LocalTime");
                    } else if (fieldValues.containsKey(targetField) == false) {
                        changedCount++;
                        continue outer;  }
                }
                break;
            }
            if (changedCount == 50) {  throw new DateTimeException("One of the parsed fields has an incorrectly implemented resolve method");
            }
            if (changedCount > 0) {
                resolveInstantFields();
                resolveDateFields();
                resolveTimeFields();
            }
        }
    }

    private void updateCheckConflict(TemporalField targetField, TemporalField changeField, Long changeValue) {
        Long old = fieldValues.put(changeField, changeValue);
        if (old != null && old.longValue() != changeValue.longValue()) {
            throw new DateTimeException("Conflict found: " + changeField + " " + old +
                    " differs from " + changeField + " " + changeValue +
                    " while resolving  " + targetField);
        }
    }

    private void resolveInstantFields() {
        if (fieldValues.containsKey(INSTANT_SECONDS)) {
            if (zone != null) {
                resolveInstantFields0(zone);
            } else {
                Long offsetSecs = fieldValues.get(OFFSET_SECONDS);
                if (offsetSecs != null) {
                    ZoneOffset offset = ZoneOffset.ofTotalSeconds(offsetSecs.intValue());
                    resolveInstantFields0(offset);
                }
            }
        }
    }

    private void resolveInstantFields0(ZoneId selectedZone) {
        Instant instant = Instant.ofEpochSecond(fieldValues.remove(INSTANT_SECONDS));
        ChronoZonedDateTime<?> zdt = chrono.zonedDateTime(instant, selectedZone);
        updateCheckConflict(zdt.toLocalDate());
        updateCheckConflict(INSTANT_SECONDS, SECOND_OF_DAY, (long) zdt.toLocalTime().toSecondOfDay());
    }

    private void resolveDateFields() {
        updateCheckConflict(chrono.resolveDate(fieldValues, resolverStyle));
    }

    private void updateCheckConflict(ChronoLocalDate cld) {
        if (date != null) {
            if (cld != null && date.equals(cld) == false) {
                throw new DateTimeException("Conflict found: Fields resolved to two different dates: " + date + " " + cld);
            }
        } else if (cld != null) {
            if (chrono.equals(cld.getChronology()) == false) {
                throw new DateTimeException("ChronoLocalDate must use the effective parsed chronology: " + chrono);
            }
            date = cld;
        }
    }

    private void resolveTimeFields() {
        if (fieldValues.containsKey(CLOCK_HOUR_OF_DAY)) {
            long ch = fieldValues.remove(CLOCK_HOUR_OF_DAY);
            if (resolverStyle == ResolverStyle.STRICT || (resolverStyle == ResolverStyle.SMART && ch != 0)) {
                CLOCK_HOUR_OF_DAY.checkValidValue(ch);
            }
            updateCheckConflict(CLOCK_HOUR_OF_DAY, HOUR_OF_DAY, ch == 24 ? 0 : ch);
        }
        if (fieldValues.containsKey(CLOCK_HOUR_OF_AMPM)) {
            long ch = fieldValues.remove(CLOCK_HOUR_OF_AMPM);
            if (resolverStyle == ResolverStyle.STRICT || (resolverStyle == ResolverStyle.SMART && ch != 0)) {
                CLOCK_HOUR_OF_AMPM.checkValidValue(ch);
            }
            updateCheckConflict(CLOCK_HOUR_OF_AMPM, HOUR_OF_AMPM, ch == 12 ? 0 : ch);
        }
        if (fieldValues.containsKey(AMPM_OF_DAY) && fieldValues.containsKey(HOUR_OF_AMPM)) {
            long ap = fieldValues.remove(AMPM_OF_DAY);
            long hap = fieldValues.remove(HOUR_OF_AMPM);
            if (resolverStyle == ResolverStyle.LENIENT) {
                updateCheckConflict(AMPM_OF_DAY, HOUR_OF_DAY, Math.addExact(Math.multiplyExact(ap, 12), hap));
            } else {  AMPM_OF_DAY.checkValidValue(ap);
                HOUR_OF_AMPM.checkValidValue(ap);
                updateCheckConflict(AMPM_OF_DAY, HOUR_OF_DAY, ap * 12 + hap);
            }
        }
        if (fieldValues.containsKey(NANO_OF_DAY)) {
            long nod = fieldValues.remove(NANO_OF_DAY);
            if (resolverStyle != ResolverStyle.LENIENT) {
                NANO_OF_DAY.checkValidValue(nod);
            }
            updateCheckConflict(NANO_OF_DAY, HOUR_OF_DAY, nod / 3600_000_000_000L);
            updateCheckConflict(NANO_OF_DAY, MINUTE_OF_HOUR, (nod / 60_000_000_000L) % 60);
            updateCheckConflict(NANO_OF_DAY, SECOND_OF_MINUTE, (nod / 1_000_000_000L) % 60);
            updateCheckConflict(NANO_OF_DAY, NANO_OF_SECOND, nod % 1_000_000_000L);
        }
        if (fieldValues.containsKey(MICRO_OF_DAY)) {
            long cod = fieldValues.remove(MICRO_OF_DAY);
            if (resolverStyle != ResolverStyle.LENIENT) {
                MICRO_OF_DAY.checkValidValue(cod);
            }
            updateCheckConflict(MICRO_OF_DAY, SECOND_OF_DAY, cod / 1_000_000L);
            updateCheckConflict(MICRO_OF_DAY, MICRO_OF_SECOND, cod % 1_000_000L);
        }
        if (fieldValues.containsKey(MILLI_OF_DAY)) {
            long lod = fieldValues.remove(MILLI_OF_DAY);
            if (resolverStyle != ResolverStyle.LENIENT) {
                MILLI_OF_DAY.checkValidValue(lod);
            }
            updateCheckConflict(MILLI_OF_DAY, SECOND_OF_DAY, lod / 1_000);
            updateCheckConflict(MILLI_OF_DAY, MILLI_OF_SECOND, lod % 1_000);
        }
        if (fieldValues.containsKey(SECOND_OF_DAY)) {
            long sod = fieldValues.remove(SECOND_OF_DAY);
            if (resolverStyle != ResolverStyle.LENIENT) {
                SECOND_OF_DAY.checkValidValue(sod);
            }
            updateCheckConflict(SECOND_OF_DAY, HOUR_OF_DAY, sod / 3600);
            updateCheckConflict(SECOND_OF_DAY, MINUTE_OF_HOUR, (sod / 60) % 60);
            updateCheckConflict(SECOND_OF_DAY, SECOND_OF_MINUTE, sod % 60);
        }
        if (fieldValues.containsKey(MINUTE_OF_DAY)) {
            long mod = fieldValues.remove(MINUTE_OF_DAY);
            if (resolverStyle != ResolverStyle.LENIENT) {
                MINUTE_OF_DAY.checkValidValue(mod);
            }
            updateCheckConflict(MINUTE_OF_DAY, HOUR_OF_DAY, mod / 60);
            updateCheckConflict(MINUTE_OF_DAY, MINUTE_OF_HOUR, mod % 60);
        }

        if (fieldValues.containsKey(NANO_OF_SECOND)) {
            long nos = fieldValues.get(NANO_OF_SECOND);
            if (resolverStyle != ResolverStyle.LENIENT) {
                NANO_OF_SECOND.checkValidValue(nos);
            }
            if (fieldValues.containsKey(MICRO_OF_SECOND)) {
                long cos = fieldValues.remove(MICRO_OF_SECOND);
                if (resolverStyle != ResolverStyle.LENIENT) {
                    MICRO_OF_SECOND.checkValidValue(cos);
                }
                nos = cos * 1000 + (nos % 1000);
                updateCheckConflict(MICRO_OF_SECOND, NANO_OF_SECOND, nos);
            }
            if (fieldValues.containsKey(MILLI_OF_SECOND)) {
                long los = fieldValues.remove(MILLI_OF_SECOND);
                if (resolverStyle != ResolverStyle.LENIENT) {
                    MILLI_OF_SECOND.checkValidValue(los);
                }
                updateCheckConflict(MILLI_OF_SECOND, NANO_OF_SECOND, los * 1_000_000L + (nos % 1_000_000L));
            }
        }

        if (fieldValues.containsKey(HOUR_OF_DAY) && fieldValues.containsKey(MINUTE_OF_HOUR) &&
                fieldValues.containsKey(SECOND_OF_MINUTE) && fieldValues.containsKey(NANO_OF_SECOND)) {
            long hod = fieldValues.remove(HOUR_OF_DAY);
            long moh = fieldValues.remove(MINUTE_OF_HOUR);
            long som = fieldValues.remove(SECOND_OF_MINUTE);
            long nos = fieldValues.remove(NANO_OF_SECOND);
            resolveTime(hod, moh, som, nos);
        }
    }

    private void resolveTimeLenient() {
        if (time == null) {
            if (fieldValues.containsKey(MILLI_OF_SECOND)) {
                long los = fieldValues.remove(MILLI_OF_SECOND);
                if (fieldValues.containsKey(MICRO_OF_SECOND)) {
                    long cos = los * 1_000 + (fieldValues.get(MICRO_OF_SECOND) % 1_000);
                    updateCheckConflict(MILLI_OF_SECOND, MICRO_OF_SECOND, cos);
                    fieldValues.remove(MICRO_OF_SECOND);
                    fieldValues.put(NANO_OF_SECOND, cos * 1_000L);
                } else {
                    fieldValues.put(NANO_OF_SECOND, los * 1_000_000L);
                }
            } else if (fieldValues.containsKey(MICRO_OF_SECOND)) {
                long cos = fieldValues.remove(MICRO_OF_SECOND);
                fieldValues.put(NANO_OF_SECOND, cos * 1_000L);
            }

            Long hod = fieldValues.get(HOUR_OF_DAY);
            if (hod != null) {
                Long moh = fieldValues.get(MINUTE_OF_HOUR);
                Long som = fieldValues.get(SECOND_OF_MINUTE);
                Long nos = fieldValues.get(NANO_OF_SECOND);

                if ((moh == null && (som != null || nos != null)) ||
                        (moh != null && som == null && nos != null)) {
                    return;
                }

                long mohVal = (moh != null ? moh : 0);
                long somVal = (som != null ? som : 0);
                long nosVal = (nos != null ? nos : 0);
                resolveTime(hod, mohVal, somVal, nosVal);
                fieldValues.remove(HOUR_OF_DAY);
                fieldValues.remove(MINUTE_OF_HOUR);
                fieldValues.remove(SECOND_OF_MINUTE);
                fieldValues.remove(NANO_OF_SECOND);
            }
        }

        if (resolverStyle != ResolverStyle.LENIENT && fieldValues.size() > 0) {
            for (Entry<TemporalField, Long> entry : fieldValues.entrySet()) {
                TemporalField field = entry.getKey();
                if (field instanceof ChronoField && field.isTimeBased()) {
                    ((ChronoField) field).checkValidValue(entry.getValue());
                }
            }
        }
    }

    private void resolveTime(long hod, long moh, long som, long nos) {
        if (resolverStyle == ResolverStyle.LENIENT) {
            long totalNanos = Math.multiplyExact(hod, 3600_000_000_000L);
            totalNanos = Math.addExact(totalNanos, Math.multiplyExact(moh, 60_000_000_000L));
            totalNanos = Math.addExact(totalNanos, Math.multiplyExact(som, 1_000_000_000L));
            totalNanos = Math.addExact(totalNanos, nos);
            int excessDays = (int) Math.floorDiv(totalNanos, 86400_000_000_000L);  long nod = Math.floorMod(totalNanos, 86400_000_000_000L);
            updateCheckConflict(LocalTime.ofNanoOfDay(nod), Period.ofDays(excessDays));
        } else {  int mohVal = MINUTE_OF_HOUR.checkValidIntValue(moh);
            int nosVal = NANO_OF_SECOND.checkValidIntValue(nos);
            if (resolverStyle == ResolverStyle.SMART && hod == 24 && mohVal == 0 && som == 0 && nosVal == 0) {
                updateCheckConflict(LocalTime.MIDNIGHT, Period.ofDays(1));
            } else {
                int hodVal = HOUR_OF_DAY.checkValidIntValue(hod);
                int somVal = SECOND_OF_MINUTE.checkValidIntValue(som);
                updateCheckConflict(LocalTime.of(hodVal, mohVal, somVal, nosVal), Period.ZERO);
            }
        }
    }

    private void resolvePeriod() {
        if (date != null && time != null && excessDays.isZero() == false) {
            date = date.plus(excessDays);
            excessDays = Period.ZERO;
        }
    }

    private void resolveFractional() {
        if (time == null &&
                (fieldValues.containsKey(INSTANT_SECONDS) ||
                    fieldValues.containsKey(SECOND_OF_DAY) ||
                    fieldValues.containsKey(SECOND_OF_MINUTE))) {
            if (fieldValues.containsKey(NANO_OF_SECOND)) {
                long nos = fieldValues.get(NANO_OF_SECOND);
                fieldValues.put(MICRO_OF_SECOND, nos / 1000);
                fieldValues.put(MILLI_OF_SECOND, nos / 1000000);
            } else {
                fieldValues.put(NANO_OF_SECOND, 0L);
                fieldValues.put(MICRO_OF_SECOND, 0L);
                fieldValues.put(MILLI_OF_SECOND, 0L);
            }
        }
    }

    private void resolveInstant() {
        if (date != null && time != null) {
            if (zone != null) {
                long instant = date.atTime(time).atZone(zone).getLong(ChronoField.INSTANT_SECONDS);
                fieldValues.put(INSTANT_SECONDS, instant);
            } else {
                Long offsetSecs = fieldValues.get(OFFSET_SECONDS);
                if (offsetSecs != null) {
                    ZoneOffset offset = ZoneOffset.ofTotalSeconds(offsetSecs.intValue());
                    long instant = date.atTime(time).atZone(offset).getLong(ChronoField.INSTANT_SECONDS);
                    fieldValues.put(INSTANT_SECONDS, instant);
                }
            }
        }
    }

    private void updateCheckConflict(LocalTime timeToSet, Period periodToSet) {
        if (time != null) {
            if (time.equals(timeToSet) == false) {
                throw new DateTimeException("Conflict found: Fields resolved to different times: " + time + " " + timeToSet);
            }
            if (excessDays.isZero() == false && periodToSet.isZero() == false && excessDays.equals(periodToSet) == false) {
                throw new DateTimeException("Conflict found: Fields resolved to different excess periods: " + excessDays + " " + periodToSet);
            } else {
                excessDays = periodToSet;
            }
        } else {
            time = timeToSet;
            excessDays = periodToSet;
        }
    }

    private void crossCheck() {
        if (date != null) {
            crossCheck(date);
        }
        if (time != null) {
            crossCheck(time);
            if (date != null && fieldValues.size() > 0) {
                crossCheck(date.atTime(time));
            }
        }
    }

    private void crossCheck(TemporalAccessor target) {
        for (Iterator<Entry<TemporalField, Long>> it = fieldValues.entrySet().iterator(); it.hasNext(); ) {
            Entry<TemporalField, Long> entry = it.next();
            TemporalField field = entry.getKey();
            if (target.isSupported(field)) {
                long val1;
                try {
                    val1 = target.getLong(field);
                } catch (RuntimeException ex) {
                    continue;
                }
                long val2 = entry.getValue();
                if (val1 != val2) {
                    throw new DateTimeException("Conflict found: Field " + field + " " + val1 +
                            " differs from " + field + " " + val2 + " derived from " + target);
                }
                it.remove();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(64);
        buf.append(fieldValues).append(',').append(chrono);
        if (zone != null) {
            buf.append(',').append(zone);
        }
        if (date != null || time != null) {
            buf.append(" resolved to ");
            if (date != null) {
                buf.append(date);
                if (time != null) {
                    buf.append('T').append(time);
                }
            } else {
                buf.append(time);
            }
        }
        return buf.toString();
    }

}
