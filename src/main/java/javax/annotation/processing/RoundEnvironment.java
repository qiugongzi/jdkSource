

package javax.annotation.processing;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.lang.annotation.Annotation;


public interface RoundEnvironment {

    boolean processingOver();


    boolean errorRaised();


    Set<? extends Element> getRootElements();


    Set<? extends Element> getElementsAnnotatedWith(TypeElement a);


    Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> a);
}
