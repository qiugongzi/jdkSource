

package javax.xml.bind.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;


@Retention(RUNTIME)
@Target({TYPE})
public @interface XmlRootElement {

    String namespace() default "##default";


    String name() default "##default";

}
