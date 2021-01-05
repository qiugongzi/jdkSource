

package java.beans;

import java.lang.ref.Reference;



public class BeanDescriptor extends FeatureDescriptor {

    private Reference<? extends Class<?>> beanClassRef;
    private Reference<? extends Class<?>> customizerClassRef;


    public BeanDescriptor(Class<?> beanClass) {
        this(beanClass, null);
    }


    public BeanDescriptor(Class<?> beanClass, Class<?> customizerClass) {
        this.beanClassRef = getWeakReference(beanClass);
        this.customizerClassRef = getWeakReference(customizerClass);

        String name = beanClass.getName();
        while (name.indexOf('.') >= 0) {
            name = name.substring(name.indexOf('.')+1);
        }
        setName(name);
    }


    public Class<?> getBeanClass() {
        return (this.beanClassRef != null)
                ? this.beanClassRef.get()
                : null;
    }


    public Class<?> getCustomizerClass() {
        return (this.customizerClassRef != null)
                ? this.customizerClassRef.get()
                : null;
    }


    BeanDescriptor(BeanDescriptor old) {
        super(old);
        beanClassRef = old.beanClassRef;
        customizerClassRef = old.customizerClassRef;
    }

    void appendTo(StringBuilder sb) {
        appendTo(sb, "beanClass", this.beanClassRef);
        appendTo(sb, "customizerClass", this.customizerClassRef);
    }
}
