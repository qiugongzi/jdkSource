
@jdk.Exported
public interface DoWhileLoopTree extends StatementTree {
    ExpressionTree getCondition();
    StatementTree getStatement();
}
