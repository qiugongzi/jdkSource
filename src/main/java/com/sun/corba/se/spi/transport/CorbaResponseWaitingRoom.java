
public interface CorbaResponseWaitingRoom
    extends
        ResponseWaitingRoom
{
    public void signalExceptionToAllWaiters(SystemException systemException);

    public MessageMediator getMessageMediator(int requestId);
}


