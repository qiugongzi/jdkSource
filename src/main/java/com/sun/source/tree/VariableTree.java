
@jdk.Exported
public interface VariableTree extends StatementTree {
    ModifiersTree getModifiers();
    Name getName();
    ExpressionTree getNameExpression();
    Tree getType();
    ExpressionTree getInitializer();
}
