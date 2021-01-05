


package javax.management.openmbean;


import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanNotificationInfo;




public interface OpenMBeanInfo {

    public String getClassName() ;


    public String getDescription() ;


    public MBeanAttributeInfo[] getAttributes() ;


    public MBeanOperationInfo[] getOperations() ;


    public MBeanConstructorInfo[] getConstructors() ;


    public MBeanNotificationInfo[] getNotifications() ;


    public boolean equals(Object obj);


    public int hashCode();


    public String toString();

}
