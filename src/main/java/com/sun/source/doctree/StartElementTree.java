
@jdk.Exported
public interface StartElementTree extends DocTree {
    Name getName();
    List<? extends DocTree> getAttributes();
    boolean isSelfClosing();
}
