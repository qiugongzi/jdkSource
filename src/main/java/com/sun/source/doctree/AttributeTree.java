
@jdk.Exported
public interface AttributeTree extends DocTree {
    @jdk.Exported
    enum ValueKind { EMPTY, UNQUOTED, SINGLE, DOUBLE };

    Name getName();
    ValueKind getValueKind();
    List<? extends DocTree> getValue();
}
