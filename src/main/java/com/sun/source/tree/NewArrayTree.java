
@jdk.Exported
public interface NewArrayTree extends ExpressionTree {
    Tree getType();
    List<? extends ExpressionTree> getDimensions();
    List<? extends ExpressionTree> getInitializers();
    List<? extends AnnotationTree> getAnnotations();
    List<? extends List<? extends AnnotationTree>> getDimAnnotations();
}
