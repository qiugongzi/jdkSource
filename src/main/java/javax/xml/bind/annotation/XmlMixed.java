

package javax.xml.bind.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import org.w3c.dom.Element;
import javax.xml.bind.JAXBElement;


@Retention(RUNTIME)
@Target({FIELD,METHOD})
public @interface XmlMixed {
}
