

package javax.management.monitor;

import javax.management.ObjectName;


public interface CounterMonitorMBean extends MonitorMBean {

    @Deprecated
    public Number getDerivedGauge();


    @Deprecated
    public long getDerivedGaugeTimeStamp();


    @Deprecated
    public Number getThreshold();


    @Deprecated
    public void setThreshold(Number value) throws java.lang.IllegalArgumentException;


    public Number getDerivedGauge(ObjectName object);


    public long getDerivedGaugeTimeStamp(ObjectName object);


    public Number getThreshold(ObjectName object);


    public Number getInitThreshold();


    public void setInitThreshold(Number value) throws java.lang.IllegalArgumentException;


    public Number getOffset();


    public void setOffset(Number value) throws java.lang.IllegalArgumentException;


    public Number getModulus();


    public void setModulus(Number value) throws java.lang.IllegalArgumentException;


    public boolean getNotify();


    public void setNotify(boolean value);


    public boolean getDifferenceMode();


    public void setDifferenceMode(boolean value);
}
