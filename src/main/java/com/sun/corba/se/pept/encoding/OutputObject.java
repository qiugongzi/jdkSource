
public interface OutputObject
{
    public void setMessageMediator(MessageMediator messageMediator);

    public MessageMediator getMessageMediator();

    public void close() throws IOException;
}


