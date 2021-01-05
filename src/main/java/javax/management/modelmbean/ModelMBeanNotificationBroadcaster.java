


package javax.management.modelmbean;

import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;
import javax.management.RuntimeOperationsException;



public interface ModelMBeanNotificationBroadcaster extends NotificationBroadcaster
{



        public void sendNotification(Notification ntfyObj)
        throws MBeanException, RuntimeOperationsException;


        public void sendNotification(String ntfyText)
        throws MBeanException, RuntimeOperationsException;


        public void sendAttributeChangeNotification(AttributeChangeNotification notification)
        throws MBeanException, RuntimeOperationsException;



        public void sendAttributeChangeNotification(Attribute oldValue, Attribute newValue)
        throws MBeanException, RuntimeOperationsException;



        public void addAttributeChangeNotificationListener(NotificationListener listener,
                                                           String attributeName,
                                                           Object handback)
        throws MBeanException, RuntimeOperationsException, IllegalArgumentException;




        public void removeAttributeChangeNotificationListener(NotificationListener listener,
                                                              String attributeName)
        throws MBeanException, RuntimeOperationsException, ListenerNotFoundException;

}
