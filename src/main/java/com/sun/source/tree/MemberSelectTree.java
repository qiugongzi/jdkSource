
@jdk.Exported
public interface MemberSelectTree extends ExpressionTree {
    ExpressionTree getExpression();
    Name getIdentifier();
}
