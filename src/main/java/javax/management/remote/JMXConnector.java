

package javax.management.remote;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.security.auth.Subject;


public interface JMXConnector extends Closeable {

     public static final String CREDENTIALS =
         "jmx.remote.credentials";


    public void connect() throws IOException;


    public void connect(Map<String,?> env) throws IOException;


    public MBeanServerConnection getMBeanServerConnection()
            throws IOException;


    public MBeanServerConnection getMBeanServerConnection(
                                               Subject delegationSubject)
            throws IOException;


    public void close() throws IOException;


    public void
        addConnectionNotificationListener(NotificationListener listener,
                                          NotificationFilter filter,
                                          Object handback);


    public void
        removeConnectionNotificationListener(NotificationListener listener)
            throws ListenerNotFoundException;


    public void removeConnectionNotificationListener(NotificationListener l,
                                                     NotificationFilter f,
                                                     Object handback)
            throws ListenerNotFoundException;


    public String getConnectionId() throws IOException;
}
