

package java.lang.reflect;

import sun.reflect.CallerSensitive;
import sun.reflect.MethodAccessor;
import sun.reflect.Reflection;
import sun.reflect.generics.repository.MethodRepository;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.scope.MethodScope;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.annotation.AnnotationParser;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.nio.ByteBuffer;


public final class Method extends Executable {
    private Class<?>            clazz;
    private int                 slot;
    private String              name;
    private Class<?>            returnType;
    private Class<?>[]          parameterTypes;
    private Class<?>[]          exceptionTypes;
    private int                 modifiers;
    private transient String              signature;
    private transient MethodRepository genericInfo;
    private byte[]              annotations;
    private byte[]              parameterAnnotations;
    private byte[]              annotationDefault;
    private volatile MethodAccessor methodAccessor;
    private Method              root;

    private String getGenericSignature() {return signature;}

    private GenericsFactory getFactory() {
        return CoreReflectionFactory.make(this, MethodScope.make(this));
    }

    @Override
    MethodRepository getGenericInfo() {
        if (genericInfo == null) {
            genericInfo = MethodRepository.make(getGenericSignature(),
                                                getFactory());
        }
        return genericInfo; }


    Method(Class<?> declaringClass,
           String name,
           Class<?>[] parameterTypes,
           Class<?> returnType,
           Class<?>[] checkedExceptions,
           int modifiers,
           int slot,
           String signature,
           byte[] annotations,
           byte[] parameterAnnotations,
           byte[] annotationDefault) {
        this.clazz = declaringClass;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.exceptionTypes = checkedExceptions;
        this.modifiers = modifiers;
        this.slot = slot;
        this.signature = signature;
        this.annotations = annotations;
        this.parameterAnnotations = parameterAnnotations;
        this.annotationDefault = annotationDefault;
    }


    Method copy() {
        if (this.root != null)
            throw new IllegalArgumentException("Can not copy a non-root Method");

        Method res = new Method(clazz, name, parameterTypes, returnType,
                                exceptionTypes, modifiers, slot, signature,
                                annotations, parameterAnnotations, annotationDefault);
        res.root = this;
        res.methodAccessor = methodAccessor;
        return res;
    }


    @Override
    Executable getRoot() {
        return root;
    }

    @Override
    boolean hasGenericInformation() {
        return (getGenericSignature() != null);
    }

    @Override
    byte[] getAnnotationBytes() {
        return annotations;
    }


    @Override
    public Class<?> getDeclaringClass() {
        return clazz;
    }


    @Override
    public String getName() {
        return name;
    }


    @Override
    public int getModifiers() {
        return modifiers;
    }


    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public TypeVariable<Method>[] getTypeParameters() {
        if (getGenericSignature() != null)
            return (TypeVariable<Method>[])getGenericInfo().getTypeParameters();
        else
            return (TypeVariable<Method>[])new TypeVariable[0];
    }


    public Class<?> getReturnType() {
        return returnType;
    }


    public Type getGenericReturnType() {
      if (getGenericSignature() != null) {
        return getGenericInfo().getReturnType();
      } else { return getReturnType();}
    }


    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes.clone();
    }


    public int getParameterCount() { return parameterTypes.length; }



    @Override
    public Type[] getGenericParameterTypes() {
        return super.getGenericParameterTypes();
    }


    @Override
    public Class<?>[] getExceptionTypes() {
        return exceptionTypes.clone();
    }


    @Override
    public Type[] getGenericExceptionTypes() {
        return super.getGenericExceptionTypes();
    }


    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Method) {
            Method other = (Method)obj;
            if ((getDeclaringClass() == other.getDeclaringClass())
                && (getName() == other.getName())) {
                if (!returnType.equals(other.getReturnType()))
                    return false;
                return equalParamTypes(parameterTypes, other.parameterTypes);
            }
        }
        return false;
    }


    public int hashCode() {
        return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
    }


    public String toString() {
        return sharedToString(Modifier.methodModifiers(),
                              isDefault(),
                              parameterTypes,
                              exceptionTypes);
    }

    @Override
    void specificToStringHeader(StringBuilder sb) {
        sb.append(getReturnType().getTypeName()).append(' ');
        sb.append(getDeclaringClass().getTypeName()).append('.');
        sb.append(getName());
    }


    @Override
    public String toGenericString() {
        return sharedToGenericString(Modifier.methodModifiers(), isDefault());
    }

    @Override
    void specificToGenericStringHeader(StringBuilder sb) {
        Type genRetType = getGenericReturnType();
        sb.append(genRetType.getTypeName()).append(' ');
        sb.append(getDeclaringClass().getTypeName()).append('.');
        sb.append(getName());
    }


    @CallerSensitive
    public Object invoke(Object obj, Object... args)
        throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException
    {
        if (!override) {
            if (!Reflection.quickCheckMemberAccess(clazz, modifiers)) {
                Class<?> caller = Reflection.getCallerClass();
                checkAccess(caller, clazz, obj, modifiers);
            }
        }
        MethodAccessor ma = methodAccessor;             if (ma == null) {
            ma = acquireMethodAccessor();
        }
        return ma.invoke(obj, args);
    }


    public boolean isBridge() {
        return (getModifiers() & Modifier.BRIDGE) != 0;
    }


    @Override
    public boolean isVarArgs() {
        return super.isVarArgs();
    }


    @Override
    public boolean isSynthetic() {
        return super.isSynthetic();
    }


    public boolean isDefault() {
        return ((getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) ==
                Modifier.PUBLIC) && getDeclaringClass().isInterface();
    }

    private MethodAccessor acquireMethodAccessor() {
        MethodAccessor tmp = null;
        if (root != null) tmp = root.getMethodAccessor();
        if (tmp != null) {
            methodAccessor = tmp;
        } else {
            tmp = reflectionFactory.newMethodAccessor(this);
            setMethodAccessor(tmp);
        }

        return tmp;
    }

    MethodAccessor getMethodAccessor() {
        return methodAccessor;
    }

    void setMethodAccessor(MethodAccessor accessor) {
        methodAccessor = accessor;
        if (root != null) {
            root.setMethodAccessor(accessor);
        }
    }


    public Object getDefaultValue() {
        if  (annotationDefault == null)
            return null;
        Class<?> memberType = AnnotationType.invocationHandlerReturnType(
            getReturnType());
        Object result = AnnotationParser.parseMemberValue(
            memberType, ByteBuffer.wrap(annotationDefault),
            sun.misc.SharedSecrets.getJavaLangAccess().
                getConstantPool(getDeclaringClass()),
            getDeclaringClass());
        if (result instanceof sun.reflect.annotation.ExceptionProxy)
            throw new AnnotationFormatError("Invalid default: " + this);
        return result;
    }


    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return super.getAnnotation(annotationClass);
    }


    public Annotation[] getDeclaredAnnotations()  {
        return super.getDeclaredAnnotations();
    }


    @Override
    public Annotation[][] getParameterAnnotations() {
        return sharedGetParameterAnnotations(parameterTypes, parameterAnnotations);
    }


    @Override
    public AnnotatedType getAnnotatedReturnType() {
        return getAnnotatedReturnType0(getGenericReturnType());
    }

    @Override
    void handleParameterNumberMismatch(int resultLength, int numParameters) {
        throw new AnnotationFormatError("Parameter annotations don't match number of parameters");
    }
}
