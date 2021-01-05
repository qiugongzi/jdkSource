

package javax.xml.bind.annotation;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;



@Inherited @Retention(RUNTIME) @Target({PACKAGE, TYPE})
public @interface XmlAccessorType {


    XmlAccessType value() default XmlAccessType.PUBLIC_MEMBER;
}
