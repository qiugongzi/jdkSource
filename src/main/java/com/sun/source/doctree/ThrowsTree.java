
@jdk.Exported
public interface ThrowsTree extends BlockTagTree {
    ReferenceTree getExceptionName();
    List<? extends DocTree> getDescription();
}
