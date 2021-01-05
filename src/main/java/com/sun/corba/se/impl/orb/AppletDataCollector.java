

package com.sun.corba.se.impl.orb ;

import java.applet.Applet ;
import java.util.Properties ;

public class AppletDataCollector extends DataCollectorBase {
    private Applet applet ;

    AppletDataCollector( Applet app, Properties props, String localHostName,
        String configurationHostName )
    {
        super( props, localHostName, configurationHostName ) ;
        this.applet = app ;
    }

    public boolean isApplet()
    {
        return true ;
    }

    protected void collect( )
    {
        checkPropertyDefaults() ;

        findPropertiesFromFile() ;




        findPropertiesFromProperties() ;
        findPropertiesFromApplet( applet ) ;
    }
}
