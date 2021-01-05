

package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.*;
import javax.lang.model.util.*;


public interface TypeElement extends Element, Parameterizable, QualifiedNameable {

    @Override
    List<? extends Element> getEnclosedElements();


    NestingKind getNestingKind();


    Name getQualifiedName();


    @Override
    Name getSimpleName();


    TypeMirror getSuperclass();


    List<? extends TypeMirror> getInterfaces();


    List<? extends TypeParameterElement> getTypeParameters();


    @Override
    Element getEnclosingElement();
}
