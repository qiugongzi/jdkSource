


package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.management.Notification;


public class TargetedNotification implements Serializable {

    private static final long serialVersionUID = 7676132089779300926L;

public TargetedNotification(Notification notification,
                                Integer listenerID) {
        validate(notification, listenerID);
        this.notif = notification;
        this.id = listenerID;
    }


    public Notification getNotification() {
        return notif;
    }


    public Integer getListenerID() {
        return id;
    }


    public String toString() {
        return "{" + notif + ", " + id + "}";
    }


    private Notification notif;

    private Integer id;
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        try {
            validate(this.notif, this.id);
        } catch (IllegalArgumentException e) {
            throw new InvalidObjectException(e.getMessage());
        }
    }

    private static void validate(Notification notif, Integer id) throws IllegalArgumentException {
        if (notif == null) {
            throw new IllegalArgumentException("Invalid notification: null");
        }
        if (id == null) {
            throw new IllegalArgumentException("Invalid listener ID: null");
        }
    }
}
