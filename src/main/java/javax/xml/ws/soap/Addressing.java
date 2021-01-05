

package javax.xml.ws.soap;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.WebServiceRefs;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.soap.AddressingFeature.Responses;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;


@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id=AddressingFeature.ID,bean=AddressingFeature.class)
public @interface Addressing {

    boolean enabled() default true;


    boolean required() default false;


    Responses responses() default Responses.ALL;

}
