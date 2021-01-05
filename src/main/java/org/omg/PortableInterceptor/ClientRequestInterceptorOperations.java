package org.omg.PortableInterceptor;






public interface ClientRequestInterceptorOperations  extends org.omg.PortableInterceptor.InterceptorOperations
{


  void send_request (org.omg.PortableInterceptor.ClientRequestInfo ri) throws org.omg.PortableInterceptor.ForwardRequest;


  void send_poll (org.omg.PortableInterceptor.ClientRequestInfo ri);


  void receive_reply (org.omg.PortableInterceptor.ClientRequestInfo ri);


  void receive_exception (org.omg.PortableInterceptor.ClientRequestInfo ri) throws org.omg.PortableInterceptor.ForwardRequest;


  void receive_other (org.omg.PortableInterceptor.ClientRequestInfo ri) throws org.omg.PortableInterceptor.ForwardRequest;
}