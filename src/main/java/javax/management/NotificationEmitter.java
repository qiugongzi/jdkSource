

package javax.management;

import java.util.concurrent.CopyOnWriteArrayList;  public interface NotificationEmitter extends NotificationBroadcaster {

    public void removeNotificationListener(NotificationListener listener,
                                           NotificationFilter filter,
                                           Object handback)
            throws ListenerNotFoundException;
}
