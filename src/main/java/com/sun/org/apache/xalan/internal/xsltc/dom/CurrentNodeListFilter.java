
public interface CurrentNodeListFilter {
    public abstract boolean test(int node, int position, int last, int current,
                                 AbstractTranslet translet, DTMAxisIterator iter);
}
