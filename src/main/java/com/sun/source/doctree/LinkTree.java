
@jdk.Exported
public interface LinkTree extends InlineTagTree {
    ReferenceTree getReference();
    List<? extends DocTree> getLabel();
}
