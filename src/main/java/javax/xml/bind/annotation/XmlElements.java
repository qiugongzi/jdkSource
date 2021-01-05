

package javax.xml.bind.annotation;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Retention(RUNTIME) @Target({FIELD,METHOD})
public @interface XmlElements {

    XmlElement[] value();
}
