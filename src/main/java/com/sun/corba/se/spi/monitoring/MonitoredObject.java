
package com.sun.corba.se.spi.monitoring;


import com.sun.corba.se.spi.monitoring.MonitoredAttribute;
import java.util.*;
import java.util.Collection;


public interface MonitoredObject {

  public String getName();

    public String getDescription();

    public void addChild( MonitoredObject m );

    public void removeChild( String name );


    public MonitoredObject getChild(String name);

    public Collection getChildren();

    public void setParent( MonitoredObject m );

    public MonitoredObject getParent();


    public void addAttribute(MonitoredAttribute value);

    public void removeAttribute(String name);


    public MonitoredAttribute getAttribute(String name);

    public Collection getAttributes();

    public void clearState();

}