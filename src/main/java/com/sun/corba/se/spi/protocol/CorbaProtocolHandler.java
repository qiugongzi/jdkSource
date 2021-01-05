
public interface CorbaProtocolHandler
    extends ProtocolHandler
{
    public void handleRequest(RequestMessage header,
                              CorbaMessageMediator messageMediator);

    public void handleRequest(LocateRequestMessage header,
                              CorbaMessageMediator messageMediator);

    public CorbaMessageMediator createResponse(
        CorbaMessageMediator messageMediator,
        ServiceContexts svc);
    public CorbaMessageMediator createUserExceptionResponse(
        CorbaMessageMediator messageMediator,
        ServiceContexts svc);
    public CorbaMessageMediator createUnknownExceptionResponse(
        CorbaMessageMediator messageMediator,
        UnknownException ex);
    public CorbaMessageMediator createSystemExceptionResponse(
        CorbaMessageMediator messageMediator,
        SystemException ex,
        ServiceContexts svc);
    public CorbaMessageMediator createLocationForward(
        CorbaMessageMediator messageMediator,
        IOR ior,
        ServiceContexts svc);

    public void handleThrowableDuringServerDispatch(
        CorbaMessageMediator request,
        Throwable exception,
        CompletionStatus completionStatus);

}


