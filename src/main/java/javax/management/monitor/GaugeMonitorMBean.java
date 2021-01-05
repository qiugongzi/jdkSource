

package javax.management.monitor;

import javax.management.ObjectName;


public interface GaugeMonitorMBean extends MonitorMBean {

    @Deprecated
    public Number getDerivedGauge();


    @Deprecated
    public long getDerivedGaugeTimeStamp();


    public Number getDerivedGauge(ObjectName object);


    public long getDerivedGaugeTimeStamp(ObjectName object);


    public Number getHighThreshold();


    public Number getLowThreshold();


    public void setThresholds(Number highValue, Number lowValue) throws java.lang.IllegalArgumentException;


    public boolean getNotifyHigh();


    public void setNotifyHigh(boolean value);


    public boolean getNotifyLow();


    public void setNotifyLow(boolean value);


    public boolean getDifferenceMode();


    public void setDifferenceMode(boolean value);
}
