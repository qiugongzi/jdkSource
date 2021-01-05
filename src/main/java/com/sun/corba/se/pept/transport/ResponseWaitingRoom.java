
public interface ResponseWaitingRoom
{
    public void registerWaiter(MessageMediator messageMediator);


    public InputObject waitForResponse(MessageMediator messageMediator);

    public void responseReceived(InputObject inputObject);

    public void unregisterWaiter(MessageMediator messageMediator);

    public int numberRegistered();
}


