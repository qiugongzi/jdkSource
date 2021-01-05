
@jdk.Exported
public interface AnnotatedTypeTree extends ExpressionTree {
    List<? extends AnnotationTree> getAnnotations();
    ExpressionTree getUnderlyingType();
}
