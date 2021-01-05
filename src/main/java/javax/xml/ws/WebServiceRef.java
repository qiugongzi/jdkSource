

package javax.xml.ws;

import javax.xml.ws.soap.Addressing;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebServiceRef {

    String name() default "";


    Class<?> type() default Object.class;


    String mappedName() default "";


    Class<? extends Service> value() default Service.class;


    String wsdlLocation() default "";


    String lookup() default "";

}
