

package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.*;


public interface ExecutableElement extends Element, Parameterizable {

    List<? extends TypeParameterElement> getTypeParameters();


    TypeMirror getReturnType();


    List<? extends VariableElement> getParameters();


    TypeMirror getReceiverType();


    boolean isVarArgs();


    boolean isDefault();


    List<? extends TypeMirror> getThrownTypes();


    AnnotationValue getDefaultValue();


    @Override
    Name getSimpleName();
}
