

package javax.lang.model.type;

import java.lang.annotation.Annotation;
import java.util.List;
import javax.lang.model.element.*;
import javax.lang.model.util.Types;


public interface TypeMirror extends javax.lang.model.AnnotatedConstruct {


    TypeKind getKind();


    boolean equals(Object obj);


    int hashCode();


    String toString();


    <R, P> R accept(TypeVisitor<R, P> v, P p);
}
