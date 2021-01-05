

package javax.xml.bind;

import javax.xml.namespace.QName;
import java.io.Serializable;



public class JAXBElement<T> implements Serializable {


    final protected QName name;


    final protected Class<T> declaredType;


    final protected Class scope;


    protected T value;


    protected boolean nil = false;


    public static final class GlobalScope {}


    public JAXBElement(QName name,
                       Class<T> declaredType,
                       Class scope,
                       T value) {
        if(declaredType==null || name==null)
            throw new IllegalArgumentException();
        this.declaredType = declaredType;
        if(scope==null)     scope = GlobalScope.class;
        this.scope = scope;
        this.name = name;
        setValue(value);
    }


    public JAXBElement(QName name, Class<T> declaredType, T value ) {
        this(name,declaredType,GlobalScope.class,value);
    }


    public Class<T> getDeclaredType() {
        return declaredType;
    }


    public QName getName() {
        return name;
    }


    public void setValue(T t) {
        this.value = t;
    }


    public T getValue() {
        return value;
    }


    public Class getScope() {
        return scope;
    }


    public boolean isNil() {
        return (value == null) || nil;
    }


    public void setNil(boolean value) {
        this.nil = value;
    }




    public boolean isGlobalScope() {
        return this.scope == GlobalScope.class;
    }


    public boolean isTypeSubstituted() {
        if(value==null)     return false;
        return value.getClass() != declaredType;
    }

    private static final long serialVersionUID = 1L;
}
