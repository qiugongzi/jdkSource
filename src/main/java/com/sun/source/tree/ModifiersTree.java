
@jdk.Exported
public interface ModifiersTree extends Tree {
    Set<Modifier> getFlags();
    List<? extends AnnotationTree> getAnnotations();
}
