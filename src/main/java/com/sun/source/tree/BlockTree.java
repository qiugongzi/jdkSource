
@jdk.Exported
public interface BlockTree extends StatementTree {
    boolean isStatic();
    List<? extends StatementTree> getStatements();
}
