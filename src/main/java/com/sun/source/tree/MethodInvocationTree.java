
@jdk.Exported
public interface MethodInvocationTree extends ExpressionTree {
    List<? extends Tree> getTypeArguments();
    ExpressionTree getMethodSelect();
    List<? extends ExpressionTree> getArguments();
}
