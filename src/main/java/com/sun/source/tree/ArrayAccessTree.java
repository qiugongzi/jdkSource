
@jdk.Exported
public interface ArrayAccessTree extends ExpressionTree {
    ExpressionTree getExpression();
    ExpressionTree getIndex();
}
