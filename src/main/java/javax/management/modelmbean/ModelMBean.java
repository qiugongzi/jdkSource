


package javax.management.modelmbean;

import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.PersistentMBean;
import javax.management.RuntimeOperationsException;



public interface ModelMBean extends
         DynamicMBean,
         PersistentMBean,
         ModelMBeanNotificationBroadcaster
{


        public void setModelMBeanInfo(ModelMBeanInfo inModelMBeanInfo)
            throws MBeanException, RuntimeOperationsException;


        public void setManagedResource(Object mr, String mr_type)
        throws MBeanException, RuntimeOperationsException,
                 InstanceNotFoundException, InvalidTargetObjectTypeException ;

}
