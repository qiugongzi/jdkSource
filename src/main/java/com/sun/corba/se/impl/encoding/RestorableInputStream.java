
interface RestorableInputStream
{
    Object createStreamMemento();

    void restoreInternalState(Object streamMemento);
}
