
@jdk.Exported
public interface SwitchTree extends StatementTree {
    ExpressionTree getExpression();
    List<? extends CaseTree> getCases();
}
