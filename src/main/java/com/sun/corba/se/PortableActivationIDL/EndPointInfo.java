package com.sun.corba.se.PortableActivationIDL;




public final class EndPointInfo implements org.omg.CORBA.portable.IDLEntity
{
  public String endpointType = null;
  public int port = (int)0;

  public EndPointInfo ()
  {
  }

  public EndPointInfo (String _endpointType, int _port)
  {
    endpointType = _endpointType;
    port = _port;
  }

}
