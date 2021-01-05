

package javax.management.monitor;

import javax.management.ObjectName;


public interface MonitorMBean {


    public void start();


    public void stop();

    public void addObservedObject(ObjectName object) throws java.lang.IllegalArgumentException;


    public void removeObservedObject(ObjectName object);


    public boolean containsObservedObject(ObjectName object);


    public ObjectName[] getObservedObjects();


    @Deprecated
    public ObjectName getObservedObject();


    @Deprecated
    public void setObservedObject(ObjectName object);


    public String getObservedAttribute();


    public void setObservedAttribute(String attribute);


    public long getGranularityPeriod();


    public void setGranularityPeriod(long period) throws java.lang.IllegalArgumentException;


    public boolean isActive();
}
