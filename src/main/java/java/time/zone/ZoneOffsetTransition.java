


package java.time.zone;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public final class ZoneOffsetTransition
        implements Comparable<ZoneOffsetTransition>, Serializable {


    private static final long serialVersionUID = -6946044323557704546L;

    private final LocalDateTime transition;

    private final ZoneOffset offsetBefore;

    private final ZoneOffset offsetAfter;

    public static ZoneOffsetTransition of(LocalDateTime transition, ZoneOffset offsetBefore, ZoneOffset offsetAfter) {
        Objects.requireNonNull(transition, "transition");
        Objects.requireNonNull(offsetBefore, "offsetBefore");
        Objects.requireNonNull(offsetAfter, "offsetAfter");
        if (offsetBefore.equals(offsetAfter)) {
            throw new IllegalArgumentException("Offsets must not be equal");
        }
        if (transition.getNano() != 0) {
            throw new IllegalArgumentException("Nano-of-second must be zero");
        }
        return new ZoneOffsetTransition(transition, offsetBefore, offsetAfter);
    }


    ZoneOffsetTransition(LocalDateTime transition, ZoneOffset offsetBefore, ZoneOffset offsetAfter) {
        this.transition = transition;
        this.offsetBefore = offsetBefore;
        this.offsetAfter = offsetAfter;
    }


    ZoneOffsetTransition(long epochSecond, ZoneOffset offsetBefore, ZoneOffset offsetAfter) {
        this.transition = LocalDateTime.ofEpochSecond(epochSecond, 0, offsetBefore);
        this.offsetBefore = offsetBefore;
        this.offsetAfter = offsetAfter;
    }

    private void readObject(ObjectInputStream s) throws InvalidObjectException {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }


    private Object writeReplace() {
        return new Ser(Ser.ZOT, this);
    }


    void writeExternal(DataOutput out) throws IOException {
        Ser.writeEpochSec(toEpochSecond(), out);
        Ser.writeOffset(offsetBefore, out);
        Ser.writeOffset(offsetAfter, out);
    }


    static ZoneOffsetTransition readExternal(DataInput in) throws IOException {
        long epochSecond = Ser.readEpochSec(in);
        ZoneOffset before = Ser.readOffset(in);
        ZoneOffset after = Ser.readOffset(in);
        if (before.equals(after)) {
            throw new IllegalArgumentException("Offsets must not be equal");
        }
        return new ZoneOffsetTransition(epochSecond, before, after);
    }

    public Instant getInstant() {
        return transition.toInstant(offsetBefore);
    }


    public long toEpochSecond() {
        return transition.toEpochSecond(offsetBefore);
    }

    public LocalDateTime getDateTimeBefore() {
        return transition;
    }


    public LocalDateTime getDateTimeAfter() {
        return transition.plusSeconds(getDurationSeconds());
    }


    public ZoneOffset getOffsetBefore() {
        return offsetBefore;
    }


    public ZoneOffset getOffsetAfter() {
        return offsetAfter;
    }


    public Duration getDuration() {
        return Duration.ofSeconds(getDurationSeconds());
    }


    private int getDurationSeconds() {
        return getOffsetAfter().getTotalSeconds() - getOffsetBefore().getTotalSeconds();
    }


    public boolean isGap() {
        return getOffsetAfter().getTotalSeconds() > getOffsetBefore().getTotalSeconds();
    }


    public boolean isOverlap() {
        return getOffsetAfter().getTotalSeconds() < getOffsetBefore().getTotalSeconds();
    }


    public boolean isValidOffset(ZoneOffset offset) {
        return isGap() ? false : (getOffsetBefore().equals(offset) || getOffsetAfter().equals(offset));
    }


    List<ZoneOffset> getValidOffsets() {
        if (isGap()) {
            return Collections.emptyList();
        }
        return Arrays.asList(getOffsetBefore(), getOffsetAfter());
    }

    @Override
    public int compareTo(ZoneOffsetTransition transition) {
        return this.getInstant().compareTo(transition.getInstant());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof ZoneOffsetTransition) {
            ZoneOffsetTransition d = (ZoneOffsetTransition) other;
            return transition.equals(d.transition) &&
                offsetBefore.equals(d.offsetBefore) && offsetAfter.equals(d.offsetAfter);
        }
        return false;
    }


    @Override
    public int hashCode() {
        return transition.hashCode() ^ offsetBefore.hashCode() ^ Integer.rotateLeft(offsetAfter.hashCode(), 16);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Transition[")
            .append(isGap() ? "Gap" : "Overlap")
            .append(" at ")
            .append(transition)
            .append(offsetBefore)
            .append(" to ")
            .append(offsetAfter)
            .append(']');
        return buf.toString();
    }

}
