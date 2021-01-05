
@jdk.Exported
public interface EnhancedForLoopTree extends StatementTree {
    VariableTree getVariable();
    ExpressionTree getExpression();
    StatementTree getStatement();
}
