

package javax.xml.bind.annotation;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;


@Retention(RUNTIME)
@Target({FIELD,METHOD})
public @interface XmlElementRef {

    Class type() default DEFAULT.class;


    String namespace() default "";

    String name() default "##default";


    static final class DEFAULT {}


    boolean required() default true;
}
