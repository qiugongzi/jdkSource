


package javax.management;

import java.io.Serializable;

import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;


public interface Descriptor extends Serializable, Cloneable
{


    public Object getFieldValue(String fieldName)
            throws RuntimeOperationsException;


    public void setField(String fieldName, Object fieldValue)
        throws RuntimeOperationsException;



    public String[] getFields();



    public String[] getFieldNames();


    public Object[] getFieldValues(String... fieldNames);


    public void removeField(String fieldName);


    public void setFields(String[] fieldNames, Object[] fieldValues)
        throws RuntimeOperationsException;



    public Object clone() throws RuntimeOperationsException;



    public boolean isValid() throws RuntimeOperationsException;


    public boolean equals(Object obj);


    public int hashCode();
}
