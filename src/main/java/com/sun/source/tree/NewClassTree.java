
@jdk.Exported
public interface NewClassTree extends ExpressionTree {
    ExpressionTree getEnclosingExpression();
    List<? extends Tree> getTypeArguments();
    ExpressionTree getIdentifier();
    List<? extends ExpressionTree> getArguments();
    ClassTree getClassBody();
}
