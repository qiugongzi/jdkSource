


package java.time.temporal;

import java.time.DateTimeException;


public interface Temporal extends TemporalAccessor {


    boolean isSupported(TemporalUnit unit);


    default Temporal with(TemporalAdjuster adjuster) {
        return adjuster.adjustInto(this);
    }


    Temporal with(TemporalField field, long newValue);

    default Temporal plus(TemporalAmount amount) {
        return amount.addTo(this);
    }


    Temporal plus(long amountToAdd, TemporalUnit unit);

    default Temporal minus(TemporalAmount amount) {
        return amount.subtractFrom(this);
    }


    default Temporal minus(long amountToSubtract, TemporalUnit unit) {
        return (amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit));
    }

    long until(Temporal endExclusive, TemporalUnit unit);

}
