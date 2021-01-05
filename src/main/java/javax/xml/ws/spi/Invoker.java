

package javax.xml.ws.spi;

import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceFeature;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;



public abstract class Invoker {


    public abstract void inject(WebServiceContext webServiceContext)
    throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;


    public abstract Object invoke(Method m, Object... args)
    throws  IllegalAccessException, IllegalArgumentException, InvocationTargetException;

}
