


package com.sun.jmx.snmp.agent;



import java.util.Vector;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;
import javax.management.InstanceNotFoundException;
import javax.management.ServiceNotFoundException;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;



public interface SnmpMibAgentMBean {

    public void get(SnmpMibRequest req) throws SnmpStatusException;


    public void getNext(SnmpMibRequest req) throws SnmpStatusException;


    public void getBulk(SnmpMibRequest req, int nonRepeat, int maxRepeat)
        throws SnmpStatusException;


    public void set(SnmpMibRequest req) throws SnmpStatusException;


    public void check(SnmpMibRequest req) throws SnmpStatusException;

    public MBeanServer getMBeanServer();


    public SnmpMibHandler getSnmpAdaptor();


    public void setSnmpAdaptor(SnmpMibHandler stack);


    public void setSnmpAdaptor(SnmpMibHandler stack, SnmpOid[] oids);


    public void setSnmpAdaptor(SnmpMibHandler stack, String contextName);


    public void setSnmpAdaptor(SnmpMibHandler stack,
                               String contextName,
                               SnmpOid[] oids);


    public ObjectName getSnmpAdaptorName();


    public void setSnmpAdaptorName(ObjectName name)
        throws InstanceNotFoundException, ServiceNotFoundException;



    public void setSnmpAdaptorName(ObjectName name, SnmpOid[] oids)
        throws InstanceNotFoundException, ServiceNotFoundException;


    public void setSnmpAdaptorName(ObjectName name, String contextName)
        throws InstanceNotFoundException, ServiceNotFoundException;


    public void setSnmpAdaptorName(ObjectName name,
                                   String contextName,
                                   SnmpOid[] oids)
        throws InstanceNotFoundException, ServiceNotFoundException;


    public boolean getBindingState();


    public String getMibName();
}
