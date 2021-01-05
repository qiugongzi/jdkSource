


package javax.management;

import java.util.concurrent.CopyOnWriteArrayList;  public interface NotificationBroadcaster {


    public void addNotificationListener(NotificationListener listener,
                                        NotificationFilter filter,
                                        Object handback)
            throws java.lang.IllegalArgumentException;


    public void removeNotificationListener(NotificationListener listener)
            throws ListenerNotFoundException;


    public MBeanNotificationInfo[] getNotificationInfo();
}
