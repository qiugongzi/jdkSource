

package com.sun.security.auth.login;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.net.URI;

@jdk.Exported
public class ConfigFile extends Configuration {

    private final sun.security.provider.ConfigFile.Spi spi;


    public ConfigFile() {
        spi = new sun.security.provider.ConfigFile.Spi();
    }


    public ConfigFile(URI uri) {
        spi = new sun.security.provider.ConfigFile.Spi(uri);
    }


    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry
        (String applicationName) {

        return spi.engineGetAppConfigurationEntry(applicationName);
    }


    @Override
    public void refresh() {
        spi.engineRefresh();
    }
}
