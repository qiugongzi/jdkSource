

package javax.management.timer;



import java.util.Date;
import java.util.Vector;
import javax.management.InstanceNotFoundException;


public interface TimerMBean {


    public void start();


    public void stop();


public Integer addNotification(String type, String message, Object userData,
                                   Date date, long period, long nbOccurences, boolean fixedRate)
        throws java.lang.IllegalArgumentException;


public Integer addNotification(String type, String message, Object userData,
                                   Date date, long period, long nbOccurences)
        throws java.lang.IllegalArgumentException;


public Integer addNotification(String type, String message, Object userData,
                                   Date date, long period)
        throws java.lang.IllegalArgumentException;


public Integer addNotification(String type, String message, Object userData, Date date)
        throws java.lang.IllegalArgumentException;


    public void removeNotification(Integer id) throws InstanceNotFoundException;


    public void removeNotifications(String type) throws InstanceNotFoundException;


    public void removeAllNotifications();

    public int getNbNotifications();


    public Vector<Integer> getAllNotificationIDs();


    public Vector<Integer> getNotificationIDs(String type);


    public String getNotificationType(Integer id);


    public String getNotificationMessage(Integer id);


    public Object getNotificationUserData(Integer id);

    public Date getDate(Integer id);


    public Long getPeriod(Integer id);


    public Long getNbOccurences(Integer id);


    public Boolean getFixedRate(Integer id);


    public boolean getSendPastNotifications();


    public void setSendPastNotifications(boolean value);


    public boolean isActive();


    public boolean isEmpty();
}
