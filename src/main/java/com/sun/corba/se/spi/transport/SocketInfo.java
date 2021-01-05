

package com.sun.corba.se.spi.transport;

public interface SocketInfo
{





    public static final String IIOP_CLEAR_TEXT = "IIOP_CLEAR_TEXT";


    public String getType();

    public String getHost();

    public int    getPort();
}
