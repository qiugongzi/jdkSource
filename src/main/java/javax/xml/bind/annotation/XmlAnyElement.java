

package javax.xml.bind.annotation;

import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Retention(RUNTIME)
@Target({FIELD,METHOD})
public @interface XmlAnyElement {


    boolean lax() default false;


    Class<? extends DomHandler> value() default W3CDomHandler.class;
}
