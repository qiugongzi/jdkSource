

package com.sun.jmx.mbeanserver;

import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerBuilder;


public class JmxMBeanServerBuilder extends MBeanServerBuilder {

    public MBeanServerDelegate newMBeanServerDelegate() {
        return JmxMBeanServer.newMBeanServerDelegate();
    }


    public MBeanServer newMBeanServer(String defaultDomain,
                                      MBeanServer outer,
                                      MBeanServerDelegate delegate) {
        return JmxMBeanServer.newMBeanServer(defaultDomain,outer,delegate,
                                             true);
    }

}
