


package javax.management;

import javax.management.MBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.InstanceNotFoundException;


public interface PersistentMBean {



    public void load()
    throws MBeanException, RuntimeOperationsException, InstanceNotFoundException;


    public void store()
    throws MBeanException, RuntimeOperationsException, InstanceNotFoundException;

}
