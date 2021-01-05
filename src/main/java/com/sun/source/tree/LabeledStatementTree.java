
@jdk.Exported
public interface LabeledStatementTree extends StatementTree {
    Name getLabel();
    StatementTree getStatement();
}
