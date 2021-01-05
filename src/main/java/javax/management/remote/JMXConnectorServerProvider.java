

package javax.management.remote;

import java.io.IOException;
import java.util.Map;

import javax.management.MBeanServer;


public interface JMXConnectorServerProvider {

    public JMXConnectorServer newJMXConnectorServer(JMXServiceURL serviceURL,
                                                    Map<String,?> environment,
                                                    MBeanServer mbeanServer)
            throws IOException;
}
