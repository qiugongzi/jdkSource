
@jdk.Exported
public interface ClassTree extends StatementTree {
    ModifiersTree getModifiers();
    Name getSimpleName();
    List<? extends TypeParameterTree> getTypeParameters();
    Tree getExtendsClause();
    List<? extends Tree> getImplementsClause();
    List<? extends Tree> getMembers();
}
