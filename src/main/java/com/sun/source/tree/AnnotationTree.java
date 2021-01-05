
@jdk.Exported
public interface AnnotationTree extends ExpressionTree {
    Tree getAnnotationType();
    List<? extends ExpressionTree> getArguments();
}
