
public interface AnnotatedType extends Type {

    AnnotationDesc[] annotations();

    Type underlyingType();
}
