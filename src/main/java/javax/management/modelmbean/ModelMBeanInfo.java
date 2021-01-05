


package javax.management.modelmbean;

import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;



public interface ModelMBeanInfo
{



    public Descriptor[] getDescriptors(String inDescriptorType)
            throws MBeanException, RuntimeOperationsException;


    public void setDescriptors(Descriptor[] inDescriptors)
            throws MBeanException, RuntimeOperationsException;



    public Descriptor getDescriptor(String inDescriptorName, String inDescriptorType)
            throws MBeanException, RuntimeOperationsException;



    public void setDescriptor(Descriptor inDescriptor, String inDescriptorType)
            throws MBeanException, RuntimeOperationsException;



    public Descriptor getMBeanDescriptor()
            throws MBeanException, RuntimeOperationsException;



    public void setMBeanDescriptor(Descriptor inDescriptor)
            throws MBeanException, RuntimeOperationsException;




    public ModelMBeanAttributeInfo getAttribute(String inName)
            throws MBeanException, RuntimeOperationsException;




    public ModelMBeanOperationInfo getOperation(String inName)
            throws MBeanException, RuntimeOperationsException;



    public ModelMBeanNotificationInfo getNotification(String inName)
            throws MBeanException, RuntimeOperationsException;


    public java.lang.Object clone();


    public MBeanAttributeInfo[] getAttributes();


    public java.lang.String getClassName();


    public MBeanConstructorInfo[] getConstructors();


    public java.lang.String getDescription();


    public MBeanNotificationInfo[] getNotifications();


    public MBeanOperationInfo[] getOperations();

}
