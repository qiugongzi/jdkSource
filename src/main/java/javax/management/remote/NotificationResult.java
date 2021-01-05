

package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;


public class NotificationResult implements Serializable {

    private static final long serialVersionUID = 1191800228721395279L;


    public NotificationResult(long earliestSequenceNumber,
                              long nextSequenceNumber,
                              TargetedNotification[] targetedNotifications) {
        validate(targetedNotifications, earliestSequenceNumber, nextSequenceNumber);
        this.earliestSequenceNumber = earliestSequenceNumber;
        this.nextSequenceNumber = nextSequenceNumber;
        this.targetedNotifications = (targetedNotifications.length == 0 ? targetedNotifications : targetedNotifications.clone());
    }


    public long getEarliestSequenceNumber() {
        return earliestSequenceNumber;
    }


    public long getNextSequenceNumber() {
        return nextSequenceNumber;
    }


    public TargetedNotification[] getTargetedNotifications() {
        return targetedNotifications.length == 0 ? targetedNotifications : targetedNotifications.clone();
    }


    public String toString() {
        return "NotificationResult: earliest=" + getEarliestSequenceNumber() +
            "; next=" + getNextSequenceNumber() + "; nnotifs=" +
            getTargetedNotifications().length;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        try {
            validate(
                this.targetedNotifications,
                this.earliestSequenceNumber,
                this.nextSequenceNumber
            );

            this.targetedNotifications = this.targetedNotifications.length == 0 ?
                                            this.targetedNotifications :
                                            this.targetedNotifications.clone();
        } catch (IllegalArgumentException e) {
            throw new InvalidObjectException(e.getMessage());
        }
    }

    private long earliestSequenceNumber;
    private long nextSequenceNumber;
    private TargetedNotification[] targetedNotifications;

    private static void validate(TargetedNotification[] targetedNotifications,
                                 long earliestSequenceNumber,
                                 long nextSequenceNumber)
        throws IllegalArgumentException {
        if (targetedNotifications == null) {
            final String msg = "Notifications null";
            throw new IllegalArgumentException(msg);
        }

        if (earliestSequenceNumber < 0 || nextSequenceNumber < 0)
            throw new IllegalArgumentException("Bad sequence numbers");

    }
}
