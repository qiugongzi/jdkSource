
@jdk.Exported
public interface ParamTree extends BlockTagTree {
    boolean isTypeParameter();
    IdentifierTree getName();
    List<? extends DocTree> getDescription();
}
