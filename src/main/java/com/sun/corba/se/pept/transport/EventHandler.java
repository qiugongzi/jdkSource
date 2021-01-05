
public interface EventHandler
{
    public void setUseSelectThreadToWait(boolean x);
    public boolean shouldUseSelectThreadToWait();

    public SelectableChannel getChannel();

    public int getInterestOps();

    public void setSelectionKey(SelectionKey selectionKey);
    public SelectionKey getSelectionKey();

    public void handleEvent();




    public void setUseWorkerThreadForEvent(boolean x);
    public boolean shouldUseWorkerThreadForEvent();

    public void setWork(Work work);
    public Work getWork();


    public Acceptor getAcceptor();
    public Connection getConnection();

}


