
@jdk.Exported
public interface AssignmentTree extends ExpressionTree {
    ExpressionTree getVariable();
    ExpressionTree getExpression();
}
