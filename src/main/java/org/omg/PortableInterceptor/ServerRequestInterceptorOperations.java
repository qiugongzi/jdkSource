package org.omg.PortableInterceptor;






public interface ServerRequestInterceptorOperations  extends org.omg.PortableInterceptor.InterceptorOperations
{


  void receive_request_service_contexts (org.omg.PortableInterceptor.ServerRequestInfo ri) throws org.omg.PortableInterceptor.ForwardRequest;


  void receive_request (org.omg.PortableInterceptor.ServerRequestInfo ri) throws org.omg.PortableInterceptor.ForwardRequest;


  void send_reply (org.omg.PortableInterceptor.ServerRequestInfo ri);


  void send_exception (org.omg.PortableInterceptor.ServerRequestInfo ri) throws org.omg.PortableInterceptor.ForwardRequest;


  void send_other (org.omg.PortableInterceptor.ServerRequestInfo ri) throws org.omg.PortableInterceptor.ForwardRequest;
}