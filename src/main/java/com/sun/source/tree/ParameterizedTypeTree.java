
@jdk.Exported
public interface ParameterizedTypeTree extends Tree {
    Tree getType();
    List<? extends Tree> getTypeArguments();
}
